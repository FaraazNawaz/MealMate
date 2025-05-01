package uk.ac.aston.cs3mdd.mealmate.adapter;

import android.app.Activity;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uk.ac.aston.cs3mdd.mealmate.MealPlanDatabaseHelper;
import uk.ac.aston.cs3mdd.mealmate.R;
import uk.ac.aston.cs3mdd.mealmate.model.ShoppingListItem;


public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {

    private List<ShoppingListItem> shoppingItems;
    private MealPlanDatabaseHelper dbHelper;
    private Activity activity;

    public ShoppingListAdapter(List<ShoppingListItem> shoppingItems, Activity activity) {
        this.shoppingItems = shoppingItems;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping_list, parent, false);
        dbHelper = new MealPlanDatabaseHelper(parent.getContext());

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShoppingListItem item = shoppingItems.get(position);
        holder.textItem.setText(item.getItemName());
        holder.checkBox.setChecked(item.isCompleted());


        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dbHelper.saveShoppingListItem(item.getItemName(), isChecked);


            item.setCompleted(isChecked);


            updateItemUI(holder, isChecked);
        });


        updateItemUI(holder, item.isCompleted());
    }

    private void updateItemUI(ViewHolder holder, boolean completed) {
        if (completed) {
            holder.textItem.setPaintFlags(holder.textItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.textItem.setPaintFlags(holder.textItem.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    public void enableSwipeToDelete(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                deleteItem(position);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return shoppingItems.size();
    }



    private void deleteItem(int position) {
        dbHelper.deleteShoppingListItem(shoppingItems.get(position).getItemName());


        shoppingItems.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;
        public TextView textItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
            textItem = itemView.findViewById(R.id.textItem);
        }
    }
}
