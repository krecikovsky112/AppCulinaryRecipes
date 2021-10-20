package com.appculinaryrecipes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.appculinaryrecipes.databinding.ActivityAuthBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthActivity extends AppCompatActivity {
    private ActivityAuthBinding activityAuthBinding;
    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    private static final String TAG = "GOOGLE_SIGN_IN_TAG";

    private Button normalSingInButton;
    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAuthBinding = DataBindingUtil.setContentView(this, R.layout.activity_auth);

        normalSingInButton =  (Button) findViewById(R.id.normalSignInButton);
        usernameEditText = (EditText) findViewById(R.id.inputEmailText);
        passwordEditText = (EditText) findViewById(R.id.editTextPassword);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                            firebaseAuthWithGoogleAccount(account);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());

                        }
                    }
                });

        normalSingInButton.setOnClickListener(v -> {
            System.out.println("#email: " + usernameEditText.getText().toString());
            System.out.println("#pass: " + passwordEditText.getText().toString());
            firebaseLogin(usernameEditText.getText().toString(), passwordEditText.getText().toString());
        });

        activityAuthBinding.signInButton.setOnClickListener(v -> {
            Intent intent = googleSignInClient.getSignInIntent();
            someActivityResultLauncher.launch(intent);
        });
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    startActivity(new Intent(AuthActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AuthActivity.this, "Failure", Toast.LENGTH_LONG).show();
                });
    }

    private void firebaseLogin(String email, String password){
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(AuthActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(AuthActivity.this, "Failure", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}