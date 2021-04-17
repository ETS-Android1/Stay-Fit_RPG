package com.edward_costache.stay_fitrpg.exercises;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.hardware.Sensor;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edward_costache.stay_fitrpg.R;
import com.edward_costache.stay_fitrpg.User;
import com.edward_costache.stay_fitrpg.util.Accelerometer;
import com.edward_costache.stay_fitrpg.util.SoundLibrary;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class SitupExerciseActivity extends AppCompatActivity {

    private static final String TAG = "SitupExercise";
    private LinearLayout layoutRound, layoutBreak;
    private TextView txtTitle;
    private boolean isRound = true;

    private DatabaseReference reference, weekRef;
    private String userID;
    private androidx.constraintlayout.widget.ConstraintLayout mainLayout;
    private User userProfile;

    // Round
    private TextView txtSitupCount;
    private ArrayList<Integer> rounds;
    private int round = 0;
    private int maxRounds, goal, currentSitups, userStamina, userHealth, overallSitups;
    private boolean ready = true;
    private Vibrator vibrator;
    private Accelerometer accelerometer;

    // Break
    private CountDownTimer breakTimer;
    private long startMilliseconds;
    private TextView txtRound1, txtRound2, txtRound3, txtRound4, txtRound5, txtRound6, txtTime;
    private final int BREAK_TIME = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_situp_exercise);

        rounds = getIntent().getIntegerArrayListExtra("rounds");
        maxRounds = rounds.size();
        startMilliseconds = System.currentTimeMillis();
        initViews();
        layoutRound.setVisibility(View.VISIBLE);
        layoutBreak.setVisibility(View.GONE);

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
    protected void onPause() {
        super.onPause();
        accelerometer.un_registerListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        accelerometer.registerListener();
    }


    @Override
    protected void onStop() {
        super.onStop();
        accelerometer.un_registerListener();
        SoundLibrary.stopSound();
    }

    @Override
    public void onBackPressed() {
        displayClosingAlertBox();

    }

    private void displayClosingAlertBox() {
        int seconds = (int) ((System.currentTimeMillis() - startMilliseconds) / 1000);
        new AlertDialog.Builder(SitupExerciseActivity.this, R.style.MyDialogTheme)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exiting the Exercise")
                .setMessage(String.format("Quiting yields NO rewards\nTotal Time: %02dm and %02ds\nTotal situps: %d", seconds / 60, seconds % 60, overallSitups))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("ON STOP: ", "YES");
                        accelerometer.un_registerListener();
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void initViews() {
        layoutRound = findViewById(R.id.situpExerciseRoundLayout);
        layoutBreak = findViewById(R.id.situpExerciseBreakLayout);
        mainLayout = findViewById(R.id.situpExerciseMainLayout);

        txtSitupCount = findViewById(R.id.situpExerciseTxtSitupCount);
        txtTitle = findViewById(R.id.situpExerciseTxtTitle);
        txtRound1 = findViewById(R.id.situpExerciseTxtRound1);
        txtRound2 = findViewById(R.id.situpExerciseTxtRound2);
        txtRound3 = findViewById(R.id.situpExerciseTxtRound3);
        txtRound4 = findViewById(R.id.situpExerciseTxtRound4);
        txtRound5 = findViewById(R.id.situpExerciseTxtRound5);
        txtRound6 = findViewById(R.id.situpExerciseTxtRound6);
        txtTime = findViewById(R.id.situpExerciseTxtTime);

        accelerometer = new Accelerometer(SitupExerciseActivity.this, Sensor.TYPE_ACCELEROMETER);
        accelerometer.setListener(new Accelerometer.Listener() {
            @Override
            public void onTranslation(float tx, float ty, float tz) {
                double startingValue = -6.7;
                double distance = 5.5;

                /*
                As mentioned in the Accelerometer class, onTranslate is a method which is part of the listener Interface. Here we use the Interface as a medium to pass values from the
                actual sensorChanged() Overwritten method to a class where we want to use the Accelerometer.
                At first, the SitupExerciseActivity would implement a SensorEventListener, but this was impractical, so i created a seperate class called Accelerometer where all of the Accelerometer code
                would be stored, and to use it simply create the Accelerometer object.
                 */

                if (tz <= 1 && ready) {
                    if ((tz - startingValue) >= distance) {
                        SoundLibrary.playSound(SitupExerciseActivity.this, R.raw.ding);
                        currentSitups++;
                        overallSitups++;
                        ready = false;

                        if (currentSitups == goal) {
                            round++;
                            if (round == maxRounds) {
                                vibrator.vibrate(500);
                                endOfExercise();
                            } else {
                                vibrator.vibrate(500);
                                goal = rounds.get(round);
                                currentSitups = 0;
                                switchLayout();
                            }
                        } else {
                            updateTextView();
                        }
                    }
                } else {
                    if (tz <= startingValue) {
                        ready = true;
                    }
                }
            }
        });

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void updateTextView() {
        txtSitupCount.setText(String.format("%02d / %02d", currentSitups, goal));
    }

    @SuppressLint("ResourceAsColor")
    private void switchLayout() {
        if (isRound) {
            // Change to break
            ready = false;
            accelerometer.un_registerListener();
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
            ready = true;
            accelerometer.registerListener();

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
                    userStamina = (int) userProfile.getStamina();
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
        ready = false;
        reference.child(userID).child("stamina").setValue(userStamina + getIntent().getIntExtra("stamina", 0));
        reference.child(userID).child("health").setValue(userHealth + getIntent().getIntExtra("health", 0));

        weekRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("progress");
        Calendar calendar = new GregorianCalendar();
        String week_year = Integer.toString(calendar.get(Calendar.WEEK_OF_YEAR));
        SimpleDateFormat formatterForDay = new SimpleDateFormat("E, dd-MM");
        String todayDay = formatterForDay.format(calendar.getTime());
        weekRef.child(week_year).child("days").child(todayDay).child("situps").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    weekRef.child(week_year).child("days").child(todayDay).child("situps").setValue(snapshot.getValue(Integer.class) + overallSitups);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // currentTimeInMillis() returns the milliseconds for Epoch time, just like the Util.java class
        // Here i am subtracting the milliseconds at the start of the Activity from the milliseconds recorded at the end of the Activity
        // in order to get the amount of milliseconds the Activity has been running, then convert that to seconds
        int seconds = (int) ((System.currentTimeMillis() - startMilliseconds) / 1000);

        new AlertDialog.Builder(SitupExerciseActivity.this)
                .setTitle("Exercise Finished, Well Done!")
                .setMessage(String.format("Total Time: %02dm and %02ds\nTotal situps: %d\nStamina +%02d\t\tHealth +%02d", seconds / 60, seconds % 60, overallSitups, getIntent().getIntExtra("stamina", 0), getIntent().getIntExtra("health", 0)))
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
            Snackbar.make(SitupExerciseActivity.this, mainLayout, "Something went wrong, logout and login again!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            }).show();
        }
    }
}