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
import com.edward_costache.stay_fitrpg.util.Gravimeter;
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

public class SquatExerciseActivity extends AppCompatActivity {

    public static final String TAG = "SquatExercise";
    private Accelerometer accelerometer;
    private Gravimeter gravimeter;

    private TextView AccX, AccY, AccZ, gravX, gravY, gravZ;

    private DatabaseReference reference, weekRef;
    private String userID;
    private LinearLayout layoutRound, layoutBreak;
    private androidx.constraintlayout.widget.ConstraintLayout mainLayout;
    private User userProfile;
    private int userStrength, userStamina;
    private long startMilliseconds;
    private boolean isRound = true;

    // ROUND
    private TextView txtSquatCount;
    private int overallSquats;
    private int currentSquats;
    private ArrayList<Integer> rounds;
    private int goal;
    private int round = 0;
    private boolean ready = false;
    private float gravityX = 0;
    private int maxRounds;
    private Vibrator vibrator;

    // Break
    private CountDownTimer breakTimer;
    private TextView txtRound1, txtRound2, txtRound3, txtRound4, txtRound5, txtRound6, txtTime, txtTitle;
    private final int BREAK_TIME = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squat_exercise);

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

        accelerometer.setListener(new Accelerometer.Listener() {
            @Override
            public void onTranslation(float tx, float ty, float tz) {
                /*
                TESTING
                AccX.setText(String.format("X:%.2f", tx));
                AccY.setText(String.format("Y:%.2f", ty));
                AccZ.setText(String.format("Z:%.2f", tz));
                 */

                final double FINISH = 11.6;
                final double START = 9.2;

                // Make sure the phone is sideways
                if (Math.abs(gravityX) >= 9 && Math.abs(gravityX) < 10) {
                    // How a squat is recorded
                    if (Math.abs(tx) >= FINISH && ready) {
                        Log.i(TAG, String.format("X: %.2f", tx));
                        SoundLibrary.playSound(SquatExerciseActivity.this, R.raw.ding);
                        currentSquats++;
                        overallSquats++;
                        ready = false;

                        if (currentSquats == goal) {
                            round++;
                            if (round == maxRounds) {
                                vibrator.vibrate(500);
                                endOfExercise();
                            } else {
                                vibrator.vibrate(500);
                                goal = rounds.get(round);
                                currentSquats = 0;
                                switchLayout();
                            }
                        } else {
                            updateTextView();
                        }

                    } else if (Math.abs(tx) <= START) {
                        ready = true;
                    }
                }
            }
        });

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

        gravimeter.setListener(new Gravimeter.Listener() {
            @Override
            public void onGravitation(float gx, float gy, float gz) {
                gravityX = gx;
                /*
                TESTING
                gravX.setText(String.format("X: %.2f", gx));
                gravY.setText(String.format("Y: %.2f", gy));
                gravZ.setText(String.format("Z: %.2f", gz));

                 */
            }
        });
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
                    userStrength = (int) userProfile.getStrength();
                    userStamina = (int) userProfile.getStamina();
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
        reference.child(userID).child("strength").setValue(userStrength + getIntent().getIntExtra("strength", 0));
        reference.child(userID).child("stamina").setValue(userStamina + getIntent().getIntExtra("stamina", 0));

        weekRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("progress");
        Calendar calendar = new GregorianCalendar();
        String week_year = Integer.toString(calendar.get(Calendar.WEEK_OF_YEAR));
        SimpleDateFormat formatterForDay = new SimpleDateFormat("E, dd-MM");
        String todayDay = formatterForDay.format(calendar.getTime());
        weekRef.child(week_year).child("days").child(todayDay).child("squats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    weekRef.child(week_year).child("days").child(todayDay).child("squats").setValue(snapshot.getValue(Integer.class) + overallSquats);
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

        new AlertDialog.Builder(SquatExerciseActivity.this)
                .setTitle("Exercise Finished, Well Done!")
                .setMessage(String.format("Total Time: %02dm and %02ds\nTotal squats: %d\nStamina +%02d\t\tHealth +%02d", seconds / 60, seconds % 60, overallSquats, getIntent().getIntExtra("stamina", 0), getIntent().getIntExtra("health", 0)))
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

    private void updateTextView() {
        txtSquatCount.setText(String.format("%02d / %02d", currentSquats, goal));
    }

    private void initViews() {
        layoutRound = findViewById(R.id.squatExerciseRoundLayout);
        layoutBreak = findViewById(R.id.squatExerciseBreakLayout);
        mainLayout = findViewById(R.id.squatExerciseMainLayout);

        txtTitle = findViewById(R.id.squatExerciseTxtTitle);
        txtRound1 = findViewById(R.id.squatExerciseTxtRound1);
        txtRound2 = findViewById(R.id.squatExerciseTxtRound2);
        txtRound3 = findViewById(R.id.squatExerciseTxtRound3);
        txtRound4 = findViewById(R.id.squatExerciseTxtRound4);
        txtRound5 = findViewById(R.id.squatExerciseTxtRound5);
        txtRound6 = findViewById(R.id.squatExerciseTxtRound6);
        txtTime = findViewById(R.id.squatExerciseTxtTime);

        accelerometer = new Accelerometer(SquatExerciseActivity.this, Sensor.TYPE_ACCELEROMETER);
        gravimeter = new Gravimeter(SquatExerciseActivity.this);
        AccX = findViewById(R.id.AccX);
        AccY = findViewById(R.id.AccY);
        AccZ = findViewById(R.id.AccZ);

        gravX = findViewById(R.id.gravityX);
        gravY = findViewById(R.id.gravityY);
        gravZ = findViewById(R.id.gravityZ);

        txtSquatCount = findViewById(R.id.squatExerciseTxtSquatCount);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        accelerometer.registerListener();
        gravimeter.registerListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        accelerometer.un_registerListener();
        gravimeter.un_registerListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SoundLibrary.stopSound();
    }

    @Override
    public void onBackPressed() {
        displayClosingAlertBox();
    }

    private void displayClosingAlertBox() {
        int seconds = (int) ((System.currentTimeMillis() - startMilliseconds) / 1000);
        new AlertDialog.Builder(SquatExerciseActivity.this, R.style.MyDialogTheme)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exiting the Exercise")
                .setMessage(String.format("Quiting yields no rewards!\nTotal Time: %02dm and %02ds\nTotal squats: %d", seconds / 60, seconds % 60, overallSquats))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("ON STOP: ", "YES");
                        accelerometer.un_registerListener();
                        gravimeter.un_registerListener();
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void setUpUser() {
        reference = FirebaseDatabase.getInstance().getReference("users");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userID = user.getUid();
        } else {
            Snackbar.make(SquatExerciseActivity.this, mainLayout, "Something went wrong, logout and login again!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            }).show();
        }
    }
}