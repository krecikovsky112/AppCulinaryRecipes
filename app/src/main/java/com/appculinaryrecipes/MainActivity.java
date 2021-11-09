package com.appculinaryrecipes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.adapters.AdapterViewBindingAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.appculinaryrecipes.databinding.ActivityMainBinding;
import com.appculinaryrecipes.databinding.FragmentHomeBinding;
import com.appculinaryrecipes.generated.callback.OnClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private ActivityMainBinding activityMainBinding;
    private ChipNavigationBar chipNavigationBar;

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

        HomeFragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentHomeContainer, fragment);
        fragmentTransaction.commit();
        chipNavigationBar = findViewById(R.id.navigationBar);
        chipNavigationBar.setItemSelected(R.id.home,true);
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                if(i == R.id.home)
                    fragment = new HomeFragment();
                else if(i == R.id.creator){
                    checkUser();
                    fragment = new RecipeFragment();
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentHomeContainer,
                                fragment).commit();
            }
        });

    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(this, AuthActivity.class));
        }
    }

}