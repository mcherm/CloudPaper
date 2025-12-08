package com.example.cloudpaper.gl;

import android.content.Context;
import android.opengl.GLES20;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * OpenGL ES 2.0 renderer for GPU-based cloud generation.
 *
 * This renderer uses FastNoiseLite GLSL shaders to generate clouds on the GPU,
 * replacing the CPU-based noise generation which takes ~900-1000ms per frame.
 * Target performance: <20ms per frame.
 */
public class GLCloudRenderer implements android.opengl.GLSurfaceView.Renderer {

    private final Context context;

    // Shader program and handles
    private int shaderProgram;
    private int vertexShaderHandle;
    private int fragmentShaderHandle;

    // Uniform locations
    private int uTimeLocation;
    private int uDriftXLocation;
    private int uDriftYLocation;
    private int uResolutionLocation;

    // Animation state
    private float time = 0.0f;
    private float driftX = 0.0f;
    private float driftY = 0.0f;

    // Screen dimensions
    private int screenWidth;
    private int screenHeight;

    /**
     * Creates a new GLCloudRenderer.
     *
     * @param context The application context, used for loading shader resources
     */
    public GLCloudRenderer(Context context) {
        this.context = context;
    }

    /**
     * Called when the surface is created or recreated.
     * Sets up shaders and OpenGL state.
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set clear color to sky blue
        GLES20.glClearColor(0.53f, 0.81f, 0.92f, 1.0f);

        // TODO: Load and compile shaders
        // TODO: Create shader program
        // TODO: Get uniform locations
        // TODO: Set up full-screen quad geometry
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

        // TODO: Update resolution uniform in shader
    }

    /**
     * Called for each frame to render.
     * Updates time/drift uniforms and draws the full-screen quad.
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear the screen
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // TODO: Update time and drift based on elapsed time
        // TODO: Set uniforms
        // TODO: Draw full-screen quad
    }

    /**
     * Updates animation time based on elapsed milliseconds.
     *
     * @param deltaTimeMs Time elapsed since last update in milliseconds
     */
    public void updateTime(float deltaTimeMs) {
        // Evolution rate: how fast clouds change shape (z-axis in noise function)
        float evolutionRate = 0.001f; // Adjust as needed
        time += deltaTimeMs * evolutionRate;
    }

    /**
     * Updates drift based on elapsed milliseconds.
     *
     * @param deltaTimeMs Time elapsed since last update in milliseconds
     */
    public void updateDrift(float deltaTimeMs) {
        // Drift rate: how fast clouds move across the screen
        // Default to east-south-east direction
        float driftRateX = 0.0001f; // Adjust as needed
        float driftRateY = 0.00005f; // Adjust as needed

        driftX += deltaTimeMs * driftRateX;
        driftY += deltaTimeMs * driftRateY;
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
