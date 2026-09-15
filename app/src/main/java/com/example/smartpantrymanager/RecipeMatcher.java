package com.example.smartpantrymanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class RecipeMatcher {

    public static List<Recipe> getStrictlySuggestedRecipes(DatabaseHelper dbHelper) {
        List<Recipe> matchingRecipes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor recipeCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_RECIPES, null);

        if (recipeCursor.moveToFirst()) {
            do {
                int recipeId = recipeCursor.getInt(recipeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String recipeName = recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_NAME));
                String instructions = recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_STEPS));

                Cursor ingCursor = db.rawQuery(
                        "SELECT * FROM " + DatabaseHelper.TABLE_RECIPE_ING + " WHERE " + DatabaseHelper.COLUMN_ING_RECIPE_ID + "=?",
                        new String[]{String.valueOf(recipeId)}
                );

                boolean canMakeRecipe = true;
                List<String> recipeIngredientList = new ArrayList<>();

                if (ingCursor.moveToFirst()) {
                    do {
                        String reqIngName = ingCursor.getString(ingCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ING_NAME));
                        double reqQty = ingCursor.getDouble(ingCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ING_QTY));
                        String reqUnit = ingCursor.getString(ingCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ING_UNIT));

                        recipeIngredientList.add(reqQty + " " + reqUnit + " " + reqIngName);

                        if (!isIngredientAvailable(db, reqIngName, reqQty)) {
                            canMakeRecipe = false;
                            break;
                        }
                    } while (ingCursor.moveToNext());
                }
                ingCursor.close();

                if (canMakeRecipe) {
                    matchingRecipes.add(new Recipe(recipeId, recipeName, "General", instructions, recipeIngredientList));
                }

            } while (recipeCursor.moveToNext());
        }
        recipeCursor.close();
        return matchingRecipes;
    }

    private static boolean isIngredientAvailable(SQLiteDatabase db, String reqName, double reqQty) {
        String normalizedReq = normalizeName(reqName);

        Cursor pantryCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PANTRY, null);
        boolean foundAndSufficient = false;

        if (pantryCursor.moveToFirst()) {
            do {
                String pantryIngName = pantryCursor.getString(pantryCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_NAME));
                double pantryQty = pantryCursor.getDouble(pantryCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_QTY));

                String normalizedPantry = normalizeName(pantryIngName);

                if (normalizedPantry.equalsIgnoreCase(normalizedReq)) {
                    if (pantryQty >= reqQty) {
                        foundAndSufficient = true;
                        break;
                    }
                }
            } while (pantryCursor.moveToNext());
        }
        pantryCursor.close();
        return foundAndSufficient;
    }

    private static String normalizeName(String name) {
        if (name == null) return "";
        String clean = name.trim().toLowerCase();
        if (clean.endsWith("es")) {
            return clean.substring(0, clean.length() - 2);
        } else if (clean.endsWith("s") && !clean.endsWith("ss")) {
            return clean.substring(0, clean.length() - 1);
        }
        return clean;
    }
}