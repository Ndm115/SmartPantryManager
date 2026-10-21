package com.example.smartpantrymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements PantryAdapter.OnPantryItemClickListener {

    private RecyclerView recyclerPantry;
    private TextView txtEmptyPantry;
    private Button btnAddIngredient;
    private Button btnSuggestedRecipes;
    private Button btnSettings;

    private PantryAdapter pantryAdapter;
    private final List<PantryItem> pantryItems = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListenerRegistration pantryListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        recyclerPantry = findViewById(R.id.recyclerPantry);
        txtEmptyPantry = findViewById(R.id.txtEmptyPantry);
        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        btnSuggestedRecipes = findViewById(R.id.btnSuggestedRecipes);
        btnSettings = findViewById(R.id.btnSettings);

        setupRecyclerView();
        setupButtons();

        // Wait for Firebase Authentication before loading pantry data.
        signInUser();
    }

    private void signInUser() {

        // Reuse the existing anonymous account when available.
        if (auth.getCurrentUser() != null) {
            listenForPantryItems();
            return;
        }

        // Disable database buttons until sign-in succeeds.
        btnAddIngredient.setEnabled(false);
        btnSuggestedRecipes.setEnabled(false);

        auth.signInAnonymously()
                .addOnSuccessListener(result -> {

                    btnAddIngredient.setEnabled(true);
                    btnSuggestedRecipes.setEnabled(true);

                    listenForPantryItems();
                })
                .addOnFailureListener(e -> Toast.makeText(
                        MainActivity.this,
                        "Firebase sign-in failed: " + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show());
    }

    // Get the pantry collection belonging to this user.
    private CollectionReference getPantryCollection() {

        String userId = auth.getCurrentUser().getUid();

        return db.collection("users")
                .document(userId)
                .collection("pantryItems");
    }

    private void setupRecyclerView() {

        recyclerPantry.setLayoutManager(new LinearLayoutManager(this));

        pantryAdapter = new PantryAdapter(pantryItems, this);

        recyclerPantry.setAdapter(pantryAdapter);
    }

    private void setupButtons() {

        btnAddIngredient.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditIngredientActivity.class);
            startActivity(intent);
        });

        btnSuggestedRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SuggestedRecipesActivity.class);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void listenForPantryItems() {

        pantryListener = getPantryCollection().addSnapshotListener((value, error) -> {

            if (error != null) {

                Toast.makeText(MainActivity.this,
                        "Unable to load pantry: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();

                return;
            }

            pantryItems.clear();

            if (value != null) {

                for (QueryDocumentSnapshot document : value) {

                    PantryItem item = document.toObject(PantryItem.class);

                    item.setId(document.getId());

                    pantryItems.add(item);
                }
            }

            // Apply the selected sorting preference.
            sortPantryItems();

            pantryAdapter.notifyDataSetChanged();
            updateEmptyState();
        });
    }

    // Sort pantry items based on the option selected in Settings.
    private void sortPantryItems() {

        SharedPreferences preferences = getSharedPreferences(
                "SmartPantryPreferences", MODE_PRIVATE
        );

        int sortOption = preferences.getInt("sortPantry", 0);

        if (sortOption == 0) {

            // Name A to Z.
            Collections.sort(pantryItems, (a, b) ->
                    a.getName().compareToIgnoreCase(b.getName())
            );

        } else if (sortOption == 1) {

            // Name Z to A.
            Collections.sort(pantryItems, (a, b) ->
                    b.getName().compareToIgnoreCase(a.getName())
            );

        } else if (sortOption == 2) {

            // Expiry date: soonest first.
            Collections.sort(pantryItems, (a, b) -> {

                String dateA = a.getExpiryDate();
                String dateB = b.getExpiryDate();

                if (dateA == null || dateA.isEmpty()) {
                    return (dateB == null || dateB.isEmpty()) ? 0 : 1;
                }

                if (dateB == null || dateB.isEmpty()) {
                    return -1;
                }

                // Dates use yyyy-MM-dd, so text comparison works.
                return dateA.compareTo(dateB);
            });
        }
    }

    private void updateEmptyState() {

        if (pantryItems.isEmpty()) {

            txtEmptyPantry.setVisibility(View.VISIBLE);
            recyclerPantry.setVisibility(View.GONE);

        } else {

            txtEmptyPantry.setVisibility(View.GONE);
            recyclerPantry.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh sorting and expiry display after returning from Settings.
        if (pantryAdapter != null) {
            sortPantryItems();
            pantryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onEditClick(PantryItem item) {

        Intent intent = new Intent(MainActivity.this, AddEditIngredientActivity.class);

        intent.putExtra("itemId", item.getId());
        intent.putExtra("name", item.getName());
        intent.putExtra("quantity", item.getQuantity());
        intent.putExtra("unit", item.getUnit());
        intent.putExtra("expiryDate", item.getExpiryDate());

        startActivity(intent);
    }

    @Override
    public void onDeleteClick(PantryItem item) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage("Are you sure you want to remove " + item.getName() + " from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> deletePantryItem(item))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deletePantryItem(PantryItem item) {

        if (item.getId() == null || item.getId().isEmpty()) {

            Toast.makeText(this, "Unable to delete ingredient.", Toast.LENGTH_SHORT).show();
            return;
        }

        getPantryCollection()
                .document(item.getId())
                .delete()
                .addOnSuccessListener(unused -> Toast.makeText(
                        MainActivity.this,
                        item.getName() + " deleted.",
                        Toast.LENGTH_SHORT
                ).show())
                .addOnFailureListener(e -> Toast.makeText(
                        MainActivity.this,
                        "Delete failed: " + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (pantryListener != null) {
            pantryListener.remove();
        }
    }
}
