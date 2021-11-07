package com.appculinaryrecipes;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.core.FirestoreClient;
import com.google.firestore.v1.WriteResult;

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

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Map<String, Object> docData = new HashMap<>();
                docData.put("area", editTextArea.getText());
                docData.put("category", editTextCategory.getText());
                docData.put("instructions", editTextInstructions.getText());
                docData.put("meal", editTextMeal.getText());
                docData.put("youtube", editTextYoutube.getText());
                database.collection("recipes").document("test").set(docData);
                Fragment fragment = new HomeFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentHomeContainer,
                                fragment).commit();
            }
        });

        return view;
    }
}