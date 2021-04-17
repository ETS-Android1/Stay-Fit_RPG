package com.edward_costache.stay_fitrpg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.edward_costache.stay_fitrpg.exercises.PushupMenuActivity;
import com.edward_costache.stay_fitrpg.exercises.RunningMenuActivity;
import com.edward_costache.stay_fitrpg.exercises.SitupMenuActivity;
import com.edward_costache.stay_fitrpg.exercises.SquatMenuActivity;
import com.edward_costache.stay_fitrpg.util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class ExercisesActivity extends AppCompatActivity {

    private com.google.android.material.card.MaterialCardView cardViewExercise1, cardViewExercise2, cardViewExercise3, cardViewExercise4, cardViewExercise5, cardViewExercise6;
    private androidx.constraintlayout.widget.ConstraintLayout layout;

    private DatabaseReference weekRef;
    private HashMap<String, ProgressDay> days;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);
        initViews();
        initClickListeners();
        weekRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("progress");

        Calendar weekCalendar = new GregorianCalendar();
        SimpleDateFormat formatterForWeek = new SimpleDateFormat("dd MMMM yyyy");
        String startOfWeek = formatterForWeek.format(weekCalendar.getTime());
        String week_year = Integer.toString(weekCalendar.get(Calendar.WEEK_OF_YEAR));
        weekCalendar.add(Calendar.WEEK_OF_YEAR, 1);
        String endOfWeek = formatterForWeek.format(weekCalendar.getTime());

        weekRef.child(week_year).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() == null)
                {
                    days = new HashMap<>();
                    String weekName = startOfWeek.substring(0, startOfWeek.length()-5)+" - "+endOfWeek.substring(0, endOfWeek.length()-5);

                    Calendar dayCalendar = new GregorianCalendar();
                    SimpleDateFormat formatterForDay = new SimpleDateFormat("E, dd-MM");
                    for(int i = 1; i<8;i++)
                    {
                        //adding i to the name, so that in the firebase database they are sorted numerically
                        String name = i+formatterForDay.format(dayCalendar.getTime());
                        days.put(name, new ProgressDay());
                        dayCalendar.add(Calendar.DAY_OF_MONTH, 1);
                    }
                    weekRef.child(week_year).setValue(new ProgressWeek(weekName, days));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                        .setTitle("What are Pushups?")
                        .setMessage(R.string.pushup_description)
                        .setPositiveButton("OK", null)
                        .show();
                return true;
            }
        });

        cardViewExercise2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExercisesActivity.this, RunningMenuActivity.class);
                startActivity(intent);
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
                Intent intent = new Intent(ExercisesActivity.this, SitupMenuActivity.class);
                startActivity(intent);
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
                Intent intent = new Intent(ExercisesActivity.this, SquatMenuActivity.class);
                startActivity(intent);
            }
        });

        cardViewExercise4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(ExercisesActivity.this, R.style.MyDialogTheme)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle("What are Squats?")
                        .setMessage(R.string.squat_description)
                        .setPositiveButton("OK", null)
                        .show();
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