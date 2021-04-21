package com.edward_costache.stay_fitrpg.exercises;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.edward_costache.stay_fitrpg.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SquatMenuActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton radioButtonVeryEasy, radioButtonEasy, radioButtonMedium, radioButtonHard;
    private TextView txtStrength, txtStamina, txtRound1, txtRound2, txtRound3, txtRound4, txtRound5, txtRound6;
    private Button btnStart;

    private int strengthGained;
    private int staminaGained;
    private int maxReps;
    private final ArrayList<Integer> rounds = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squat_menu);
        initViews();
        radioButtonMedium.setChecked(true);
        setMedium();
        updateViews();
        setOnCheckedListeners();
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SquatMenuActivity.this, SquatExerciseActivity.class);
                intent.putExtra("strength", strengthGained);
                intent.putExtra("stamina", staminaGained);
                intent.putExtra("rounds", rounds);

                Log.i("ARRAY LIST: ", rounds.toString());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setOnCheckedListeners()
    {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.squatRadioButtonVeryEasy:
                        setVeryEasy();
                        break;
                    case R.id.squatRadioButtonEasy:
                        setEasy();
                        break;
                    case R.id.squatRadioButtonMedium:
                        setMedium();
                        break;
                    case R.id.squatRadioButtonHard:
                        setHard();
                        break;

                }
                updateViews();
            }
        });
    }

    private void setVeryEasy()
    {
        strengthGained = 6;
        staminaGained = 5;
        maxReps = 10;

        //Round 4
        txtRound4.setVisibility(View.GONE);
        //Round 5
        txtRound5.setVisibility(View.GONE);
        //Round 6
        txtRound6.setVisibility(View.GONE);

        rounds.clear();
        rounds.add((int)(maxReps*0.5));
        rounds.add((int)(maxReps*0.8));
        rounds.add(maxReps);
    }

    private void setEasy()
    {
        strengthGained = 8;
        staminaGained = 6;
        maxReps = 12;

        //Round 5
        txtRound5.setVisibility(View.GONE);
        //Round 6
        txtRound6.setVisibility(View.GONE);

        rounds.clear();
        rounds.add((int)(maxReps*0.5));
        rounds.add((int)(maxReps*0.8));
        rounds.add(maxReps);
        rounds.add((int)(maxReps*0.5));
    }

    private void setMedium()
    {
        strengthGained = 10;
        staminaGained = 9;
        maxReps = 14;

        txtRound6.setVisibility(View.GONE);

        rounds.clear();
        rounds.add((int)(maxReps*0.5));
        rounds.add((int)(maxReps*0.8));
        rounds.add(maxReps);
        rounds.add((int)(maxReps*0.5));
        rounds.add((int)(maxReps*0.8));
    }

    private void setHard()
    {
        strengthGained = 12;
        staminaGained = 11;
        maxReps = 118;

        rounds.clear();
        rounds.add((int)(maxReps*0.5));
        rounds.add((int)(maxReps*0.8));
        rounds.add(maxReps);
        rounds.add((int)(maxReps*0.5));
        rounds.add((int)(maxReps*0.8));
        rounds.add(maxReps);
    }

    private void updateViews()
    {
        //Round 1
        txtRound1.setText(String.format("ROUND 1: %02d", rounds.get(0)));
        //Round 2
        txtRound2.setText(String.format("ROUND 2: %02d", rounds.get(1)));
        //Round 3
        txtRound3.setText(String.format("ROUND 3: %02d", rounds.get(2)));


        //try catch used to stop the code at the required rounds.
        //calling setText will produce an error if the TextView is set to Gone
        //i.e setMedium() sets the visibility of round 5 and 6 TextViews to Gone which will produce an error here
        //unless its a try catch, the code will only update for round 4 TextView and skip the rest as round 5 will give error
        //this trick avoids if statements for all TextViews to check if they are visible.
        try {
            txtRound4.setText(String.format("ROUND 4: %02d", rounds.get(3)));
            txtRound4.setVisibility(View.VISIBLE);
            txtRound5.setText(String.format("ROUND 5: %02d", rounds.get(4)));
            txtRound5.setVisibility(View.VISIBLE);
            txtRound6.setText(String.format("ROUND 6: %02d", rounds.get(5)));
            txtRound6.setVisibility(View.VISIBLE);
        }
        catch (Exception e)
        {
            Log.i("UPDATE VIEWS", ": STOPPED");
        }

        txtStrength.setText(String.format("STAMINA: +%02d", strengthGained));
        txtStamina.setText(String.format("HEALTH: +%02d", staminaGained));
    }


    private void initViews()
    {
        radioGroup = findViewById(R.id.squatDifficultyRadioGrp);
        radioButtonVeryEasy = findViewById(R.id.squatRadioButtonVeryEasy);
        radioButtonEasy = findViewById(R.id.squatRadioButtonEasy);
        radioButtonMedium = findViewById(R.id.squatRadioButtonMedium);
        radioButtonHard = findViewById(R.id.squatRadioButtonHard);

        txtStrength = findViewById(R.id.squatTxtStrength);
        txtStamina = findViewById(R.id.squatTxtStamina);
        txtRound1 = findViewById(R.id.squatTxtRound1);
        txtRound2 = findViewById(R.id.squatTxtRound2);
        txtRound3 = findViewById(R.id.squatTxtRound3);
        txtRound4 = findViewById(R.id.squatTxtRound4);
        txtRound5 = findViewById(R.id.squatTxtRound5);
        txtRound6 = findViewById(R.id.squatTxtRound6);

        btnStart = findViewById(R.id.squatBtnStart);
    }
}