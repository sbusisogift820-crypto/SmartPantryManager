package com.example.smartpantrymanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText etName, etQuantity, etUnit, etExpiry;
    private Button btnSave;
    private TextView tvTitle;
    private DatabaseHelper dbHelper;
    private int itemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        dbHelper = new DatabaseHelper(this);
        tvTitle = findViewById(R.id.tvFormTitle);
        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etExpiry = findViewById(R.id.etExpiry);
        btnSave = findViewById(R.id.btnSave);

        // Check if editing an existing item
        if (getIntent().hasExtra("ITEM_ID")) {
            itemId = getIntent().getIntExtra("ITEM_ID", -1);
            tvTitle.setText("Edit Pantry Item");
            etName.setText(getIntent().getStringExtra("ITEM_NAME"));
            etQuantity.setText(String.valueOf(getIntent().getDoubleExtra("ITEM_QTY", 0)));
            etUnit.setText(getIntent().getStringExtra("ITEM_UNIT"));
            etExpiry.setText(getIntent().getStringExtra("ITEM_EXPIRY"));
        }

        btnSave.setOnClickListener(v -> saveIngredient());
    }

    private void saveIngredient() {
        String name = etName.getText().toString().trim();
        String qtyStr = etQuantity.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();

        // Form Validation
        if (TextUtils.isEmpty(name)) {
            etName.setError("Ingredient name is required");
            return;
        }
        if (TextUtils.isEmpty(qtyStr)) {
            etQuantity.setError("Quantity is required");
            return;
        }
        if (TextUtils.isEmpty(unit)) {
            etUnit.setError("Unit is required");
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(qtyStr);
        } catch (NumberFormatException e) {
            etQuantity.setError("Enter a valid number");
            return;
        }

        boolean success;
        if (itemId == -1) {
            success = dbHelper.addPantryItem(name, quantity, unit, expiry);
        } else {
            success = dbHelper.updatePantryItem(itemId, name, quantity, unit, expiry);
        }

        if (success) {
            Toast.makeText(this, "Item saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving item", Toast.LENGTH_SHORT).show();
        }
    }
}