package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SuggestedRecipeListAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int HEADER = 0;
    private static final int RECIPE = 1;

    private final List<Recipe> matchingRecipes;
    private final List<Recipe> almostThereRecipes;
    private final RecipeAdapter.OnRecipeClickListener listener;

    public SuggestedRecipeListAdapter(
            List<Recipe> matchingRecipes,
            List<Recipe> almostThereRecipes,
            RecipeAdapter.OnRecipeClickListener listener) {

        this.matchingRecipes = matchingRecipes;
        this.almostThereRecipes = almostThereRecipes;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        // Matching header + matching recipes
        // Almost There header + Almost There recipes (if any).
        return 1 + matchingRecipes.size()
                + (almostThereRecipes.isEmpty() ? 0 : 1 + almostThereRecipes.size());
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        }

        if (!almostThereRecipes.isEmpty()
                && position == matchingRecipes.size() + 1) {
            return HEADER;
        }

        return RECIPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == HEADER) {
            View view = inflater.inflate(
                    R.layout.item_recipe_section, parent, false);
            return new HeaderHolder(view);
        }

        View view = inflater.inflate(R.layout.item_recipe, parent, false);
        return new RecipeHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof HeaderHolder) {

            HeaderHolder header = (HeaderHolder) holder;

            if (position == 0) {
                int count = matchingRecipes.size();

                header.title.setText("Suggested Recipes");
                header.subtitle.setText(
                        "Recipes you can make with the ingredients currently in your pantry."
                );
                header.count.setText(
                        count == 1 ? "1 recipe available"
                                : count + " recipes available"
                );

            } else {
                int count = almostThereRecipes.size();

                header.title.setText("Almost There");
                header.subtitle.setText("Recipes missing just one ingredient");
                header.count.setText(
                        count == 1 ? "1 recipe missing one ingredient"
                                : count + " recipes missing one ingredient"
                );
            }

            return;
        }

        Recipe recipe;

        if (position <= matchingRecipes.size()) {
            recipe = matchingRecipes.get(position - 1);
        } else {
            recipe = almostThereRecipes.get(
                    position - matchingRecipes.size() - 2
            );
        }

        RecipeHolder recipeHolder = (RecipeHolder) holder;

        recipeHolder.name.setText(recipe.getName());
        recipeHolder.description.setText(recipe.getDescription());

        int count = recipe.getIngredients() == null
                ? 0 : recipe.getIngredients().size();

        recipeHolder.ingredientCount.setText(
                count == 1 ? "1 ingredient" : count + " ingredients"
        );

        recipeHolder.button.setOnClickListener(
                v -> listener.onRecipeClick(recipe)
        );

        recipeHolder.itemView.setOnClickListener(
                v -> listener.onRecipeClick(recipe)
        );
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {

        TextView title, subtitle, count;

        HeaderHolder(View view) {
            super(view);
            title = view.findViewById(R.id.txtSectionTitle);
            subtitle = view.findViewById(R.id.txtSectionSubtitle);
            count = view.findViewById(R.id.txtSectionCount);
        }
    }

    static class RecipeHolder extends RecyclerView.ViewHolder {

        TextView name, description, ingredientCount;
        Button button;

        RecipeHolder(View view) {
            super(view);
            name = view.findViewById(R.id.txtRecipeName);
            description = view.findViewById(R.id.txtRecipeDescription);
            ingredientCount = view.findViewById(R.id.txtIngredientCount);
            button = view.findViewById(R.id.btnViewRecipe);
        }
    }
}
