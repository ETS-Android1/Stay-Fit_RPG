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

import java.util.ArrayList;

public class RunningMenuActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton radioButtonMedium;
    private TextView txtStamina, txtAgility, txtRound1, txtRound2, txtRound3, txtRound4, txtRound5, txtRound6;
    private Button btnStart;

    private int staminaGained;
    private int agilityGained;
    private double maxDistance;
    private final ArrayList<Double> rounds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_menu);
        initViews();
        radioButtonMedium.setChecked(true);
        setMedium();
        updateViews();
        setOnCheckedListeners();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RunningMenuActivity.this, RunningExerciseActivity.class);

                double[] arr = new double[rounds.size()];
                for(int i = 0; i<rounds.size(); i++)
                {
                    arr[i] = rounds.get(i);
                }

                intent.putExtra("stamina", staminaGained);
                intent.putExtra("agility", agilityGained);
                intent.putExtra("rounds", arr);

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
                    case R.id.runningRadioButtonVeryEasy:
                        setVeryEasy();
                        break;
                    case R.id.runningRadioButtonEasy:
                        setEasy();
                        break;
                    case R.id.runningRadioButtonMedium:
                        setMedium();
                        break;
                    case R.id.runningRadioButtonHard:
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
        agilityGained = 6;
        maxDistance = 1.0;

        //Round 4
        txtRound4.setVisibility(View.GONE);
        //Round 5
        txtRound5.setVisibility(View.GONE);
        //Round 6
        txtRound6.setVisibility(View.GONE);

        rounds.clear();
        rounds.add((maxDistance*0.5));
        rounds.add((maxDistance*0.8));
        rounds.add(maxDistance);
    }

    private void setEasy()
    {
        staminaGained = 7;
        agilityGained = 7;
        maxDistance = 1.8;

        //Round 5
        txtRound5.setVisibility(View.GONE);
        //Round 6
        txtRound6.setVisibility(View.GONE);

        rounds.clear();
        rounds.add((maxDistance*0.5));
        rounds.add((maxDistance*0.8));
        rounds.add(maxDistance);
        rounds.add((maxDistance*0.5));
    }

    private void setMedium()
    {
        staminaGained = 9;
        agilityGained = 11;
        maxDistance = 2.4;

        txtRound6.setVisibility(View.GONE);

        rounds.clear();
        rounds.add((maxDistance*0.5));
        rounds.add((maxDistance*0.8));
        rounds.add(maxDistance);
        rounds.add((maxDistance*0.5));
        rounds.add((maxDistance*0.8));
    }

    private void setHard()
    {
        staminaGained = 12;
        agilityGained = 15;
        maxDistance = 4.0;

        rounds.clear();
        rounds.add((maxDistance*0.5));
        rounds.add((maxDistance*0.8));
        rounds.add(maxDistance);
        rounds.add((maxDistance*0.5));
        rounds.add((maxDistance*0.8));
        rounds.add(maxDistance);
    }

    private void updateViews()
    {
        //Round 1
        txtRound1.setText(String.format("ROUND 1: %.2fkm", rounds.get(0)));
        //Round 2
        txtRound2.setText(String.format("ROUND 2: %.2fkm", rounds.get(1)));
        //Round 3
        txtRound3.setText(String.format("ROUND 3: %.2fkm", rounds.get(2)));

        //try catch used to stop the code at the required rounds.
        //calling setText will produce an error if the TextView is set to Gone
        //i.e setMedium() sets the visibility of round 5 and 6 TextViews to Gone which will produce an error here
        //unless its a try catch, the code will only update for round 4 TextView and skip the rest as round 5 will give error
        //this trick avoids if statements for all TextViews to check if they are visible.
        try {
            txtRound4.setText(String.format("ROUND 4: %.2fkm", rounds.get(3)));
            txtRound4.setVisibility(View.VISIBLE);
            txtRound5.setText(String.format("ROUND 5: %.2fkm", rounds.get(4)));
            txtRound5.setVisibility(View.VISIBLE);
            txtRound6.setText(String.format("ROUND 6: %.2fkm", rounds.get(5)));
            txtRound6.setVisibility(View.VISIBLE);
        }
        catch (Exception ignored)
        {
        }

        txtStamina.setText(String.format("STAMINA: +%02d", staminaGained));
        txtAgility.setText(String.format("AGILITY: +%02d", agilityGained));
    }

    private void initViews()
    {
        radioGroup = findViewById(R.id.runningDifficultyRadioGrp);
        radioButtonMedium = findViewById(R.id.runningRadioButtonMedium);

        txtStamina = findViewById(R.id.runningTxtStrength);
        txtAgility = findViewById(R.id.runningTxtHealth);
        txtRound1 = findViewById(R.id.runningTxtRound1);
        txtRound2 = findViewById(R.id.runningTxtRound2);
        txtRound3 = findViewById(R.id.runningTxtRound3);
        txtRound4 = findViewById(R.id.runningTxtRound4);
        txtRound5 = findViewById(R.id.runningTxtRound5);
        txtRound6 = findViewById(R.id.runningTxtRound6);

        btnStart = findViewById(R.id.runningBtnStart);
    }
}