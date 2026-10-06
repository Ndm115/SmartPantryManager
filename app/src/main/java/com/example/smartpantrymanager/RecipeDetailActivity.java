package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView txtDetailRecipeName;
    private TextView txtDetailDescription;
    private LinearLayout layoutIngredients;
    private LinearLayout layoutSteps;
    private Button btnBackToRecipes;

    private FirebaseFirestore db;
    private String recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        db = FirebaseFirestore.getInstance();

        txtDetailRecipeName = findViewById(R.id.txtDetailRecipeName);

        txtDetailDescription = findViewById(R.id.txtDetailDescription);

        layoutIngredients = findViewById(R.id.layoutIngredients);

        layoutSteps = findViewById(R.id.layoutSteps);

        btnBackToRecipes = findViewById(R.id.btnBackToRecipes);

        btnBackToRecipes.setOnClickListener(v -> finish());

        recipeId = getIntent().getStringExtra("recipeId");

        if (recipeId == null || recipeId.isEmpty()) {

            Toast.makeText(this, "Recipe could not be loaded.", Toast.LENGTH_SHORT).show();

            finish();
            return;
        }

        loadRecipe();
    }

    private void loadRecipe() {

        db.collection("recipes").document(recipeId).get().addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {

                        Toast.makeText(RecipeDetailActivity.this, "Recipe not found.", Toast.LENGTH_SHORT).show();

                        finish();
                        return;
                    }

                    Recipe recipe = documentSnapshot.toObject(Recipe.class);

                    if (recipe == null) {

                        Toast.makeText(RecipeDetailActivity.this, "Recipe could not be loaded.", Toast.LENGTH_SHORT).show();

                        finish();
                        return;
                    }

                    displayRecipe(recipe);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(RecipeDetailActivity.this, "Unable to load recipe.", Toast.LENGTH_LONG).show()
                );
    }

    private void displayRecipe(Recipe recipe) {

        txtDetailRecipeName.setText(recipe.getName());
        txtDetailDescription.setText(recipe.getDescription());

        displayIngredients(recipe.getIngredients());
        displaySteps(recipe.getSteps());
    }

    private void displayIngredients(
            List<RecipeIngredient> ingredients) {

        layoutIngredients.removeAllViews();

        if (ingredients == null) {
            return;
        }

        for (RecipeIngredient ingredient : ingredients) {

            TextView ingredientView = new TextView(this);

            String text = "• " + formatQuantity(ingredient.getQuantity()) + " " + ingredient.getUnit() + " " + ingredient.getName();

            ingredientView.setText(text);
            ingredientView.setTextSize(16);
            ingredientView.setPadding(0, 8, 0, 8);

            layoutIngredients.addView(ingredientView);
        }
    }

    private void displaySteps(List<String> steps) {

        layoutSteps.removeAllViews();

        if (steps == null) {
            return;
        }

        for (int i = 0; i < steps.size(); i++) {

            TextView stepView = new TextView(this);

            String text = (i + 1) + ". " + steps.get(i);

            stepView.setText(text);
            stepView.setTextSize(16);
            stepView.setPadding(0, 8, 0, 12);

            layoutSteps.addView(stepView);
        }
    }

    private String formatQuantity(double quantity) {

        if (quantity == (long) quantity) {
            return String.valueOf((long) quantity);
        }

        return String.valueOf(quantity);
    }
}