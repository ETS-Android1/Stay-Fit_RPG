package com.edward_costache.stay_fitrpg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Edward Costahce
 */
public class UsernameChangeActivity extends AppCompatActivity {
    private EditText editTxtUsername;
    private Button btnChange;

    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username_change);

        userRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("username");
        initViews();

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTxtUsername.getText().toString();

                if(username.isEmpty())
                {
                    editTxtUsername.setError("Please enter a username!");
                    editTxtUsername.requestFocus();
                    return;
                }

                //change the username in the database for the user currently logged in
                userRef.setValue(username).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * A function for initializing all Views
     */
    private void initViews()
    {
        editTxtUsername = findViewById(R.id.usernameEditTxtUsername);
        btnChange = findViewById(R.id.usernameBtnChange);
    }
}