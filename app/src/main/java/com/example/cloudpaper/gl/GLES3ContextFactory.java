package com.example.cloudpaper.gl;

import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * Custom EGL Context Factory that requests OpenGL ES 3.0 context.
 *
 * This is required for using integer bitwise operations in GLSL shaders.
 */
public class GLES3ContextFactory implements GLSurfaceView.EGLContextFactory {

    private static final String TAG = "GLES3ContextFactory";
    private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

    @Override
    public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig config) {
        Log.d(TAG, "Creating OpenGL ES 3.0 context");

        // Request OpenGL ES 3.0 context
        int[] attrib_list = {
            EGL_CONTEXT_CLIENT_VERSION, 3,
            EGL10.EGL_NONE
        };

        EGLContext context = egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, attrib_list);

        if (context == null || context == EGL10.EGL_NO_CONTEXT) {
            Log.e(TAG, "Failed to create OpenGL ES 3.0 context, EGL error: " + egl.eglGetError());
            throw new RuntimeException("Failed to create OpenGL ES 3.0 context");
        }

        Log.d(TAG, "Successfully created OpenGL ES 3.0 context");
        return context;
    }

    @Override
    public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
        if (!egl.eglDestroyContext(display, context)) {
            Log.e(TAG, "Failed to destroy context, EGL error: " + egl.eglGetError());
        }
    }
}
