package com.example.cloudpaper.renderer;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

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

    // Noise parameters
    private float noiseScale = 1.00f;  // Controls cloud size (smaller = bigger clouds)
    private float threshold = 0.50f;     // Cloud density threshold (higher = fewer clouds)
    private float zPosition = 0.0f;     // Z-axis position for 3D noise (for animation later)

    public CloudRenderer() {
        // Initialize FastNoiseLite with OpenSimplex2S and FBm
        noise = new FastNoiseLite();
        noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        noise.SetFractalType(FastNoiseLite.FractalType.FBm);
        noise.SetFractalOctaves(3);
        noise.SetFrequency(0.005f);
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
    public Bitmap generateClouds() {
        if (cloudBitmap == null) {
            Log.w("CloudPaper", "generateClouds: cloudBitmap is null!");
            return null;
        }

        Log.d("CloudPaper", "generateClouds: Starting generation for " + width + "x" + height);

        int index = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Get 3D noise value (x, y, z) where z is static for now
                float noiseValue = noise.GetNoise(
                    x * noiseScale,
                    y * noiseScale,
                    zPosition
                );


                // FIXME: This normalization might not be necessary if I set my threshold differently
                // Normalize noise from [-1, 1] to [0, 1]
                noiseValue = (noiseValue + 1.0f) / 2.0f;

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

    /**
     * Set the noise scale (controls cloud size)
     * Smaller values = larger clouds
     */
    public void setNoiseScale(float scale) {
        this.noiseScale = scale;
    }

    /**
     * Set the threshold (controls cloud density)
     * Higher values = fewer, sparser clouds
     * Range: 0.0 to 1.0
     */
    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    /**
     * Set the Z position for 3D noise
     * This will be used for animation in Phase 5
     */
    public void setZPosition(float z) {
        this.zPosition = z;
    }

    /**
     * Get the current cloud bitmap
     */
    public Bitmap getCloudBitmap() {
        return cloudBitmap;
    }
}
