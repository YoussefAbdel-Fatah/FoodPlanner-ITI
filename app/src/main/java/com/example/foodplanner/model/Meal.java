package com.example.foodplanner.model;

import com.google.gson.annotations.SerializedName;

public class Meal {
    // We use @SerializedName to map the weird JSON keys (like "idMeal") to clean Java variables.

    @SerializedName("idMeal")
    private String id;

    @SerializedName("strMeal")
    private String name;

    @SerializedName("strCategory")
    private String category;

    @SerializedName("strArea")
    private String area;

    @SerializedName("strInstructions")
    private String instructions;

    @SerializedName("strMealThumb")
    private String imageUrl;

    @SerializedName("strYoutube")
    private String youtubeUrl;

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getArea() { return area; }
    public String getInstructions() { return instructions; }
    public String getImageUrl() { return imageUrl; }
    public String getYoutubeUrl() { return youtubeUrl; }
}
