package com.appculinaryrecipes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.appculinaryrecipes.databinding.RegisterFragmentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FragmentRegister extends Fragment {
    private RegisterFragmentBinding registerFragmentBinding;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        registerFragmentBinding = DataBindingUtil.inflate(inflater,R.layout.register_fragment,container,false);
        registerFragmentBinding.setCallback(this);
        View view = registerFragmentBinding.getRoot();
        firebaseAuth = FirebaseAuth.getInstance();
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
                }
                else
                {
                    Toast.makeText(getActivity(),"Registering user failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
