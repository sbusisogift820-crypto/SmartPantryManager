package com.example.smartpantrymanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipeMatcher {

    public static List<Recipe> getStrictlySuggestedRecipes(DatabaseHelper dbHelper) {
        List<Recipe> suggestions = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor recipeCursor = db.rawQuery("SELECT * FROM recipes", null);

        if (recipeCursor.moveToFirst()) {
            do {
                int recipeId = recipeCursor.getInt(recipeCursor.getColumnIndexOrThrow("_id"));
                String title = recipeCursor.getString(recipeCursor.getColumnIndexOrThrow("name"));
                String instructions = recipeCursor.getString(recipeCursor.getColumnIndexOrThrow("instructions"));
                String category = "General";

                Cursor ingCursor = db.rawQuery("SELECT * FROM recipe_ingredients WHERE recipe_id = ?",
                        new String[]{String.valueOf(recipeId)});

                int totalIngredients = 0;
                int matchedIngredients = 0;
                // Inside the recipe loop in RecipeMatcher.java
                List<String> missingList = new ArrayList<>();

                if (ingCursor.moveToFirst()) {
                    do {
                        totalIngredients++;
                        String reqName = ingCursor.getString(ingCursor.getColumnIndexOrThrow("ingredient_name"));
                        double reqQty = ingCursor.getDouble(ingCursor.getColumnIndexOrThrow("required_quantity"));

                        if (isIngredientAvailable(db, reqName, reqQty)) {
                            matchedIngredients++;
                        } else {
                            missingList.add(reqName); // Track missing ingredients
                        }
                    } while (ingCursor.moveToNext());
                }
                ingCursor.close();

                if (totalIngredients > 0) {
                    double matchPercentage = ((double) matchedIngredients / totalIngredients) * 100.0;
                    if (matchPercentage > 0) {
                        Recipe recipe = new Recipe(recipeId, title, category, instructions, missingList);
                        recipe.setMatchPercentage(matchPercentage);
                        suggestions.add(recipe);
                    }
                }

            } while (recipeCursor.moveToNext());
        }
        recipeCursor.close();

        // Sort highest match percentage first
        Collections.sort(suggestions, (r1, r2) -> Double.compare(r2.getMatchPercentage(), r1.getMatchPercentage()));

        return suggestions;
    }

    private static boolean isIngredientAvailable(SQLiteDatabase db, String reqName, double reqQty) {
        String normalizedReq = normalizeName(reqName);

        Cursor pantryCursor = db.rawQuery("SELECT * FROM pantry", null);
        boolean found = false;

        if (pantryCursor.moveToFirst()) {
            do {
                String pantryIngName = pantryCursor.getString(pantryCursor.getColumnIndexOrThrow("name"));
                String normalizedPantry = normalizeName(pantryIngName);

                // Flexible string matching (e.g., "Tomato" matches "Tomatoes" or "Tomato Sauce")
                if (normalizedPantry.contains(normalizedReq) || normalizedReq.contains(normalizedPantry)) {
                    found = true;
                    break;
                }
            } while (pantryCursor.moveToNext());
        }
        pantryCursor.close();

        return found;
    }

    private static String normalizeName(String name) {
        if (name == null) return "";
        String clean = name.trim().toLowerCase();
        if (clean.endsWith("es")) return clean.substring(0, clean.length() - 2);
        if (clean.endsWith("s") && !clean.endsWith("ss")) return clean.substring(0, clean.length() - 1);
        return clean;
    }
}