package com.example.cloudpaper.gl;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.util.Log;

import com.example.cloudpaper.AnimationSettings;
import com.example.cloudpaper.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * OpenGL ES 2.0 renderer for GPU-based cloud generation.
 *
 * <p>This renderer uses FastNoiseLite GLSL shaders to generate clouds on the GPU,
 * replacing the CPU-based noise generation which takes ~900-1000ms per frame.
 * Target performance: &lt;20ms per frame.
 */
public class GLCloudRenderer implements android.opengl.GLSurfaceView.Renderer {

    private static final String TAG = "GLCloudRenderer";

    private final Context context;
    private final AnimationSettings animationSettings;

    // Shader program and handles
    private int shaderProgram;
    private int vertexShaderHandle;
    private int fragmentShaderHandle;

    // Attribute locations
    private int aPositionLocation;
    private int aTexCoordLocation;

    // Uniform locations
    private int uTimeLocation;
    private int uDriftXLocation;
    private int uDriftYLocation;
    private int uResolutionLocation;
    private int uFrequencyLocation;
    private int uThresholdLocation;
    private int uSkyColorLocation;
    private int uCloudColorLocation;

    // Vertex buffer for full-screen quad
    private FloatBuffer vertexBuffer;
    private FloatBuffer texCoordBuffer;

    // Animation state
    private float time = 0.0f;
    private float driftX = 0.0f;
    private float driftY = 0.0f;
    private long lastFrameTime = 0;

    // Screen dimensions
    private int screenWidth;
    private int screenHeight;

    // Cloud appearance parameters (parsed from AnimationSettings)
    private final float frequency;
    private final float threshold;
    private final float evolutionRate;
    private final float[] skyColor;
    private final float[] cloudColor = {1.0f, 1.0f, 1.0f}; // White clouds

    /**
     * Creates a new GLCloudRenderer.
     *
     * @param context The application context, used for loading shader resources
     * @param animationSettings The animation settings containing appearance parameters
     */
    public GLCloudRenderer(Context context, AnimationSettings animationSettings) {
        Log.e(TAG, "*** GLCloudRenderer constructor ENTRY ***");
        this.context = context;
        this.animationSettings = animationSettings;

        // Extract values from AnimationSettings
        Log.e(TAG, "*** About to extract values from AnimationSettings ***");
        this.frequency = animationSettings.noiseFrequency;
        this.threshold = animationSettings.cloudDensityThreshold;
        this.evolutionRate = animationSettings.evolutionRate;
        this.skyColor = parseHexColor(animationSettings.skyColor);

        Log.e(TAG, "*** GLCloudRenderer initialized with settings: " +
                   "frequency=" + frequency + ", threshold=" + threshold +
                   ", evolutionRate=" + evolutionRate +
                   ", skyColor=" + animationSettings.skyColor + " ***");
    }

    /**
     * Parses a hex color string (e.g., "#55B4E1") to RGB float array [0.0-1.0].
     *
     * @param hexColor Hex color string with # prefix
     * @return float array [r, g, b] with values in range [0.0, 1.0]
     */
    private float[] parseHexColor(String hexColor) {
        try {
            int color = Color.parseColor(hexColor);
            return new float[] {
                Color.red(color) / 255.0f,
                Color.green(color) / 255.0f,
                Color.blue(color) / 255.0f
            };
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Failed to parse color '" + hexColor + "', using default sky blue", e);
            return new float[] {0.53f, 0.81f, 0.92f}; // Default sky blue
        }
    }

    /**
     * Called when the surface is created or recreated.
     * Sets up shaders and OpenGL state.
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "Beginning the function onSurfaceCreated()");
        // Set clear color to sky blue
        GLES20.glClearColor(skyColor[0], skyColor[1], skyColor[2], 1.0f);

        // Load and compile shaders
        Log.d(TAG, "Loading and compiling shaders...");
        shaderProgram = ShaderUtils.createProgram(context, R.raw.vertex_shader, R.raw.fragment_shader);

        if (shaderProgram == 0) {
            Log.e(TAG, "Failed to create shader program!");
            return;
        }

        // Get attribute locations
        aPositionLocation = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
        aTexCoordLocation = GLES20.glGetAttribLocation(shaderProgram, "aTexCoord");

        // Get uniform locations
        uTimeLocation = GLES20.glGetUniformLocation(shaderProgram, "uTime");
        uDriftXLocation = GLES20.glGetUniformLocation(shaderProgram, "uDriftX");
        uDriftYLocation = GLES20.glGetUniformLocation(shaderProgram, "uDriftY");
        uResolutionLocation = GLES20.glGetUniformLocation(shaderProgram, "uResolution");
        uFrequencyLocation = GLES20.glGetUniformLocation(shaderProgram, "uFrequency");
        uThresholdLocation = GLES20.glGetUniformLocation(shaderProgram, "uThreshold");
        uSkyColorLocation = GLES20.glGetUniformLocation(shaderProgram, "uSkyColor");
        uCloudColorLocation = GLES20.glGetUniformLocation(shaderProgram, "uCloudColor");

        Log.d(TAG, "Attribute locations: aPosition=" + aPositionLocation + ", aTexCoord=" + aTexCoordLocation);
        Log.d(TAG, "Uniform locations: uTime=" + uTimeLocation + ", uDriftX=" + uDriftXLocation +
                   ", uDriftY=" + uDriftYLocation + ", uResolution=" + uResolutionLocation);

        // Set up full-screen quad geometry
        setupGeometry();

        Log.d(TAG, "Shader initialization complete");
    }

    /**
     * Sets up the geometry for a full-screen quad (two triangles).
     */
    private void setupGeometry() {
        // Full-screen quad vertices (position)
        // Two triangles forming a quad covering NDC space [-1, 1]
        float[] vertices = {
            -1.0f, -1.0f,  // Bottom-left
             1.0f, -1.0f,  // Bottom-right
            -1.0f,  1.0f,  // Top-left
             1.0f,  1.0f   // Top-right
        };

        // Texture coordinates (0 to 1)
        float[] texCoords = {
            0.0f, 0.0f,  // Bottom-left
            1.0f, 0.0f,  // Bottom-right
            0.0f, 1.0f,  // Top-left
            1.0f, 1.0f   // Top-right
        };

        // Create native buffers
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4);
        tbb.order(ByteOrder.nativeOrder());
        texCoordBuffer = tbb.asFloatBuffer();
        texCoordBuffer.put(texCoords);
        texCoordBuffer.position(0);
    }

    /**
     * Called when the surface size changes (e.g., screen rotation).
     * Updates viewport and resolution uniforms.
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;

        // Update viewport
        GLES20.glViewport(0, 0, width, height);

        Log.d(TAG, "Surface changed: " + width + "x" + height);
    }

    /**
     * Called for each frame to render.
     * Updates time/drift uniforms and draws the full-screen quad.
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear the screen
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        if (shaderProgram == 0) {
            return; // Shader not initialized
        }

        // Update animation state based on elapsed time
        long currentTime = System.currentTimeMillis();
        if (lastFrameTime != 0) {
            float deltaTimeMs = currentTime - lastFrameTime;
            updateTime(deltaTimeMs);
            updateDrift(deltaTimeMs);
        }
        lastFrameTime = currentTime;

        // Use shader program
        GLES20.glUseProgram(shaderProgram);

        // Set uniforms
        GLES20.glUniform1f(uTimeLocation, time);
        GLES20.glUniform1f(uDriftXLocation, driftX);
        GLES20.glUniform1f(uDriftYLocation, driftY);
        GLES20.glUniform2f(uResolutionLocation, (float) screenWidth, (float) screenHeight);
        GLES20.glUniform1f(uFrequencyLocation, frequency);
        GLES20.glUniform1f(uThresholdLocation, threshold);
        GLES20.glUniform3f(uSkyColorLocation, skyColor[0], skyColor[1], skyColor[2]);
        GLES20.glUniform3f(uCloudColorLocation, cloudColor[0], cloudColor[1], cloudColor[2]);

        // Set up vertex attributes
        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(aPositionLocation, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(aPositionLocation);

        texCoordBuffer.position(0);
        GLES20.glVertexAttribPointer(aTexCoordLocation, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer);
        GLES20.glEnableVertexAttribArray(aTexCoordLocation);

        // Draw full-screen quad (triangle strip with 4 vertices)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        // Disable vertex arrays
        GLES20.glDisableVertexAttribArray(aPositionLocation);
        GLES20.glDisableVertexAttribArray(aTexCoordLocation);
    }

    /**
     * Updates animation time based on elapsed milliseconds.
     * Uses evolutionRate from AnimationSettings to control how fast clouds change shape.
     *
     * @param deltaTimeMs Time elapsed since last update in milliseconds
     */
    public void updateTime(float deltaTimeMs) {
        // Evolution rate from AnimationSettings: how fast clouds change shape (z-axis in noise function)
        time += deltaTimeMs * evolutionRate;
    }

    /**
     * Updates drift based on elapsed milliseconds.
     * Uses driftX and driftY from AnimationSettings to control cloud movement.
     *
     * @param deltaTimeMs Time elapsed since last update in milliseconds
     */
    public void updateDrift(float deltaTimeMs) {
        // Drift rates from AnimationSettings: how fast clouds move across the screen
        driftX += deltaTimeMs * animationSettings.driftX;
        driftY += deltaTimeMs * animationSettings.driftY;
    }

    /**
     * Cleans up OpenGL resources.
     * Call this when the renderer is destroyed.
     */
    public void cleanup() {
        if (shaderProgram != 0) {
            GLES20.glDeleteProgram(shaderProgram);
            shaderProgram = 0;
        }
        if (vertexShaderHandle != 0) {
            GLES20.glDeleteShader(vertexShaderHandle);
            vertexShaderHandle = 0;
        }
        if (fragmentShaderHandle != 0) {
            GLES20.glDeleteShader(fragmentShaderHandle);
            fragmentShaderHandle = 0;
        }
    }
}
