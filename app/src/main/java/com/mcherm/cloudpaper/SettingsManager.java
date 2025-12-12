package com.mcherm.cloudpaper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages loading and saving AnimationSettings to/from SharedPreferences.
 */
public class SettingsManager {
    private static final String PREFS_NAME = "CloudPaperSettings";

    private static final String KEY_PIXEL_SIZE = "pixelSize";
    private static final String KEY_FRAMES_PER_SECOND = "framesPerSecond";
    private static final String KEY_SKY_COLOR = "skyColor";
    private static final String KEY_EVOLUTION_RATE = "evolutionRate";
    private static final String KEY_NOISE_FREQUENCY = "noiseFrequency";
    private static final String KEY_CLOUD_DENSITY_THRESHOLD = "cloudDensityThreshold";
    private static final String KEY_DRIFT_X = "driftX";
    private static final String KEY_DRIFT_Y = "driftY";

    private final SharedPreferences prefs;

    public SettingsManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Loads AnimationSettings from SharedPreferences.
     * Returns a new AnimationSettings instance with saved values, or defaults if not set.
     */
    public AnimationSettings loadSettings() {
        int pixelSize = prefs.getInt(KEY_PIXEL_SIZE, AnimationSettings.DEFAULT_PIXEL_SIZE);
        int framesPerSecond = prefs.getInt(KEY_FRAMES_PER_SECOND, AnimationSettings.DEFAULT_FRAMES_PER_SECOND);
        String skyColor = prefs.getString(KEY_SKY_COLOR, AnimationSettings.DEFAULT_SKY_COLOR);
        float evolutionRate = prefs.getFloat(KEY_EVOLUTION_RATE, AnimationSettings.DEFAULT_EVOLUTION_RATE);
        float noiseFrequency = prefs.getFloat(KEY_NOISE_FREQUENCY, AnimationSettings.DEFAULT_NOISE_FREQUENCY);
        float cloudDensityThreshold = prefs.getFloat(KEY_CLOUD_DENSITY_THRESHOLD, AnimationSettings.DEFAULT_CLOUD_DENSITY_THRESHOLD);
        float driftX = prefs.getFloat(KEY_DRIFT_X, AnimationSettings.DEFAULT_DRIFT_X);
        float driftY = prefs.getFloat(KEY_DRIFT_Y, AnimationSettings.DEFAULT_DRIFT_Y);

        return new AnimationSettings(pixelSize, framesPerSecond, skyColor,
                                    evolutionRate, noiseFrequency, cloudDensityThreshold,
                                    driftX, driftY);
    }

    /**
     * Saves AnimationSettings to SharedPreferences.
     */
    public void saveSettings(AnimationSettings settings) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_PIXEL_SIZE, settings.pixelSize);
        editor.putInt(KEY_FRAMES_PER_SECOND, settings.framesPerSecond);
        editor.putString(KEY_SKY_COLOR, settings.skyColor);
        editor.putFloat(KEY_EVOLUTION_RATE, settings.evolutionRate);
        editor.putFloat(KEY_NOISE_FREQUENCY, settings.noiseFrequency);
        editor.putFloat(KEY_CLOUD_DENSITY_THRESHOLD, settings.cloudDensityThreshold);
        editor.putFloat(KEY_DRIFT_X, settings.driftX);
        editor.putFloat(KEY_DRIFT_Y, settings.driftY);
        editor.apply();
    }

    /**
     * Resets all settings to defaults.
     */
    public void resetToDefaults() {
        saveSettings(new AnimationSettings());
    }

    /**
     * Registers a listener to be notified when settings change.
     */
    public void registerListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Unregisters a previously registered listener.
     */
    public void unregisterListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
