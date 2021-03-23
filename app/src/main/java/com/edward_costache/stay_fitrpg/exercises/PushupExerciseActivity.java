package com.edward_costache.stay_fitrpg.exercises;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edward_costache.stay_fitrpg.ProfileActivity;
import com.edward_costache.stay_fitrpg.R;
import com.edward_costache.stay_fitrpg.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class PushupExerciseActivity extends AppCompatActivity {

    private LinearLayout layoutRound, layoutBreak;
    private TextView txtTitle;
    private boolean isRound = true;

    private DatabaseReference reference;
    private String userID;
    private androidx.constraintlayout.widget.ConstraintLayout mainLayout;
    private User userProfile;

    // Round
    private Button btnAction;
    private TextView txtPushupCount;

    private ArrayList<Integer> rounds;
    private int round = 0;
    private int maxRounds, goal, currentPushups, userStrength, userHealth;

    // Break
    private CountDownTimer timer;
    private TextView txtRound1, txtRound2, txtRound3, txtRound4, txtRound5, txtRound6, txtTime;
    private int breakTime = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushup_exercise);

        rounds = getIntent().getIntegerArrayListExtra("rounds");
        maxRounds = rounds.size();
        Log.i("ARRAY AFTER INTENT: ", rounds.toString());
        initViews();
        layoutRound.setVisibility(View.VISIBLE);
        layoutBreak.setVisibility(View.GONE);
        initListeners();
        goal = rounds.get(round);
        updateTextView();
        txtTitle.setText(String.format("ROUND: %d", round + 1));

        if (maxRounds == 4) {
            txtRound4.setVisibility(View.VISIBLE);
        } else if (maxRounds == 5) {
            txtRound4.setVisibility(View.VISIBLE);
            txtRound5.setVisibility(View.VISIBLE);
        } else if (maxRounds == 6) {
            txtRound4.setVisibility(View.VISIBLE);
            txtRound5.setVisibility(View.VISIBLE);
            txtRound6.setVisibility(View.VISIBLE);
        }
        setUpUser();
        getUserCurrentStats();

        timer = new CountDownTimer(breakTime * 1000 + 100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                txtTime.setText(String.format("%01d:%02d", seconds / 60, seconds % 60));
            }

            @Override
            public void onFinish() {
                switchLayout();
            }
        };
    }

    @Override
    public void onBackPressed() {
        displayClosingAlertBox();
    }

    private void displayClosingAlertBox() {
        new AlertDialog.Builder(PushupExerciseActivity.this, R.style.MyDialogTheme)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exiting the Application")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("ON STOP: ", "YES");
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void initViews() {
        layoutRound = findViewById(R.id.pushupExerciseRoundLayout);
        layoutBreak = findViewById(R.id.pushupExerciseBreakLayout);
        mainLayout = findViewById(R.id.pushupExerciseMainLayout);

        txtPushupCount = findViewById(R.id.pushupExerciseTxtPushupCount);
        txtTitle = findViewById(R.id.pushupExerciseTxtTitle);
        txtRound1 = findViewById(R.id.pushupExerciseTxtRound1);
        txtRound2 = findViewById(R.id.pushupExerciseTxtRound2);
        txtRound3 = findViewById(R.id.pushupExerciseTxtRound3);
        txtRound4 = findViewById(R.id.pushupExerciseTxtRound4);
        txtRound5 = findViewById(R.id.pushupExerciseTxtRound5);
        txtRound6 = findViewById(R.id.pushupExerciseTxtRound6);
        txtTime = findViewById(R.id.pushupExerciseTxtTime);

        btnAction = findViewById(R.id.pushupExerciseBtnAction);
    }

    private void initListeners() {
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPushups++;
                if (currentPushups == goal) {
                    round++;
                    Log.i("ROUND: ", Integer.toString(round));
                    Log.i("MAX ROUNDS: ", Integer.toString(maxRounds));
                    if (round == maxRounds) {
                        endOfExercise();
                        finish();
                        //TODO: Add fancy Dialog Box
                    } else {
                        currentPushups = 0;
                        goal = rounds.get(round);
                        switchLayout();
                    }

                } else {
                    updateTextView();
                }
            }
        });
    }

    private void updateTextView() {
        txtPushupCount.setText(String.format("%02d / %02d", currentPushups, goal));
    }

    @SuppressLint("ResourceAsColor")
    private void switchLayout() {
        if (isRound) {
            // Change to break
            layoutRound.setVisibility(View.GONE);
            layoutBreak.setVisibility(View.VISIBLE);

            txtTitle.setText("BREAK");

            txtRound1.setText(String.format("ROUND 1: %02d", rounds.get(0)));
            txtRound1.setTextColor(Color.GREEN);
            txtRound2.setText(String.format("ROUND 2: %02d", rounds.get(1)));
            if (round == 2) {
                txtRound2.setTextColor(Color.GREEN);
            }
            txtRound3.setText(String.format("ROUND 3: %02d", rounds.get(2)));
            if (round == 3) {
                txtRound3.setTextColor(Color.GREEN);
            }

            try {
                txtRound4.setText(String.format("ROUND 3: %02d", rounds.get(3)));
                if (round == 4) {
                    txtRound4.setTextColor(Color.GREEN);
                }
                txtRound5.setText(String.format("ROUND 3: %02d", rounds.get(4)));
                if (round == 5) {
                    txtRound5.setTextColor(Color.GREEN);
                }
                txtRound6.setText(String.format("ROUND 3: %02d", rounds.get(5)));

            } catch (Exception e) {
                Log.i("PUSHUP EXERCISE: ", "NOT ENOUGH ROUNDS");
            }
            timer.start();
            isRound = false;
        } else {
            // Change to round
            layoutRound.setVisibility(View.VISIBLE);
            layoutBreak.setVisibility(View.GONE);

            txtTitle.setText(String.format("ROUND: %d", round + 1));
            updateTextView();
            timer.cancel();
            isRound = true;
        }
    }

    private void getUserCurrentStats() {
        reference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    userProfile = snapshot.getValue(User.class);
                    userStrength = (int) userProfile.getStrength();
                    userHealth = (int) userProfile.getHealth();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("DISPLAY USER INFO: ", "USER IS NULL");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void endOfExercise() {
        reference.child(userID).child("strength").setValue(userStrength + getIntent().getIntExtra("strength", 0));
        reference.child(userID).child("health").setValue(userHealth + getIntent().getIntExtra("health", 0));
    }

    private void setUpUser() {
        reference = FirebaseDatabase.getInstance().getReference("users");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userID = user.getUid();
        } else {
            Snackbar.make(PushupExerciseActivity.this, mainLayout, "Something went wrong, logout and login again!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            }).show();
        }
    }
}