package com.appculinaryrecipes;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecipeViewHolder extends RecyclerView.ViewHolder {
    public ImageView recipeImageView;
    public TextView recipeTitle;
    public TextView areaCategory;
    public RatingBar rating;

    public RecipeViewHolder(@NonNull View itemView) {
        super(itemView);
        recipeImageView = itemView.findViewById(R.id.recipeImage);
        recipeTitle = itemView.findViewById(R.id.recipeTitle);
        rating = itemView.findViewById(R.id.rating);
        areaCategory = itemView.findViewById(R.id.areaCategory);
    }

}
