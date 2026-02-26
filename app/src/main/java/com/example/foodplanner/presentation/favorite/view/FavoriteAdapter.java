package com.example.foodplanner.presentation.favorite.view;

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
import com.example.foodplanner.data.db.MealEntity;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavViewHolder> {

    private Context context;
    private List<MealEntity> meals;
    private OnFavoriteClickListener listener;

    public interface OnFavoriteClickListener {
        void onRemoveClick(MealEntity meal);

        void onItemClick(MealEntity meal);
    }

    public FavoriteAdapter(Context context, List<MealEntity> meals, OnFavoriteClickListener listener) {
        this.context = context;
        this.meals = meals;
        this.listener = listener;
    }

    public void setList(List<MealEntity> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_meal, parent, false);
        return new FavViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavViewHolder holder, int position) {
        MealEntity meal = meals.get(position);
        holder.tvName.setText(meal.strMeal);

        Glide.with(context)
                .load(meal.strMealThumb)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgMeal);

        holder.btnRemove.setOnClickListener(v -> listener.onRemoveClick(meal));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(meal));
    }

    @Override
    public int getItemCount() {
        return meals == null ? 0 : meals.size();
    }

    class FavViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMeal, btnRemove;
        TextView tvName;

        public FavViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMeal = itemView.findViewById(R.id.imgFavMeal);
            btnRemove = itemView.findViewById(R.id.btnRemoveFav);
            tvName = itemView.findViewById(R.id.tvFavMealName);
        }
    }
}
