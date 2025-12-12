package com.mcherm.cloudpaper;

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
    private EditText pixelSizeEdit;
    private EditText fpsEdit;
    private EditText skyColorEdit;
    private View skyColorPreview;
    private EditText evolutionRateEdit;
    private EditText noiseFrequencyEdit;
    private SeekBar cloudDensitySeekBar;
    private TextView cloudDensityValue;
    private EditText driftXEdit;
    private EditText driftYEdit;
    private Button saveButton;
    private Button creditsButton;

    // Reset buttons
    private Button pixelSizeResetButton;
    private Button fpsResetButton;
    private Button skyColorResetButton;
    private Button evolutionRateResetButton;
    private Button noiseFrequencyResetButton;
    private Button cloudDensityResetButton;
    private Button driftXResetButton;
    private Button driftYResetButton;

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
        pixelSizeEdit = findViewById(R.id.pixelSizeEdit);
        fpsEdit = findViewById(R.id.fpsEdit);
        skyColorEdit = findViewById(R.id.skyColorEdit);
        skyColorPreview = findViewById(R.id.skyColorPreview);
        evolutionRateEdit = findViewById(R.id.evolutionRateEdit);
        noiseFrequencyEdit = findViewById(R.id.noiseFrequencyEdit);
        cloudDensitySeekBar = findViewById(R.id.cloudDensitySeekBar);
        cloudDensityValue = findViewById(R.id.cloudDensityValue);
        driftXEdit = findViewById(R.id.driftXEdit);
        driftYEdit = findViewById(R.id.driftYEdit);
        saveButton = findViewById(R.id.saveButton);
        creditsButton = findViewById(R.id.creditsButton);

        // Initialize reset buttons
        pixelSizeResetButton = findViewById(R.id.pixelSizeResetButton);
        fpsResetButton = findViewById(R.id.fpsResetButton);
        skyColorResetButton = findViewById(R.id.skyColorResetButton);
        evolutionRateResetButton = findViewById(R.id.evolutionRateResetButton);
        noiseFrequencyResetButton = findViewById(R.id.noiseFrequencyResetButton);
        cloudDensityResetButton = findViewById(R.id.cloudDensityResetButton);
        driftXResetButton = findViewById(R.id.driftXResetButton);
        driftYResetButton = findViewById(R.id.driftYResetButton);
    }

    private void loadSettings() {
        AnimationSettings settings = settingsManager.loadSettings();

        // Pixel Size
        pixelSizeEdit.setText(String.valueOf(settings.pixelSize));

        // FPS
        fpsEdit.setText(String.valueOf(settings.framesPerSecond));

        // Sky Color
        skyColorEdit.setText(settings.skyColor);
        updateColorPreview(settings.skyColor);

        // Evolution Rate
        evolutionRateEdit.setText(String.format("%.4f", settings.evolutionRate));

        // Noise Frequency
        noiseFrequencyEdit.setText(String.format("%.4f", settings.noiseFrequency));

        // Cloud Density: 0.0-1.0 (mapped to 0-100)
        cloudDensitySeekBar.setProgress(Math.round(settings.cloudDensityThreshold * 100));
        cloudDensityValue.setText(String.format("%.2f", settings.cloudDensityThreshold));

        // Drift X
        driftXEdit.setText(String.format("%.4f", settings.driftX));

        // Drift Y
        driftYEdit.setText(String.format("%.4f", settings.driftY));
    }

    private void setupListeners() {
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

        // Individual reset buttons
        pixelSizeResetButton.setOnClickListener(v -> {
            pixelSizeEdit.setText(String.valueOf(AnimationSettings.DEFAULT_PIXEL_SIZE));
        });

        fpsResetButton.setOnClickListener(v -> {
            fpsEdit.setText(String.valueOf(AnimationSettings.DEFAULT_FRAMES_PER_SECOND));
        });

        skyColorResetButton.setOnClickListener(v -> {
            skyColorEdit.setText(AnimationSettings.DEFAULT_SKY_COLOR);
            updateColorPreview(AnimationSettings.DEFAULT_SKY_COLOR);
        });

        evolutionRateResetButton.setOnClickListener(v -> {
            evolutionRateEdit.setText(String.format("%.4f", AnimationSettings.DEFAULT_EVOLUTION_RATE));
        });

        noiseFrequencyResetButton.setOnClickListener(v -> {
            noiseFrequencyEdit.setText(String.format("%.4f", AnimationSettings.DEFAULT_NOISE_FREQUENCY));
        });

        cloudDensityResetButton.setOnClickListener(v -> {
            int progress = Math.round(AnimationSettings.DEFAULT_CLOUD_DENSITY_THRESHOLD * 100);
            cloudDensitySeekBar.setProgress(progress);
            cloudDensityValue.setText(String.format("%.2f", AnimationSettings.DEFAULT_CLOUD_DENSITY_THRESHOLD));
        });

        driftXResetButton.setOnClickListener(v -> {
            driftXEdit.setText(String.format("%.4f", AnimationSettings.DEFAULT_DRIFT_X));
        });

        driftYResetButton.setOnClickListener(v -> {
            driftYEdit.setText(String.format("%.4f", AnimationSettings.DEFAULT_DRIFT_Y));
        });

        // Save Button
        saveButton.setOnClickListener(v -> saveSettings());

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
            int pixelSize = Integer.parseInt(pixelSizeEdit.getText().toString().trim());
            int fps = Integer.parseInt(fpsEdit.getText().toString().trim());
            String skyColor = skyColorEdit.getText().toString().trim();
            float evolutionRate = Float.parseFloat(evolutionRateEdit.getText().toString().trim());
            float noiseFrequency = Float.parseFloat(noiseFrequencyEdit.getText().toString().trim());
            float cloudDensity = cloudDensitySeekBar.getProgress() / 100.0f;
            float driftX = Float.parseFloat(driftXEdit.getText().toString().trim());
            float driftY = Float.parseFloat(driftYEdit.getText().toString().trim());

            // Validate ranges
            if (pixelSize < 1 || pixelSize > 10) {
                Toast.makeText(this, "Pixel Size must be between 1 and 10", Toast.LENGTH_LONG).show();
                return;
            }
            if (fps < 1 || fps > 30) {
                Toast.makeText(this, "FPS must be between 1 and 30", Toast.LENGTH_LONG).show();
                return;
            }

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
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format. Please check all fields.", Toast.LENGTH_LONG).show();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Invalid color format. Use hex format like #55B4E1", Toast.LENGTH_LONG).show();
        }
    }

    private void openCredits() {
        Intent intent = new Intent(this, CreditsActivity.class);
        startActivity(intent);
    }
}
