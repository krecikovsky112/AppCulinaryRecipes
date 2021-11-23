package com.appculinaryrecipes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.appculinaryrecipes.databinding.FragmentSearchBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.gson.Gson;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding fragmentSearchBinding;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerViewAdapter recyclerViewAdapter;
    private String selectedArea;
    private String selectedCategory;
    private FirebaseFunctions firebaseFunctions;


    public SearchFragment() {
    }

    public static SearchFragment newInstance(String param1, String param2) {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        firebaseFunctions = FirebaseFunctions.getInstance();
        fragmentSearchBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);
        View view = fragmentSearchBinding.getRoot();

        recyclerView = fragmentSearchBinding.searchRecyclerView;
        swipeRefreshLayout = fragmentSearchBinding.searchSwipeRefreshLayout;
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerViewAdapter = new RecyclerViewAdapter(this.getContext());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int itemCount = layoutManager.getItemCount();
                int lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();

            }
        });

        NiceSpinner areaSpinner = (NiceSpinner) fragmentSearchBinding.areaSpinner;
        List<String> categories = new LinkedList<>(Arrays.asList("Beef", "Breakfast", "Chicken", "Dessert", "Goat", "Lamb",
                "Miscellaneous", "Pasta", "Pork", "Seafood", "Side", "Starter", "Vegan", "Vegetarian"));
        List<String> areas = new LinkedList<>(Arrays.asList("American", "British", "Canadian", "Chinese", "Croatian",
                "Dutch", "Egyptian", "French", "Greek", "Indian", "Irish", "Italian", "Jamaican", "Japanese", "Kenyan",
                "Malaysian", "Mexican", "Moroccan", "Polish", "Portuguese", "Russian", "Spanish", "Thai", "Tunisian", "Turkish", "Unknown", "Vietnamese"));

        selectedCategory = categories.get(0);
        selectedArea = areas.get(0);

        areaSpinner.attachDataSource(areas);
        areaSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                selectedArea = areas.get(position);
            }
        });


        NiceSpinner categorySpinner = (NiceSpinner) fragmentSearchBinding.categorySpinner;
        categorySpinner.attachDataSource(categories);
        categorySpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                selectedCategory = categories.get(position);
            }
        });

        Button getSelectedRecipe = fragmentSearchBinding.buttonSearch;

        getSelectedRecipe.setOnClickListener(v -> {
            getRecipeByAreaAndCategory(selectedArea, selectedCategory)
                    .addOnCompleteListener(new OnCompleteListener<String>() {
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

                                ArrayList<Recipe> a = new ArrayList<>();
                                for (Recipe recipeItem : recipesArray) {

                                    Recipe recipe = new Recipe();
                                    recipe.setMealThumb(recipeItem.getMealThumb());
                                    recipe.setMeal(recipeItem.getMeal());
                                    recipe.setId(recipeItem.getId());
                                    recipe.setCategory(recipeItem.getCategory());
                                    recipe.setRating(recipeItem.getRating());
                                    recipe.setArea(recipeItem.getArea());
                                    a.add(recipe);
                                }

                                recyclerViewAdapter.setItems(a);
                                recyclerViewAdapter.notifyDataSetChanged();
                                swipeRefreshLayout.setRefreshing(false);

                            }
                        }
                    });
        });

        return view;
    }


    private Task<String> getRecipeByAreaAndCategory(String area, String category) {
        Map<String, Object> data = new HashMap<>();
        data.put("area", area);
        data.put("category", category);
        data.put("push", true);
        return firebaseFunctions
                .getHttpsCallable("getRecipeByAreaAndCategory")
                .call(data)
                .continueWith(task -> (String) task.getResult().getData());
    }
}