package com.appculinaryrecipes.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appculinaryrecipes.R;
import com.appculinaryrecipes.RecyclerViewAdapter;
import com.appculinaryrecipes.RecyclerViewAdapterShoppingLists;
import com.appculinaryrecipes.databinding.FragmentShoppingListsBinding;
import com.appculinaryrecipes.shoppinglist.ShoppingList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ShoppingListsFragment extends Fragment {
    private FragmentShoppingListsBinding fragmentShoppingListsBinding;
    private RecyclerViewAdapterShoppingLists recyclerViewAdapterShoppingLists;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    ArrayList<ShoppingList>shoppingLists = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentShoppingListsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_shopping_lists, container, false);
        View view = fragmentShoppingListsBinding.getRoot();
        firebaseAuth = FirebaseAuth.getInstance();
        fragmentShoppingListsBinding.shoppingListsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        fragmentShoppingListsBinding.shoppingListsRecyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapterShoppingLists = new RecyclerViewAdapterShoppingLists(this.getContext());
        fragmentShoppingListsBinding.shoppingListsRecyclerView.setAdapter(recyclerViewAdapterShoppingLists);

        leadData();

        return view;
    }

    private void leadData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ref = db.collection("shopping_lists");
        firebaseUser = firebaseAuth.getCurrentUser();

        Query query = ref.whereEqualTo("user", firebaseUser.getUid());

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error == null){
                    shoppingLists.clear();
                    for (QueryDocumentSnapshot document : value) {
                        ShoppingList shoppingList = new ShoppingList();
                        shoppingList.setMealName(document.getString("meal"));
                        String id = document.getId();
                        shoppingList.setShoppingListUid(id);
                        shoppingLists.add(shoppingList);
                    }
                    recyclerViewAdapterShoppingLists.setItems(shoppingLists);
                    recyclerViewAdapterShoppingLists.notifyDataSetChanged();
                }
            }
        });
    }
}
