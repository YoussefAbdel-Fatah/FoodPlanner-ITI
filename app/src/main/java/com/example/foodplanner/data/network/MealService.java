package com.example.foodplanner.data.network;

import com.example.foodplanner.model.CategoryResponse;
import com.example.foodplanner.model.MealResponse;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealService {
    // This tells Retrofit: "Go to /random.php and give me a Single response"
    @GET("random.php")
    Single<MealResponse> getRandomMeal();

    @GET("categories.php")
    Single<CategoryResponse> getCategories();

    @GET("search.php")
    Single<MealResponse> searchMeals(@Query("s") String query);

    @GET("lookup.php")
    Single<MealResponse> getMealById(@Query("i") String id);
}