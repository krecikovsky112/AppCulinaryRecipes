package com.appculinaryrecipes;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShoppingListViewHolder extends RecyclerView.ViewHolder  {
    public TextView shoppinglistNameTextView;

    public ShoppingListViewHolder(@NonNull View itemView) {
        super(itemView);
        shoppinglistNameTextView = itemView.findViewById(R.id.name_shopping_list);
    }
}
