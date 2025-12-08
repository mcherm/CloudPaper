package com.example.cloudpaper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

/**
 * CloudPaper Live Wallpaper Service
 *
 * This service provides an animated sky wallpaper with moving clouds.
 * Phase 2: Basic wallpaper service with solid color background.
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

        private Paint backgroundPaint;
        private int surfaceWidth;
        private int surfaceHeight;
        private boolean visible;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            // Initialize the paint for the background
            backgroundPaint = new Paint();
            backgroundPaint.setColor(Color.parseColor("#55B4E1")); // Light blue color
            backgroundPaint.setStyle(Paint.Style.FILL);

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

            // Redraw with new dimensions
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
         * Draw the wallpaper
         */
        private void draw() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;

            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    // Fill the entire canvas with light blue
                    canvas.drawRect(0, 0, surfaceWidth, surfaceHeight, backgroundPaint);
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
