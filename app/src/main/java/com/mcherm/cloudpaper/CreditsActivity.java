package com.mcherm.cloudpaper;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

/**
 * Activity that displays copyright notices and credits.
 */
public class CreditsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        Button closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> finish());
    }
}
