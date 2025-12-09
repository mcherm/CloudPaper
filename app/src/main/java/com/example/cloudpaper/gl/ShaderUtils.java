package com.example.cloudpaper.gl;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Utility class for loading, compiling, and linking GLSL shaders.
 *
 * Provides methods to:
 * - Load shader source code from res/raw resources
 * - Compile vertex and fragment shaders
 * - Link shaders into a shader program
 * - Handle and report compilation/linking errors
 */
public class ShaderUtils {

    private static final String TAG = "ShaderUtils";

    /**
     * Loads shader source code from a raw resource file.
     *
     * @param context    The application context
     * @param resourceId The resource ID of the shader file (e.g., R.raw.vertex_shader)
     * @return The shader source code as a String, or null if loading fails
     */
    public static String loadShaderSource(Context context, int resourceId) {
        StringBuilder shaderSource = new StringBuilder();

        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }

            reader.close();
            return shaderSource.toString();

        } catch (IOException e) {
            Log.e(TAG, "Error loading shader source from resource " + resourceId, e);
            return null;
        }
    }

    /**
     * Compiles a shader from source code.
     *
     * @param shaderType   The type of shader (GLES20.GL_VERTEX_SHADER or GLES20.GL_FRAGMENT_SHADER)
     * @param shaderSource The shader source code
     * @return The shader handle, or 0 if compilation fails
     */
    public static int compileShader(int shaderType, String shaderSource) {
        if (shaderSource == null || shaderSource.isEmpty()) {
            Log.e(TAG, "Shader source is null or empty");
            return 0;
        }

        // Create shader object
        int shaderHandle = GLES20.glCreateShader(shaderType);
        if (shaderHandle == 0) {
            Log.e(TAG, "Error creating shader object");
            return 0;
        }

        // Load shader source
        GLES20.glShaderSource(shaderHandle, shaderSource);

        // Compile shader
        GLES20.glCompileShader(shaderHandle);

        // Check compilation status
        int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        if (compileStatus[0] == 0) {
            // Compilation failed - get error log
            String errorLog = GLES20.glGetShaderInfoLog(shaderHandle);
            Log.e(TAG, "Shader compilation failed:\n" + errorLog);
            Log.e(TAG, "Shader source:\n" + shaderSource);

            // Delete the shader
            GLES20.glDeleteShader(shaderHandle);
            return 0;
        }

        String shaderTypeName = (shaderType == GLES20.GL_VERTEX_SHADER) ? "vertex" : "fragment";
        Log.d(TAG, "Successfully compiled " + shaderTypeName + " shader");

        return shaderHandle;
    }

    /**
     * Creates a shader program from vertex and fragment shader source code.
     *
     * @param context            The application context
     * @param vertexResourceId   The resource ID of the vertex shader
     * @param fragmentResourceId The resource ID of the fragment shader
     * @return The shader program handle, or 0 if creation fails
     */
    public static int createProgram(Context context, int vertexResourceId, int fragmentResourceId) {
        // Load shader sources
        String vertexSource = loadShaderSource(context, vertexResourceId);
        String fragmentSource = loadShaderSource(context, fragmentResourceId);

        if (vertexSource == null || fragmentSource == null) {
            Log.e(TAG, "Failed to load shader sources");
            return 0;
        }

        // Compile shaders
        int vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        int fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);

        if (vertexShader == 0 || fragmentShader == 0) {
            Log.e(TAG, "Failed to compile shaders");
            if (vertexShader != 0) {
                GLES20.glDeleteShader(vertexShader);
            }
            if (fragmentShader != 0) {
                GLES20.glDeleteShader(fragmentShader);
            }
            return 0;
        }

        // Create and link program
        return linkProgram(vertexShader, fragmentShader);
    }

    /**
     * Links compiled vertex and fragment shaders into a shader program.
     *
     * @param vertexShader   The compiled vertex shader handle
     * @param fragmentShader The compiled fragment shader handle
     * @return The shader program handle, or 0 if linking fails
     */
    public static int linkProgram(int vertexShader, int fragmentShader) {
        // Create program object
        int programHandle = GLES20.glCreateProgram();
        if (programHandle == 0) {
            Log.e(TAG, "Error creating program object");
            return 0;
        }

        // Attach shaders
        GLES20.glAttachShader(programHandle, vertexShader);
        GLES20.glAttachShader(programHandle, fragmentShader);

        // Link program
        GLES20.glLinkProgram(programHandle);

        // Check link status
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

        if (linkStatus[0] == 0) {
            // Linking failed - get error log
            String errorLog = GLES20.glGetProgramInfoLog(programHandle);
            Log.e(TAG, "Program linking failed:\n" + errorLog);

            // Delete the program
            GLES20.glDeleteProgram(programHandle);
            return 0;
        }

        Log.d(TAG, "Successfully linked shader program");

        return programHandle;
    }

    /**
     * Validates a shader program.
     * This is useful for debugging but should not be called in production code.
     *
     * @param programHandle The shader program handle
     * @return True if the program is valid, false otherwise
     */
    public static boolean validateProgram(int programHandle) {
        GLES20.glValidateProgram(programHandle);

        int[] validateStatus = new int[1];
        GLES20.glGetProgramiv(programHandle, GLES20.GL_VALIDATE_STATUS, validateStatus, 0);

        if (validateStatus[0] == 0) {
            String errorLog = GLES20.glGetProgramInfoLog(programHandle);
            Log.e(TAG, "Program validation failed:\n" + errorLog);
            return false;
        }

        Log.d(TAG, "Program validation successful");
        return true;
    }

    /**
     * Checks for OpenGL errors and logs them.
     *
     * @param operation The name of the operation that was just performed
     */
    public static void checkGLError(String operation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, operation + ": glError " + error);
            throw new RuntimeException(operation + ": glError " + error);
        }
    }
}
