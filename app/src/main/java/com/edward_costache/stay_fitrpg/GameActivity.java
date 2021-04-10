package com.edward_costache.stay_fitrpg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameActivity extends AppCompatActivity {

    public static final String TAG = "GAME ACTIVITY";
    private TextView txtUsername1, txtUsername2, txtHealthUser1, txtHealthUser2;
    private ProgressBar progressBarUser1Health, progressBarUser2Health;

    private String role, userID1, userID2, gameID;
    private DatabaseReference gameRef;

    private User user1, user2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initViews();

        role = getIntent().getStringExtra("role");
        userID1 = getIntent().getStringExtra("userID1");
        userID2 = getIntent().getStringExtra("userID2");
        gameID = getIntent().getStringExtra("gameID");
        gameRef = FirebaseDatabase.getInstance().getReference("games");


        //-------------------------------------------------------------------- HOST
        if(role.equals("host"))
        {
            gameRef.child(gameID).child("user1").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue() != null)
                    {
                        user1 = snapshot.getValue(User.class);
                        txtUsername1.setText(user1.getUsername());
                        txtHealthUser1.setText(Integer.toString(user1.getHealth()));
                        progressBarUser1Health.setProgress(user1.getHealth());
                    }
                    else
                    {
                        Log.d(TAG, "onDataChange: USER1 NOT FOUND ON DATABASE");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            gameRef.child(gameID).child("user2").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue() != null)
                    {
                        user2 = snapshot.getValue(User.class);
                        txtUsername2.setText(user2.getUsername());
                        txtHealthUser2.setText(Integer.toString(user2.getHealth()));
                        progressBarUser2Health.setProgress(user2.getHealth());
                    }
                    else
                    {
                        Log.d(TAG, "onDataChange: USER2 NOT FOUND ON DATABASE");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        //-------------------------------------------------------------------- GUEST
        else if(role.equals("guest"))
        {
            gameRef.child(gameID).child("user1").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue() != null)
                    {
                        user1 = snapshot.getValue(User.class);
                        txtUsername2.setText(user1.getUsername());
                        txtHealthUser2.setText(Integer.toString(user1.getHealth()));
                        progressBarUser2Health.setProgress(user1.getHealth());
                    }
                    else
                    {
                        Log.d(TAG, "onDataChange: USER1 NOT FOUND ON DATABASE");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            gameRef.child(gameID).child("user2").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue() != null)
                    {
                        user2 = snapshot.getValue(User.class);
                        txtUsername1.setText(user2.getUsername());
                        txtHealthUser1.setText(Integer.toString(user2.getHealth()));
                        progressBarUser1Health.setProgress(user2.getHealth());
                    }
                    else
                    {
                        Log.d(TAG, "onDataChange: USER2 NOT FOUND ON DATABASE");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else
        {
            Log.d(TAG, "onCreate: COULD'T DETERMINE HOST OR GUEST");
        }
    }

    private void initViews()
    {
        txtUsername1 = findViewById(R.id.gameTxtUser1Username);
        txtUsername2 = findViewById(R.id.gameTxtUser2Username);
        txtHealthUser1 = findViewById(R.id.gameTxtUser1HealthValue);
        txtHealthUser2 = findViewById(R.id.gameTxtUser2HealthValue);

        progressBarUser1Health = findViewById(R.id.gameProgressBarUser1Health);
        progressBarUser2Health = findViewById(R.id.gameProgressBarUser2Health);
    }
}