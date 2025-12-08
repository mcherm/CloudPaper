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
    private int width;
    private int height;
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
        if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;

            // Create bitmap and pixel array
            cloudBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            pixels = new int[width * height];
        }
    }

    /**
     * Generate cloud texture using 3D noise
     * Returns a bitmap with clouds rendered over transparent background
     */
    public Bitmap generateClouds(final float z) {
        if (cloudBitmap == null) {
            Log.w("CloudPaper", "generateClouds: cloudBitmap is null!");
            return null;
        }

        Log.d("CloudPaper", "generateClouds: Starting generation for " + width + "x" + height);

        int index = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Get 3D noise value (x, y, z) where x,y is screen coordinates and z is time
                float noiseValue = noise.GetNoise(x,y,z);

                // FIXME: This normalization might not be necessary if I set my threshold differently
                // Normalize noise from [-1, 1] to [0, 1]
                noiseValue = (noiseValue + 1.0f) / 2.0f;

                final float threshold = animationSettings.cloudDensityThreshold;

                // Apply threshold to create sparse clouds
                if (noiseValue < threshold) {
                    // No cloud - fully transparent
                    pixels[index] = Color.TRANSPARENT;
                } else {
                    // Cloud present - map noise to opacity
                    float cloudIntensity = (noiseValue - threshold) / (1.0f - threshold);

                    // Cloud opacity (0 to 255)
                    int alpha = (int) (cloudIntensity * 255);

                    // White clouds with calculated opacity
                    pixels[index] = Color.argb(alpha, 255, 255, 255);
                }

                index++;
            }
        }

        // Write pixels to bitmap
        cloudBitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return cloudBitmap;
    }

}
