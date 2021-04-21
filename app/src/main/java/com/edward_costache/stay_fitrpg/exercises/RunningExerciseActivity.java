package com.edward_costache.stay_fitrpg.exercises;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edward_costache.stay_fitrpg.R;
import com.edward_costache.stay_fitrpg.User;
import com.edward_costache.stay_fitrpg.util.SoundLibrary;
import com.edward_costache.stay_fitrpg.util.StepDetector;
import com.edward_costache.stay_fitrpg.util.Util;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RunningExerciseActivity extends AppCompatActivity {

    private LinearLayout layoutRound, layoutBreak;
    private TextView txtTitle;
    private boolean isRound = true;

    private DatabaseReference reference, weekRef;
    private String userID;
    private androidx.constraintlayout.widget.ConstraintLayout mainLayout;
    private User userProfile;
    private final int STEP_LENGTH = 78;

    // Round
    private TextView txtDistance;
    private StepDetector stepDetector;
    private Vibrator vibrator;
    private Button btnTest;

    private double[] rounds;
    private int round = 0;
    private int maxRounds, userStamina, userAgility;
    private double goal;
    private double distanceKm;
    private double distanceOverall;
    private int steps;

    // Break
    private CountDownTimer breakTimer;
    private long startMillis;
    private TextView txtRound1, txtRound2, txtRound3, txtRound4, txtRound5, txtRound6, txtTime;
    private final int BREAK_TIME = 180;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_exercise);

        rounds = getIntent().getDoubleArrayExtra("rounds");
        maxRounds = rounds.length;
        Log.i("TAG", "ROUNDS.LENGTH(): "+maxRounds);
        startMillis = System.currentTimeMillis();
        Log.i("ARRAY AFTER INTENT: ", rounds.toString());
        initViews();
        stepDetector.setListener(new StepDetector.Listener() {
            @Override
            public void onStep() {
                steps++;
                distanceKm = (steps * STEP_LENGTH)/100000.0;
                distanceOverall = (steps * STEP_LENGTH)/100000.0;
                if (distanceKm >= goal) {       //break time
                    round++;
                    if (round == maxRounds) {   //end of exercise
                        endOfExercise();
                        SoundLibrary.playLoopSound(RunningExerciseActivity.this, R.raw.ding, 3);
                        vibrator.vibrate(700);
                    } else {
                        SoundLibrary.playLoopSound(RunningExerciseActivity.this, R.raw.ding, 3);
                        vibrator.vibrate(700);
                        distanceKm = 0.0;
                        steps = 0;
                        goal = rounds[round];
                        switchLayout();
                    }

                } else {
                    updateTextView();
                }
            }
        });
        layoutRound.setVisibility(View.VISIBLE);
        layoutBreak.setVisibility(View.GONE);
        goal = rounds[round];
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

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                round++;
                if (round == maxRounds) {
                    endOfExercise();
                    SoundLibrary.playLoopSound(RunningExerciseActivity.this, R.raw.ding, 3);
                    vibrator.vibrate(700);
                } else {
                    SoundLibrary.playLoopSound(RunningExerciseActivity.this, R.raw.ding, 3);
                    vibrator.vibrate(700);
                    distanceKm = 0.0;
                    goal = rounds[round];
                    switchLayout();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        stepDetector.registerListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stepDetector.un_registerListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stepDetector.un_registerListener();
        SoundLibrary.stopSound();
    }

    @Override
    public void onBackPressed() {
        displayClosingAlertBox();

    }

    private void displayClosingAlertBox() {
        int seconds = (int) ((System.currentTimeMillis() - startMillis) / 1000);
        new AlertDialog.Builder(RunningExerciseActivity.this, R.style.MyDialogTheme)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exiting the Exercise")
                .setMessage(String.format("Quiting yields NO rewards!\nTotal Time: %02dm and %02ds\nTotal distance: %.2fkm", seconds / 60, seconds % 60, distanceOverall))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("ON STOP: ", "YES");
                        stepDetector.un_registerListener();
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void initViews() {
        stepDetector = new StepDetector(RunningExerciseActivity.this);

        layoutRound = findViewById(R.id.runningExerciseRoundLayout);
        layoutBreak = findViewById(R.id.runningExerciseBreakLayout);
        mainLayout = findViewById(R.id.runningExerciseMainLayout);

        txtTitle = findViewById(R.id.runningExerciseTxtTitle);
        txtRound1 = findViewById(R.id.runningExerciseTxtRound1);
        txtRound2 = findViewById(R.id.runningExerciseTxtRound2);
        txtRound3 = findViewById(R.id.runningExerciseTxtRound3);
        txtRound4 = findViewById(R.id.runningExerciseTxtRound4);
        txtRound5 = findViewById(R.id.runningExerciseTxtRound5);
        txtRound6 = findViewById(R.id.runningExerciseTxtRound6);
        txtTime = findViewById(R.id.runningExerciseTxtTime);
        txtDistance = findViewById(R.id.runningExerciseTxtDistance);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        btnTest = findViewById(R.id.btnTest);
    }

    @SuppressLint("ResourceAsColor")
    private void switchLayout() {
        //instead of creating a new activity for break time. I decided to create a new layout for break, make it visible and make the round layout GONE
        //because i am making the layout gone, all children within the layout will adopt gone as well.
        if (isRound) {
            // Change to break
            stepDetector.un_registerListener();
            layoutRound.setVisibility(View.GONE);
            layoutBreak.setVisibility(View.VISIBLE);

            txtTitle.setText("BREAK");

            txtRound1.setText(String.format("ROUND 1: %.2fkm", rounds[0]));
            txtRound1.setTextColor(Color.GREEN);
            txtRound2.setText(String.format("ROUND 2: %.2fkm", rounds[1]));
            if (round == 2) {
                txtRound2.setTextColor(Color.GREEN);
            }
            txtRound3.setText(String.format("ROUND 3: %.2fkm", rounds[2]));
            if (round == 3) {
                txtRound3.setTextColor(Color.GREEN);
            }

            try {
                txtRound4.setText(String.format("ROUND 3: %.2fkm", rounds[3]));
                if (round == 4) {
                    txtRound4.setTextColor(Color.GREEN);
                }
                txtRound5.setText(String.format("ROUND 3: %.2fkm", rounds[4]));
                if (round == 5) {
                    txtRound5.setTextColor(Color.GREEN);
                }
                txtRound6.setText(String.format("ROUND 3: %.2fkm", rounds[5]));

            } catch (Exception e) {
                Log.i("PUSHUP EXERCISE: ", "NOT ENOUGH ROUNDS");
            }
            breakTimer.start();
            isRound = false;
        } else {
            // Change to round
            layoutRound.setVisibility(View.VISIBLE);
            layoutBreak.setVisibility(View.GONE);

            txtTitle.setText(String.format("ROUND: %d", round + 1));
            updateTextView();
            breakTimer.cancel();
            isRound = true;
        }
    }

    private void updateTextView() {
        txtDistance.setText(String.format("%.2f / %.2f", distanceKm, goal));
    }

    private void endOfExercise() {
        stepDetector.un_registerListener();     //i am unregistering the detector so that steps aren't counted after exercise completion.
        reference.child(userID).child("stamina").setValue(userStamina + getIntent().getIntExtra("stamina", 0));     //reward values are given through the intent in the previous activity
        reference.child(userID).child("agility").setValue(userAgility + getIntent().getIntExtra("agility", 0));

        weekRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("progress");
        weekRef.child(Util.getCurrentWeekOfYear()).child("days").child(Util.getTodayAsStringFormat()).child("running").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    weekRef.child(Util.getCurrentWeekOfYear()).child("days").child(Util.getTodayAsStringFormat()).child("running").setValue(snapshot.getValue(Double.class) + distanceOverall);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // currentTimeInMillis() returns the milliseconds for Epoch time, just like the Util.java class
        // Here i am subtracting the milliseconds at the start of the Activity from the milliseconds recorded at the end of the Activity
        // in order to get the amount of milliseconds the Activity has been running, then convert that to seconds
        int seconds = (int)((System.currentTimeMillis() - startMillis)/1000);

        new AlertDialog.Builder(RunningExerciseActivity.this)
                .setTitle("Exercise Finished, Well Done!")
                .setMessage(String.format("Total Time: %02dm and %02ds\nTotal distance: %.2fkm\nStamina +%02d\t\tAgility +%02d", seconds / 60, seconds % 60, distanceOverall, getIntent().getIntExtra("stamina", 0), getIntent().getIntExtra("agility", 0)))
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

    private void getUserCurrentStats() {
        reference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    userProfile = snapshot.getValue(User.class);
                    userStamina = (int) userProfile.getStamina();
                    userAgility = (int) userProfile.getAgility();
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

    private void setUpUser() {
        reference = FirebaseDatabase.getInstance().getReference("users");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userID = user.getUid();
        } else {
            Snackbar.make(RunningExerciseActivity.this, mainLayout, "Something went wrong, logout and login again!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            }).show();
        }
    }
}
