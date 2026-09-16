package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditIngredientActivity extends AppCompatActivity {

    private TextView tvTitle;
    private EditText etName, etQuantity, etUnit, etExpiry;
    private Button btnSave;
    private DatabaseHelper dbHelper;
    private int itemId = -1; // -1 indicates creation mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        // 1. Initialize views
        ImageButton btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvFormTitle);
        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etExpiry = findViewById(R.id.etExpiry);
        btnSave = findViewById(R.id.btnSave);
        dbHelper = new DatabaseHelper(this);

        // 2. Set up back button navigation
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // 3. Check if editing an existing item
        if (getIntent().hasExtra("ITEM_ID")) {
            itemId = getIntent().getIntExtra("ITEM_ID", -1);
            if (tvTitle != null) tvTitle.setText("Edit Pantry Item");
            if (etName != null) etName.setText(getIntent().getStringExtra("ITEM_NAME"));
            if (etQuantity != null) etQuantity.setText(String.valueOf(getIntent().getDoubleExtra("ITEM_QUANTITY", 0.0)));
            if (etUnit != null) etUnit.setText(getIntent().getStringExtra("ITEM_UNIT"));
            if (etExpiry != null) etExpiry.setText(getIntent().getStringExtra("ITEM_EXPIRY"));
        }

        // 4. Save button listener
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> saveIngredient());
        }
    }

    // Backup XML onClick handler for back button
    public void onBackClicked(View view) {
        finish();
    }

    private void saveIngredient() {
        String name = etName.getText().toString().trim();
        String qtyStr = etQuantity.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();

        if (name.isEmpty() || qtyStr.isEmpty()) {
            Toast.makeText(this, "Please enter at least a name and quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(qtyStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid quantity value", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);
        values.put("expiry_date", expiry);

        if (itemId == -1) {
            // Insert new pantry item
            db.insert("pantry", null, values);
            Toast.makeText(this, "Item added to pantry", Toast.LENGTH_SHORT).show();
        } else {
            // Update existing item
            db.update("pantry", values, "id = ?", new String[]{String.valueOf(itemId)});
            Toast.makeText(this, "Pantry item updated", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}