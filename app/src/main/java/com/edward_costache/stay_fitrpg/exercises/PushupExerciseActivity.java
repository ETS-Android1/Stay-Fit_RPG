package com.edward_costache.stay_fitrpg.exercises;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edward_costache.stay_fitrpg.R;
import com.edward_costache.stay_fitrpg.User;
import com.edward_costache.stay_fitrpg.util.Proximiter;
import com.edward_costache.stay_fitrpg.util.SoundLibrary;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
    private int maxRounds, goal, currentPushups, userStrength, userHealth, overallPushups;
    private Proximiter proximiter;
    private boolean readyForPushup = true;

    // Break
    private CountDownTimer breakTimer;
    private long startMilliseconds;
    private TextView txtRound1, txtRound2, txtRound3, txtRound4, txtRound5, txtRound6, txtTime;
    private final int BREAK_TIME = 60;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushup_exercise);

        rounds = getIntent().getIntegerArrayListExtra("rounds");
        maxRounds = rounds.size();
        startMilliseconds = System.currentTimeMillis();
        Log.i("ARRAY AFTER INTENT: ", rounds.toString());
        initViews();

        // TESTING
        btnAction.setVisibility(View.GONE);

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

        breakTimer = new CountDownTimer(BREAK_TIME * 1000 + 100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                txtTime.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));
            }

            @Override
            public void onFinish() {
                switchLayout();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        proximiter.registerListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        proximiter.un_registerListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SoundLibrary.stopSound();
        proximiter.un_registerListener();
    }

    @Override
    public void onBackPressed() {
        displayClosingAlertBox();
    }

    private void displayClosingAlertBox() {
        int seconds = (int) ((System.currentTimeMillis() - startMilliseconds) / 1000);
        new AlertDialog.Builder(PushupExerciseActivity.this, R.style.MyDialogTheme)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exiting the Exercise")
                .setMessage(String.format("Quitting yields NO rewards! \nTotal Time: %02dm and %02ds\nTotal pushups: %d", seconds / 60, seconds % 60, overallPushups))
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
        proximiter = new Proximiter(PushupExerciseActivity.this);
    }

    private void initListeners() {
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundLibrary.playSound(PushupExerciseActivity.this, R.raw.ding);
                currentPushups++;
                overallPushups++;
                if (currentPushups == goal) {
                    round++;
                    if (round == maxRounds) {
                        endOfExercise();
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

        proximiter.setListener(new Proximiter.Listener() {
            @Override
            public void onDistance(float cm) {
                // When user's face gets close to sensor, count a pushup
                if (cm < 8.0f && readyForPushup) {
                    readyForPushup = false;
                    SoundLibrary.playSound(PushupExerciseActivity.this, R.raw.ding);
                    currentPushups++;
                    overallPushups++;
                    if (currentPushups == goal) {
                        round++;
                        if (round == maxRounds) {
                            endOfExercise();
                        } else {
                            currentPushups = 0;
                            goal = rounds.get(round);
                            switchLayout();
                        }

                    } else {
                        updateTextView();
                    }
                    // After user lifted his face, enable pushups
                } else {
                    readyForPushup = true;
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
            readyForPushup = false;
            proximiter.un_registerListener();
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
            breakTimer.start();
            isRound = false;
        } else {
            // Change to round
            readyForPushup = true;
            proximiter.registerListener();
            layoutRound.setVisibility(View.VISIBLE);
            layoutBreak.setVisibility(View.GONE);

            txtTitle.setText(String.format("ROUND: %d", round + 1));
            updateTextView();
            breakTimer.cancel();
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
        readyForPushup = false;
        reference.child(userID).child("strength").setValue(userStrength + getIntent().getIntExtra("strength", 0));
        reference.child(userID).child("health").setValue(userHealth + getIntent().getIntExtra("health", 0));

        // currentTimeInMillis() returns the milliseconds for Epoch time, just like the Util.java class
        // Here i am subtracting the milliseconds at the start of the Activity from the milliseconds recorded at the end of the Activity
        // in order to get the amount of milliseconds the Activity has been running, then convert that to seconds
        int seconds = (int) ((System.currentTimeMillis() - startMilliseconds) / 1000);

        new AlertDialog.Builder(PushupExerciseActivity.this)
                .setTitle("Exercise Finished, Well Done!")
                .setMessage(String.format("Total Time: %02dm and %02ds\nTotal pushups: %d\nStrength +%02d\t\tHealth +%02d", seconds / 60, seconds % 60, overallPushups, getIntent().getIntExtra("strength", 0), getIntent().getIntExtra("health", 0)))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
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