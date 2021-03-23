package com.edward_costache.stay_fitrpg.exercises;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.edward_costache.stay_fitrpg.R;

import java.util.ArrayList;

public class PushupMenuActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton radioButtonVeryEasy, radioButtonEasy, radioButtonMedium, radioButtonHard;
    private TextView txtStrength, txtHealth, txtRound1, txtRound2, txtRound3, txtRound4, txtRound5, txtRound6;
    private Button btnStart;

    private int strengthGained;
    private int healthGained;
    private int maxReps;
    private final ArrayList<Integer> rounds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushup_menu);
        initViews();
        radioButtonMedium.setChecked(true);
        setMedium();
        updateViews();
        setOnCheckedListeners();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PushupMenuActivity.this, PushupExerciseActivity.class);
                intent.putExtra("strength", strengthGained);
                intent.putExtra("health", healthGained);
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
                    case R.id.pushupRadioButtonVeryEasy:
                        setVeryEasy();
                        break;
                    case R.id.pushupRadioButtonEasy:
                        setEasy();
                        break;
                    case R.id.pushupRadioButtonMedium:
                        setMedium();
                        break;
                    case R.id.pushupRadioButtonHard:
                        setHard();
                        break;

                }
                updateViews();
            }
        });
    }

    private void setVeryEasy()
    {
        strengthGained = 5;
        healthGained = 3;
        maxReps = 4;

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
        strengthGained = 6;
        healthGained = 4;
        maxReps = 8;

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
        strengthGained = 8;
        healthGained = 5;
        maxReps = 12;

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
        strengthGained = 10;
        healthGained = 6;
        maxReps = 16;

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


        txtStrength.setText(String.format("STRENGTH: +%02d", strengthGained));
        txtHealth.setText(String.format("HEALTH: +%02d", healthGained));
    }


    private void initViews()
    {
        radioGroup = findViewById(R.id.pushupDifficultyRadioGrp);
        radioButtonVeryEasy = findViewById(R.id.pushupRadioButtonVeryEasy);
        radioButtonEasy = findViewById(R.id.pushupRadioButtonEasy);
        radioButtonMedium = findViewById(R.id.pushupRadioButtonMedium);
        radioButtonHard = findViewById(R.id.pushupRadioButtonHard);

        txtStrength = findViewById(R.id.pushupTxtStrength);
        txtHealth = findViewById(R.id.pushupTxtHealth);
        txtRound1 = findViewById(R.id.pushupTxtRound1);
        txtRound2 = findViewById(R.id.pushupTxtRound2);
        txtRound3 = findViewById(R.id.pushupTxtRound3);
        txtRound4 = findViewById(R.id.pushupTxtRound4);
        txtRound5 = findViewById(R.id.pushupTxtRound5);
        txtRound6 = findViewById(R.id.pushupTxtRound6);

        btnStart = findViewById(R.id.pushupBtnStart);
    }
}