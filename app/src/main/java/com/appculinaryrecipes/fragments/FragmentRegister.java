package com.appculinaryrecipes.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.appculinaryrecipes.R;
import com.appculinaryrecipes.databinding.RegisterFragmentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FragmentRegister extends Fragment {
    private RegisterFragmentBinding registerFragmentBinding;
    private FirebaseAuth firebaseAuth;
    String userID;
    private FirebaseFirestore fstore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        registerFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.register_fragment,container,false);
        registerFragmentBinding.setCallback(this);
        View view = registerFragmentBinding.getRoot();
        firebaseAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        return view;
    }

    public void onSubmit() {
        if (!registerFragmentBinding.inputEmailText.getText().toString().isEmpty() &&
                !registerFragmentBinding.editTextPassword.getText().toString().isEmpty() &&
                validate(registerFragmentBinding.editTextPassword2.getText().toString(),registerFragmentBinding.editTextPassword.getText().toString())) {
            firebaseRegister(registerFragmentBinding.inputEmailText.getText().toString(), registerFragmentBinding.editTextPassword.getText().toString());
        }

    }

    private boolean validate(String txt_password, String txt_confirm_password) {
        if(txt_password.equals(txt_confirm_password)){
            return true;
        }
        else{
            Toast.makeText(getActivity(),"Password not matching!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void firebaseRegister(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(),"Registering user succesfull", Toast.LENGTH_SHORT).show();
                    userID = firebaseAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fstore.collection("users").document(userID);
                    Map<String,Object> user = new HashMap<>();
                    user.put("fullname",registerFragmentBinding.inputFullnameText.getText().toString());
                    user.put("email",registerFragmentBinding.inputEmailText.getText().toString());
                    user.put("password",registerFragmentBinding.editTextPassword.getText().toString());
                    user.put("listsLeft", 10);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("TAG","onSuccess: user Profile is created for " + userID);
                        }
                    });
                }
                else
                {
                    Toast.makeText(getActivity(),"Registering user failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
