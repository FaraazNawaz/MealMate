package uk.ac.aston.cs3mdd.mealmate;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import uk.ac.aston.cs3mdd.mealmate.model.MealPlan;
import uk.ac.aston.cs3mdd.mealmate.model.ShoppingListItem;

public class MealPlanDatabaseHelper extends SQLiteOpenHelper {



    private static final String DATABASE_NAME = "meal_plan_db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_MEAL_PLAN = "meal_plan";
    public static final String COLUMN_TITLE = "title";

    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_BREAKFAST = "breakfast";
    public static final String COLUMN_LUNCH = "lunch";
    public static final String COLUMN_DINNER = "dinner";
    public static final String COLUMN_SNACK = "snack";


    private static final String CREATE_TABLE_MEAL_PLAN = "CREATE TABLE " + TABLE_MEAL_PLAN + " (" +
            COLUMN_TITLE + " TEXT PRIMARY KEY," +
            COLUMN_DATE + " TEXT," +
            COLUMN_BREAKFAST + " TEXT," +
            COLUMN_LUNCH + " TEXT," +
            COLUMN_DINNER + " TEXT," +
            COLUMN_SNACK + " TEXT)";


    public static final String TABLE_SHOPPING_LIST = "shopping_list";
    public static final String COLUMN_ITEM_NAME = "item_name";
    public static final String COLUMN_ITEM_COMPLETED = "item_completed";

    private static final String CREATE_TABLE_SHOPPING_LIST = "CREATE TABLE " + TABLE_SHOPPING_LIST + " (" +
            COLUMN_ITEM_NAME + " TEXT PRIMARY KEY," +
            COLUMN_ITEM_COMPLETED + " INTEGER)";
    public MealPlanDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MEAL_PLAN);
        db.execSQL(CREATE_TABLE_SHOPPING_LIST);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void saveMealPlan(MealPlan mealPlan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, mealPlan.getTitle());
        values.put(COLUMN_DATE, mealPlan.getDate());
        values.put(COLUMN_BREAKFAST, mealPlan.getBreakfast());
        values.put(COLUMN_LUNCH, mealPlan.getLunch());
        values.put(COLUMN_DINNER, mealPlan.getDinner());
        values.put(COLUMN_SNACK, mealPlan.getSnack());

        db.insert(TABLE_MEAL_PLAN, null, values);
        db.close();
    }

    @SuppressLint("Range")
    public List<MealPlan> getAllMealPlans() {
        List<MealPlan> mealPlanList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MEAL_PLAN;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                String breakfast = cursor.getString(cursor.getColumnIndex(COLUMN_BREAKFAST));
                String lunch = cursor.getString(cursor.getColumnIndex(COLUMN_LUNCH));
                String dinner = cursor.getString(cursor.getColumnIndex(COLUMN_DINNER));
                String snack = cursor.getString(cursor.getColumnIndex(COLUMN_SNACK));

                MealPlan mealPlan = new MealPlan(title,date, breakfast, lunch, dinner, snack);
                mealPlanList.add(mealPlan);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return mealPlanList;
    }

    @SuppressLint("Range")
    public List<MealPlan> getMealPlansByDate(String date) {
        List<MealPlan> mealPlans = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_MEAL_PLAN,
                null,
                COLUMN_DATE + "=?",
                new String[]{date},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {

                String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                String breakfast = cursor.getString(cursor.getColumnIndex(COLUMN_BREAKFAST));
                String lunch = cursor.getString(cursor.getColumnIndex(COLUMN_LUNCH));
                String dinner = cursor.getString(cursor.getColumnIndex(COLUMN_DINNER));
                String snack = cursor.getString(cursor.getColumnIndex(COLUMN_SNACK));

                MealPlan mealPlan = new MealPlan(title, date, breakfast, lunch, dinner, snack);
                mealPlans.add(mealPlan);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();

        return mealPlans;
    }

    public void updateMealPlan(String title, MealPlan updatedMealPlan) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, updatedMealPlan.getTitle());
        values.put(COLUMN_DATE, updatedMealPlan.getDate());
        values.put(COLUMN_BREAKFAST, updatedMealPlan.getBreakfast());
        values.put(COLUMN_LUNCH, updatedMealPlan.getLunch());
        values.put(COLUMN_DINNER, updatedMealPlan.getDinner());
        values.put(COLUMN_SNACK, updatedMealPlan.getSnack());

        db.update(TABLE_MEAL_PLAN, values, COLUMN_TITLE + "=?", new String[]{title});

        db.close();
    }


    public void deleteMealPlan(String title) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_MEAL_PLAN, COLUMN_TITLE + "=?", new String[]{title});

        db.close();
    }





    public void saveShoppingListItem(String itemName, boolean completed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, itemName);
        values.put(COLUMN_ITEM_COMPLETED, completed ? 1 : 0);
        db.insertWithOnConflict(TABLE_SHOPPING_LIST, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    @SuppressLint("Range")
    public List<ShoppingListItem> getAllShoppingListItems() {
        List<ShoppingListItem> shoppingListItems = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_SHOPPING_LIST;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String itemName = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_NAME));
                boolean completed = cursor.getInt(cursor.getColumnIndex(COLUMN_ITEM_COMPLETED)) == 1;

                ShoppingListItem shoppingListItem = new ShoppingListItem(itemName, completed);
                shoppingListItems.add(shoppingListItem);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return shoppingListItems;
    }


    public void updateShoppingListItem(String oldItemName, String newItemName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, newItemName);

        db.update(TABLE_SHOPPING_LIST, values, COLUMN_ITEM_NAME + " = ?", new String[]{oldItemName});
        db.close();
    }

    public void deleteShoppingListItem(String itemName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SHOPPING_LIST, COLUMN_ITEM_NAME + " = ?", new String[]{itemName});
        db.close();
    }

}
