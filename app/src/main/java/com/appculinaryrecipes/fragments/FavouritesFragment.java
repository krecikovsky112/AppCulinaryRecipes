package com.appculinaryrecipes.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.appculinaryrecipes.R;
import com.appculinaryrecipes.Recipe;
import com.appculinaryrecipes.RecyclerViewAdapter;
import com.appculinaryrecipes.databinding.FragmentFavouritesBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.appculinaryrecipes.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


public class FavouritesFragment extends Fragment {

    private static final int PAGE_ITEM_SIZE = 8;
    private static final String DB_RECIPE_DOCUMENT_NAME = "recipes";
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerViewAdapter recyclerViewAdapter;
    private boolean isLoading = false;
    private String key = null;
    private DocumentSnapshot lastResult = null;
    private FragmentFavouritesBinding fragmentFavouritesBinding;
    private ArrayList<String> favourites = new ArrayList<>();

    public FavouritesFragment() {
    }

    public static FavouritesFragment newInstance(String param1, String param2) {
        return new FavouritesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        lastResult = null;
        fragmentFavouritesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourites, container, false);
        View view = fragmentFavouritesBinding.getRoot();
        recyclerView = fragmentFavouritesBinding.favouritesRecyclerView;
        swipeRefreshLayout = fragmentFavouritesBinding.favouritesSwipeRefreshLayout;
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerViewAdapter = new RecyclerViewAdapter(this.getContext());
        recyclerView.setAdapter(recyclerViewAdapter);
        leadData();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int itemCount = layoutManager.getItemCount();
                int lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                if (itemCount < lastCompletelyVisibleItemPosition + 3) {
                    if (!isLoading) {
                        isLoading = true;
                        leadData();
                    }
                }

            }
        });


        return view;
    }

    private void leadData() {
        swipeRefreshLayout.setRefreshing(true);
        String userUID = getUser();
        ArrayList<Recipe> recipeArrayList = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("favourites").document(userUID);
        Query query;

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        favourites = (ArrayList<String>) doc.get("favourites");
                    }
                }
            }
        });

        if (lastResult == null) {
            query = db
                    .collection(DB_RECIPE_DOCUMENT_NAME)
                    .limit(PAGE_ITEM_SIZE);
        } else {
            query = db
                    .collection(DB_RECIPE_DOCUMENT_NAME)
                    .startAfter(lastResult)
                    .limit(PAGE_ITEM_SIZE);
        }


        query
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            String id = document.getId();
                            if (favourites.contains(id)) {
                                String meal = (String) data.get("meal");
                                String mealThumb = (String) data.get("mealThumb");

                                if ((meal != null) && (mealThumb != null)) {
                                    Recipe recipe = new Recipe();
                                    recipe.setMealThumb(mealThumb);
                                    recipe.setMeal(meal);
                                    recipe.setId(id);
                                    recipe.setArea((String) data.get("area"));
                                    recipe.setCategory((String) data.get("category"));
                                    recipe.setRating((String) data.get("rating"));
                                    recipeArrayList.add(recipe);
                                }
                            }

                        }

                        if (favourites.size() > 0) {
                            recyclerViewAdapter.setItems(recipeArrayList);
                            recyclerViewAdapter.notifyDataSetChanged();
                            lastResult = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        }
                        isLoading = false;
                        swipeRefreshLayout.setRefreshing(false);
                    } else {
                        Log.w("EXCEPTION", "Error getting documents.", task.getException());
                    }
                });
    }

    private String getUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        return firebaseUser.getUid();
    }
}