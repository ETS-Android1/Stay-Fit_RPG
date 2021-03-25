package com.edward_costache.stay_fitrpg.exercises;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.edward_costache.stay_fitrpg.R;
import com.edward_costache.stay_fitrpg.util.Accelerometer;
import com.edward_costache.stay_fitrpg.util.Gyroscope;

public class SquatExerciseActivity extends AppCompatActivity {

    private Accelerometer accelerometer;
    private Gyroscope gyroscope;

    private TextView AccX, AccY, AccZ, GyroX, GyroY, GyroZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squat_exercise);
        initViews();

        accelerometer = new Accelerometer(SquatExerciseActivity.this);
        gyroscope = new Gyroscope(SquatExerciseActivity.this);

        accelerometer.setListener(new Accelerometer.Listener() {
            @Override
            public void onTranslation(float tx, float ty, float tz) {
                //TODO: FINISH SQUAT EXERCISE
                AccX.setText(String.format("X:%.3f", tx));
                AccY.setText(String.format("Y:%.3f", ty));
                AccZ.setText(String.format("Z:%.3f", tz));
            }
        });

        gyroscope.setListener(new Gyroscope.Listener() {
            @Override
            public void onRotation(float rx, float ry, float rz) {
                GyroX.setText(String.format("X:%.3f", rx));
                GyroY.setText(String.format("Y:%.3f", ry));
                GyroZ.setText(String.format("Z:%.3f", rz));
            }
        });
    }

    private void initViews()
    {
        AccX = findViewById(R.id.AccX);
        AccY = findViewById(R.id.AccY);
        AccZ = findViewById(R.id.AccZ);

        GyroX = findViewById(R.id.GyroX);
        GyroY = findViewById(R.id.GyroY);
        GyroZ = findViewById(R.id.GyroZ);
    }

    @Override
    protected void onResume() {
        super.onResume();
        accelerometer.registerListener();
        gyroscope.registerListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        accelerometer.un_registerListener();
        gyroscope.un_registerListener();
    }
}