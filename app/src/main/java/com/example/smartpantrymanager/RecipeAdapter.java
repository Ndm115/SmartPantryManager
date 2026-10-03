package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipeAdapter
        extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final List<Recipe> recipes;
    private final OnRecipeClickListener listener;

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    public RecipeAdapter(List<Recipe> recipes, OnRecipeClickListener listener) {

        this.recipes = recipes;
        this.listener = listener;
    }


    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {

        Recipe recipe = recipes.get(position);
        holder.txtRecipeName.setText(recipe.getName());
        holder.txtRecipeDescription.setText(recipe.getDescription());

        int ingredientCount = 0;

        if (recipe.getIngredients() != null) {
            ingredientCount = recipe.getIngredients().size();
        }

        if (ingredientCount == 1) {
            holder.txtIngredientCount.setText("1 ingredient");
        } else {
            holder.txtIngredientCount.setText(
                    ingredientCount + " ingredients"
            );
        }

        holder.btnViewRecipe.setOnClickListener(v -> listener.onRecipeClick(recipe));

        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class RecipeViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtRecipeName;
        TextView txtRecipeDescription;
        TextView txtIngredientCount;
        Button btnViewRecipe;

        public RecipeViewHolder(View itemView) {

            super(itemView);

            txtRecipeName = itemView.findViewById(R.id.txtRecipeName);

            txtRecipeDescription = itemView.findViewById(R.id.txtRecipeDescription);

            txtIngredientCount = itemView.findViewById(R.id.txtIngredientCount);

            btnViewRecipe = itemView.findViewById(R.id.btnViewRecipe);
        }
    }
}