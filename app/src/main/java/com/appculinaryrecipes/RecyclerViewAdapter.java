package com.appculinaryrecipes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.appculinaryrecipes.fragments.RecipeFragment;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int NUMBER_STARS = 6;

    private Context context;
    private ArrayList<Recipe> recipeArrayList = new ArrayList<>();
    public ArrayList<Recipe> getRecipeArrayList() {
        return recipeArrayList;
    }
    public RecyclerViewAdapter(Context context) {
        this.context = context;
    }
    public void setItems(ArrayList<Recipe> arrayList) {
        this.recipeArrayList.addAll(arrayList);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecipeViewHolder recipeViewHolder = (RecipeViewHolder) holder;
        Recipe recipe = recipeArrayList.get(position);
        recipeViewHolder.recipeTitle.setText(recipe.getMeal());
        Glide.with(context).load(recipe.getMealThumb()).into(recipeViewHolder.recipeImageView);
        recipeViewHolder.rating.setNumStars(NUMBER_STARS);
        recipeViewHolder.rating.setRating(Float.parseFloat(recipe.getRating()));
        recipeViewHolder.areaCategory.setText("Area: " + recipe.getArea() + "   Category: " + recipe.getCategory());

        holder.itemView.setOnClickListener(view -> {
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            RecipeFragment myFragment = RecipeFragment.newInstance(recipe.getMeal(), recipe.getMealThumb(), recipe.getId());
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHomeContainer, myFragment).addToBackStack("HOME_FRAGMENT").commit();

        });
    }

    @Override
    public int getItemCount() {
        return recipeArrayList.size();
    }
}
