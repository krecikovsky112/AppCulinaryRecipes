package com.appculinaryrecipes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appculinaryrecipes.databinding.FragmentRecipeBinding;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;

public class RecipeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "name";
    private static final String ARG_PARAM2 = "image";

    // TODO: Rename and change types of parameters
    private String title;
    private String imageURL;

    private FragmentRecipeBinding fragmentRecipeBinding;

    public RecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecipeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeFragment newInstance(String param1, String param2) {
        RecipeFragment fragment = new RecipeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_PARAM1);
            imageURL = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentRecipeBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_recipe,container,false);
        fragmentRecipeBinding.setCallback(this);
        View view = fragmentRecipeBinding.getRoot();

        fragmentRecipeBinding.titleRecipe.setText(title);
        Picasso.get().load(imageURL).into(fragmentRecipeBinding.imageRecipe);

        return view;
    }
}