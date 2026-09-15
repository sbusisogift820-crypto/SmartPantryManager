package com.example.smartpantrymanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvMatch, tvIngredients, tvInstructions;
    private MaterialButton btnCook;
    private DatabaseHelper dbHelper;
    private int recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        dbHelper = new DatabaseHelper(this);

        tvTitle = findViewById(R.id.tvDetailTitle);
        tvMatch = findViewById(R.id.tvDetailMatch);
        tvIngredients = findViewById(R.id.tvDetailIngredients);
        tvInstructions = findViewById(R.id.tvDetailInstructions);
        btnCook = findViewById(R.id.btnCookRecipe);

        // Get passed recipe ID or details from Intent
        recipeId = getIntent().getIntExtra("RECIPE_ID", -1);
        String title = getIntent().getStringExtra("RECIPE_TITLE");
        double match = getIntent().getDoubleExtra("RECIPE_MATCH", 100.0);

        if (title != null) tvTitle.setText(title);

        int matchInt = (int) match;
        tvMatch.setText(matchInt + "% Match");
        if (matchInt == 100) {
            tvMatch.setBackgroundColor(Color.parseColor("#E8F5E9"));
            tvMatch.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            tvMatch.setBackgroundColor(Color.parseColor("#FFF8E1"));
            tvMatch.setTextColor(Color.parseColor("#F57F17"));
        }

        loadRecipeDetails(recipeId);

        btnCook.setOnClickListener(v -> deductIngredientsAndFinish());
    }

    private void loadRecipeDetails(int recipeId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // 1. Fetch instructions
        Cursor cursor = db.rawQuery("SELECT instructions FROM recipes WHERE _id = ?",
                new String[]{String.valueOf(recipeId)});
        if (cursor.moveToFirst()) {
            String instructions = cursor.getString(0);
            tvInstructions.setText(instructions != null && !instructions.isEmpty() ? instructions : "No instructions provided.");
        }
        cursor.close();

        // 2. Fetch required ingredients list
        Cursor ingCursor = db.rawQuery("SELECT ingredient_name, required_quantity FROM recipe_ingredients WHERE recipe_id = ?",
                new String[]{String.valueOf(recipeId)});

        StringBuilder ingBuilder = new StringBuilder();
        if (ingCursor.moveToFirst()) {
            do {
                String name = ingCursor.getString(0);
                double qty = ingCursor.getDouble(1);
                ingBuilder.append("• ").append(name).append(" (").append(qty).append(")\n");
            } while (ingCursor.moveToNext());
        }
        ingCursor.close();

        tvIngredients.setText(ingBuilder.toString().trim());
    }

    private void deductIngredientsAndFinish() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Get required ingredients for this recipe
        Cursor ingCursor = db.rawQuery("SELECT ingredient_name, required_quantity FROM recipe_ingredients WHERE recipe_id = ?",
                new String[]{String.valueOf(recipeId)});

        if (ingCursor.moveToFirst()) {
            do {
                String reqName = ingCursor.getString(0);
                double reqQty = ingCursor.getDouble(1);

                // Deduct from matching pantry row
                db.execSQL("UPDATE pantry SET quantity = MAX(0, quantity - ?) WHERE LOWER(name) LIKE LOWER(?)",
                        new Object[]{reqQty, "%" + reqName.trim() + "%"});

            } while (ingCursor.moveToNext());
        }
        ingCursor.close();

        Toast.makeText(this, "Enjoy your meal! Pantry items updated.", Toast.LENGTH_SHORT).show();
        finish(); // Return back to suggested recipes
    }
}