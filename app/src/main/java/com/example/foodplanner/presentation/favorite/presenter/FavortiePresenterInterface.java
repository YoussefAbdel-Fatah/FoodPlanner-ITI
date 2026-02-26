package com.example.foodplanner.presentation.favorite.presenter;

import com.example.foodplanner.data.db.MealEntity;

public interface FavortiePresenterInterface {
    void loadFavorites();

    void removeFavorite(MealEntity meal);
}
