package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvNoRecipes;
    private DatabaseHelper dbHelper;

    public void onBackClicked(View view) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        // Bind back button click listener
        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        recyclerView = findViewById(R.id.recyclerViewRecipes);
        tvNoRecipes = findViewById(R.id.tvNoRecipes);
        dbHelper = new DatabaseHelper(this);

        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {
        try {
            List<Recipe> matches = RecipeMatcher.getStrictlySuggestedRecipes(dbHelper);

            if (matches == null || matches.isEmpty()) {
                if (tvNoRecipes != null) tvNoRecipes.setVisibility(View.VISIBLE);
                if (recyclerView != null) recyclerView.setVisibility(View.GONE);
            } else {
                if (tvNoRecipes != null) tvNoRecipes.setVisibility(View.GONE);
                if (recyclerView != null) {
                    recyclerView.setVisibility(View.VISIBLE);

                    RecipeAdapter adapter = new RecipeAdapter(this, matches, recipe -> {
                        Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
                        intent.putExtra("RECIPE_ID", recipe.getId());
                        intent.putExtra("RECIPE_TITLE", recipe.getTitle());
                        intent.putExtra("RECIPE_MATCH", recipe.getMatchPercentage());
                        startActivity(intent);
                    });

                    recyclerView.setAdapter(adapter);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (tvNoRecipes != null) {
                tvNoRecipes.setVisibility(View.VISIBLE);
                tvNoRecipes.setText("Error loading recipes: " + e.getLocalizedMessage());
            }
        }
    }
}