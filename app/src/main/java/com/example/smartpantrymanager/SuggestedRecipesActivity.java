
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity
        implements RecipeAdapter.OnRecipeClickListener {

    private RecyclerView recyclerRecipes;
    private RecyclerView recyclerAlmostThere;

    private TextView txtRecipeCount;
    private TextView txtNoRecipes;
    private TextView txtAlmostThereTitle;
    private TextView txtAlmostThereCount;

    private Button btnBackToPantry;

    private RecipeAdapter recipeAdapter;
    private RecipeAdapter almostThereAdapter;

    private final List<Recipe> matchingRecipes = new ArrayList<>();
    private final List<Recipe> almostThereRecipes = new ArrayList<>();
    private final List<PantryItem> pantryItems = new ArrayList<>();

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        db = FirebaseFirestore.getInstance();

        recyclerRecipes = findViewById(R.id.recyclerRecipes);
        recyclerAlmostThere = findViewById(R.id.recyclerAlmostThere);

        txtRecipeCount = findViewById(R.id.txtRecipeCount);
        txtNoRecipes = findViewById(R.id.txtNoRecipes);
        txtAlmostThereTitle = findViewById(R.id.txtAlmostThereTitle);
        txtAlmostThereCount = findViewById(R.id.txtAlmostThereCount);

        btnBackToPantry = findViewById(R.id.btnBackToPantry);

        setupRecyclerView();

        btnBackToPantry.setOnClickListener(v -> finish());

        // Recipes are already stored in Firebase.
        // Load this user's pantry before checking recipe matches.
        loadPantry();
    }

    private void setupRecyclerView() {

        // RecyclerView for fully matching recipes.
        recyclerRecipes.setLayoutManager(new LinearLayoutManager(this));

        recipeAdapter = new RecipeAdapter(matchingRecipes, this);

        recyclerRecipes.setAdapter(recipeAdapter);

        // Separate RecyclerView for Almost There recipes.
        recyclerAlmostThere.setLayoutManager(new LinearLayoutManager(this));

        almostThereAdapter = new RecipeAdapter(almostThereRecipes, this);

        recyclerAlmostThere.setAdapter(almostThereAdapter);
    }

    private void loadPantry() {

        // Check that the user is signed in.
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {

            Toast.makeText(this,
                    "Please wait for Firebase sign-in.",
                    Toast.LENGTH_LONG).show();

            return;
        }

        // Get the current user's unique ID.
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Read ingredients from this user's private pantry.
        db.collection("users")
                .document(userId)
                .collection("pantryItems")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    pantryItems.clear();

                    for (QueryDocumentSnapshot document : querySnapshot) {

                        PantryItem item = document.toObject(PantryItem.class);

                        item.setId(document.getId());

                        pantryItems.add(item);
                    }

                    loadRecipes();

                })
                .addOnFailureListener(e -> Toast.makeText(
                        SuggestedRecipesActivity.this,
                        "Unable to load pantry: " + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show());
    }

    private void loadRecipes() {

        // Recipes are shared and read-only for app users.
        db.collection("recipes")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    matchingRecipes.clear();
                    almostThereRecipes.clear();

                    for (QueryDocumentSnapshot document : querySnapshot) {

                        Recipe recipe = document.toObject(Recipe.class);

                        recipe.setId(document.getId());

                        // Full match: every ingredient is available.
                        if (IngredientMatcher.recipeMatches(recipe, pantryItems)) {

                            matchingRecipes.add(recipe);

                            // Almost There: exactly one ingredient is missing.
                        } else if (IngredientMatcher.recipeAlmostMatches(recipe, pantryItems)) {

                            almostThereRecipes.add(recipe);
                        }
                    }

                    recipeAdapter.notifyDataSetChanged();
                    almostThereAdapter.notifyDataSetChanged();

                    updateScreen();
                })
                .addOnFailureListener(e -> Toast.makeText(
                        SuggestedRecipesActivity.this,
                        "Unable to load recipes: " + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show());
    }

    private void updateScreen() {

        int count = matchingRecipes.size();
        int almostCount = almostThereRecipes.size();

        // Update strict recipe count.
        if (count == 1) {
            txtRecipeCount.setText("1 recipe available");
        } else {
            txtRecipeCount.setText(count + " recipes available");
        }

        if (count == 0) {

            txtNoRecipes.setVisibility(View.VISIBLE);
            recyclerRecipes.setVisibility(View.GONE);

        } else {

            txtNoRecipes.setVisibility(View.GONE);
            recyclerRecipes.setVisibility(View.VISIBLE);
        }

        // Update Almost There section.
        if (almostCount == 0) {

            txtAlmostThereTitle.setVisibility(View.GONE);
            txtAlmostThereCount.setVisibility(View.GONE);
            recyclerAlmostThere.setVisibility(View.GONE);

        } else {

            txtAlmostThereTitle.setVisibility(View.VISIBLE);
            txtAlmostThereCount.setVisibility(View.VISIBLE);
            recyclerAlmostThere.setVisibility(View.VISIBLE);

            if (almostCount == 1) {
                txtAlmostThereCount.setText("1 recipe missing one ingredient");
            } else {
                txtAlmostThereCount.setText(almostCount + " recipes missing one ingredient");
            }
        }
    }

    @Override
    public void onRecipeClick(Recipe recipe) {

        Intent intent = new Intent(
                SuggestedRecipesActivity.this,
                RecipeDetailActivity.class
        );

        intent.putExtra("recipeId", recipe.getId());

        startActivity(intent);
    }
}
