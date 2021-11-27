package com.appculinaryrecipes.shoppinglist;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import com.appculinaryrecipes.R;
import com.appculinaryrecipes.databinding.FragmentShoppingListDetailsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShoppingList {

    private final static int LIST_DELETE = 1;
    private final static int LIST_CREATE = -1;
    private final static int INGREDIENTS_UPDATE = 0;

    private String shoppingListUid;

    protected ArrayList<String> ingredients;
    protected ArrayList<String> measures;
    protected ArrayList<Boolean> checked;
    protected String userUid;
    protected String mealName;

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    FragmentShoppingListDetailsBinding fragmentShoppingListDetailsBinding;
    Context context;

    public ShoppingList(){

    }

    protected ShoppingList(FragmentShoppingListDetailsBinding fragmentShoppingListDetailsBinding, Context context){
        this.fragmentShoppingListDetailsBinding = fragmentShoppingListDetailsBinding;
        this.context = context;
    }

    protected void newInstance(String recipeId, String userId) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection("recipes").document(recipeId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    ingredients = (ArrayList<String>) document.get("indigrients");
                    measures = (ArrayList<String>) document.get("measure");
                    checked = new ArrayList<>();
                    for(int i = 0; i < ingredients.size(); i++){
                        checked.add(false);
                    }
                    userUid = userId;
                    mealName = String.valueOf(document.getData().get("meal"));
                    updateDatabase(LIST_CREATE);
                    displayList();
                }
            }
        });
    }

    private void updateDatabase(int update){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        if(update == INGREDIENTS_UPDATE){
            DocumentReference documentReference = database.collection("shopping_lists").document(shoppingListUid);
            Map<String, Object> docData = new HashMap<>();
            docData.put("checked", checked);
            docData.put("ingredients", ingredients);
            docData.put("measure", measures);
            documentReference.update(docData);
        }
        else{
            DocumentReference documentReference = database.collection("users").document(userUid);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    int listsLeft = Integer.parseInt(String.valueOf(document.getData().get("listsLeft")));
                    Map<String, Object> userMap = new HashMap<>();
                    if(update == LIST_CREATE && listsLeft > 0){
                        listsLeft--;
                        userMap.put("listsLeft", listsLeft);
                        documentReference.update(userMap);
                        addToDatabase();

                    }
                    else if(update == LIST_DELETE && listsLeft < 10){
                        listsLeft++;
                        userMap.put("listsLeft", listsLeft);
                        documentReference.update(userMap);
                        removeFromDatabase();
                    }
                }
            });
        }
    }

    protected void addToDatabase(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection("shopping_lists").document();
        Map<String, Object> docData = new HashMap<>();
        docData.put("user", userUid);
        docData.put("meal", mealName);
        docData.put("checked", checked);
        docData.put("ingredients", ingredients);
        docData.put("measure", measures);
        shoppingListUid = documentReference.getId();
        documentReference.set(docData);
    }

    protected void removeFromDatabase(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection("shopping_lists").document(shoppingListUid);
        documentReference.delete();
    }

    protected void displayList(){
        int length = ingredients.size();
        for(int i = 0; i < length; i++){
            ImageView imageView = new ImageView(context);
            Drawable drawable = AppCompatResources.getDrawable(context, R.drawable.icon_delete);
            drawable.setBounds(5, 5, 5, 5);
            imageView.setImageDrawable(drawable);
            imageView.setPadding(5, 5, 5, 5);

            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setPadding(10, 10, 10, 10);

            CheckBox c = new CheckBox(context);
            c.setTag(ingredients.get(i));
            c.setScaleX(1.6f);
            c.setScaleY(1.6f);
            c.setPadding(5, 5, 5, 5);

            c.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    String tag = String.valueOf(layout.getTag());
                    int index = ingredients.indexOf(tag);
                    checked.set(index, b);
                    updateDatabase(INGREDIENTS_UPDATE);
                }
            });

            TextView t = new TextView(context);
            t.setText(ingredients.get(i));
            t.setTextSize(20.0f);
            t.setPadding(5, 5, 5, 5);

            TextView m = new TextView(context);
            m.setText("[" + measures.get(i) + "]");
            m.setTextSize(20.0f);
            m.setPadding(5, 5, 5, 5);

            layout.setTag(ingredients.get(i));
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
                    String tag = String.valueOf(layout.getTag());
                    int index = ingredients.indexOf(tag);
                    ingredients.remove(index);
                    measures.remove(index);
                    checked.remove(index);
                    updateDatabase(INGREDIENTS_UPDATE);
                }
            });
            fragmentShoppingListDetailsBinding.constraintLayout.addView(layout);
        }
        fragmentShoppingListDetailsBinding.constraintLayout.invalidate();
        fragmentShoppingListDetailsBinding.constraintLayout.requestLayout();
    }

}
