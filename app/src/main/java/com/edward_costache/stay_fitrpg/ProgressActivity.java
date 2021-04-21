package com.edward_costache.stay_fitrpg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ProgressActivity extends AppCompatActivity {

    private RecyclerView weeksRevView;
    private WeekRecViewAdapter adapter;
    private ArrayList<ProgressWeek> weeksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        initViews();
        weeksList = new ArrayList<>();
        weeksRevView.setLayoutManager(new LinearLayoutManager(ProgressActivity.this));
        adapter = new WeekRecViewAdapter(ProgressActivity.this);
        displayWeeks();
    }

    private void initViews()
    {
        weeksRevView = findViewById(R.id.progressWeeksRV);
    }

    /**
     * A function for displaying all of the recorded weeks since the user has started exercising using the application.
     * The function uses a ValueEventListener to listen to the Firebase Database and fetch all of the weeks for that user, and store
     * that information in a ProgressWeek object, which is then added to a list.
     *
     * NOTE: When a ValueEventListener is constructed and added to a FirebaseDatabase reference, the code within onDataChanged() will be triggered when called once.
     * With a ValueEventListener, the code will also trigger reference the data in the reference changes.
     * A listenerForSingleValueEvent is only triggered when added to a reference.
     */
    private void displayWeeks() {
        FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("progress").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                weeksList.clear();
                if (snapshot.getChildrenCount() != 0) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ProgressWeek week = ds.getValue(ProgressWeek.class);
                        weeksList.add(week);
                    }
                }
                refreshRooms();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter.setWeeks(weeksList);
        weeksRevView.setAdapter(adapter);
        weeksRevView.setLayoutManager(new LinearLayoutManager(ProgressActivity.this));
    }

    private void refreshRooms() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}