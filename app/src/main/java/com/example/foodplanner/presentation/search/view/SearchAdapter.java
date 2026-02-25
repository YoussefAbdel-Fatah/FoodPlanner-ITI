package com.example.foodplanner.presentation.search.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.foodplanner.R;
import com.example.foodplanner.model.Meal;
import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private Context context;
    private List<Meal> meals;

    public SearchAdapter(Context context) {
        this.context = context;
        this.meals = new ArrayList<>();
    }

    public void setList(List<Meal> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Meal meal = meals.get(position);
        holder.tvTitle.setText(meal.getName());

        // We will mock the rating/calories for now since the API doesn't give them
        holder.tvRating.setText("4.5 (99)");
        holder.tvCalories.setText("350 kcal");
        holder.tvTime.setText("30 min");

        Glide.with(context)
                .load(meal.getImageUrl())
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgRecipe);
    }

    @Override
    public int getItemCount() {
        return meals == null ? 0 : meals.size();
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRecipe;
        TextView tvTitle, tvRating, tvCalories, tvTime;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRecipe = itemView.findViewById(R.id.imgRecipe);
            tvTitle = itemView.findViewById(R.id.tvRecipeTitle);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvCalories = itemView.findViewById(R.id.tvCalories);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}