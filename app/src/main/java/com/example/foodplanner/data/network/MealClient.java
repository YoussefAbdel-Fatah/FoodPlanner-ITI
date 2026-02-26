package com.example.foodplanner.data.network;

import com.example.foodplanner.model.CategoryResponse;
import com.example.foodplanner.model.MealResponse;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MealClient {
    // 1. The Base URL for TheMealDB API
    private static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";

    private static MealClient instance = null;
    private MealService mealService;

    // 2. Private Constructor (Singleton Pattern)
    private MealClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // Converts JSON to Java Objects
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create()) // Converts Call to RxJava Single
                .build();

        mealService = retrofit.create(MealService.class);
    }

    // 3. Get the Single Instance
    public static MealClient getInstance() {
        if (instance == null) {
            instance = new MealClient();
        }
        return instance;
    }

    // 4. The method to get the Random Meal
    // We subscribe on io() here to make the Presenter's job cleaner later
    public Single<MealResponse> getRandomMeal() {
        return mealService.getRandomMeal()
                .subscribeOn(Schedulers.io());
    }

    public Single<CategoryResponse> getCategories() {
        return mealService.getCategories()
                .subscribeOn(Schedulers.io());
    }

    public Single<MealResponse> searchMeals(String query) {
        return mealService.searchMeals(query)
                .subscribeOn(Schedulers.io());
    }

    public Single<MealResponse> getMealById(String id) {
        return mealService.getMealById(id)
                .subscribeOn(Schedulers.io());
    }
}