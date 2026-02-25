package com.example.foodplanner.presentation.search.view;

import com.example.foodplanner.model.Meal;
import java.util.List;

public interface SearchViewInterface {
    void showMeals(List<Meal> meals);
    void showError(String errorMsg);
    void showLoading();
    void hideLoading();
}