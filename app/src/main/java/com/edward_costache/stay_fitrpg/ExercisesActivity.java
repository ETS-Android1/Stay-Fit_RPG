package com.edward_costache.stay_fitrpg;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.edward_costache.stay_fitrpg.util.Util;

public class ExercisesActivity extends AppCompatActivity {

    private com.google.android.material.card.MaterialCardView cardViewExercise1, cardViewExercise2, cardViewExercise3, cardViewExercise4, cardViewExercise5, cardViewExercise6;

    private androidx.constraintlayout.widget.ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);
        initViews();
        initOnClickListeners();
    }

    private void initViews() {
        cardViewExercise1 = findViewById(R.id.exercises_pageCardViewExercise1);
        cardViewExercise2 = findViewById(R.id.exercises_pageCardViewExercise2);
        cardViewExercise3 = findViewById(R.id.exercises_pageCardViewExercise3);
        cardViewExercise4 = findViewById(R.id.exercises_pageCardViewExercise4);
        cardViewExercise5 = findViewById(R.id.exercises_pageCardViewExercise5);
        cardViewExercise6 = findViewById(R.id.exercises_pageCardViewExercise6);

        layout = findViewById(R.id.exercises_pageLayout);
    }

    private void initOnClickListeners()
    {
        cardViewExercise1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.displayNotImplemented(ExercisesActivity.this, layout);
            }
        });

        cardViewExercise2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.displayNotImplemented(ExercisesActivity.this, layout);
            }
        });

        cardViewExercise3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.displayNotImplemented(ExercisesActivity.this, layout);
            }
        });

        cardViewExercise4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.displayNotImplemented(ExercisesActivity.this, layout);
            }
        });

        cardViewExercise5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.displayNotImplemented(ExercisesActivity.this, layout);
            }
        });

        cardViewExercise6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.displayNotImplemented(ExercisesActivity.this, layout);
            }
        });
    }
}