package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
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
    private ListenerRegistration pantryListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        recyclerPantry = findViewById(R.id.recyclerPantry);
        txtEmptyPantry = findViewById(R.id.txtEmptyPantry);
        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        btnSuggestedRecipes = findViewById(R.id.btnSuggestedRecipes);
        btnSettings = findViewById(R.id.btnSettings);

        setupRecyclerView();
        setupButtons();
        listenForPantryItems();
    }

    private void setupRecyclerView() {

        recyclerPantry.setLayoutManager(new LinearLayoutManager(this));

        pantryAdapter = new PantryAdapter(pantryItems, this);

        recyclerPantry.setAdapter(pantryAdapter);
    }

    private void setupButtons() {

        btnAddIngredient.setOnClickListener(v -> {Intent intent = new Intent(MainActivity.this, AddEditIngredientActivity.class);
            startActivity(intent);
        });

        btnSuggestedRecipes.setOnClickListener(v -> {Intent intent = new Intent(MainActivity.this, SuggestedRecipesActivity.class);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void listenForPantryItems() {

        pantryListener = db.collection("pantryItems").addSnapshotListener((value, error) -> {

                    if (error != null) {

                        Toast.makeText(MainActivity.this, "Unable to load pantry: " + error.getMessage(), Toast.LENGTH_LONG).show();

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

                    pantryAdapter.notifyDataSetChanged();
                    updateEmptyState();
                });
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

        // Refresh pantry cards when returning from Settings.
        // This makes changes such as "Show expiry dates"
        // appear immediately.
        if (pantryAdapter != null) {
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

        new AlertDialog.Builder(this).setTitle("Delete Ingredient").setMessage("Are you sure you want to remove " + item.getName() + " from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> deletePantryItem(item)).setNegativeButton("Cancel", null).show();
    }

    private void deletePantryItem(PantryItem item) {

        if (item.getId() == null || item.getId().isEmpty()) {

            Toast.makeText(this, "Unable to delete ingredient.", Toast.LENGTH_SHORT).show();

            return;
        }

        db.collection("pantryItems")
                .document(item.getId())
                .delete()
                .addOnSuccessListener(unused ->

                        Toast.makeText(MainActivity.this, item.getName() + " deleted.", Toast.LENGTH_SHORT).show()).addOnFailureListener(e ->

                        Toast.makeText(MainActivity.this, "Delete failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (pantryListener != null) {
            pantryListener.remove();
        }
    }
}