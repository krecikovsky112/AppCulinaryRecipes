package com.appculinaryrecipes;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appculinaryrecipes.databinding.FragmentRecipeBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecipeFragment extends Fragment {

    private static final String ARG_PARAM1 = "name";
    private static final String ARG_PARAM2 = "image";
    private static final String ARG_PARAM3 = "id";
    private static final String DB_RECIPE_DOCUMENT_NAME = "recipes";
    private String title;
    private String imageURL;
    private String id;
    private FragmentRecipeBinding fragmentRecipeBinding;
    private ArrayList<String> ingriedients;
    private ArrayList<String> measures;

    public RecipeFragment() {
        // Required empty public constructor
    }

    public static RecipeFragment newInstance(String param1, String param2, String param3) {
        RecipeFragment fragment = new RecipeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_PARAM1);
            imageURL = getArguments().getString(ARG_PARAM2);
            id = getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentRecipeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe, container, false);
        fragmentRecipeBinding.setCallback(this);
        View view = fragmentRecipeBinding.getRoot();

        fragmentRecipeBinding.titleRecipe.setText(title);
        Picasso.get().load(imageURL).into(fragmentRecipeBinding.imageRecipe);

        getIngriedients();

        return view;
    }


    private void setIngriedientsInTextView() {
        TextView title = new TextView(getActivity());
        title.setTextSize(40);
        title.setTextColor(Color.parseColor("#ae0216"));
        title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        Typeface face = ResourcesCompat.getFont(getActivity(), R.font.dongle_regular);
        title.setTypeface(face);
        title.setText("Ingriedients");
        fragmentRecipeBinding.container.addView(title);
        for (int i = 0; i < ingriedients.size(); i++) {
            String ingriedient = ingriedients.get(i);
            String measure = measures.get(i);
            TextView textView = new TextView(getActivity());
            textView.setTextSize(25);
            textView.setPadding(100, 0, 0, 0);
            textView.setTextColor(Color.parseColor("#ae0216"));
            textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            textView.setTypeface(face);
            textView.setText((i + 1) + ". " + ingriedient + "(" + measure + ")");
            fragmentRecipeBinding.container.addView(textView);
        }
    }

    private void getIngriedients() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db
                .collection(DB_RECIPE_DOCUMENT_NAME).document(id);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                ingriedients = (ArrayList<String>) document.get("indigrients");
                measures = (ArrayList<String>) document.get("measure");
                setIngriedientsInTextView();
                String instruction = document.getString("instructions");
                setInstuctionTextView(instruction);

                //TODO: Do poprawy załączanie filmu z youtube, pokazuje "No Network Security Config specified"
//                String urlVideo = document.getString("youtube");
//                assert urlVideo != null;
//                char[] temp = urlVideo.toCharArray();
//                String result = null;
//                for (int i = temp.length - 1; i > 0; i--)
//                    if (temp[i] == '='){
//                        result = urlVideo.substring(i, temp.length);
//                    }
//                    getLifecycle().addObserver(fragmentRecipeBinding.videoView);
//                String finalResult = result;
//                fragmentRecipeBinding.videoView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
//                        @Override
//                        public void onReady(@NonNull YouTubePlayer youTubePlayer) {
//                            youTubePlayer.loadVideo(finalResult,0);
//                        }
//                    });
            } else {
                System.out.println("get failed with " + task.getException());
            }
        });
    }

    private void setInstuctionTextView(String instruction) {
        Typeface face = ResourcesCompat.getFont(getActivity(), R.font.dongle_regular);
        TextView textView = new TextView(getActivity());
        textView.setTextSize(40);
        textView.setTextColor(Color.parseColor("#ae0216"));
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTypeface(face);
        textView.setText("Instruction");
        fragmentRecipeBinding.container.addView(textView);

        textView = new TextView(getActivity());
        textView.setTextSize(25);
        textView.setPadding(100, 0, 100, 0);
        textView.setTextColor(Color.parseColor("#ae0216"));
        textView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        textView.setTypeface(face);
        textView.setText(instruction);
//        textView.setBackgroundResource(R.drawable.background_instruction);
        fragmentRecipeBinding.container.addView(textView);
    }
}