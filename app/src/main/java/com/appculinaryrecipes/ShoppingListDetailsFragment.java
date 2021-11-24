package com.appculinaryrecipes;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appculinaryrecipes.databinding.FragmentShoppingListDetailsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ShoppingListDetailsFragment extends Fragment {

    private String id;

    FragmentShoppingListDetailsBinding fragmentShoppingListDetailsBinding;

    public ShoppingListDetailsFragment() {

    }

    public ShoppingListDetailsFragment(String id){
        this.id = id;
    }

    public static ShoppingListDetailsFragment newInstance() {
        return new ShoppingListDetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentShoppingListDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_shopping_list_details, container, false);
        View view = fragmentShoppingListDetailsBinding.getRoot();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentSnapshot = database.collection("recipes").document(id);
        documentSnapshot.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    ArrayList<String> ingredients = (ArrayList<String>) document.getData().get("indigrients");
                    ArrayList<String> measures = (ArrayList<String>) document.getData().get("measure");
                    ArrayList<Boolean> checked = new ArrayList<>();
                    int length = ingredients.size();
                        for(int i = 0; i < length; i++){
                            ImageView imageView = new ImageView(getContext());
                            Drawable drawable = AppCompatResources.getDrawable(getContext(), R.drawable.icon_delete);
                            drawable.setBounds(5, 5, 5, 5);
                            imageView.setImageDrawable(drawable);
                            imageView.setPadding(5, 5, 5, 5);

                            LinearLayout layout = new LinearLayout(getContext());
                            layout.setOrientation(LinearLayout.HORIZONTAL);
                            layout.setPadding(10, 10, 10, 10);

                            CheckBox c = new CheckBox(getContext());
                            c.setScaleX(1.6f);
                            c.setScaleY(1.6f);
                            c.setPadding(5, 5, 5, 5);

                            TextView t = new TextView(getContext());
                            t.setText(ingredients.get(i));
                            t.setTextSize(20.0f);
                            t.setPadding(5, 5, 5, 5);

                            TextView m = new TextView(getContext());
                            m.setText("[" + measures.get(i) + "]");
                            m.setTextSize(20.0f);
                            m.setPadding(5, 5, 5, 5);

                            layout.addView(imageView);
                            layout.addView(c);
                            layout.addView(t);
                            layout.addView(m);

                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    fragmentShoppingListDetailsBinding.constraintLayout.removeView(layout);
                                    fragmentShoppingListDetailsBinding.constraintLayout.invalidate();
                                    fragmentShoppingListDetailsBinding.constraintLayout.requestLayout();
                                }
                            });
                            fragmentShoppingListDetailsBinding.constraintLayout.addView(layout);
                        }
                    fragmentShoppingListDetailsBinding.constraintLayout.invalidate();
                    fragmentShoppingListDetailsBinding.constraintLayout.requestLayout();
                    container.invalidate();
                } else {
                    Log.d("Error", "Error getting documents: ", task.getException());
                }
            }
        });
        return view;
    }
}