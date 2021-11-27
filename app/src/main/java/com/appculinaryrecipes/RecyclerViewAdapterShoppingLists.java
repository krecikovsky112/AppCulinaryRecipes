package com.appculinaryrecipes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.appculinaryrecipes.fragments.RecipeFragment;
import com.appculinaryrecipes.shoppinglist.ShoppingList;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecyclerViewAdapterShoppingLists extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private ArrayList<ShoppingList> shoppingListArrayList = new ArrayList<>();

    public RecyclerViewAdapterShoppingLists(Context context) {
        this.context = context;
    }

    public void setItems(ArrayList<ShoppingList> arrayList){
        this.shoppingListArrayList.clear();
        this.shoppingListArrayList.addAll(arrayList);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shopping_list, parent, false);
        return new ShoppingListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ShoppingListViewHolder shoppingListViewHolder = (ShoppingListViewHolder) holder;
        ShoppingList shoppingList = shoppingListArrayList.get(position);
        shoppingListViewHolder.shoppinglistNameTextView.setText(shoppingList.getMealName());
//        Glide.with(context).load(recipe.getMealThumb()).into(shoppingListViewHolder.recipeImageView);
//        shoppingListViewHolder.rating.setNumStars(NUMBER_STARS);
//        shoppingListViewHolder.rating.setRating(Float.parseFloat(recipe.getRating()));
//        shoppingListViewHolder.areaCategory.setText("Area: " + recipe.getArea() + "   Category: " + recipe.getCategory());

        holder.itemView.setOnClickListener(view -> {
//            AppCompatActivity activity = (AppCompatActivity) view.getContext();
//            RecipeFragment myFragment = RecipeFragment.newInstance(recipe.getMeal(), recipe.getMealThumb(), recipe.getId());
//            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHomeContainer, myFragment).addToBackStack("HOME_FRAGMENT").commit();

        });
    }

    @Override
    public int getItemCount() {
        return shoppingListArrayList.size();
    }
}
