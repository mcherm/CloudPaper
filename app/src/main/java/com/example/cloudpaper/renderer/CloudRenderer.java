package com.example.cloudpaper.renderer;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.example.cloudpaper.AnimationSettings;
import com.example.cloudpaper.noise.FastNoiseLite;

/**
 * CloudRenderer - Generates procedural cloud textures using 3D Simplex noise
 *
 * <p>Uses FastNoiseLite with OpenSimplex2S and FBm (Fractal Brownian Motion)
 * to create realistic, wispy cloud patterns.
 */
public class CloudRenderer {

    private final FastNoiseLite noise;
    private Bitmap cloudBitmap;
    private int[] pixels;
    private int widthPixels;
    private int heightPixels;
    private final AnimationSettings animationSettings;

    public CloudRenderer(AnimationSettings animationSettings) {
        this.animationSettings = animationSettings;

        // Initialize FastNoiseLite with OpenSimplex2S and FBm
        noise = new FastNoiseLite();
        noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        noise.SetFractalType(FastNoiseLite.FractalType.FBm);
        noise.SetFractalOctaves(3);
        noise.SetFrequency(animationSettings.noiseFrequency);
    }

    /**
     * Initialize or resize the cloud bitmap
     */
    public void setSurfaceSize(int width, int height) {
        if (this.widthPixels != width || this.heightPixels != height) {
            this.widthPixels = width;
            this.heightPixels = height;

            // Create bitmap and pixel array
            cloudBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            pixels = new int[width * height];
        }
    }

    /**
     * Generate cloud texture using 3D noise
     * Returns a bitmap with clouds rendered over transparent background
     *
     * @param xOffset X-offset for horizontal drift
     * @param yOffset Y-offset for vertical drift
     * @param zOffset Z-position for evolution over time
     */
    public Bitmap generateClouds(final float xOffset, final float yOffset, final float zOffset) {
        if (cloudBitmap == null) {
            Log.w("CloudPaper", "generateClouds: cloudBitmap is null!");
            return null;
        }

        final int blockSize = animationSettings.pixelSize;

        for (int y = 0; y < heightPixels; y += blockSize) {
            for (int x = 0; x < widthPixels; x += blockSize) {
                // Get 3D noise value with drift offsets applied
                // x,y is screen coordinates (with drift), z is time
                float noiseValue = noise.GetNoise(x - xOffset, y - yOffset, zOffset);

                // FIXME: This normalization might not be necessary if I set my threshold differently
                // Normalize noise from [-1, 1] to [0, 1]
                noiseValue = (noiseValue + 1.0f) / 2.0f;

                final float threshold = animationSettings.cloudDensityThreshold;

                final int pixelValue;
                // Apply threshold to create sparse clouds
                if (noiseValue < threshold) {
                    // No cloud - fully transparent
                    pixelValue = Color.TRANSPARENT;
                } else {
                    // Cloud present - map noise to opacity
                    float cloudIntensity = (noiseValue - threshold) / (1.0f - threshold);

                    // Cloud opacity (0 to 255)
                    int alpha = (int) (cloudIntensity * 255);

                    // White clouds with calculated opacity
                    pixelValue = Color.argb(alpha, 255, 255, 255);
                }

                // We got the pixelValue, now fill it in for the whole (blockSize x blockSize) chunk
                for (int dY = 0; dY < blockSize; dY++) {
                    final int yPix = y + dY;
                    if (yPix < heightPixels) {
                        for (int dX = 0; dX < blockSize; dX++) {
                            final int xPix = x + dX;
                            if (xPix < widthPixels) {
                                final int index = yPix * widthPixels + xPix;
                                pixels[index] = pixelValue;
                            }
                        }
                    }
                }

            }
        }

        // Write pixels to bitmap
        cloudBitmap.setPixels(pixels, 0, widthPixels, 0, 0, widthPixels, heightPixels);

        return cloudBitmap;
    }

}
