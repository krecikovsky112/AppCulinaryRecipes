package com.appculinaryrecipes;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.appculinaryrecipes.databinding.ItemHomeRecipeBinding;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Recipe> recipeArrayList =  new ArrayList<>();

    public RecyclerViewAdapter(Context context) {
        this.context = context;
    }

    public void setItems(ArrayList<Recipe> arrayList){
        this.recipeArrayList.addAll(arrayList);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_recipe , parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecipeViewHolder recipeViewHolder = (RecipeViewHolder) holder;
        Recipe recipe = recipeArrayList.get(position);
        recipeViewHolder.recipeTitle.setText(recipe.getTitle());
        Glide.with(context).load(recipe.getImageURL()).into(recipeViewHolder.recipeImageView);

        holder.itemView.setOnClickListener(view -> {
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            RecipeFragment myFragment= RecipeFragment.newInstance(recipe.getTitle(),recipe.getImageURL());
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHomeContainer, myFragment).addToBackStack("okj").commit();
        });
    }

    @Override
    public int getItemCount() {
        return recipeArrayList.size();
    }
}
