package com.example.cloudpaper;

/**
 * This is a data class which contains all the configurable parameters for the animation.
 * Instances are immutable.
 */
public class AnimationSettings {

    /** Number of frames per second controls how often we re-draw the screen. */
    public final int framesPerSecond = 8;

    /** Controls the base color of the sky. */
    public final String skyColor = "#55B4E1";  // Light blue background

    /** Affects the pace at which the clouds evolve over time. */
    public final float evolutionRate = 0.0025f;

    /** Affects the horizontal and vertical spacing of the clouds. */
    public final float noiseFrequency = 0.005f;

    /** Controls cloud density. Higher values = fewer, sparser clouds. Range: 0.0 to 1.0 */
    public final float cloudDensityThreshold = 0.50f;
}
