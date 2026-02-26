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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private Context context;
    private List<Meal> meals;
    private OnSearchItemClickListener listener;
    private Set<String> favoriteIds = new HashSet<>();

    public interface OnSearchItemClickListener {
        void onItemClick(Meal meal);

        void onFavoriteClick(Meal meal, boolean isCurrentlyFavorite);
    }

    public SearchAdapter(Context context, OnSearchItemClickListener listener) {
        this.context = context;
        this.meals = new ArrayList<>();
        this.listener = listener;
    }

    public void setList(List<Meal> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }

    public void setFavoriteIds(Set<String> ids) {
        this.favoriteIds = ids;
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

        // Show area and category from API
        if (meal.getArea() != null && !meal.getArea().isEmpty()) {
            holder.tvArea.setText(meal.getArea());
            holder.tvArea.setVisibility(View.VISIBLE);
        } else {
            holder.tvArea.setVisibility(View.GONE);
        }

        if (meal.getCategory() != null && !meal.getCategory().isEmpty()) {
            holder.tvCategory.setText(meal.getCategory());
            holder.tvCategory.setVisibility(View.VISIBLE);
        } else {
            holder.tvCategory.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(meal.getImageUrl())
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgRecipe);

        // Favorite state
        boolean isFav = favoriteIds.contains(meal.getId());
        updateFavoriteIcon(holder.btnFavorite, isFav);

        holder.btnFavorite.setOnClickListener(v -> {
            boolean currentFav = favoriteIds.contains(meal.getId());
            listener.onFavoriteClick(meal, currentFav);
        });

        holder.itemView.setOnClickListener(v -> listener.onItemClick(meal));
    }

    private void updateFavoriteIcon(ImageView btn, boolean isFav) {
        if (isFav) {
            btn.setImageResource(R.drawable.ic_heart_filled);
            btn.setColorFilter(btn.getContext().getResources().getColor(R.color.chip_red_text, null));
        } else {
            btn.setImageResource(R.drawable.ic_heart_filled);
            btn.setColorFilter(btn.getContext().getResources().getColor(R.color.text_grey, null));
        }
    }

    @Override
    public int getItemCount() {
        return meals == null ? 0 : meals.size();
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRecipe, btnFavorite;
        TextView tvTitle, tvArea, tvCategory;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRecipe = itemView.findViewById(R.id.imgRecipe);
            tvTitle = itemView.findViewById(R.id.tvRecipeTitle);
            tvArea = itemView.findViewById(R.id.tvArea);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
}