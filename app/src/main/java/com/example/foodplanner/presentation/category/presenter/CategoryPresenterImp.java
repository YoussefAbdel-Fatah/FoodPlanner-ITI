package com.example.foodplanner.presentation.category.presenter;

import com.example.foodplanner.data.network.MealClient;
import com.example.foodplanner.presentation.category.view.CategoryViewInterface;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

public class CategoryPresenterImp implements CategoryPresenterInterface {

    private CategoryViewInterface view;

    public CategoryPresenterImp(CategoryViewInterface view) {
        this.view = view;
    }

    @Override
    public void getMealsByCategory(String category) {
        view.showLoading();
        MealClient.getInstance().filterByCategory(category)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        mealResponse -> {
                            view.hideLoading();
                            if (mealResponse.getMeals() != null) {
                                view.showMeals(mealResponse.getMeals());
                            }
                        },
                        error -> {
                            view.hideLoading();
                            view.showError(error.getMessage());
                        });
    }
}
