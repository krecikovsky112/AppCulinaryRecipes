package com.appculinaryrecipes;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.appculinaryrecipes.databinding.FragmentRecipeBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
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
    private boolean flagButtonLike = false;

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

        getInfoRecipe();

        return view;
    }


    private void setIngriedientsInTextView() {
        setTitle("Ingredients");
        Typeface face = ResourcesCompat.getFont(getActivity(), R.font.dongle_regular);
        for (int i = 0; i < ingriedients.size(); i++) {
            String ingriedient = ingriedients.get(i);
            String measure = measures.get(i);
            TextView textView = new TextView(getActivity());
            textView.setTextSize(25);
            textView.setPadding(0, 0, 0, 0);
            textView.setTextColor(Color.parseColor("#ae0216"));
            textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            textView.setTypeface(face);
            textView.setText((i + 1) + ". " + ingriedient + "(" + measure + ")");
            fragmentRecipeBinding.container.addView(textView);
        }
    }

    private void getInfoRecipe() {
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
                String urlVideo = document.getString("youtube");
                assert urlVideo != null;
                setYoutubePlayer(urlVideo);
            } else {
                Log.w("ERROR", "get failed with " + task.getException());
            }
        });
    }

    private void setYoutubePlayer(String urlVideo) {
        setTitle("Video");

        YouTubePlayerView youTubePlayerView = new YouTubePlayerView(getActivity());
        youTubePlayerView.setPadding(0,0,0,60);
        youTubePlayerView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        youTubePlayerView.setLayoutParams(new FrameLayout.LayoutParams(1300,1000));
        fragmentRecipeBinding.container.addView(youTubePlayerView);

        String result = substringURLVideoToVideoId(urlVideo);
        getLifecycle().addObserver(youTubePlayerView);
        String finalResult = result;
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    youTubePlayer.cueVideo(finalResult,0);
                }
            });
    }

    @Nullable
    private String substringURLVideoToVideoId(String urlVideo) {
        assert urlVideo != null;
        char[] temp = urlVideo.toCharArray();
        String result = null;
        for (int i = temp.length - 1; i > 0; i--)
            if (temp[i] == '='){
                result = urlVideo.substring(i+1, temp.length);
            }
        return result;
    }

    private void setTitle(String name) {
        Typeface face = ResourcesCompat.getFont(getActivity(), R.font.dongle_regular);
        TextView textView = new TextView(getActivity());
        textView.setTextSize(40);
        textView.setPadding(0,50,0,0);
        textView.setTextColor(Color.parseColor("#ae0216"));
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTypeface(face);
        textView.setText(name);
        fragmentRecipeBinding.container.addView(textView);
    }

    private void setInstuctionTextView(String instruction) {
        setTitle("Instruction");
        Typeface face = ResourcesCompat.getFont(getActivity(), R.font.dongle_regular);
        TextView textView = new TextView(getActivity());
        textView.setTextSize(25);
        textView.setPadding(0, 0, 0, 0);
        textView.setTextColor(Color.parseColor("#ae0216"));
        textView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        textView.setTypeface(face);
        textView.setText(instruction);
        fragmentRecipeBinding.container.addView(textView);
    }

    //TODO: Trzeba dodać tutaj zapamiętywanie stanu czy dany przepis jest w ulubionych czy nie
    public void onClickLike(){
        if(!flagButtonLike)
        {
            fragmentRecipeBinding.favBtn.setBackgroundResource(R.drawable.ic_favorite_red_24);
            flagButtonLike = true;
        }
        else{
            fragmentRecipeBinding.favBtn.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);
            flagButtonLike = false;
        }
    }

    //TODO: Do zaimplementowanie przejście do generatora listy zakupów
    public void onClickGenerateList(){

    }
}







