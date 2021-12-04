package com.appculinaryrecipes.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ListAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.appculinaryrecipes.R;
import com.appculinaryrecipes.databinding.FragmentAddRecipeBinding;
import com.appculinaryrecipes.youtube.search.Response;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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

    private LinkedList<String> ingredients;

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

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference ingredientsRef = database.collection("indigriends");
        ingredientsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    ingredients = new LinkedList<>();
                    for(DocumentSnapshot document : task.getResult()){
                        ingredients.add(String.valueOf(document.getData().get("indegrient")));
                    }
                    NiceSpinner niceSpinner = (NiceSpinner) view.findViewById(R.id.ingredientsSpinner);
                    List<String> sortedListIngredients = ingredients;
                    Collections.sort(sortedListIngredients);
                    niceSpinner.attachDataSource(sortedListIngredients);
                    niceSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
                        @Override
                        public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                        }
                    });
                }
            }
        });

        NiceSpinner niceSpinner1 = (NiceSpinner) view.findViewById(R.id.categorySpinner);
        List<String> sortedListCategories = SearchFragment.categories;
        Collections.sort(sortedListCategories);
        niceSpinner1.attachDataSource(sortedListCategories);
        niceSpinner1.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
            }
        });

        NiceSpinner niceSpinner2 = (NiceSpinner) view.findViewById(R.id.areaSpinner);
        List<String> sortedListAreas = SearchFragment.areas;
        Collections.sort(sortedListAreas);
        niceSpinner2.attachDataSource(sortedListAreas);
        niceSpinner2.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
            }
        });

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

        fragmentAddRecipeBinding.addIngredientButton1.setOnClickListener(
                v -> {
                    Typeface face = ResourcesCompat.getFont(getActivity(), R.font.dongle_regular);
                    LinearLayout layout = new LinearLayout(context);
                    layout.setPadding(0,20,0,0);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    EditText ingredientEditText = new EditText(context);
                    ingredientEditText.setHint("ingredient");
                    ingredientEditText.setText(fragmentAddRecipeBinding.ingredientsSpinner.getText());
                    setAttributesEditText(face,ingredientEditText);
                    ingredientEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                    ingredientsArray.add(ingredientEditText);

                    EditText measureEditText = new EditText(context);
                    measureEditText.setHint("measure");
                    setAttributesEditText(face, measureEditText);
                    measureEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    measuresArray.add(measureEditText);
                    ingredientEditText.setLayoutParams(new
                            LinearLayout.LayoutParams(800,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

                    measureEditText.setPadding(10, 10, 10, 10);
                    ingredientEditText.setPadding(10, 10, 10 ,10);

                    measureEditText.setLayoutParams(new
                            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

                    layout.addView(ingredientEditText);
                    layout.addView(measureEditText);

                    fragmentAddRecipeBinding.addIngredientsLayout.addView(layout);
                });

        fragmentAddRecipeBinding.addIngredientButton2.setOnClickListener(
                v -> {
                    Typeface face = ResourcesCompat.getFont(getActivity(), R.font.dongle_regular);
                    LinearLayout layout = new LinearLayout(context);
                    layout.setPadding(0,20,0,0);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    EditText ingredientEditText = new EditText(context);
                    ingredientEditText.setHint("ingredient");
                    setAttributesEditText(face,ingredientEditText);
                    ingredientEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                    ingredientsArray.add(ingredientEditText);

                    EditText measureEditText = new EditText(context);
                    measureEditText.setHint("measure");
                    setAttributesEditText(face, measureEditText);
                    measureEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    measuresArray.add(measureEditText);
                    ingredientEditText.setLayoutParams(new
                            LinearLayout.LayoutParams(800,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

                    measureEditText.setPadding(10, 10, 10, 10);
                    ingredientEditText.setPadding(10, 10, 10 ,10);

                    measureEditText.setLayoutParams(new
                            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

                    layout.addView(ingredientEditText);
                    layout.addView(measureEditText);

                    fragmentAddRecipeBinding.addIngredientsLayout.addView(layout);
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
                                fragmentAddRecipeBinding.areaSpinner.getText().toString(),
                                fragmentAddRecipeBinding.categorySpinner.getText().toString(),
                                ingredients,
                                measures,
                                fragmentAddRecipeBinding.editTextInstructions.getText().toString(),
                                fragmentAddRecipeBinding.editTextYoutube.getText().toString(),
                                fragmentAddRecipeBinding.editTextMealThumb.getText().toString(),
                                "5.0"
                        );
                    }
                });

        return view;
    }

    private void setAttributesEditText(Typeface face, EditText editText) {
        editText.setBackgroundResource(R.drawable.background_field);
        editText.setClickable(true);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setHintTextColor(getResources().getColor(R.color.own_red));
        editText.setTextColor(getResources().getColor(R.color.own_red));
        editText.setTypeface(face);

        editText.setPadding(80,0,0,0);
        editText.setTextSize(25);
    }

    private void addRecipe(Context context, String meal, String area, String category, ArrayList<String> ingredients, ArrayList<String> measures, String instructions, String youtube, String mealThumb, String rating) {
        if(meal.isEmpty() || area.isEmpty() || category.isEmpty() || ingredients.isEmpty() || measures.isEmpty() || instructions.isEmpty() || youtube.isEmpty() || mealThumb.isEmpty() || rating.isEmpty())
            return;
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
                    Toast.makeText(context, "Failed upload!", Toast.LENGTH_SHORT);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(context, "Successful upload!", Toast.LENGTH_SHORT);
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
                }
            });
        } catch (Exception e) {
            Toast.makeText(context, "Image not found!", Toast.LENGTH_SHORT);
        }
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