package com.appculinaryrecipes.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.appculinaryrecipes.R;
import com.appculinaryrecipes.databinding.ActivityMainBinding;
import com.appculinaryrecipes.fragments.AddRecipeFragment;
import com.appculinaryrecipes.fragments.FridgeFragment;
import com.appculinaryrecipes.fragments.HomeFragment;
import com.appculinaryrecipes.fragments.ShoppingListsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
//        TODO - Zakomentowalem ten kod, ponieważ był to testowy button słuzocy do wylogowania usera.
//        TODO - Trzaba dodać do menu bara opcje wylogowywania i dodać tą opcje tam
//        activityMainBinding.logout.setOnClickListener(v -> {
//            firebaseAuth.signOut();
//            checkUser();
//        });
        activityMainBinding.navigationBar.setItemSelected(R.id.home,true);
        activityMainBinding.navigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i){
                    case R.id.creator:
                        checkUser();
                        fragment = new AddRecipeFragment();
                        break;
                    case R.id.fridge:
                        checkUser();
                        fragment = new FridgeFragment();
                        break;
                    case R.id.home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.shopping_lists:
                        fragment = new ShoppingListsFragment();
                        break;
                    default:
                        fragment = new HomeFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentHomeContainer,
                                fragment).commit();
            }
        });

        HomeFragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentHomeContainer, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Uri selectedImageUri = data.getData();
            Bitmap b = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri));
            ImageView imageViewUpload = findViewById(R.id.imageViewUpload);
            TextView textView = findViewById(R.id.editTextMealThumb);
            textView.setText(selectedImageUri.toString());
            imageViewUpload.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            Toast.makeText(this.getApplicationContext(), "Resource not found!", Toast.LENGTH_SHORT);
        }
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(this, AuthActivity.class));
        }
    }

}