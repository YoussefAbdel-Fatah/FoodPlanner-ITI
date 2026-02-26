package com.example.foodplanner.presentation.category.view;

import com.example.foodplanner.model.Meal;
import java.util.List;

public interface CategoryViewInterface {
    void showMeals(List<Meal> meals);

    void showError(String errorMsg);

    void showLoading();

    void hideLoading();
}
