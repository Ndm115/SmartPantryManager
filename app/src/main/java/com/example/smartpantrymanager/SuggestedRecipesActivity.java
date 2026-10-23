package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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

    private RecyclerView recyclerSuggestedRecipes;
    private Button btnBackToPantry;

    private SuggestedRecipeListAdapter adapter;

    private final List<Recipe> matchingRecipes = new ArrayList<>();
    private final List<Recipe> almostThereRecipes = new ArrayList<>();
    private final List<PantryItem> pantryItems = new ArrayList<>();

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        db = FirebaseFirestore.getInstance();

        recyclerSuggestedRecipes = findViewById(R.id.recyclerSuggestedRecipes);
        btnBackToPantry = findViewById(R.id.btnBackToPantry);

        recyclerSuggestedRecipes.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new SuggestedRecipeListAdapter(
                matchingRecipes,
                almostThereRecipes,
                this
        );

        recyclerSuggestedRecipes.setAdapter(adapter);

        btnBackToPantry.setOnClickListener(v -> finish());

        loadPantry();
    }

    private void loadPantry() {

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {

            Toast.makeText(this,
                    "Please wait for Firebase sign-in.",
                    Toast.LENGTH_LONG).show();

            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

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

        db.collection("recipes")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    matchingRecipes.clear();
                    almostThereRecipes.clear();

                    for (QueryDocumentSnapshot document : querySnapshot) {

                        Recipe recipe = document.toObject(Recipe.class);
                        recipe.setId(document.getId());

                        if (IngredientMatcher.recipeMatches(recipe, pantryItems)) {

                            matchingRecipes.add(recipe);

                        } else if (IngredientMatcher.recipeAlmostMatches(
                                recipe, pantryItems)) {

                            almostThereRecipes.add(recipe);
                        }
                    }

                    adapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> Toast.makeText(
                        SuggestedRecipesActivity.this,
                        "Unable to load recipes: " + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show());
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
