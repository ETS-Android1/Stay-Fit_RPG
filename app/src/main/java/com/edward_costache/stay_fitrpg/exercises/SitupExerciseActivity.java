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
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edward_costache.stay_fitrpg.LoginActivity;
import com.edward_costache.stay_fitrpg.R;
import com.edward_costache.stay_fitrpg.User;
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

public class SitupExerciseActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "SITUP_SENSOR - ";
    private LinearLayout layoutRound, layoutBreak;
    private TextView txtTitle;
    private boolean isRound = true;

    private DatabaseReference reference;
    private String userID;
    private androidx.constraintlayout.widget.ConstraintLayout mainLayout;
    private User userProfile;
    private TextView txtX, txtY, txtZ;

    // Round
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView txtSitupCount;
    private ArrayList<Integer> rounds;
    private int round = 0;
    private int maxRounds, goal, currentSitups, userStamina, userHealth, overallSitups;
    private boolean ready = true;
    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;

    // Break
    private CountDownTimer breakTimer;
    private long startMilliseconds;
    private TextView txtRound1, txtRound2, txtRound3, txtRound4, txtRound5, txtRound6, txtTime;
    private final int BREAK_TIME = 2;

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
    public void onBackPressed() {
        displayClosingAlertBox();
    }

    @Override
    protected void onStop() {
        super.onStop();
        un_registerAccelerometer();
        SoundLibrary.stopSound();
    }

    @Override
    protected void onPause() {
        super.onPause();
        un_registerAccelerometer();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        registerAccelerometer();
    }

    private void displayClosingAlertBox() {
        new AlertDialog.Builder(SitupExerciseActivity.this, R.style.MyDialogTheme)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exiting the Application")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("ON STOP: ", "YES");
                        un_registerAccelerometer();
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

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        registerAccelerometer();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mediaPlayer = MediaPlayer.create(SitupExerciseActivity.this, R.raw.ding);

        txtX = findViewById(R.id.xAxis);
        txtY = findViewById(R.id.yAxis);
        txtZ = findViewById(R.id.zAxis);
    }

    private void registerAccelerometer() {
        try {
            sensorManager.registerListener(SitupExerciseActivity.this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            Log.i(TAG, "registerAccelerometer: ACCELEROMETER REGISTERED");
        } catch (Exception e) {
            Log.i(TAG, "registerAccelerometer: CANNOT REGISTER SENSOR");
        }
    }

    private void un_registerAccelerometer() {
        try {
            sensorManager.unregisterListener(SitupExerciseActivity.this, accelerometer);
        } catch (Exception e) {
            Log.i(TAG, "un_registerAccelerometer: CANNOT UNREGISTER SENSOR");
        }
    }

    private void updateTextView() {
        txtSitupCount.setText(String.format("%02d / %02d", currentSitups, goal));
    }

    @SuppressLint("ResourceAsColor")
    private void switchLayout() {
        if (isRound) {
            // Change to break
            ready = false;
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
        reference.child(userID).child("stamina").setValue(userStamina + getIntent().getIntExtra("stamina", 0));
        reference.child(userID).child("health").setValue(userHealth + getIntent().getIntExtra("health", 0));

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

    @Override
    public void onSensorChanged(SensorEvent event) {
        /*
        How this method works:
        This method which is Overwritten is called each and every time the Hardware Sensor is changed.
        In order to somewhat accurately decide which movement is a situp, i use the Z axis of the Accelerometer to determine if the phone has been tilted
        at least a certain distance. For this to work, i need to predefine a starting value i.e what the z value is roughly when the user is about to do a situp
        and i need to define a distance which is used to calculate the difference between the starting distance and the Z axis when the method is called. If this difference
        is equal or bigger than the predefined distance, a situp is recorded.

        NOTES:
        I though of building a Machine Learning model that could differentiate between what is a situp and what isn't. However, that was quite ambitious because it would require a lot of data to be accurate,
        and this data would have to be recorded manually, we are talking about me doing thousands of situps, which would be a damn good exercise but very impractical.
        Additionally, it was a question of, how much accurate would that be, will predefined values work fairy wel. After some testing i have found that if the user takes some steps to prepare the setup, situps could be
        recorded quite well.
         */
        double z = event.values[2];
        double startingValue = -6.7;
        double distance = 5.5;

        if (z <= 1 && ready) {
            if ((z - startingValue) >= distance) {
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
            if (z <= startingValue) {
                ready = true;
            }
        }

        // Testing
        //txtX.setText(String.format("X Axis: %.3f", x));
        //txtY.setText(String.format("Y Axis: %.3f", y));
        //txtZ.setText(String.format("Z Axis: %.3f", z));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i("SITUP EXERCISE: ", "ACCURACY CHANGED TO " + accuracy);
    }
}