package com.example.foodplanner.data.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "meals_table")
public class MealEntity {
    @PrimaryKey
    @NonNull
    public String idMeal;
    public String strMeal;
    public String strMealThumb;
    public String strArea;
    public String strInstructions;
    public String strYoutube;
    // You can add more fields if you want to store ingredients offline

    public MealEntity(@NonNull String idMeal, String strMeal, String strMealThumb) {
        this.idMeal = idMeal;
        this.strMeal = strMeal;
        this.strMealThumb = strMealThumb;
    }
}