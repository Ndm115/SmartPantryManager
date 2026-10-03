package com.example.smartpantrymanager;

import java.util.List;

public class IngredientMatcher {

    public static boolean recipeMatches(
            Recipe recipe,
            List<PantryItem> pantryItems) {

        if (recipe.getIngredients() == null ||
                recipe.getIngredients().isEmpty()) {
            return false;
        }

        if (pantryItems == null || pantryItems.isEmpty()) {
            return false;
        }

        // Check every ingredient required by the recipe.
        for (RecipeIngredient required : recipe.getIngredients()) {

            double requiredQuantity = convertToBaseUnit(required.getQuantity(), required.getUnit());

            double totalAvailable = 0;

            // Add together all matching pantry entries.
            for (PantryItem pantryItem : pantryItems) {

                if (namesMatch(required.getName(), pantryItem.getName()) && unitsCompatible(required.getUnit(), pantryItem.getUnit())) {

                    totalAvailable += convertToBaseUnit(pantryItem.getQuantity(), pantryItem.getUnit());
                }
            }

            // Strict matching:
            // if even one required ingredient does not have
            // enough quantity, the recipe cannot be suggested.
            if (totalAvailable < requiredQuantity) {
                return false;
            }
        }

        // Every ingredient exists in enough quantity.
        return true;
    }

    private static boolean namesMatch(String firstName, String secondName) {

        String first = normalizeName(firstName);
        String second = normalizeName(secondName);

        return first.equals(second);
    }

    private static String normalizeName(String name) {

        if (name == null) {return "";}

        String normalized = name.toLowerCase().trim();

        // Handle simple singular/plural differences.
        // Examples:
        // Eggs -> Egg
        // Potatoes -> Potato
        // Berries -> Berry

        if (normalized.endsWith("ies")) {
            normalized = normalized.substring(0, normalized.length() - 3) + "y";

        } else if (normalized.endsWith("oes")) {
            normalized = normalized.substring(0, normalized.length() - 2);

        } else if (normalized.endsWith("s") && !normalized.endsWith("ss")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        return normalized;
    }

    private static boolean unitsCompatible(String firstUnit, String secondUnit) {

        String first = normalizeUnit(firstUnit);
        String second = normalizeUnit(secondUnit);

        // Same units are always compatible.
        if (first.equals(second)) {
            return true;
        }

        // Weight units can be converted.
        if (isWeightUnit(first) && isWeightUnit(second)) {
            return true;
        }

        // Volume units can be converted.
        if (isVolumeUnit(first) && isVolumeUnit(second)) {
            return true;
        }

        return false;
    }

    private static double convertToBaseUnit(
            double quantity,
            String unit) {

        String normalizedUnit = normalizeUnit(unit);

        switch (normalizedUnit) {

            // Weight is converted to grams.

            case "kg":
                return quantity * 1000;

            case "g":
                return quantity;


            // Volume is converted to millilitres.

            case "l":
                return quantity * 1000;

            case "ml":
                return quantity;

            case "tsp":
                return quantity * 5;

            case "tbsp":
                return quantity * 15;

            case "cups":
                return quantity * 250;


            // Items and slices do not need conversion.

            default:
                return quantity;
        }
    }

    private static String normalizeUnit(String unit) {

        if (unit == null) {
            return "";
        }

        String normalized = unit.toLowerCase().trim();

        if (normalized.equals("item") || normalized.equals("items")) {
            return "items";
        }

        if (normalized.equals("cup") || normalized.equals("cups")) {
            return "cups";
        }

        if (normalized.equals("slice") || normalized.equals("slices")) {
            return "slices";
        }

        return normalized;
    }

    private static boolean isWeightUnit(String unit) {

        return unit.equals("g") || unit.equals("kg");
    }

    private static boolean isVolumeUnit(String unit) {

        return unit.equals("ml") || unit.equals("l") || unit.equals("tsp") || unit.equals("tbsp") || unit.equals("cups");
    }
}