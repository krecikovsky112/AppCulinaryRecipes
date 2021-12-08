package com.appculinaryrecipes.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appculinaryrecipes.R;
import com.appculinaryrecipes.databinding.FragmentFridgeBinding;
import com.appculinaryrecipes.generated.callback.OnClickListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class FridgeFragment extends Fragment {

    FragmentFridgeBinding fragmentFridgeBinding;
    ArrayList<String> ingredients;
    Context context;
    boolean sortAsc = true;

    public FridgeFragment() {
    }

    public static FridgeFragment newInstance() {
        return new FridgeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentFridgeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_fridge, container, false);
        View view = fragmentFridgeBinding.getRoot();
        context = getContext();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference ingredientsRef = database.collection("indigriends");
        ingredientsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    ingredients = new ArrayList<>();
                    for(DocumentSnapshot document : task.getResult()){
                        ingredients.add(String.valueOf(document.getData().get("indegrient")));
                    }
                    displayIngredients(ingredients);
                }
            }
        });
        NiceSpinner niceSpinner = (NiceSpinner) view.findViewById(R.id.sortingSpinner);
        LinkedList<String> linkedList = new LinkedList<>();
        linkedList.add("A - Z");
        linkedList.add("Z - A");
        niceSpinner.attachDataSource(linkedList);
        niceSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                sortAsc = position == 0;
                sortList(ingredients);
                displayIngredients(ingredients);
            }
        });

        fragmentFridgeBinding.containsEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String search = fragmentFridgeBinding.containsEditText.getText().toString();
                    if (!search.isEmpty())
                        displayIngredients(filterList(ingredients, search));
                    else
                        displayIngredients(ingredients);

                    return true;
                }
                return false;
            }
        });


        fragmentFridgeBinding.generateRecipesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFoundRecipes();
            }
        });
        return view;
    }

    void sortList(ArrayList<String> toSort){
        if(sortAsc)
            Collections.sort(toSort);
        else
            Collections.sort(toSort, Collections.reverseOrder());
        sortAsc = !sortAsc;
    }

    ArrayList<String> filterList(ArrayList<String> toFilter, String search){
        Collection<String> collection
                = Collections2.filter(toFilter, Predicates.contains(Pattern.compile(search, Pattern.CASE_INSENSITIVE)));
        ArrayList<String> result = new ArrayList<>();
        result.addAll(collection);
        return result;
    }

    void displayIngredients(ArrayList<String> arrayList){
        fragmentFridgeBinding.fridgeView.removeAllViews();
        for(String ingredient : arrayList){
            fragmentFridgeBinding.fridgeView.addView(generateLayout(ingredient));
        }
        fragmentFridgeBinding.fridgeView.invalidate();
        fragmentFridgeBinding.fridgeView.requestLayout();
    }

    LinearLayout generateLayout(String ingredient){
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(10, 10, 10, 10);

        CheckBox c = new CheckBox(context);
        c.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.own_red)));
        c.setTag(ingredient);
        c.setScaleX(1.6f);
        c.setScaleY(1.6f);
        c.setPadding(5, 5, 5, 5);

        c.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    LinearLayout innerLayout = new LinearLayout(context);
                    innerLayout.setOrientation(LinearLayout.HORIZONTAL);
                    innerLayout.setPadding(10, 10, 10, 10);
                    innerLayout.setTag(ingredient);

                    CheckBox c = new CheckBox(context);
                    c.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.own_red)));
                    c.setTag(ingredient);
                    c.setScaleX(1.6f);
                    c.setScaleY(1.6f);
                    c.setPadding(5, 5, 5, 5);
                    c.setChecked(true);

                    c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            fragmentFridgeBinding.yourIngredients.removeView(innerLayout);
                            CheckBox checkBox = fragmentFridgeBinding.fridgeView.findViewWithTag(ingredient);
                            checkBox.setChecked(false);
                            fragmentFridgeBinding.yourIngredients.invalidate();
                            fragmentFridgeBinding.yourIngredients.requestLayout();
                        }
                    });

                    TextView t = new TextView(context);
                    t.setTextColor(context.getResources().getColor(R.color.own_red));
                    t.setText(ingredient);
                    t.setTextSize(20.0f);
                    t.setPadding(5, 5, 5, 5);

                    innerLayout.addView(c);
                    innerLayout.addView(t);

                    fragmentFridgeBinding.yourIngredients.addView(innerLayout);
                }
                else{
                    View view = fragmentFridgeBinding.yourIngredients.findViewWithTag(ingredient);
                    if(view != null){
                        fragmentFridgeBinding.yourIngredients.removeView(view);
                    }
                }
                fragmentFridgeBinding.yourIngredients.invalidate();
                fragmentFridgeBinding.yourIngredients.requestLayout();
            }
        });

        TextView t = new TextView(context);
        t.setText(ingredient);
        t.setTextSize(20.0f);
        t.setPadding(5, 5, 5, 5);

        layout.addView(c);
        layout.addView(t);

        return layout;
    }


    ArrayList<String> getCheckedIngredients(){
        ArrayList<String> result = new ArrayList<>();
        int size = fragmentFridgeBinding.yourIngredients.getChildCount();
        for(int i = 0; i < size; i++){
            result.add(String.valueOf(fragmentFridgeBinding.yourIngredients.getChildAt(i).getTag()));
        }
        return result;
    }

    void showFoundRecipes(){
        AppCompatActivity activity = (AppCompatActivity) context;
        FoundRecipesFragment myFragment= new FoundRecipesFragment(getCheckedIngredients());
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHomeContainer, myFragment).addToBackStack("FRIDGE_FRAGMENT").commit();
    }

}