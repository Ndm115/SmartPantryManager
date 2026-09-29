package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddEditIngredientActivity extends AppCompatActivity {

    private TextView txtFormTitle;

    private TextInputLayout layoutIngredientName;
    private TextInputLayout layoutQuantity;

    private TextInputEditText editIngredientName;
    private TextInputEditText editQuantity;
    private TextInputEditText editExpiryDate;

    private Spinner spinnerUnit;

    private Button btnSaveIngredient;
    private Button btnCancel;

    private FirebaseFirestore db;

    private String itemId = null;
    private boolean isEditMode = false;

    private final String[] units = {
            "items",
            "g",
            "kg",
            "ml",
            "l",
            "tsp",
            "tbsp",
            "cups",
            "slices"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        db = FirebaseFirestore.getInstance();

        connectViews();
        setupUnitSpinner();
        checkForEditMode();
        setupExpiryDatePicker();
        setupButtons();
    }

    private void connectViews() {

        txtFormTitle = findViewById(R.id.txtFormTitle);

        layoutIngredientName = findViewById(R.id.layoutIngredientName);

        layoutQuantity = findViewById(R.id.layoutQuantity);

        editIngredientName = findViewById(R.id.editIngredientName);

        editQuantity = findViewById(R.id.editQuantity);

        editExpiryDate = findViewById(R.id.editExpiryDate);

        spinnerUnit = findViewById(R.id.spinnerUnit);

        btnSaveIngredient = findViewById(R.id.btnSaveIngredient);

        btnCancel = findViewById(R.id.btnCancel);
    }

    private void setupUnitSpinner() {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        units
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerUnit.setAdapter(adapter);
    }

    private void checkForEditMode() {

        if (getIntent().hasExtra("itemId")) {

            isEditMode = true;

            itemId = getIntent().getStringExtra("itemId");

            String name =
                    getIntent().getStringExtra("name");

            double quantity =
                    getIntent().getDoubleExtra("quantity", 0);

            String unit =
                    getIntent().getStringExtra("unit");

            String expiryDate =
                    getIntent().getStringExtra("expiryDate");

            txtFormTitle.setText("Edit Ingredient");
            btnSaveIngredient.setText("Update Ingredient");

            editIngredientName.setText(name);
            editQuantity.setText(formatQuantity(quantity));

            if (expiryDate != null) {
                editExpiryDate.setText(expiryDate);
            }

            selectUnit(unit);
        }
    }

    private void selectUnit(String unit) {

        if (unit == null) {
            return;
        }

        for (int i = 0; i < units.length; i++) {

            if (units[i].equalsIgnoreCase(unit)) {
                spinnerUnit.setSelection(i);
                return;
            }
        }
    }

    private void setupExpiryDatePicker() {

        editExpiryDate.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog =
                    new DatePickerDialog(AddEditIngredientActivity.this, (view, selectedYear, selectedMonth, selectedDay) -> {

                                String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);

                                editExpiryDate.setText(date);
                            },
                            year,
                            month,
                            day
                    );

            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

            datePickerDialog.show();
        });
    }

    private void setupButtons() {

        btnSaveIngredient.setOnClickListener(v -> validateAndSave());

        btnCancel.setOnClickListener(v -> finish());
    }

    private void validateAndSave() {

        layoutIngredientName.setError(null);
        layoutQuantity.setError(null);

        String name = "";

        if (editIngredientName.getText() != null) {
            name = editIngredientName.getText().toString().trim();
        }

        String quantityText = "";

        if (editQuantity.getText() != null) {
            quantityText = editQuantity.getText().toString().trim();
        }

        if (name.isEmpty()) {

            layoutIngredientName.setError("Ingredient name is required.");

            editIngredientName.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {

            layoutQuantity.setError("Quantity is required.");

            editQuantity.requestFocus();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {

            layoutQuantity.setError("Enter a valid quantity.");

            editQuantity.requestFocus();
            return;
        }

        if (quantity <= 0) {

            layoutQuantity.setError("Quantity must be greater than zero.");

            editQuantity.requestFocus();
            return;
        }

        String unit = spinnerUnit.getSelectedItem().toString();

        String expiryDate = "";

        if (editExpiryDate.getText() != null) {
            expiryDate = editExpiryDate.getText().toString().trim();
        }

        saveIngredient(name, quantity, unit, expiryDate);
    }

    private void saveIngredient(String name, double quantity, String unit, String expiryDate) {

        btnSaveIngredient.setEnabled(false);

        Map<String, Object> ingredient = new HashMap<>();

        ingredient.put("name", name);
        ingredient.put("quantity", quantity);
        ingredient.put("unit", unit);
        ingredient.put("expiryDate", expiryDate);

        if (isEditMode && itemId != null) {

            db.collection("pantryItems").document(itemId).update(ingredient).addOnSuccessListener(unused -> {

                        Toast.makeText(AddEditIngredientActivity.this, "Ingredient updated successfully.", Toast.LENGTH_SHORT).show();

                        finish();
                    }).addOnFailureListener(e -> {

                        btnSaveIngredient.setEnabled(true);

                        Toast.makeText(AddEditIngredientActivity.this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });

        } else {

            db.collection("pantryItems").add(ingredient).addOnSuccessListener(documentReference -> {

                        Toast.makeText(AddEditIngredientActivity.this, "Ingredient added successfully.", Toast.LENGTH_SHORT).show();

                        finish();
                    }).addOnFailureListener(e -> {

                        btnSaveIngredient.setEnabled(true);

                        Toast.makeText(AddEditIngredientActivity.this, "Save failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }

    private String formatQuantity(double quantity) {

        if (quantity == (long) quantity) {
            return String.valueOf((long) quantity);
        }

        return String.valueOf(quantity);
    }
}