package com.edward_costache.stay_fitrpg;

import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordChangeActivity extends AppCompatActivity {
    private EditText editTxtOldPassword, editTxtNewPassword;
    private Button btnChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);
        initViews();

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = editTxtOldPassword.getText().toString();
                String newPassword = editTxtNewPassword.getText().toString();

                if (oldPassword.isEmpty()) {
                    editTxtOldPassword.setError("Please enter your old password!");
                    editTxtOldPassword.requestFocus();
                    return;
                }

                if (newPassword.isEmpty()) {
                    editTxtNewPassword.setError("Please enter a new password!");
                    editTxtNewPassword.requestFocus();
                    return;
                }

                changePassword(oldPassword, newPassword);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void changePassword(String currentPassword, String newPassword) {
        //get the current user logged in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
        //check if they have entered the current current password by attempting to re-authenticate them using the old password they have provided
        user.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //old password is correct
                        user.updatePassword(newPassword);
                        Toast.makeText(PasswordChangeActivity.this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    //old password is incorrect
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PasswordChangeActivity.this, "You old password is not correct!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initViews() {
        editTxtNewPassword = findViewById(R.id.passChangeNewPassword);
        editTxtOldPassword = findViewById(R.id.passChangeOldPassword);
        btnChange = findViewById(R.id.passChangeBtnPass);
    }
}