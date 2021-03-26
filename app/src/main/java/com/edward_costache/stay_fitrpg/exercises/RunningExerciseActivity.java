package com.edward_costache.stay_fitrpg.exercises;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.edward_costache.stay_fitrpg.R;
import com.edward_costache.stay_fitrpg.util.StepDetector;

public class RunningExerciseActivity extends AppCompatActivity {

    private TextView txtStepCounter, txtDistance, txtSpeed, txtTime;
    private StepDetector stepDetector;

    private final int STEP_LENGTH = 78;
    private double startSeconds;
    private double distance;
    private double timeInSeconds;
    private double speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_exercise);
        startSeconds = System.currentTimeMillis()/1000.0;
        initViews();
    }

    private void initViews()
    {
        txtStepCounter = findViewById(R.id.stepCounter);
        txtDistance = findViewById(R.id.txtDistance);
        txtSpeed = findViewById(R.id.txtSpeed);
        txtTime = findViewById(R.id.txtTime);

        stepDetector = new StepDetector(RunningExerciseActivity.this);
        stepDetector.setListener(new StepDetector.Listener() {
            @Override
            public void onStep(int steps) {
                //TODO: FINISH RUNNING LOGIC, REWRITE RUNNING_INSTRUCTIONS
                txtStepCounter.setText(String.format("Steps: %d", steps));
                distance = ((double)steps * 78)/100.0;
                txtDistance.setText(String.format("Distance: %.2f m", distance));
                double currentSeconds = System.currentTimeMillis()/1000.0;
                timeInSeconds = currentSeconds - startSeconds;
                txtTime.setText(String.format("Time: %.2f", timeInSeconds));
                double speed = distance / timeInSeconds;
                txtSpeed.setText(String.format("Speed: %.2f", speed));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        stepDetector.registerListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stepDetector.un_registerListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stepDetector.un_registerListener();
    }
}