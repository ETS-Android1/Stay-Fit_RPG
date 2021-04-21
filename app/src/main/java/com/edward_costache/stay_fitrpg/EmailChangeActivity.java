package com.edward_costache.stay_fitrpg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailChangeActivity extends AppCompatActivity {
    private EditText editTxtNewEmail;
    private Button btnChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_change);
        initViews();

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTxtNewEmail.getText().toString();

                if (email.isEmpty()) {
                    editTxtNewEmail.setError("Please enter an email address!");
                    editTxtNewEmail.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTxtNewEmail.setError("Please enter a valid new email address!");
                    editTxtNewEmail.requestFocus();
                }

                changeEmail(email);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void changeEmail(String email) {
        //get the current user logged in and update their email
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.updateEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EmailChangeActivity.this, "Email successfully changed!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EmailChangeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        editTxtNewEmail.setText("");
                    }
                });
    }

    private void initViews() {
        editTxtNewEmail = findViewById(R.id.emailChangeEditTxtEmail);
        btnChange = findViewById(R.id.emailChangeBtnChange);
    }
}