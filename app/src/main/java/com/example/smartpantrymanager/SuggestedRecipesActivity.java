package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity
        implements RecipeAdapter.OnRecipeClickListener {

    private RecyclerView recyclerRecipes;
    private TextView txtRecipeCount;
    private TextView txtNoRecipes;
    private Button btnBackToPantry;

    private RecipeAdapter recipeAdapter;

    private final List<Recipe> matchingRecipes = new ArrayList<>();
    private final List<PantryItem> pantryItems = new ArrayList<>();

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        db = FirebaseFirestore.getInstance();

        recyclerRecipes = findViewById(R.id.recyclerRecipes);
        txtRecipeCount = findViewById(R.id.txtRecipeCount);
        txtNoRecipes = findViewById(R.id.txtNoRecipes);
        btnBackToPantry = findViewById(R.id.btnBackToPantry);

        setupRecyclerView();

        btnBackToPantry.setOnClickListener(v -> finish());

        // Add the 18 predefined recipes to Firebase
        // only if the recipes collection is empty.
        RecipeSeeder.seedRecipesIfNeeded();

        loadPantry();
    }

    private void setupRecyclerView() {

        recyclerRecipes.setLayoutManager(new LinearLayoutManager(this));

        recipeAdapter = new RecipeAdapter(matchingRecipes, this);

        recyclerRecipes.setAdapter(recipeAdapter);
    }

    private void loadPantry() {

        db.collection("pantryItems").get().addOnSuccessListener(querySnapshot -> {

                    pantryItems.clear();

                    for (QueryDocumentSnapshot document : querySnapshot) {

                        PantryItem item = document.toObject(PantryItem.class);

                        item.setId(document.getId());

                        pantryItems.add(item);
                    }

                    loadRecipes();
                }).addOnFailureListener(e -> Toast.makeText(SuggestedRecipesActivity.this, "Unable to load pantry.", Toast.LENGTH_LONG).show());
    }

    private void loadRecipes() {

        db.collection("recipes").get().addOnSuccessListener(querySnapshot -> {

                    matchingRecipes.clear();

                    for (QueryDocumentSnapshot document : querySnapshot) {

                        Recipe recipe = document.toObject(Recipe.class);

                        recipe.setId(document.getId());

                        if (IngredientMatcher.recipeMatches(recipe, pantryItems)) {

                            matchingRecipes.add(recipe);
                        }
                    }

                    recipeAdapter.notifyDataSetChanged();
                    updateScreen();
                })
                .addOnFailureListener(e -> Toast.makeText(SuggestedRecipesActivity.this, "Unable to load recipes.", Toast.LENGTH_LONG).show()
                );
    }

    private void updateScreen() {

        int count = matchingRecipes.size();

        if (count == 1) {
            txtRecipeCount.setText("1 recipe available");
        } else {
            txtRecipeCount.setText(count + " recipes available"
            );
        }

        if (count == 0) {

            txtNoRecipes.setVisibility(View.VISIBLE);
            recyclerRecipes.setVisibility(View.GONE);

        } else {

            txtNoRecipes.setVisibility(View.GONE);
            recyclerRecipes.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRecipeClick(Recipe recipe) {

        Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);

        intent.putExtra("recipeId", recipe.getId());

        startActivity(intent);
    }
}