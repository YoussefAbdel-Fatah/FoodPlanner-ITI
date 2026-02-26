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
    public String strCategory;
    public String strInstructions;
    public String strYoutube;

    public MealEntity(@NonNull String idMeal, String strMeal, String strMealThumb) {
        this.idMeal = idMeal;
        this.strMeal = strMeal;
        this.strMealThumb = strMealThumb;
    }
}