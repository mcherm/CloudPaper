package com.example.cloudpaper;

/**
 * This is a data class which contains all the configurable parameters for the animation.
 * Instances are immutable.
 */
public class AnimationSettings {

    /** Blocks of pixelSize x pixelSize will be drawn the same. Range 1 and up */
    public final int pixelSize = 4;

    /** Number of frames per second controls how often we re-draw the screen. */
    public final int framesPerSecond = 4;

    /** Controls the base color of the sky. */
    public final String skyColor = "#55B4E1";  // Light blue background

    /** Affects the pace at which the clouds evolve over time. */
    public final float evolutionRate = 0.005f;

    /** Affects the horizontal and vertical spacing of the clouds. */
    public final float noiseFrequency = 0.005f;

    /** Controls cloud density. Higher values = fewer, sparser clouds. Range: 0.0 to 1.0 */
    public final float cloudDensityThreshold = 0.50f;

    /** Horizontal drift rate (positive = drift east/right). Default is slow drift eastward. */
    public final float driftX = 0.003f;

    /** Vertical drift rate (positive = drift south/down). Default is slow drift southward. */
    public final float driftY = 0.0005f;
}
