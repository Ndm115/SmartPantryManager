package com.example.smartpantrymanager;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeSeeder {

    public static void seedRecipesIfNeeded() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("recipes").limit(1).get().addOnSuccessListener(querySnapshot -> {

            // Recipes already exist, so do not add them again.
            if (!querySnapshot.isEmpty()) {
                return;
            }

            List<Recipe> recipes = createRecipes();

            for (Recipe recipe : recipes) {

                db.collection("recipes").document(recipe.getId()).set(recipe);
            }
        });
    }

    private static List<Recipe> createRecipes() {

        List<Recipe> recipes = new ArrayList<>();

        // 1. Scrambled Eggs
        recipes.add(new Recipe("scrambled_eggs", "Scrambled Eggs", "Quick and simple scrambled eggs.",
                Arrays.asList(ingredient("Eggs", 2, "items"), ingredient("Milk", 50, "ml")
                ),
                Arrays.asList("Crack the eggs into a bowl.", "Add the milk and whisk together.", "Cook in a pan while stirring until the eggs are set.")
        ));

        // 2. Cheese Omelette
        recipes.add(new Recipe("cheese_omelette", "Cheese Omelette", "A simple omelette filled with cheese.",
                Arrays.asList(ingredient("Eggs", 2, "items"), ingredient("Milk", 30, "ml"), ingredient("Cheese", 50, "g")
                ),
                Arrays.asList("Whisk the eggs and milk together.", "Pour the mixture into a heated pan.", "Add the cheese.", "Fold the omelette and cook until ready.")
        ));

        // 3. Peanut Butter Toast
        recipes.add(new Recipe("peanut_butter_toast", "Peanut Butter Toast", "Simple toast with peanut butter.",
                Arrays.asList(ingredient("Bread", 2, "slices"), ingredient("Peanut Butter", 30, "g")
                ),
                Arrays.asList("Toast the bread.", "Spread peanut butter over the toast.", "Serve immediately.")
        ));

        // 4. Cheese Toast
        recipes.add(new Recipe("cheese_toast", "Cheese Toast", "Warm toast topped with melted cheese.",
                Arrays.asList(ingredient("Bread", 2, "slices"), ingredient("Cheese", 50, "g")
                ),
                Arrays.asList("Place the cheese on the bread.", "Toast or grill until the cheese has melted.", "Serve while warm.")
        ));

        // 5. Grilled Cheese Sandwich
        recipes.add(new Recipe("grilled_cheese", "Grilled Cheese Sandwich", "A toasted sandwich with melted cheese.",
                Arrays.asList(ingredient("Bread", 2, "slices"), ingredient("Cheese", 60, "g"), ingredient("Butter", 20, "g")
                ),
                Arrays.asList("Butter the bread.", "Place the cheese between the bread slices.", "Cook in a pan until golden on both sides.")
        ));

        // 6. Boiled Rice
        recipes.add(new Recipe("boiled_rice", "Boiled Rice", "Simple fluffy rice.",
                Arrays.asList(ingredient("Rice", 200, "g"), ingredient("Water", 400, "ml")
                ),
                Arrays.asList("Rinse the rice.", "Add the rice and water to a pot.", "Simmer until the water is absorbed and the rice is cooked."
                )
        ));

        // 7. Cereal with Milk
        recipes.add(new Recipe("cereal_milk", "Cereal with Milk", "A quick bowl of cereal and milk.",
                Arrays.asList(ingredient("Cereal", 50, "g"), ingredient("Milk", 200, "ml")
                ),
                Arrays.asList("Add the cereal to a bowl.", "Pour the milk over the cereal.", "Serve immediately."
                )
        ));

        // 8. Buttered Toast
        recipes.add(new Recipe("buttered_toast", "Buttered Toast", "Simple toast spread with butter.",
                Arrays.asList(ingredient("Bread", 2, "slices"), ingredient("Butter", 20, "g")
                ),
                Arrays.asList("Toast the bread.", "Spread butter over the warm toast.", "Serve immediately."
                )
        ));

        // 9. Mashed Potatoes
        recipes.add(new Recipe("mashed_potatoes", "Mashed Potatoes", "Creamy homemade mashed potatoes.",
                Arrays.asList(ingredient("Potatoes", 500, "g"), ingredient("Milk", 100, "ml"), ingredient("Butter", 30, "g")
                ),
                Arrays.asList("Peel and boil the potatoes until soft.", "Drain the potatoes.", "Add the milk and butter.", "Mash until smooth.")
        ));

        // 10. Fried Potatoes
        recipes.add(new Recipe("fried_potatoes", "Fried Potatoes", "Simple golden fried potatoes.",
                Arrays.asList(ingredient("Potatoes", 500, "g"), ingredient("Oil", 30, "ml")
                ),
                Arrays.asList("Cut the potatoes into small pieces.", "Heat the oil in a pan.", "Fry the potatoes until golden and cooked through.")
        ));

        // 11. Tomato Pasta
        recipes.add(new Recipe("tomato_pasta", "Tomato Pasta", "A basic pasta with tomato and onion.",
                Arrays.asList(ingredient("Pasta", 200, "g"), ingredient("Tomato", 2, "items"), ingredient("Onion", 1, "items"), ingredient("Oil", 15, "ml")),
                Arrays.asList("Boil the pasta until cooked.", "Chop the tomato and onion.", "Cook the onion and tomato in the oil.", "Mix the sauce with the pasta.")
        ));

        // 12. Cheesy Pasta
        recipes.add(new Recipe("cheesy_pasta", "Cheesy Pasta", "Simple creamy cheese pasta.",
                Arrays.asList(ingredient("Pasta", 200, "g"), ingredient("Cheese", 80, "g"), ingredient("Milk", 100, "ml")
                ),
                Arrays.asList("Boil the pasta until cooked.", "Warm the milk in a pan.", "Add the cheese and stir until melted.", "Mix the cheese sauce with the pasta."
                )
        ));

        // 13. Pancakes
        recipes.add(new Recipe("pancakes", "Pancakes", "Easy homemade pancakes.",
                Arrays.asList(ingredient("Flour", 200, "g"), ingredient("Milk", 250, "ml"), ingredient("Eggs", 1, "items"), ingredient("Sugar", 20, "g")
                ),
                Arrays.asList("Mix the flour and sugar.", "Add the egg and milk.", "Whisk until the batter is smooth.", "Cook portions of batter in a heated pan."
                )
        ));

        // 14. French Toast
        recipes.add(new Recipe("french_toast", "French Toast", "Bread dipped in egg and milk, then pan cooked.",
                Arrays.asList(ingredient("Bread", 2, "slices"), ingredient("Eggs", 1, "items"), ingredient("Milk", 100, "ml")
                ),
                Arrays.asList("Whisk the egg and milk together.", "Dip the bread into the mixture.", "Cook the bread in a pan until golden on both sides.")
        ));

        // 15. Egg Sandwich
        recipes.add(new Recipe("egg_sandwich", "Egg Sandwich", "A quick sandwich filled with cooked egg.",
                Arrays.asList(ingredient("Bread", 2, "slices"), ingredient("Eggs", 2, "items")
                ),
                Arrays.asList("Cook the eggs.", "Place the cooked eggs between the bread slices.", "Serve immediately.")
        ));

        // 16. Peanut Butter Sandwich
        recipes.add(new Recipe("peanut_butter_sandwich", "Peanut Butter Sandwich", "A quick and simple peanut butter sandwich.",
                Arrays.asList(ingredient("Bread", 2, "slices"), ingredient("Peanut Butter", 30, "g")
                ),
                Arrays.asList("Place the bread slices on a plate.", "Spread peanut butter over one slice.", "Place the second slice on top and serve."
                )
        ));

        // 17. Hot Chocolate
        recipes.add(new Recipe("hot_chocolate", "Hot Chocolate", "A warm chocolate drink.",
                Arrays.asList(ingredient("Milk", 250, "ml"), ingredient("Cocoa", 20, "g"), ingredient("Sugar", 15, "g")
                ),
                Arrays.asList("Pour the milk into a pot.", "Add the cocoa and sugar.", "Heat while stirring until hot and well combined.")
        ));

        // 18. Cheese and Tomato Sandwich
        recipes.add(new Recipe("cheese_tomato_sandwich", "Cheese and Tomato Sandwich", "A simple sandwich with cheese and tomato.",
                Arrays.asList(ingredient("Bread", 2, "slices"), ingredient("Cheese", 50, "g"), ingredient("Tomato", 1, "items")
                ),
                Arrays.asList("Slice the tomato.", "Place the cheese and tomato between the bread slices.", "Serve as is or toast the sandwich.")
        ));

        return recipes;
    }

    private static RecipeIngredient ingredient(String name, double quantity, String unit) {

        return new RecipeIngredient(name, quantity, unit);
    }
}