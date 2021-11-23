package com.appculinaryrecipes;

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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.appculinaryrecipes.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Map;


public class HomeFragment extends Fragment {

    private static final int PAGE_ITEM_SIZE = 8;
    private static final String DB_RECIPE_DOCUMENT_NAME = "recipes";
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerViewAdapter recyclerViewAdapter;
    private boolean isLoading = false;
    private String key = null;
    private DocumentSnapshot lastResult = null;
    private FragmentHomeBinding fragmentHomeBinding;
    private Button button;

    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        lastResult = null;
        fragmentHomeBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_home, container, false);
        View view = fragmentHomeBinding.getRoot();
        recyclerView = fragmentHomeBinding.homeRecyclerView;
        swipeRefreshLayout = fragmentHomeBinding.homeSwipeRefreshLayout;
        button = fragmentHomeBinding.goToSearchFragmentButton;
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

        button.setOnClickListener(v -> {
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            SearchFragment myFragment= new SearchFragment();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHomeContainer, myFragment).addToBackStack("okj").commit();
        });

        return view;
    }

    private void leadData() {
        swipeRefreshLayout.setRefreshing(true);
        ArrayList<Recipe> a = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query;

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
                            String meal = (String) data.get("meal");
                            String mealThumb = (String) data.get("mealThumb");
                            String id = document.getId();
                            if ((meal != null) && (mealThumb != null)) {
                                Recipe recipe = new Recipe();
                                recipe.setMealThumb(mealThumb);
                                recipe.setMeal(meal);
                                recipe.setId(id);
                                recipe.setArea((String)data.get("area"));
                                recipe.setCategory((String)data.get("category"));
                                recipe.setRating((String)data.get("rating"));
                                a.add(recipe);
                            }
                        }

                        if (task.getResult().size() > 0) {
                            recyclerViewAdapter.setItems(a);
                            recyclerViewAdapter.notifyDataSetChanged();
                            lastResult = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        }

                        isLoading = false;
                        swipeRefreshLayout.setRefreshing(false);
                    } else {
                        Log.w("XD", "Error getting documents.", task.getException());
                    }
                });
    }
}