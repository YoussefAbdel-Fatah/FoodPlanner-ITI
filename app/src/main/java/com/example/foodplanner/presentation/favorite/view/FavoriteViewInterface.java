package com.example.foodplanner.presentation.favorite.view;

import com.example.foodplanner.data.db.MealEntity;

import java.util.List;

public interface FavoriteViewInterface {
    void showFavorites(List<MealEntity> meals);

    void showEmpty();

    void showRemoveSuccess();

    void showError(String message);
}
