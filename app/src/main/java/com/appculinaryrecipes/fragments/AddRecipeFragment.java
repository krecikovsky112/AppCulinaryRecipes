package com.appculinaryrecipes.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.appculinaryrecipes.R;
import com.appculinaryrecipes.databinding.FragmentAddRecipeBinding;
import com.appculinaryrecipes.youtube.search.Response;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddRecipeFragment extends Fragment {

    private FragmentAddRecipeBinding fragmentAddRecipeBinding;

    private String search = null;

    private final int MAX_RESULTS = 20;
    private final String YOUTUBE_VIDEO_URL_BASE = "https://www.youtube.com/watch?v=";
    private final String YOUTUBE_RESOURCE_TYPE = "video";
    private final String YOUTUBE_DATA_API_RESOURCE_PROPERTY = "snippet";
    private final String YOUTUBE_DATA_API_BASE_URL = "https://www.googleapis.com/youtube/v3/search";
    private final String COLLECTION_PATH = "recipes";
    private final String UPLOAD_FIRESTORE_STORAGE_DIRECTORY = "images/";
    private final String API_KEYS_COLLECTION_NAME = "api_keys";
    private final String API_KEYS_DOCUMENT_NAME = "youtube_data_api";

    private String youtubeDataApiKey;

    public AddRecipeFragment() {
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
        fragmentAddRecipeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_recipe, container, false);
        View view = fragmentAddRecipeBinding.getRoot();

        Context context = this.getContext();

        fetchApiKey(API_KEYS_COLLECTION_NAME, API_KEYS_DOCUMENT_NAME);

        RequestQueue queue = Volley.newRequestQueue(context);

        com.android.volley.Response.Listener<String> listener =
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            fragmentAddRecipeBinding.youtubeVideosLayout.removeAllViews();
                            for (int i = 0; i < MAX_RESULTS; i++) {
                                Gson gson = new Gson();
                                Response youtubeSearchResponse = gson.fromJson(response, Response.class);
                                ImageView imageView = new ImageView(context);
                                TextView textView = new TextView(context);
                                Glide.with(view).load(youtubeSearchResponse.getItem(i).getSnippet().getThumbnails().getMedium().getUrl()).into(imageView);
                                textView.setText(youtubeSearchResponse.getItem(i).getSnippet().getTitle());
                                textView.setTextSize(20);
                                textView.setTextColor(Color.parseColor("#ae0216"));
                                final String videoURL = YOUTUBE_VIDEO_URL_BASE + youtubeSearchResponse.getItem(i).getId().getVideoId();
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        fragmentAddRecipeBinding.editTextYoutube.setText(videoURL);
                                    }
                                });
                                imageView.setPadding(10, 10, 10, 0);
                                fragmentAddRecipeBinding.youtubeVideosLayout.addView(imageView);
                                fragmentAddRecipeBinding.youtubeVideosLayout.addView(textView);
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, "Request not successfully completed!", Toast.LENGTH_SHORT);
                        }
                    }
                };

//        fragmentAddRecipeBinding.searchButton.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        search = fragmentAddRecipeBinding.editTextSearchOnYoutube.getText().toString();
//                        String requestURL = YOUTUBE_DATA_API_BASE_URL + "?part=" + YOUTUBE_DATA_API_RESOURCE_PROPERTY + "&maxResults=" + MAX_RESULTS + "&q=" + search + "&type=" + YOUTUBE_RESOURCE_TYPE + "&key=" + youtubeDataApiKey;
//                        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestURL, listener, null);
//                        queue.add(stringRequest);
//                    }
//                });
        fragmentAddRecipeBinding.editTextSearchOnYoutube.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEARCH){
                    search = fragmentAddRecipeBinding.editTextSearchOnYoutube.getText().toString();
                        String requestURL = YOUTUBE_DATA_API_BASE_URL + "?part=" + YOUTUBE_DATA_API_RESOURCE_PROPERTY + "&maxResults=" + MAX_RESULTS + "&q=" + search + "&type=" + YOUTUBE_RESOURCE_TYPE + "&key=" + youtubeDataApiKey;
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestURL, listener, null);
                        queue.add(stringRequest);
                    return true;
                }
                return false;
            }
        });

        fragmentAddRecipeBinding.loadImageButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                    }
                });

        ArrayList<EditText> ingredientsArray = new ArrayList<>();
        ArrayList<EditText> measuresArray = new ArrayList<>();

        fragmentAddRecipeBinding.addIngredientButton.setOnClickListener(
                new View.OnClickListener() {
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

                        fragmentAddRecipeBinding.addIngredientsLayout.addView(layout);
                    }
                });

        fragmentAddRecipeBinding.addRecipeSubmitButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {

                        ArrayList<String> ingredients = new ArrayList<>();
                        ArrayList<String> measures = new ArrayList<>();

                        for (EditText editText : ingredientsArray) {
                            ingredients.add(editText.getText().toString());
                        }
                        for (EditText editText : measuresArray) {
                            measures.add(editText.getText().toString());
                        }

                        addRecipe(
                                context,
                                fragmentAddRecipeBinding.editTextMeal.getText().toString(),
                                fragmentAddRecipeBinding.editTextArea.getText().toString(),
                                fragmentAddRecipeBinding.editTextCategory.getText().toString(),
                                ingredients,
                                measures,
                                fragmentAddRecipeBinding.editTextInstructions.getText().toString(),
                                fragmentAddRecipeBinding.editTextYoutube.getText().toString(),
                                fragmentAddRecipeBinding.editTextMealThumb.getText().toString(),
                                ""
                        );
                    }
                });

        return view;
    }

    private void addRecipe(Context context, String meal, String area, String category, ArrayList<String> ingredients, ArrayList<String> measures, String instructions, String youtube, String mealThumb, String rating) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(COLLECTION_PATH).document();
        String storagePath = UPLOAD_FIRESTORE_STORAGE_DIRECTORY + documentReference.getId();
        try {
            Uri uri = Uri.parse(mealThumb);
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference();
            StorageReference riversRef = storageReference.child(storagePath);
            UploadTask uploadTask = riversRef.putFile(uri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(context, "Successful upload!", Toast.LENGTH_SHORT);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(context, "Upload failed!", Toast.LENGTH_SHORT);
                }
            });
        } catch (Exception e) {
            Toast.makeText(context, "Image not found!", Toast.LENGTH_SHORT);
        }

        Map<String, Object> docData = new HashMap<>();
        docData.put("area", area);
        docData.put("category", category);
        docData.put("indigrients", ingredients);
        docData.put("instructions", instructions);
        docData.put("meal", meal);
        docData.put("mealThumb", storagePath);
        docData.put("measure", measures);
        docData.put("rating", rating);
        docData.put("youtube", youtube);

        documentReference.set(docData);

        Fragment fragment = new HomeFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentHomeContainer,
                        fragment).commit();
    }

    public void fetchApiKey(String collectionName, String documentName) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(collectionName).document(documentName);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                youtubeDataApiKey = snapshot.getString("key");
            } else {
                Toast.makeText(this.getContext(), "Youtube connection failed!", Toast.LENGTH_SHORT);
            }
        });
    }
}
