package com.example.foodplanner.presentation.details.view;

import com.example.foodplanner.model.Meal;

public interface MealDetailsView {
    void showMealDetails(Meal meal);

    void showError(String errorMsg);

    void showLoading();

    void hideLoading();
}