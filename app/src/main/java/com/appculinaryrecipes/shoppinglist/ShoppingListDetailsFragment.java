package com.appculinaryrecipes.shoppinglist;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appculinaryrecipes.R;
import com.appculinaryrecipes.databinding.FragmentShoppingListDetailsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ShoppingListDetailsFragment extends Fragment {

    private String recipeId;
    private String userId;

    private ShoppingList shoppingList;

    FragmentShoppingListDetailsBinding fragmentShoppingListDetailsBinding;

    public ShoppingListDetailsFragment() {

    }

    public ShoppingListDetailsFragment(String recipeId, String userId){
        this.recipeId = recipeId;
        this.userId = userId;
    }

    public static ShoppingListDetailsFragment newInstance() {
        return new ShoppingListDetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentShoppingListDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_shopping_list_details, container, false);
        View view = fragmentShoppingListDetailsBinding.getRoot();
        shoppingList = new ShoppingList(fragmentShoppingListDetailsBinding, getContext());
        shoppingList.newInstance(recipeId, userId);
        return view;
    }

}