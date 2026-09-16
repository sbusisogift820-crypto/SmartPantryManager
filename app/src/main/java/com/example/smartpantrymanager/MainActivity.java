package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private Button btnAdd, btnRecipes;
    private SearchView searchView;
    private ChipGroup chipGroup;
    private DatabaseHelper dbHelper;
    private PantryAdapter adapter;
    private List<PantryItem> pantryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerViewPantry);
        tvEmpty = findViewById(R.id.tvEmptyPantry);
        btnAdd = findViewById(R.id.btnAddIngredient);
        btnRecipes = findViewById(R.id.btnViewRecipes);
        searchView = findViewById(R.id.searchViewPantry);
        chipGroup = findViewById(R.id.chipGroupFilter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        pantryList = new ArrayList<>();

        // Adapter setup
        adapter = new PantryAdapter(this, pantryList, new PantryAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int id) {
                dbHelper.deletePantryItem(id);
                Toast.makeText(MainActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                loadPantryItems();
            }

            @Override
            public void onItemClick(PantryItem item) {
                Intent intent = new Intent(MainActivity.this, AddEditIngredientActivity.class);
                intent.putExtra("ITEM_ID", item.getId());
                intent.putExtra("ITEM_NAME", item.getName());
                intent.putExtra("ITEM_QTY", item.getQuantity());
                intent.putExtra("ITEM_UNIT", item.getUnit());
                intent.putExtra("ITEM_EXPIRY", item.getExpiryDate());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        // Buttons
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditIngredientActivity.class);
            startActivity(intent);
        });

        btnRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SuggestedRecipesActivity.class);
            startActivity(intent);
        });

        // Search listener
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    applyCurrentFilters();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    applyCurrentFilters();
                    return true;
                }
            });
        }

        // Chip selection listener
        if (chipGroup != null) {
            chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> applyCurrentFilters());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems();
    }

    private void loadPantryItems() {
        pantryList.clear();
        Cursor cursor = dbHelper.getAllPantryItems();

        if (cursor != null && cursor.getCount() > 0) {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_NAME));
                double qty = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_QTY));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_UNIT));
                String expiry = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_EXPIRY));

                pantryList.add(new PantryItem(id, name, qty, unit, expiry));
            }
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        if (cursor != null) {
            cursor.close();
        }

        adapter.updateData(pantryList);
        applyCurrentFilters();
    }

    private void applyCurrentFilters() {
        if (adapter != null) {
            String query = (searchView != null) ? searchView.getQuery().toString() : "";
            int checkedChipId = (chipGroup != null) ? chipGroup.getCheckedChipId() : View.NO_ID;
            adapter.filter(query, checkedChipId);
        }
    }
}