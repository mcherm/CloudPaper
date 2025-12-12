package com.example.cloudpaper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Settings activity for configuring CloudPaper wallpaper parameters.
 */
public class SettingsActivity extends Activity {

    private SettingsManager settingsManager;

    // UI elements
    private SeekBar pixelSizeSeekBar;
    private TextView pixelSizeValue;
    private SeekBar fpsSeekBar;
    private TextView fpsValue;
    private EditText skyColorEdit;
    private View skyColorPreview;
    private SeekBar evolutionRateSeekBar;
    private TextView evolutionRateValue;
    private SeekBar noiseFrequencySeekBar;
    private TextView noiseFrequencyValue;
    private SeekBar cloudDensitySeekBar;
    private TextView cloudDensityValue;
    private SeekBar driftXSeekBar;
    private TextView driftXValue;
    private SeekBar driftYSeekBar;
    private TextView driftYValue;
    private Button saveButton;
    private Button resetButton;
    private Button creditsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsManager = new SettingsManager(this);

        // Initialize UI elements
        initializeViews();

        // Load and display current settings
        loadSettings();

        // Set up listeners
        setupListeners();
    }

    private void initializeViews() {
        pixelSizeSeekBar = findViewById(R.id.pixelSizeSeekBar);
        pixelSizeValue = findViewById(R.id.pixelSizeValue);
        fpsSeekBar = findViewById(R.id.fpsSeekBar);
        fpsValue = findViewById(R.id.fpsValue);
        skyColorEdit = findViewById(R.id.skyColorEdit);
        skyColorPreview = findViewById(R.id.skyColorPreview);
        evolutionRateSeekBar = findViewById(R.id.evolutionRateSeekBar);
        evolutionRateValue = findViewById(R.id.evolutionRateValue);
        noiseFrequencySeekBar = findViewById(R.id.noiseFrequencySeekBar);
        noiseFrequencyValue = findViewById(R.id.noiseFrequencyValue);
        cloudDensitySeekBar = findViewById(R.id.cloudDensitySeekBar);
        cloudDensityValue = findViewById(R.id.cloudDensityValue);
        driftXSeekBar = findViewById(R.id.driftXSeekBar);
        driftXValue = findViewById(R.id.driftXValue);
        driftYSeekBar = findViewById(R.id.driftYSeekBar);
        driftYValue = findViewById(R.id.driftYValue);
        saveButton = findViewById(R.id.saveButton);
        resetButton = findViewById(R.id.resetButton);
        creditsButton = findViewById(R.id.creditsButton);
    }

    private void loadSettings() {
        AnimationSettings settings = settingsManager.loadSettings();

        // Pixel Size: 1-10
        pixelSizeSeekBar.setProgress(settings.pixelSize - 1);
        pixelSizeValue.setText(String.valueOf(settings.pixelSize));

        // FPS: 1-30
        fpsSeekBar.setProgress(settings.framesPerSecond - 1);
        fpsValue.setText(String.valueOf(settings.framesPerSecond));

        // Sky Color
        skyColorEdit.setText(settings.skyColor);
        updateColorPreview(settings.skyColor);

        // Evolution Rate: 0.001-0.020 (mapped to 0-190)
        int evolutionProgress = Math.round((settings.evolutionRate - 0.001f) / 0.0001f);
        evolutionRateSeekBar.setProgress(evolutionProgress);
        evolutionRateValue.setText(String.format("%.4f", settings.evolutionRate));

        // Noise Frequency: 0.001-0.020 (mapped to 0-190)
        int noiseProgress = Math.round((settings.noiseFrequency - 0.001f) / 0.0001f);
        noiseFrequencySeekBar.setProgress(noiseProgress);
        noiseFrequencyValue.setText(String.format("%.4f", settings.noiseFrequency));

        // Cloud Density: 0.0-1.0 (mapped to 0-100)
        cloudDensitySeekBar.setProgress(Math.round(settings.cloudDensityThreshold * 100));
        cloudDensityValue.setText(String.format("%.2f", settings.cloudDensityThreshold));

        // Drift X: -0.010 to 0.010 (mapped to 0-200, center is 100)
        int driftXProgress = Math.round((settings.driftX + 0.010f) / 0.0001f);
        driftXSeekBar.setProgress(driftXProgress);
        driftXValue.setText(String.format("%.4f", settings.driftX));

        // Drift Y: -0.010 to 0.010 (mapped to 0-200, center is 100)
        int driftYProgress = Math.round((settings.driftY + 0.010f) / 0.0001f);
        driftYSeekBar.setProgress(driftYProgress);
        driftYValue.setText(String.format("%.4f", settings.driftY));
    }

    private void setupListeners() {
        // Pixel Size
        pixelSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pixelSizeValue.setText(String.valueOf(progress + 1));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // FPS
        fpsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                fpsValue.setText(String.valueOf(progress + 1));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Sky Color
        skyColorEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateColorPreview(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Evolution Rate
        evolutionRateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = 0.001f + (progress * 0.0001f);
                evolutionRateValue.setText(String.format("%.4f", value));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Noise Frequency
        noiseFrequencySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = 0.001f + (progress * 0.0001f);
                noiseFrequencyValue.setText(String.format("%.4f", value));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Cloud Density
        cloudDensitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = progress / 100.0f;
                cloudDensityValue.setText(String.format("%.2f", value));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Drift X
        driftXSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = -0.010f + (progress * 0.0001f);
                driftXValue.setText(String.format("%.4f", value));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Drift Y
        driftYSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = -0.010f + (progress * 0.0001f);
                driftYValue.setText(String.format("%.4f", value));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Save Button
        saveButton.setOnClickListener(v -> saveSettings());

        // Reset Button
        resetButton.setOnClickListener(v -> resetToDefaults());

        // Credits Button
        creditsButton.setOnClickListener(v -> openCredits());
    }

    private void updateColorPreview(String colorString) {
        try {
            int color = Color.parseColor(colorString);
            skyColorPreview.setBackgroundColor(color);
        } catch (IllegalArgumentException e) {
            // Invalid color format, keep previous color
        }
    }

    private void saveSettings() {
        try {
            // Read values from UI
            int pixelSize = pixelSizeSeekBar.getProgress() + 1;
            int fps = fpsSeekBar.getProgress() + 1;
            String skyColor = skyColorEdit.getText().toString();
            float evolutionRate = 0.001f + (evolutionRateSeekBar.getProgress() * 0.0001f);
            float noiseFrequency = 0.001f + (noiseFrequencySeekBar.getProgress() * 0.0001f);
            float cloudDensity = cloudDensitySeekBar.getProgress() / 100.0f;
            float driftX = -0.010f + (driftXSeekBar.getProgress() * 0.0001f);
            float driftY = -0.010f + (driftYSeekBar.getProgress() * 0.0001f);

            // Validate color
            Color.parseColor(skyColor);

            // Create new settings and save
            AnimationSettings newSettings = new AnimationSettings(
                pixelSize, fps, skyColor, evolutionRate, noiseFrequency,
                cloudDensity, driftX, driftY
            );
            settingsManager.saveSettings(newSettings);

            Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Invalid color format. Use hex format like #55B4E1", Toast.LENGTH_LONG).show();
        }
    }

    private void resetToDefaults() {
        settingsManager.resetToDefaults();
        loadSettings();
        Toast.makeText(this, "Settings reset to defaults", Toast.LENGTH_SHORT).show();
    }

    private void openCredits() {
        Intent intent = new Intent(this, CreditsActivity.class);
        startActivity(intent);
    }
}
