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
import com.appculinaryrecipes.databinding.FragmentFoundRecipesBinding;
import com.appculinaryrecipes.functions.Functions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.appculinaryrecipes.databinding.FragmentHomeBinding;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;


public class FoundRecipesFragment extends Fragment {

    private static final int PAGE_ITEM_SIZE = 8;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerViewAdapter recyclerViewAdapter;
    private boolean isLoading = false;
    private DocumentSnapshot lastResult = null;
    private FragmentFoundRecipesBinding fragmentFoundRecipesBinding;
    private ArrayList<String> checkedIngredients;


    public FoundRecipesFragment(ArrayList<String> checkedIngredients){
        this.checkedIngredients = checkedIngredients;
    }

    public FoundRecipesFragment() {
    }

    public static FoundRecipesFragment newInstance(String param1, String param2) {
        return new FoundRecipesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        lastResult = null;
        fragmentFoundRecipesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_found_recipes, container, false);
        View view = fragmentFoundRecipesBinding.getRoot();
        recyclerView = fragmentFoundRecipesBinding.homeRecyclerView;
        swipeRefreshLayout = fragmentFoundRecipesBinding.homeSwipeRefreshLayout;
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
        Functions.getRecipesByIngredients(checkedIngredients).addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Exception e = task.getException();
                    e.printStackTrace();
                    if (e instanceof FirebaseFunctionsException) {
                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                        FirebaseFunctionsException.Code code = ffe.getCode();
                        Object details = ffe.getDetails();
                    }
                } else {
                    Gson gson = new Gson();
                    Recipe[] recipesArray = gson.fromJson(task.getResult(), Recipe[].class);
                    swipeRefreshLayout.setRefreshing(true);

                    if (recyclerViewAdapter.getRecipeArrayList().size() > 0) {
                        recyclerViewAdapter.getRecipeArrayList().clear();
                    }

                    ArrayList<Recipe> recipeArrayList = new ArrayList<>();
                    for (Recipe recipeItem : recipesArray) {

                        Recipe recipe = new Recipe();
                        recipe.setMealThumb(recipeItem.getMealThumb());
                        recipe.setMeal(recipeItem.getMeal());
                        recipe.setId(recipeItem.getId());
                        recipe.setCategory(recipeItem.getCategory());
                        recipe.setRating(recipeItem.getRating());
                        recipe.setArea(recipeItem.getArea());
                        recipeArrayList.add(recipe);
                    }

                    recyclerViewAdapter.setItems(recipeArrayList);
                    recyclerViewAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);

                }
            }
        });
    }
}