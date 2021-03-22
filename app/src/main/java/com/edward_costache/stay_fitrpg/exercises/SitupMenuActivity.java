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

public class SitupMenuActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton radioButtonVeryEasy, radioButtonEasy, radioButtonMedium, radioButtonHard;
    private TextView txtStrength, txtHealth, txtRound1, txtRound2, txtRound3, txtRound4, txtRound5, txtRound6;
    private Button btnStart;

    private int maxReps;
    private int staminaGained;
    private int healthGained;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_situp_menu);
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
                    case R.id.situpRadioButtonVeryEasy:
                        setVeryEasy();
                        break;
                    case R.id.situpRadioButtonEasy:
                        setEasy();
                        break;
                    case R.id.situpRadioButtonMedium:
                        setMedium();
                        break;
                    case R.id.situpRadioButtonHard:
                        setHard();
                        break;

                }
                updateViews();
            }
        });
    }

    private void setVeryEasy()
    {
        staminaGained = 5;
        healthGained = 4;
        maxReps = 8;

        //Round 4
        txtRound4.setVisibility(View.GONE);
        //Round 5
        txtRound5.setVisibility(View.GONE);
        //Round 6
        txtRound6.setVisibility(View.GONE);
    }

    private void setEasy()
    {
        staminaGained = 6;
        healthGained = 5;
        maxReps = 10;

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
        staminaGained = 7;
        healthGained = 6;
        maxReps = 12;

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
        staminaGained = 9;
        healthGained = 8;
        maxReps = 16;

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


        txtStrength.setText(String.format("STAMINA: +%02d", staminaGained));
        txtHealth.setText(String.format("HEALTH: +%02d", healthGained));
    }

    private void initViews()
    {
        radioGroup = findViewById(R.id.situpDifficultyRadioGrp);
        radioButtonVeryEasy = findViewById(R.id.situpRadioButtonVeryEasy);
        radioButtonEasy = findViewById(R.id.situpRadioButtonEasy);
        radioButtonMedium = findViewById(R.id.situpRadioButtonMedium);
        radioButtonHard = findViewById(R.id.situpRadioButtonHard);

        txtStrength = findViewById(R.id.situpTxtStrength);
        txtHealth = findViewById(R.id.situpTxtHealth);
        txtRound1 = findViewById(R.id.situpTxtRound1);
        txtRound2 = findViewById(R.id.situpTxtRound2);
        txtRound3 = findViewById(R.id.situpTxtRound3);
        txtRound4 = findViewById(R.id.situpTxtRound4);
        txtRound5 = findViewById(R.id.situpTxtRound5);
        txtRound6 = findViewById(R.id.situpTxtRound6);

        btnStart = findViewById(R.id.situpBtnStart);
    }
}