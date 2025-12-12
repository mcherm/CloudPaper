package com.example.cloudpaper;

/**
 * This is a data class which contains all the configurable parameters for the animation.
 * Instances are immutable.
 */
public class AnimationSettings {

    // Default values
    public static final int DEFAULT_PIXEL_SIZE = 4;
    public static final int DEFAULT_FRAMES_PER_SECOND = 4;
    public static final String DEFAULT_SKY_COLOR = "#55B4E1";
    public static final float DEFAULT_EVOLUTION_RATE = 0.005f;
    public static final float DEFAULT_NOISE_FREQUENCY = 0.005f;
    public static final float DEFAULT_CLOUD_DENSITY_THRESHOLD = 0.50f;
    public static final float DEFAULT_DRIFT_X = 0.003f;
    public static final float DEFAULT_DRIFT_Y = 0.0005f;

    /** Blocks of pixelSize x pixelSize will be drawn the same. Range 1 and up */
    public final int pixelSize;

    /** Number of frames per second controls how often we re-draw the screen. */
    public final int framesPerSecond;

    /** Controls the base color of the sky. */
    public final String skyColor;

    /** Affects the pace at which the clouds evolve over time. */
    public final float evolutionRate;

    /** Affects the horizontal and vertical spacing of the clouds. */
    public final float noiseFrequency;

    /** Controls cloud density. Higher values = fewer, sparser clouds. Range: 0.0 to 1.0 */
    public final float cloudDensityThreshold;

    /** Horizontal drift rate (positive = drift east/right). Default is slow drift eastward. */
    public final float driftX;

    /** Vertical drift rate (positive = drift south/down). Default is slow drift southward. */
    public final float driftY;

    /**
     * Creates an AnimationSettings with custom values.
     */
    public AnimationSettings(int pixelSize, int framesPerSecond, String skyColor,
                           float evolutionRate, float noiseFrequency,
                           float cloudDensityThreshold, float driftX, float driftY) {
        this.pixelSize = pixelSize;
        this.framesPerSecond = framesPerSecond;
        this.skyColor = skyColor;
        this.evolutionRate = evolutionRate;
        this.noiseFrequency = noiseFrequency;
        this.cloudDensityThreshold = cloudDensityThreshold;
        this.driftX = driftX;
        this.driftY = driftY;
    }

    /**
     * Creates an AnimationSettings with default values.
     */
    public AnimationSettings() {
        this(DEFAULT_PIXEL_SIZE, DEFAULT_FRAMES_PER_SECOND, DEFAULT_SKY_COLOR,
             DEFAULT_EVOLUTION_RATE, DEFAULT_NOISE_FREQUENCY,
             DEFAULT_CLOUD_DENSITY_THRESHOLD, DEFAULT_DRIFT_X, DEFAULT_DRIFT_Y);
    }
}
