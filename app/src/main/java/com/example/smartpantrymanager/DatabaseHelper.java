package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_PANTRY = "pantry";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_ING = "recipe_ingredients";

    // Common Columns
    public static final String COLUMN_ID = "_id";

    // Pantry Columns
    public static final String COLUMN_PANTRY_NAME = "name";
    public static final String COLUMN_PANTRY_QTY = "quantity";
    public static final String COLUMN_PANTRY_UNIT = "unit";
    public static final String COLUMN_PANTRY_EXPIRY = "expiry_date";

    // Recipe Columns
    public static final String COLUMN_RECIPE_NAME = "name";
    public static final String COLUMN_RECIPE_STEPS = "instructions";

    // Recipe Ingredients Columns
    public static final String COLUMN_ING_RECIPE_ID = "recipe_id";
    public static final String COLUMN_ING_NAME = "ingredient_name";
    public static final String COLUMN_ING_QTY = "required_quantity";
    public static final String COLUMN_ING_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createPantry = "CREATE TABLE " + TABLE_PANTRY + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PANTRY_NAME + " TEXT UNIQUE, " +
                COLUMN_PANTRY_QTY + " REAL, " +
                COLUMN_PANTRY_UNIT + " TEXT, " +
                COLUMN_PANTRY_EXPIRY + " TEXT);";

        String createRecipes = "CREATE TABLE " + TABLE_RECIPES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RECIPE_NAME + " TEXT, " +
                COLUMN_RECIPE_STEPS + " TEXT);";

        String createRecipeIng = "CREATE TABLE " + TABLE_RECIPE_ING + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ING_RECIPE_ID + " INTEGER, " +
                COLUMN_ING_NAME + " TEXT, " +
                COLUMN_ING_QTY + " REAL, " +
                COLUMN_ING_UNIT + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_ING_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_ID + "));";

        db.execSQL(createPantry);
        db.execSQL(createRecipes);
        db.execSQL(createRecipeIng);

        seedInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_ING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }

    // --- PANTRY CRUD OPERATIONS ---

    public boolean addPantryItem(String name, double qty, String unit, String expiry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PANTRY_NAME, name.trim().toLowerCase());
        cv.put(COLUMN_PANTRY_QTY, qty);
        cv.put(COLUMN_PANTRY_UNIT, unit.trim().toLowerCase());
        cv.put(COLUMN_PANTRY_EXPIRY, expiry);
        long result = db.insert(TABLE_PANTRY, null, cv);
        return result != -1;
    }

    public Cursor getAllPantryItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PANTRY + " ORDER BY " + COLUMN_PANTRY_NAME + " ASC", null);
    }

    public boolean updatePantryItem(int id, String name, double qty, String unit, String expiry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PANTRY_NAME, name.trim().toLowerCase());
        cv.put(COLUMN_PANTRY_QTY, qty);
        cv.put(COLUMN_PANTRY_UNIT, unit.trim().toLowerCase());
        cv.put(COLUMN_PANTRY_EXPIRY, expiry);
        int result = db.update(TABLE_PANTRY, cv, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    public boolean deletePantryItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_PANTRY, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    // --- DATABASE SEEDING (Pre-loading Recipes) ---

    private void seedInitialData(SQLiteDatabase db) {
        // Recipe 1
        long r1 = insertRecipe(db, "Scrambled Eggs", "1. Beat eggs with milk.\n2. Melt butter in pan.\n3. Cook on low heat while stirring gently.");
        insertIngredient(db, r1, "egg", 2, "pcs");
        insertIngredient(db, r1, "milk", 50, "ml");
        insertIngredient(db, r1, "butter", 10, "g");

        // Recipe 2
        long r2 = insertRecipe(db, "Tomato Pasta", "1. Boil pasta in salted water.\n2. Heat olive oil and garlic.\n3. Add tomatoes and simmer.\n4. Mix pasta with sauce.");
        insertIngredient(db, r2, "pasta", 200, "g");
        insertIngredient(db, r2, "tomato", 2, "pcs");
        insertIngredient(db, r2, "garlic", 1, "clove");
        insertIngredient(db, r2, "olive oil", 15, "ml");

        // Recipe 3
        long r3 = insertRecipe(db, "Grilled Cheese", "1. Butter outside of bread slices.\n2. Place cheese between slices.\n3. Grill until golden brown.");
        insertIngredient(db, r3, "bread", 2, "slices");
        insertIngredient(db, r3, "cheese", 50, "g");
        insertIngredient(db, r3, "butter", 10, "g");
    }

    private long insertRecipe(SQLiteDatabase db, String name, String steps) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_RECIPE_NAME, name);
        cv.put(COLUMN_RECIPE_STEPS, steps);
        return db.insert(TABLE_RECIPES, null, cv);
    }

    private void insertIngredient(SQLiteDatabase db, long recipeId, String name, double qty, String unit) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ING_RECIPE_ID, recipeId);
        cv.put(COLUMN_ING_NAME, name.trim().toLowerCase());
        cv.put(COLUMN_ING_QTY, qty);
        cv.put(COLUMN_ING_UNIT, unit.trim().toLowerCase());
        db.insert(TABLE_RECIPE_ING, null, cv);
    }
}