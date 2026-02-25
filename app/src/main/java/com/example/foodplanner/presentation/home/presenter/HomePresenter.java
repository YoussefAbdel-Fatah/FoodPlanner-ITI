package com.example.foodplanner.presentation.home.presenter;

import com.example.foodplanner.data.network.MealClient;
import com.example.foodplanner.presentation.home.view.HomeView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomePresenter {
    private HomeView view;
    private MealClient client;

    public HomePresenter(HomeView view) {
        this.view = view;
        this.client = MealClient.getInstance();
    }

    public void getHomeData() {
        view.showLoading();

        // 1. Get Random Meal
        client.getRandomMeal()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            view.hideLoading();
                            if (response.getMeals() != null && !response.getMeals().isEmpty()) {
                                view.showRandomMeal(response.getMeals().get(0));
                            }
                        },
                        error -> {
                            view.hideLoading();
                            view.showError(error.getMessage());
                        }
                );

        // 2. Get Categories
        client.getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> view.showCategories(response.getCategories()),
                        error -> view.showError(error.getMessage())
                );
    }
}