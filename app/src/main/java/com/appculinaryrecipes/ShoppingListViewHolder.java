package com.appculinaryrecipes;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShoppingListViewHolder extends RecyclerView.ViewHolder  {
    public TextView shoppinglistNameTextView;
    public TextView number;
    public Button button;

    public ShoppingListViewHolder(@NonNull View itemView) {
        super(itemView);
        shoppinglistNameTextView = itemView.findViewById(R.id.name_shopping_list);
        number = itemView.findViewById(R.id.number);
        button = itemView.findViewById(R.id.delete);
    }
}
