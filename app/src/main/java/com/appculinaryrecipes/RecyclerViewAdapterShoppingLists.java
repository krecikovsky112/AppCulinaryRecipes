package com.appculinaryrecipes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.appculinaryrecipes.fragments.RecipeFragment;
import com.appculinaryrecipes.fragments.ShoppingListsFragment;
import com.appculinaryrecipes.shoppinglist.ShoppingList;
import com.appculinaryrecipes.shoppinglist.ShoppingListDetailsFragment;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.protobuf.Enum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecyclerViewAdapterShoppingLists extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private ArrayList<ShoppingList> shoppingListArrayList = new ArrayList<>();
    private int counter = 1;

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

    private String getUser(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        return firebaseUser.getUid();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ShoppingListViewHolder shoppingListViewHolder = (ShoppingListViewHolder) holder;
        ShoppingList shoppingList = shoppingListArrayList.get(position);
        shoppingListViewHolder.shoppinglistNameTextView.setText(shoppingList.getMealName());
        shoppingListViewHolder.number.setText(counter + ".");


        shoppingListViewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("shopping_lists").document(shoppingList.getShoppingListUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        System.out.println("DocumentSnapshot successfully deleted!");
                        AppCompatActivity activity = (AppCompatActivity) v.getContext();

                        counter--;
                        Map<String,Object> user = new HashMap<>();
                        user.put("listsLeft",10 - counter);
                        db.collection("users").document(getUser()).update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(activity,"Values added!",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(activity,"Error!",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        ShoppingListsFragment myFragment = new ShoppingListsFragment();
                        activity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentHomeContainer,
                                        myFragment).commit();
                    }
                }).addOnFailureListener(e -> System.out.println("Error deleting document" + e));
            }
        });
        if(counter<shoppingListArrayList.size())
            counter++;

        holder.itemView.setOnClickListener(view -> {
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            ShoppingListDetailsFragment fragment = new ShoppingListDetailsFragment(shoppingList.getShoppingListUid());
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHomeContainer, fragment).addToBackStack("ok").commit();
        });
    }

    @Override
    public int getItemCount() {
        return shoppingListArrayList.size();
    }
}
