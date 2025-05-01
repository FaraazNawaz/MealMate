package uk.ac.aston.cs3mdd.mealmate.activity;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import uk.ac.aston.cs3mdd.mealmate.MealPlanDatabaseHelper;
import uk.ac.aston.cs3mdd.mealmate.R;
import uk.ac.aston.cs3mdd.mealmate.model.MealPlan;

public class AddMealPlanActivity extends AppCompatActivity {

    private EditText etTitle, etDate, etBreakfast, etLunch, etDinner, etSnack;
    private Button btnSave;

    private MealPlanDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal_plan);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Meal Plan");

        }
        dbHelper = new MealPlanDatabaseHelper(this);

        etTitle = findViewById(R.id.etTitle);
        etDate = findViewById(R.id.etDate);
        etBreakfast = findViewById(R.id.etBreakfast);
        etLunch = findViewById(R.id.etLunch);
        etDinner = findViewById(R.id.etDinner);
        etSnack = findViewById(R.id.etSnack);

        btnSave = findViewById(R.id.btnSave);

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMealPlan();
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                String selectedDate = formatDate(dayOfMonth, monthOfYear + 1, year);
                etDate.setText(selectedDate);
            }
        }, year, month, day);

        datePickerDialog.show();
    }
    private String formatDate(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }
    private void saveMealPlan() {
        String title = etTitle.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String breakfast = etBreakfast.getText().toString().trim();
        String lunch = etLunch.getText().toString().trim();
        String dinner = etDinner.getText().toString().trim();
        String snack = etSnack.getText().toString().trim();

        if (!TextUtils.isEmpty(date)) {
            MealPlan mealPlan = new MealPlan(title,date, breakfast, lunch, dinner, snack);
            dbHelper.saveMealPlan(mealPlan);

            Toast.makeText(this, "Meal plan has been created", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Select date for meal plan", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
