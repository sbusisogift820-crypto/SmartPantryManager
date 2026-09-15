package com.example.smartpantrymanager;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final Context context;
    private final List<Recipe> recipeList;
    private OnRecipeClickListener listener;

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    public RecipeAdapter(Context context, List<Recipe> recipeList, OnRecipeClickListener listener) {
        this.context = context;
        this.recipeList = recipeList;
        this.listener = listener;
    }

    public RecipeAdapter(Context context, List<Recipe> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        holder.tvTitle.setText(recipe.getTitle());

        int match = (int) recipe.getMatchPercentage();
        holder.tvMatch.setText(match + "% Match");

        // Dynamic Badge Styling
        if (match == 100) {
            holder.tvMatch.setBackgroundColor(Color.parseColor("#E8F5E9"));
            holder.tvMatch.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            holder.tvMatch.setBackgroundColor(Color.parseColor("#FFF8E1"));
            holder.tvMatch.setTextColor(Color.parseColor("#F57F17"));
        }

        // FIX: Replaced getIngredients() with getMissingIngredients()
        List<String> missingIngredients = recipe.getMissingIngredients();
        if (missingIngredients == null || missingIngredients.isEmpty()) {
            holder.tvMissing.setText("Ready to cook!");
        } else {
            holder.tvMissing.setText("Missing: " + TextUtils.join(", ", missingIngredients));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecipeClick(recipe);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList != null ? recipeList.size() : 0;
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMatch, tvMissing;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvRecipeTitle);
            tvMatch = itemView.findViewById(R.id.tvMatchPercentage);
            tvMissing = itemView.findViewById(R.id.tvMissingIngredients);
        }
    }
}