package com.edward_costache.stay_fitrpg;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.edward_costache.stay_fitrpg.exercises.PushupMenuActivity;
import com.edward_costache.stay_fitrpg.util.Util;

public class ExercisesActivity extends AppCompatActivity {

    private com.google.android.material.card.MaterialCardView cardViewExercise1, cardViewExercise2, cardViewExercise3, cardViewExercise4, cardViewExercise5, cardViewExercise6;

    private androidx.constraintlayout.widget.ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);
        initViews();
        initClickListeners();
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

    private void initClickListeners()
    {
        cardViewExercise1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExercisesActivity.this, PushupMenuActivity.class);
                startActivity(intent);
            }
        });

        cardViewExercise1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(ExercisesActivity.this, R.style.MyDialogTheme)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle("What are pushups?")
                        .setMessage(R.string.pushup_description)
                        .setPositiveButton("OK", null)
                        .show();
                return true;
            }
        });

        cardViewExercise2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.displayNotImplemented(ExercisesActivity.this, layout);
            }
        });

        cardViewExercise2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(ExercisesActivity.this, R.style.MyDialogTheme)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle("What is Running?")
                        .setMessage(R.string.running_description)
                        .setPositiveButton("OK", null)
                        .show();
                return true;
            }
        });

        cardViewExercise3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.displayNotImplemented(ExercisesActivity.this, layout);
            }
        });

        cardViewExercise3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(ExercisesActivity.this, R.style.MyDialogTheme)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle("What are Situps?")
                        .setMessage(R.string.situp_description)
                        .setPositiveButton("OK", null)
                        .show();
                return true;
            }
        });

        cardViewExercise4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.displayNotImplemented(ExercisesActivity.this, layout);
            }
        });

        cardViewExercise4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Util.displayNotImplemented(ExercisesActivity.this, layout);
                return true;
            }
        });

        cardViewExercise5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.displayNotImplemented(ExercisesActivity.this, layout);
            }
        });

        cardViewExercise5.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Util.displayNotImplemented(ExercisesActivity.this, layout);
                return true;
            }
        });

        cardViewExercise6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.displayNotImplemented(ExercisesActivity.this, layout);
            }
        });

        cardViewExercise6.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Util.displayNotImplemented(ExercisesActivity.this, layout);
                return true;
            }
        });
    }
}