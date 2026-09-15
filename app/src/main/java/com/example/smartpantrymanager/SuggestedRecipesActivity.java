package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvNoRecipes;
    private DatabaseHelper dbHelper;
    private RecipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerViewRecipes);
        tvNoRecipes = findViewById(R.id.tvNoRecipes);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Cursor pantryCursor = dbHelper.getAllPantryItems();
        List<Recipe> matchedRecipes = RecipeMatcher.getStrictlySuggestedRecipes(dbHelper);

        if (matchedRecipes.isEmpty()) {
            tvNoRecipes.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoRecipes.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            adapter = new RecipeAdapter(this, matchedRecipes, recipe -> {
                Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
                intent.putExtra("RECIPE_TITLE", recipe.getTitle());
                intent.putExtra("RECIPE_CATEGORY", recipe.getCategory());
                intent.putExtra("RECIPE_INSTRUCTIONS", recipe.getInstructions());
                intent.putExtra("RECIPE_MATCH", recipe.getMatchPercentage());
                startActivity(intent);
            });

            recyclerView.setAdapter(adapter);
        }
    }
}