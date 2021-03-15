package com.edward_costache.stay_fitrpg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTxtUsername, editTxtEmail, editTxtPassword, editTxtPasswordRetry;
    private Button btnSignup;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initViews();
        mAuth = FirebaseAuth.getInstance();
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();

                InputMethodManager mgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(editTxtUsername.getWindowToken(), 0);
                mgr.hideSoftInputFromWindow(editTxtEmail.getWindowToken(), 0);
                mgr.hideSoftInputFromWindow(editTxtPassword.getWindowToken(), 0);
                mgr.hideSoftInputFromWindow(editTxtPasswordRetry.getWindowToken(), 0);
            }
        });
    }

    private void initViews() {
        editTxtUsername = findViewById(R.id.signupExitTxtUsername);
        editTxtEmail = findViewById(R.id.signupExitTxtEmail);
        editTxtPassword = findViewById(R.id.signupEditTxtPassword);
        editTxtPasswordRetry = findViewById(R.id.signupEditTxtRePassword);

        btnSignup = findViewById(R.id.signupBtnSignup);
    }

    private void createAccount() {
        String username = editTxtUsername.getText().toString().trim();
        String email = editTxtEmail.getText().toString().trim();
        String password = editTxtPassword.getText().toString().trim();

        if (username.isEmpty()) {
            editTxtUsername.setError("Username required!");
            editTxtUsername.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editTxtEmail.setError("Email required!");
            editTxtEmail.requestFocus();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTxtEmail.setError("Please enter valid email!");
            editTxtEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTxtPassword.setError("Password required!");
            editTxtPassword.requestFocus();
            return;
        } else if (password.length() < 6) {
            editTxtPassword.setError("Password must be longer than 5 characters!");
            editTxtPassword.requestFocus();
            return;
        }

        if (!editTxtPasswordRetry.getText().toString().equals(password)) {
            editTxtPasswordRetry.setError("Password doesn't match!");
            editTxtPasswordRetry.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    User user = new User(username);
                    Toast.makeText(SignUpActivity.this, username + " Registered", Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                intent.putExtra("email", email);
                                intent.putExtra("password", password);
                                startActivity(intent);
                                finish();
                                SharedPreferences sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("hasAccount", true);
                                editor.apply();
                            } else {
                                Log.i("REGISTRATION: ", "Failed to store user data on Database");
                            }
                        }
                    });
                } else {
                    Toast.makeText(SignUpActivity.this, "Registration failed, try again.", Toast.LENGTH_SHORT).show();
                    Log.i("REGISTRATION: ", "Failed to register user");
                }
            }
        });
    }
}