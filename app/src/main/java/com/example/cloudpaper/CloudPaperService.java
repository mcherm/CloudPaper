package com.example.cloudpaper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
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
        private long lastFrameMillis; // FIXME: Remove this

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
                    long newTimeMillis = System.currentTimeMillis();
                    long elapsedMillis = newTimeMillis - lastFrameMillis;
                    lastFrameMillis = newTimeMillis;
                    Log.d("CloudPaper", "Beginning new run just " + elapsedMillis + " millis later.");
                    draw();
                    if (visible) {
                        Log.d("CloudPaper", "Will schedule to run again in " + frameDelayMs + "ms");
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
            final long t1 = System.currentTimeMillis();

            // Calculate elapsed time in milliseconds since animation started
            final long currentTime = System.currentTimeMillis();
            final float elapsedMillis = (float)(currentTime - animationStartTime);

            // Calculate drift offsets based on elapsed time and drift rates
            final float xOffset = elapsedMillis * animationSettings.driftX;
            final float yOffset = elapsedMillis * animationSettings.driftY;

            // Use elapsed time for evolution (z-position in 3D noise)
            final float zOffset = elapsedMillis * animationSettings.evolutionRate;

            final long t2 = System.currentTimeMillis();

            // Generate and draw clouds with evolution and drift
            final Bitmap cloudBitmap = cloudRenderer.generateClouds(xOffset, yOffset, zOffset);

            final long t3 = System.currentTimeMillis();

            Canvas canvas = null;
            try {
                canvas = getSurfaceHolder().lockCanvas();

                final long t4 = System.currentTimeMillis();

                if (canvas != null) {
                    // Draw solid sky color
                    canvas.drawRect(0, 0, surfaceWidth, surfaceHeight, skyPaint);
                    canvas.drawBitmap(cloudBitmap, 0, 0, null);
                }

                final long t5 = System.currentTimeMillis();

                Log.d("CloudPaper", "Draw timing: prep=" + (t2-t1) + "ms, generate=" + (t3-t2) +
                      "ms, lockCanvas=" + (t4-t3) + "ms, drawOps=" + (t5-t4) + "ms, total=" + (t5-t1) + "ms");

            } finally {
                if (canvas != null) {
                    long t6pre = System.currentTimeMillis();
                    getSurfaceHolder().unlockCanvasAndPost(canvas);
                    long t6post = System.currentTimeMillis();
                    Log.d("CloudPaper", "unlockCanvasAndPost took " + (t6post-t6pre) + "ms");
                }
            }
        }
    }
}
