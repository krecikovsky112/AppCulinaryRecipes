package com.appculinaryrecipes;

import androidx.annotation.NonNull;

import java.util.List;

public class Recipe {
    private String id;
    private String rating;
    private String category;
    private String instructions;
    private String area;
    private String externalId;
    private String meal;
    private String mealThumb;
    private List<String> indigrients;
    private String youtube;
    private List<String> measure;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public String getMealThumb() {
        return mealThumb;
    }

    public void setMealThumb(String mealThumb) {
        this.mealThumb = mealThumb;
    }

    public List<String> getIndigrients() {
        return indigrients;
    }

    public void setIndigrients(List<String> indigrients) {
        this.indigrients = indigrients;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public List<String> getMeasure() {
        return measure;
    }

    public void setMeasure(List<String> measure) {
        this.measure = measure;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id='" + id + '\'' +
                ", rating='" + rating + '\'' +
                ", category='" + category + '\'' +
                ", instructions='" + instructions + '\'' +
                ", area='" + area + '\'' +
                ", externalId='" + externalId + '\'' +
                ", meal='" + meal + '\'' +
                ", mealThumb='" + mealThumb + '\'' +
                ", indigrients=" + indigrients +
                ", youtube='" + youtube + '\'' +
                ", measure=" + measure +
                '}';
    }
}
