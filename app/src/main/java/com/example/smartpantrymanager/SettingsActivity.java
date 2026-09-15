package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dbHelper = new DatabaseHelper(this);
        Button btnClear = findViewById(R.id.btnClearData);

        btnClear.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Delete All Pantry Items")
                .setMessage("Are you sure you want to clear your pantry?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.getWritableDatabase().delete("pantry", null, null);
                    Toast.makeText(this, "Pantry cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show());
    }
}