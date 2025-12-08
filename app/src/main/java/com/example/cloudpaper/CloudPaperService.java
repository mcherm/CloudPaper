package com.example.cloudpaper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
        private int surfaceWidth;
        private int surfaceHeight;
        private boolean visible;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            // Initialize sky paint with solid color
            skyPaint = new Paint();
            skyPaint.setColor(Color.parseColor("#55B4E1")); // Light blue background
            skyPaint.setStyle(Paint.Style.FILL);

            // Initialize cloud renderer
            cloudRenderer = new CloudRenderer();

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
                // Wallpaper is visible - draw it
                draw();
            } else {
                // Wallpaper is not visible - no need to draw
                // (Future: stop animation here to save battery)
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            visible = false;
        }

        /**
         * Draw the wallpaper with solid sky color and procedural clouds
         */
        private void draw() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;

            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    // Draw solid sky color
                    canvas.drawRect(0, 0, surfaceWidth, surfaceHeight, skyPaint);

                    // Generate and draw clouds
                    Bitmap cloudBitmap = cloudRenderer.generateClouds();
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
