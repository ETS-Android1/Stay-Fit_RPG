package com.edward_costache.stay_fitrpg;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edward_costache.stay_fitrpg.util.Util;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity implements SensorEventListener{

    private TextView txtUsername, txtProgress, txtLevel, txtOverallSteps;
    private Button btnLogout, btnInrSteps, btnReset, btnCharacter;
    private com.google.android.material.card.MaterialCardView cardViewTrain, cardViewFight, cardViewProgress;
    private androidx.constraintlayout.widget.ConstraintLayout layout;

    private pl.pawelkleczkowski.customgauge.CustomGauge progressBarTest;
    private ProgressBar progressBarHealth, progressBarStamina, progressBarAgility, progressBarStrength;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private DatabaseReference reference;
    private SharedPreferences sharedPreferences;

    private SensorManager sensorManager;
    private Sensor countSensor;

    private User userProfile;
    private String username;
    private String userID;
    private double health;
    private double strength;
    private double stamina;
    private double agility;
    private int steps;
    private int overallSteps;
    private int progressMax;
    private double level;

    private final int STARTING_STEP_GOAL = 100;
    private final double MULTIPLIER = 1.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);

        // Set-up functions that initialize declarations
        initViews();
        setUpStepDetector();
        setUpUser();

        Log.i("TODAY TIME: ", Long.toString(Util.getToday()));
        Log.i("TOMORROW TIME: ", Long.toString(Util.getTomorrow()));
        Log.i("CURRENT TIME: ", Long.toString(System.currentTimeMillis()));

        // If it is a new day, reset the user progress, but not the level nor the steps until next level
        if(sharedPreferences.getLong("time", 0) == Util.getToday())
        {
            Log.i("OLD DAY: ", "NOT A NEW DAY, STEPS REMAIN SAME");
        }
        else if(sharedPreferences.getLong("time", 0) > 0)
        {
            sharedPreferences.edit().putInt("progress", 0).apply();
            Log.i("NEW DAY: ", "A NEW DAY, THEREFORE STEPS RESET");
        }
        else
        {
            Log.i("TIME: ", "RESET VALUE");
        }

        // If the application has been closed before, retrieve the progress
        if(sharedPreferences.getInt("overallSteps", 0) != 0)
        {
            steps = sharedPreferences.getInt("progress", 0);
            overallSteps = sharedPreferences.getInt("overallSteps", 0);
        }
        displayUserInfo();
        initOnClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            sensorManager.registerListener(ProfileActivity.this, countSensor, SensorManager.SENSOR_DELAY_UI);
        }
        catch (Exception e)
        {
            Log.i("SENSOR MANAGER: ", "Failed to unregister the listener");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            sensorManager.unregisterListener(ProfileActivity.this);
        }
        catch (Exception e)
        {
            Log.i("SENSOR MANAGER: ", "Failed to unregister the listener");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        sharedPreferences.edit().putInt("progress", steps).apply();
        sharedPreferences.edit().putInt("overallSteps", overallSteps).apply();
        sharedPreferences.edit().putLong("time", Util.getToday()).apply();
    }

    private void setUpStepDetector()
    {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        countSensor = sensorManager .getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(ProfileActivity.this, countSensor, SensorManager.SENSOR_DELAY_UI);
    }

    private void setUpUser()
    {
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("users");
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
    }

    private void logout()
    {
        mAuth.signOut();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
        finish();
    }

    private void initViews()
    {
        txtUsername = findViewById(R.id.profileTxtUsername);
        txtProgress = findViewById(R.id.profileTxtProgress);
        txtLevel = findViewById(R.id.profileTxtLevel);
        txtOverallSteps = findViewById(R.id.profileTxtOverallSteps);

        cardViewTrain = findViewById(R.id.profileCardViewTrain);
        cardViewFight = findViewById(R.id.profileCardViewFight);
        cardViewProgress = findViewById(R.id.profileCardViewProgress);

        btnLogout = findViewById(R.id.profileBtnLogout);
        btnInrSteps = findViewById(R.id.profileBtnIncreaseSteps);
        btnReset = findViewById(R.id.profileBtnReset);
        btnCharacter = findViewById(R.id.profileBtnCharacter);

        progressBarTest = findViewById(R.id.profileProgressBar);
        progressBarHealth = findViewById(R.id.profileProgressBarHealth);
        progressBarAgility = findViewById(R.id.profileProgressBarAgility);
        progressBarStamina = findViewById(R.id.profileProgressBarStamina);
        progressBarStrength = findViewById(R.id.profileProgressBarStrength);

        layout = findViewById(R.id.profileLayout);
    }

    private void initOnClickListeners()
    {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        btnInrSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                steps += 10;
                overallSteps += 10;
                updateSteps(steps, progressMax);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                sharedPreferences.edit().putInt("progress", 0).apply();
                steps = 0;
                overallSteps = 0;
                reference.child(userID).child("level").setValue(1);
                sharedPreferences.edit().putLong("time", 0).apply();
                finishAndRemoveTask();
            }
        });

        cardViewTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ExercisesActivity.class);
                startActivity(intent);
            }
        });
        cardViewFight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.displayNotImplemented(ProfileActivity.this, layout);
            }
        });
        cardViewProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.displayNotImplemented(ProfileActivity.this, layout);
            }
        });

        btnCharacter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.displayNotImplemented(ProfileActivity.this, layout);
            }
        });

        txtLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, EasterEggActivity.class));
            }
        });
    }

    private void displayUserInfo()
    {
        reference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    userProfile = snapshot.getValue(User.class);
                    level = userProfile.getLevel();
                    username = userProfile.getUsername();

                    strength = userProfile.getStrength();
                    health = userProfile.getHealth();
                    agility = userProfile.getAgility();
                    stamina = userProfile.getStamina();

                    if(level == 1)
                    {
                        progressMax = STARTING_STEP_GOAL;
                    }
                    else
                    {
                        // A formula for deriving the required steps for next level
                        double levelPercent = ((level/7) * (level/7)) + MULTIPLIER;
                        progressMax = (int)(STARTING_STEP_GOAL * levelPercent);
                    }
                    txtUsername.setText(username);
                    txtLevel.setText(String.format("LEVEL: %d", (int)level));
                    progressBarTest.setEndValue(progressMax);
                    updateSteps(steps, progressMax);

                    progressBarHealth.setProgress((int)health);
                    progressBarAgility.setProgress((int)agility);
                    progressBarStamina.setProgress((int)stamina);
                    progressBarStrength.setProgress((int)strength);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Log.i("DISPLAY USER INFO: ", "User is null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void updateSteps(int progress, int progressMax)
    {
        if(progress >= progressMax)
        {
            increaseLevel();
        }
        progressBarTest.setValue(progress);
        txtProgress.setText(String.format("%04d/%04d", progress, progressMax));
        txtOverallSteps.setText(String.format("%05d", overallSteps));
    }

    private void increaseLevel()
    {
        reference.child(userID).child("level").setValue(level+1);
        steps = 0;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        steps++;
        overallSteps++;
        updateSteps(steps, progressMax);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i("SENSOR: ", "Accuracy changed");
    }

    @Override
    public void onBackPressed() {
        displayClosingAlertBox();
    }

    private void displayClosingAlertBox()
    {
        new AlertDialog.Builder(ProfileActivity.this, R.style.MyDialogTheme)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exiting the Application")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("ON STOP: ", "YES");
                        finishAndRemoveTask();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}