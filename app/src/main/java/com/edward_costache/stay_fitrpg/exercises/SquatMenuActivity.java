package com.edward_costache.stay_fitrpg.exercises;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.edward_costache.stay_fitrpg.R;

public class SquatMenuActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton radioButtonVeryEasy, radioButtonEasy, radioButtonMedium, radioButtonHard;
    private TextView txtStrength, txtHealth, txtRound1, txtRound2, txtRound3, txtRound4, txtRound5, txtRound6;
    private Button btnStart;

    private int strengthGained;
    private int staminaGained;
    private int maxReps;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squat_menu);
        initViews();
        radioButtonMedium.setChecked(true);
        setMedium();
        updateViews();
        setOnCheckedListeners();
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
        strengthGained = 8;
        staminaGained = 6;
        maxReps = 16;

        //Round 4
        txtRound4.setVisibility(View.GONE);
        //Round 5
        txtRound5.setVisibility(View.GONE);
        //Round 6
        txtRound6.setVisibility(View.GONE);
    }

    private void setEasy()
    {
        strengthGained = 9;
        staminaGained = 7;
        maxReps = 18;

        //Round 4
        txtRound4.setVisibility(View.VISIBLE);
        txtRound4.setText(String.format("ROUND 4: %02d", (int)(maxReps*0.5)));
        //Round 5
        txtRound5.setVisibility(View.GONE);
        //Round 6
        txtRound6.setVisibility(View.GONE);

    }

    private void setMedium()
    {
        strengthGained = 10;
        staminaGained = 8;
        maxReps = 20;

        //Round 4
        txtRound4.setVisibility(View.VISIBLE);
        txtRound4.setText(String.format("ROUND 4: %02d", (int)(maxReps*0.5)));
        //Round 5
        txtRound5.setVisibility(View.VISIBLE);
        txtRound5.setText(String.format("ROUND 5: %02d", (int)(maxReps*0.8)));
        //Round 6
        txtRound6.setVisibility(View.GONE);


    }

    private void setHard()
    {
        strengthGained = 12;
        staminaGained = 10;
        maxReps = 24;

        //Round 4
        txtRound4.setVisibility(View.VISIBLE);
        txtRound4.setText(String.format("ROUND 4: %02d", (int)(maxReps*0.5)));
        //Round 5
        txtRound5.setVisibility(View.VISIBLE);
        txtRound5.setText(String.format("ROUND 5: %02d", (int)(maxReps*0.8)));
        //Round 6
        txtRound6.setVisibility(View.VISIBLE);
        txtRound6.setText(String.format("ROUND 6: %02d", maxReps));
    }

    private void updateViews()
    {
        //Round 1
        txtRound1.setText(String.format("ROUND 1: %02d", (int)(maxReps*0.5)));
        //Round 2
        txtRound2.setText(String.format("ROUND 2: %02d", (int)(maxReps*0.8)));
        //Round 3
        txtRound3.setText(String.format("ROUND 3: %02d", maxReps));


        txtStrength.setText(String.format("STRENGTH: +%02d", strengthGained));
        txtHealth.setText(String.format("STAMINA: +%02d", staminaGained));
    }


    private void initViews()
    {
        radioGroup = findViewById(R.id.squatDifficultyRadioGrp);
        radioButtonVeryEasy = findViewById(R.id.squatRadioButtonVeryEasy);
        radioButtonEasy = findViewById(R.id.squatRadioButtonEasy);
        radioButtonMedium = findViewById(R.id.squatRadioButtonMedium);
        radioButtonHard = findViewById(R.id.squatRadioButtonHard);

        txtStrength = findViewById(R.id.squatTxtStrength);
        txtHealth = findViewById(R.id.squatTxtHealth);
        txtRound1 = findViewById(R.id.squatTxtRound1);
        txtRound2 = findViewById(R.id.squatTxtRound2);
        txtRound3 = findViewById(R.id.squatTxtRound3);
        txtRound4 = findViewById(R.id.squatTxtRound4);
        txtRound5 = findViewById(R.id.squatTxtRound5);
        txtRound6 = findViewById(R.id.squatTxtRound6);

        btnStart = findViewById(R.id.squatBtnStart);
    }
}