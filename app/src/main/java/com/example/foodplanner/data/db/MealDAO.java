package com.example.foodplanner.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface MealDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertMeal(MealEntity meal);

    @Delete
    Completable deleteMeal(MealEntity meal);

    @Query("SELECT * FROM meals_table")
    Flowable<List<MealEntity>> getAllMeals();

    @Query("SELECT EXISTS(SELECT 1 FROM meals_table WHERE idMeal = :id LIMIT 1)")
    Flowable<Boolean> isFavorite(String id);
}