package com.example.smartpantrymanager;

import java.util.ArrayList;
import java.util.List;

public class Recipe {

    private String id;
    private String name;
    private String description;
    private List<RecipeIngredient> ingredients;
    private List<String> steps;

    // Required empty constructor for Firebase Firestore
    public Recipe() {
        ingredients = new ArrayList<>();
        steps = new ArrayList<>();
    }

    public Recipe(String id, String name, String description, List<RecipeIngredient> ingredients, List<String> steps) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }
}