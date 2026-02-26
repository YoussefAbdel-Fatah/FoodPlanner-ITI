package com.example.foodplanner.model;

public class Ingredient {
    private String name;
    private String measure;
    private String imageUrl;

    public Ingredient(String name, String measure) {
        this.name = name;
        this.measure = measure;
        this.imageUrl = "https://www.themealdb.com/images/ingredients/" + name + "-Small.png";
    }

    public String getName() { return name; }
    public String getMeasure() { return measure; }
    public String getImageUrl() { return imageUrl; }
}