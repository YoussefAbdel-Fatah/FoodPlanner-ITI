package com.example.foodplanner.presentation.home.presenter;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.foodplanner.data.network.MealClient;
import com.example.foodplanner.model.Meal;
import com.example.foodplanner.model.MealResponse;
import com.example.foodplanner.presentation.home.view.HomeView;
import com.google.gson.Gson;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomePresenter {
    private HomeView view;
    private MealClient client;

    private static final String PREFS_NAME = "meal_of_day_prefs";
    private static final String KEY_MEAL_JSON = "cached_meal_json";
    private static final String KEY_CACHED_DATE = "cached_date";

    public HomePresenter(HomeView view) {
        this.view = view;
        this.client = MealClient.getInstance();
    }

    public void getHomeData(Context context) {
        view.showLoading();

        // 1. Meal of the Day — check cache first
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String todayDate = java.time.LocalDate.now().toString(); // e.g. "2026-02-26"
        String cachedDate = prefs.getString(KEY_CACHED_DATE, "");
        String cachedJson = prefs.getString(KEY_MEAL_JSON, "");

        if (todayDate.equals(cachedDate) && !cachedJson.isEmpty()) {
            // Use cached meal
            Meal cachedMeal = new Gson().fromJson(cachedJson, Meal.class);
            view.hideLoading();
            view.showRandomMeal(cachedMeal);
        } else {
            // Fetch new random meal and cache it
            client.getRandomMeal()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            response -> {
                                view.hideLoading();
                                if (response.getMeals() != null && !response.getMeals().isEmpty()) {
                                    Meal meal = response.getMeals().get(0);
                                    // Cache it
                                    prefs.edit()
                                            .putString(KEY_MEAL_JSON, new Gson().toJson(meal))
                                            .putString(KEY_CACHED_DATE, todayDate)
                                            .apply();
                                    view.showRandomMeal(meal);
                                }
                            },
                            error -> {
                                view.hideLoading();
                                view.showError(error.getMessage());
                            });
        }

        // 2. Get Categories
        client.getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> view.showCategories(response.getCategories()),
                        error -> view.showError(error.getMessage()));
    }
}