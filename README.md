# Smart Pantry Manager

A Java-based Android application designed to reduce household food waste by tracking pantry inventory and suggesting recipes strictly based on leftover ingredients.

---

## Key Features
* **Pantry Management (CRUD):** Add, edit, view, and delete pantry items with quantities, units, and expiry dates.
* **Strict-Matching Recipe Engine:** Suggests recipes only when 100% of required ingredients are available in sufficient quantities in the pantry.
* **Visual Pantry Dashboard:** Displays real-time metrics for total inventory count and items expiring soon.
* **Filtered Inventory View:** Filter pantry items by status (All, Expiring Soon, Low Stock) or search by name.

---

## Database Choice & Justification
* **Database System:** SQLite (via standard `SQLiteOpenHelper`)
* **Justification:** SQLite was selected because it provides local, lightweight relational data storage directly on the device. Since Smart Pantry Manager operates around strict ingredient-to-recipe relational matching, SQLite enables high-performance local SQL queries without requiring network latency or active internet connectivity.

---

## App Architecture & Screen Flow
The application consists of five main activities:
1. **`MainActivity` (Pantry List & Metrics):** Displays current inventory with real-time metrics and filtering options.
2. **`AddEditIngredientActivity`:** Provides input forms for creating and updating pantry items with full validation.
3. **`SuggestedRecipesActivity`:** Runs the strict-matching relational query to show only cookable recipes.
4. **`RecipeDetailActivity`:** Displays detailed preparation steps and required ingredient lists.
5. **`SettingsActivity`:** Manages user preferences and system configuration.

---

## Setup & Installation Instructions
1. **Prerequisites:** 
   * Android Studio Ladybug (or newer)
   * JDK 17 or higher
   * Android SDK (API 24 or higher)
2. **Cloning the Repository:**
   ```bash
   git clone [https://github.com/sbusisogift820-crypto/SmartPantryManager.git](https://github.com/sbusisogift820-crypto/SmartPantryManager.git)

  
