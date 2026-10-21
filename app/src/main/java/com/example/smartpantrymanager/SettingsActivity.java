package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private SwitchMaterial switchShowExpiry;
    private Spinner spinnerSortPantry;
    private Spinner spinnerExpiryWarning;
    private Button btnBackFromSettings;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchShowExpiry = findViewById(R.id.switchShowExpiry);
        spinnerSortPantry = findViewById(R.id.spinnerSortPantry);
        spinnerExpiryWarning = findViewById(R.id.spinnerExpiryWarning);
        btnBackFromSettings = findViewById(R.id.btnBackFromSettings);

        preferences = getSharedPreferences("SmartPantryPreferences", MODE_PRIVATE);

        // SHOW EXPIRY DATES
        boolean showExpiryDates = preferences.getBoolean("showExpiryDates", true);
        switchShowExpiry.setChecked(showExpiryDates);

        switchShowExpiry.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("showExpiryDates", isChecked).apply();
        });

        // PANTRY SORTING
        String[] sortOptions = {
                "Name (A to Z)",
                "Name (Z to A)",
                "Expiry Date (Soonest First)"
        };

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                sortOptions
        );

        sortAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerSortPantry.setAdapter(sortAdapter);
        spinnerSortPantry.setSelection(preferences.getInt("sortPantry", 0));

        spinnerSortPantry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view,
                                       int position, long id) {
                preferences.edit().putInt("sortPantry", position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // EXPIRY WARNING PERIOD
        String[] warningOptions = {
                "1 Day",
                "3 Days",
                "5 Days",
                "7 Days"
        };

        ArrayAdapter<String> warningAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                warningOptions
        );

        warningAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerExpiryWarning.setAdapter(warningAdapter);
        spinnerExpiryWarning.setSelection(preferences.getInt("expiryWarning", 1));

        spinnerExpiryWarning.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view,
                                       int position, long id) {
                preferences.edit().putInt("expiryWarning", position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // BACK BUTTON
        btnBackFromSettings.setOnClickListener(v -> finish());
    }
}
