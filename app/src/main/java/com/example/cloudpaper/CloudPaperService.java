package com.example.cloudpaper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import com.example.cloudpaper.renderer.CloudRenderer;

/**
 * CloudPaper Live Wallpaper Service
 *
 * <p>This service provides an animated sky wallpaper with moving clouds.
 */
public class CloudPaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new CloudPaperEngine();
    }

    /**
     * Engine inner class that handles the wallpaper rendering and lifecycle
     */
    private class CloudPaperEngine extends Engine {

        private Paint skyPaint;
        private CloudRenderer cloudRenderer;
        private AnimationSettings animationSettings;

        private int surfaceWidth;
        private int surfaceHeight;
        private boolean visible;

        // Animation parameters
        private Handler handler;
        private Runnable drawRunnable;
        private long frameDelayMs;
        private long animationStartTime;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            // Initialize the animationSettings
            animationSettings = new AnimationSettings();

            // Initialize sky paint with solid color
            skyPaint = new Paint();
            skyPaint.setColor(Color.parseColor( animationSettings.skyColor));
            skyPaint.setStyle(Paint.Style.FILL);

            // Initialize cloud renderer
            cloudRenderer = new CloudRenderer(animationSettings);

            // Initialize animation
            handler = new Handler(Looper.getMainLooper());
            frameDelayMs = 1000 / animationSettings.framesPerSecond;
            animationStartTime = System.currentTimeMillis();

            drawRunnable = new Runnable() {
                @Override
                public void run() {
                    draw();
                    if (visible) {
                        handler.postDelayed(this, frameDelayMs);
                    }
                }
            };

            visible = false;
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            // Surface is ready - we can start drawing
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

            // Store the surface dimensions
            surfaceWidth = width;
            surfaceHeight = height;

            // Update cloud renderer size
            cloudRenderer.setSurfaceSize(width, height);

            // Generate clouds and redraw
            draw();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            visible = false;
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;

            if (visible) {
                // Wallpaper is visible - start animation
                handler.post(drawRunnable);
            } else {
                // Wallpaper is not visible - stop animation to save battery
                handler.removeCallbacks(drawRunnable);
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            visible = false;
            handler.removeCallbacks(drawRunnable);
        }

        /**
         * Draw the wallpaper with solid sky color and procedural clouds
         */
        private void draw() {
            // Calculate elapsed time in seconds since animation started
            final long currentTime = System.currentTimeMillis();
            final float elapsedMillis = (float)(currentTime - animationStartTime);

            // Use elapsed time as z-position for 3D noise, with a scale factor
            // that controls how fast clouds evolve
            final float zPosition = elapsedMillis * animationSettings.evolutionRate;

            final SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;

            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    // Draw solid sky color
                    canvas.drawRect(0, 0, surfaceWidth, surfaceHeight, skyPaint);

                    // Generate and draw clouds
                    final Bitmap cloudBitmap = cloudRenderer.generateClouds(zPosition);
                    if (cloudBitmap != null) {
                        canvas.drawBitmap(cloudBitmap, 0, 0, null);
                    }
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
