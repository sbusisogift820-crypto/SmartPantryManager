package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvCategory = findViewById(R.id.tvDetailCategory);
        TextView tvMatch = findViewById(R.id.tvDetailMatch);
        TextView tvInstructions = findViewById(R.id.tvDetailInstructions);

        String title = getIntent().getStringExtra("RECIPE_TITLE");
        String category = getIntent().getStringExtra("RECIPE_CATEGORY");
        String instructions = getIntent().getStringExtra("RECIPE_INSTRUCTIONS");
        double match = getIntent().getDoubleExtra("RECIPE_MATCH", 0.0);

        tvTitle.setText(title);
        tvCategory.setText("Category: " + category);
        tvMatch.setText("Match: " + (int) match + "%");
        tvInstructions.setText(instructions);
    }
}