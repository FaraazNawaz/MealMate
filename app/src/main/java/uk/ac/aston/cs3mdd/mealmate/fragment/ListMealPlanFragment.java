package uk.ac.aston.cs3mdd.mealmate.fragment;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import uk.ac.aston.cs3mdd.mealmate.MealPlanDatabaseHelper;
import uk.ac.aston.cs3mdd.mealmate.R;
import uk.ac.aston.cs3mdd.mealmate.activity.AddMealPlanActivity;
import uk.ac.aston.cs3mdd.mealmate.adapter.MealPlanAdapter;
import uk.ac.aston.cs3mdd.mealmate.model.MealPlan;

public class ListMealPlanFragment extends Fragment implements MealPlanAdapter.OnItemLongClickListener {

    private RecyclerView recyclerView;
    private MealPlanAdapter adapter;
    private MealPlanDatabaseHelper dbHelper;
    private FloatingActionButton fabAddMealPlan;
    private ImageView btnFilterDate;
    private Calendar selectedDate;
    String date = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_plan, container, false);

        dbHelper = new MealPlanDatabaseHelper(requireContext());

        recyclerView = view.findViewById(R.id.recyclerView);
        fabAddMealPlan = view.findViewById(R.id.fabAddMealPlan);
        btnFilterDate = view.findViewById(R.id.btnFilterDate);

        adapter = new MealPlanAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        fabAddMealPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireActivity(), AddMealPlanActivity.class));
            }
        });

        btnFilterDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        selectedDate = Calendar.getInstance();
        loadMealPlans();


        adapter.setOnItemLongClickListener(this);


        return view;
    }
    @Override
    public void onItemLongClick(MealPlan mealPlan, int position) {
        showContextMenu(mealPlan, position);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMealPlans();
    }
    private void showContextMenu(final MealPlan mealPlan, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Options")
                .setItems(new CharSequence[]{"Update", "Delete"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:

                                showUpdateDialog(position);
                                break;
                            case 1:

                                deleteMealPlan(mealPlan);
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    private void showUpdateDialog(final int position) {

        MealPlan mealPlanToUpdate = adapter.getMealPlan(position);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_update_meal_plan, null);
        final EditText etUpdateTitle = dialogView.findViewById(R.id.etUpdateTitle);
        final EditText etUpdateDate = dialogView.findViewById(R.id.etUpdateDate);
        final EditText etUpdateBreakfast = dialogView.findViewById(R.id.etUpdateBreakfast);
        final EditText etUpdateLunch = dialogView.findViewById(R.id.etUpdateLunch);
        final EditText etUpdateDinner = dialogView.findViewById(R.id.etUpdateDinner);
        final EditText etUpdateSnack = dialogView.findViewById(R.id.etUpdateSnack);

        etUpdateTitle.setText(mealPlanToUpdate.getTitle());
        etUpdateDate.setText(mealPlanToUpdate.getDate());
        etUpdateBreakfast.setText(mealPlanToUpdate.getBreakfast());
        etUpdateLunch.setText(mealPlanToUpdate.getLunch());
        etUpdateDinner.setText(mealPlanToUpdate.getDinner());
        etUpdateSnack.setText(mealPlanToUpdate.getSnack());


        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView)
                .setTitle("Update Meal Plan")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String updatedTitle = etUpdateTitle.getText().toString();
                        String updatedDate = etUpdateDate.getText().toString();
                        String updatedBreakfast = etUpdateBreakfast.getText().toString();
                        String updatedLunch = etUpdateLunch.getText().toString();
                        String updatedDinner = etUpdateDinner.getText().toString();
                        String updatedSnack = etUpdateSnack.getText().toString();


                        MealPlan updatedMealPlan = new MealPlan(updatedTitle, updatedDate, updatedBreakfast, updatedLunch, updatedDinner, updatedSnack);

                        dbHelper.updateMealPlan(mealPlanToUpdate.getTitle(), updatedMealPlan);


                        loadMealPlans();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    private void deleteMealPlan(MealPlan mealPlanToDelete) {
        String titleToDelete = mealPlanToDelete.getTitle();
        dbHelper.deleteMealPlan(titleToDelete);
        loadMealPlans();
    }
    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        date = formatDate(day, month + 1, year);
                        loadMealPlansByDate();
                    }
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    private String formatDate(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }
    private void loadMealPlansByDate() {
        List<MealPlan> mealPlanList = dbHelper.getMealPlansByDate(date);
        adapter.setMealPlans(mealPlanList);
    }
    private void loadMealPlans() {
        List<MealPlan> mealPlanList = dbHelper.getAllMealPlans();
        adapter.setMealPlans(mealPlanList);
    }
}

