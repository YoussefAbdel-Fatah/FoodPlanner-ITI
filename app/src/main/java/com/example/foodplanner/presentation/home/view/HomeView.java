package com.example.foodplanner.presentation.home.view;

import com.example.foodplanner.model.Category;
import com.example.foodplanner.model.Meal;

import java.util.List;

public interface HomeView {
    void showRandomMeal(Meal meal);
    void showError(String errorMsg);
    void showLoading();
    void hideLoading();
    void showCategories(List<Category> categories);
}
