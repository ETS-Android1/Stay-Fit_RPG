package com.edward_costache.stay_fitrpg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText editTxtEmail;
    private Button btnReset;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initViews();
        mAuth = FirebaseAuth.getInstance();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void initViews()
    {
        editTxtEmail = findViewById(R.id.forgotPassEditTxtEmail);
        btnReset = findViewById(R.id.forgotPassBtnRetrieve);
    }

    private void resetPassword()
    {
        String email = editTxtEmail.getText().toString();
        if(email.isEmpty())
        {
            editTxtEmail.setError("Please enter an Email!");
            editTxtEmail.requestFocus();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editTxtEmail.setError("Please enter Valid Email!");
            editTxtEmail.requestFocus();
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(ForgotPasswordActivity.this, "Email sent to "+ email, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset link to "+email+ ". Please try again!", Toast.LENGTH_SHORT).show();
                }

                if (task.isComplete())
                {
                    Toast.makeText(ForgotPasswordActivity.this, "Password reset successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                    finish();
                }
                else
                {
                    Toast.makeText(ForgotPasswordActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}