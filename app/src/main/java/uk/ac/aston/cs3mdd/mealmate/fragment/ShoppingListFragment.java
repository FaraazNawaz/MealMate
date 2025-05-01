package uk.ac.aston.cs3mdd.mealmate.fragment;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import uk.ac.aston.cs3mdd.mealmate.MealPlanDatabaseHelper;
import uk.ac.aston.cs3mdd.mealmate.R;
import uk.ac.aston.cs3mdd.mealmate.adapter.ShoppingListAdapter;
import uk.ac.aston.cs3mdd.mealmate.model.ShoppingListItem;


public class ShoppingListFragment extends Fragment {

    private List<ShoppingListItem> shoppingItems;
    private ShoppingListAdapter adapter;
    private MealPlanDatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        dbHelper = new MealPlanDatabaseHelper(requireContext());

        shoppingItems = dbHelper.getAllShoppingListItems();
        adapter = new ShoppingListAdapter(shoppingItems, requireActivity());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewShoppingList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        adapter.enableSwipeToDelete(recyclerView);

        FloatingActionButton btnAddItem = view.findViewById(R.id.btnAddItem);
        btnAddItem.setOnClickListener(v -> showAddItemDialog());

        view.findViewById(R.id.btnFind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.action_shoppingListFragment_to_groceryStoreFragment);
            }
        });
        return view;
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Item");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String newItemName = input.getText().toString().trim();
            if (!newItemName.isEmpty()) {
                addNewItem(newItemName);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void addNewItem(String itemName) {

        dbHelper.saveShoppingListItem(itemName, false);


        shoppingItems.add(new ShoppingListItem(itemName, false));
        adapter.notifyDataSetChanged();
    }


}
