package com.edward_costache.stay_fitrpg.exercises;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.edward_costache.stay_fitrpg.R;

public class RunningMenuActivity extends AppCompatActivity {

    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_menu);
        initViews();
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RunningMenuActivity.this, RunningExerciseActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initViews()
    {
        btnStart = findViewById(R.id.runningBtnStart);
    }
}