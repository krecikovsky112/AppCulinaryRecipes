package com.appculinaryrecipes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.appculinaryrecipes.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private ActivityMainBinding activityMainBinding;
    private FirebaseFunctions mFunctions;

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
        activityMainBinding.navigationBar.setItemSelected(R.id.home, true);
        activityMainBinding.navigationBar.setOnItemSelectedListener(i -> {
            if (i == R.id.search) {
                SearchFragment searchFragment = new SearchFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHomeContainer, searchFragment).addToBackStack("ok").commit();
            } else if (i == R.id.home) {
                HomeFragment fragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHomeContainer, fragment).addToBackStack("ok").commit();
            }
        });

        HomeFragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentHomeContainer, fragment);
        fragmentTransaction.commit();

    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(this, AuthActivity.class));
        }
    }

}