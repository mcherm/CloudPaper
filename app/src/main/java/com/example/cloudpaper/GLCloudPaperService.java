package com.example.cloudpaper;

import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.cloudpaper.gl.GLES3ContextFactory;
import com.example.cloudpaper.gl.GLCloudRenderer;
import com.example.cloudpaper.gl.GLWallpaperService;

/**
 * GPU-accelerated CloudPaper Live Wallpaper Service
 *
 * <p>This service provides an animated sky wallpaper with moving clouds,
 * using OpenGL ES 3.0 for GPU-based rendering. This replaces the CPU-based
 * noise generation (which takes ~900-1000ms per frame) with GPU shaders
 * (target: &lt;20ms per frame).
 *
 * <p>This is an alternative implementation to {@link CloudPaperService},
 * using {@link GLWallpaperService} for OpenGL rendering instead of
 * Canvas-based rendering.
 */
public class GLCloudPaperService extends GLWallpaperService {

    private static final String TAG = "GLCloudPaperService";

    @Override
    public Engine onCreateEngine() {
        return new GLCloudPaperEngine();
    }

    /**
     * GLEngine inner class that handles OpenGL wallpaper rendering and lifecycle
     */
    private class GLCloudPaperEngine extends GLWallpaperService.GLEngine {

        private GLCloudRenderer renderer;
        private AnimationSettings animationSettings;

        @Override
        public void onCreate(android.view.SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            Log.d(TAG, "GLCloudPaperEngine onCreate");

            // Initialize animation settings
            animationSettings = new AnimationSettings();

            // Create GL renderer with animation settings
            renderer = new GLCloudRenderer(GLCloudPaperService.this, animationSettings);

            // Request OpenGL ES 3.0 context (required for integer bitwise operations in shader)
            setEGLContextFactory(new GLES3ContextFactory());

            // Set the renderer for this engine
            setRenderer(renderer);

            // Set render mode to continuous (animate continuously)
            // Alternative: RENDERMODE_WHEN_DIRTY for manual control
            setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

            Log.d(TAG, "GLCloudPaperEngine initialized with renderer");
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            Log.d(TAG, "Visibility changed: " + visible);

            if (visible) {
                // Wallpaper is visible - resume rendering
                onResume();
            } else {
                // Wallpaper is not visible - pause rendering to save battery
                onPause();
            }
        }

        @Override
        public void onDestroy() {
            Log.d(TAG, "GLCloudPaperEngine onDestroy - cleaning up GL resources");

            // Clean up renderer resources
            if (renderer != null) {
                renderer.cleanup();
            }

            super.onDestroy();
        }
    }
}
