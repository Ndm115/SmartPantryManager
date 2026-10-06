package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private SwitchMaterial switchShowExpiry;
    private Button btnBackFromSettings;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchShowExpiry = findViewById(R.id.switchShowExpiry);
        btnBackFromSettings = findViewById(R.id.btnBackFromSettings);

        preferences = getSharedPreferences("SmartPantryPreferences", MODE_PRIVATE);

        boolean showExpiryDates = preferences.getBoolean("showExpiryDates", true);

        switchShowExpiry.setChecked(showExpiryDates);

        switchShowExpiry.setOnCheckedChangeListener((buttonView, isChecked) -> {

                    preferences.edit().putBoolean("showExpiryDates", isChecked).apply();
                }
        );

        btnBackFromSettings.setOnClickListener(v -> finish());
    }
}