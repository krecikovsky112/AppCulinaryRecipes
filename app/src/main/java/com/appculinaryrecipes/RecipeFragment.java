package com.appculinaryrecipes;

import android.content.Context;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.core.FirestoreClient;
import com.google.firestore.v1.WriteResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RecipeFragment extends Fragment {

    private FirebaseFirestore database;

    public RecipeFragment() {
    }

    public static RecipeFragment newInstance() {
        return new RecipeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);

        Button submitButton = view.findViewById(R.id.addRecipeSubmitButton);
        EditText editTextInstructions = view.findViewById(R.id.editTextInstructions);
        EditText editTextYoutube = view.findViewById(R.id.editTextYoutube);
        EditText editTextCategory = view.findViewById(R.id.editTextCategory);
        EditText editTextMeal = view.findViewById(R.id.editTextMeal);
        EditText editTextArea = view.findViewById(R.id.editTextArea);
        EditText editTextMealThumb = view.findViewById(R.id.editTextMealThumb);
        EditText editTextTags = view.findViewById(R.id.editTextTags);


        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.addIngredientsLayout);
        Context context = this.getContext();

        ArrayList<EditText> ingredientsArray = new ArrayList<>();
        ArrayList<EditText> measuresArray = new ArrayList<>();

        Button button = view.findViewById(R.id.addIngredientButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                EditText ingredientEditText = new EditText(context);
                ingredientEditText.setHint("ingredient");
                ingredientsArray.add(ingredientEditText);
                layout.addView(ingredientEditText);

                EditText measureEditText = new EditText(context);
                measureEditText.setHint("measure");
                measuresArray.add(measureEditText);
                layout.addView(measureEditText);

                ingredientEditText.setLayoutParams(new
                        LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                measureEditText.setLayoutParams(new
                        LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                linearLayout.addView(layout);
            }
        });

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                ArrayList<String> ingredients = new ArrayList<>();
                ArrayList<String> measures = new ArrayList<>();

                for(EditText editText : ingredientsArray){
                    ingredients.add(editText.getText().toString());
                }
                for(EditText editText : measuresArray){
                    measures.add(editText.getText().toString());
                }
                Map<String, Object> docData = new HashMap<>();
                docData.put("area", editTextArea.getText().toString());
                docData.put("category", editTextCategory.getText().toString());
                docData.put("instructions", editTextInstructions.getText().toString());
                docData.put("meal", editTextMeal.getText().toString());
                docData.put("youtube", editTextYoutube.getText().toString());
                docData.put("mealThumb", editTextMealThumb.getText().toString());
                docData.put("tags", editTextTags.getText().toString());
                docData.put("rating", "");
                docData.put("indigrients", Arrays.asList(ingredients.toArray()));
                docData.put("measures", Arrays.asList(measures.toArray()));

                database.collection("recipes").document(editTextMeal.getText().toString()).set(docData);
                Fragment fragment = new HomeFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentHomeContainer,
                                fragment).commit();
            }
        });

        return view;
    }
}