package uk.ac.aston.cs3mdd.mealmate.adapter;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uk.ac.aston.cs3mdd.mealmate.R;
import uk.ac.aston.cs3mdd.mealmate.model.MealPlan;

public class MealPlanAdapter extends RecyclerView.Adapter<MealPlanAdapter.ViewHolder> {
    public interface OnItemLongClickListener {
        void onItemLongClick(MealPlan mealPlan, int position);
    }

    private OnItemLongClickListener longClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }
    private List<MealPlan> mealPlanList;
    public void setMealPlans(List<MealPlan> mealPlans) {
        this.mealPlanList = mealPlans;
        notifyDataSetChanged();
    }
    public MealPlanAdapter(List<MealPlan> mealPlanList) {
        this.mealPlanList = mealPlanList;
    }


    public MealPlan getMealPlan(int position) {
        if (position >= 0 && position < mealPlanList.size()) {
            return mealPlanList.get(position);
        } else {
            return null;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal_plan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MealPlan mealPlan = mealPlanList.get(position);
        holder.bind(mealPlan);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (longClickListener != null) {
                    longClickListener.onItemLongClick(mealPlan, position);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mealPlanList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate;
        private TextView tvDetails,tvTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDetails = itemView.findViewById(R.id.tvDetails);
        }

        public void bind(MealPlan mealPlan) {
            tvDate.setText(mealPlan.getDate());
            tvTitle.setText(mealPlan.getTitle());

            String details = "Breakfast: " + mealPlan.getBreakfast() +
                    "\n\nLunch: " + mealPlan.getLunch() +
                    "\n\nDinner: " + mealPlan.getDinner() +
                    "\n\nSnack: " + mealPlan.getSnack();
            tvDetails.setText(details);
        }
    }
}
