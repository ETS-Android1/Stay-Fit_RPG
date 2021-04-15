package com.edward_costache.stay_fitrpg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OnlineMenuActivity extends AppCompatActivity {

    private RecyclerView roomsRecView;
    private Button btnCreateRoom;
    private ArrayList<Room> rooms;
    private RoomsRecViewAdapter adapter;

    public static Activity OMA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_menu);
        OMA = this;
        initViews();
        roomsRecView.setLayoutManager(new LinearLayoutManager(this));
        rooms = new ArrayList<>();
        displayRooms();

        btnCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            User user = snapshot.getValue(User.class);
                            String roomName = user.getUsername() + "'s room";
                            FirebaseDatabase.getInstance().getReference("rooms").child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "ROOM").setValue(new Room(roomName, FirebaseAuth.getInstance().getCurrentUser().getUid()));
                            refreshRooms();
                            Intent intent = new Intent(OnlineMenuActivity.this, RoomActivity.class);
                            intent.putExtra("roomName", roomName);
                            intent.putExtra("role", "host");
                            intent.putExtra("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            startActivity(intent);
                            rooms.clear();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

    private void displayRooms() {
        FirebaseDatabase.getInstance().getReference("rooms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rooms.clear();
                if (snapshot.getChildrenCount() != 0) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Room room = ds.getValue(Room.class);
                        rooms.add(room);
                    }
                }
                refreshRooms();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter.setRooms(rooms);
        roomsRecView.setAdapter(adapter);
        roomsRecView.setLayoutManager(new LinearLayoutManager(OnlineMenuActivity.this));
    }

    private void refreshRooms() {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initViews() {
        adapter = new RoomsRecViewAdapter(OnlineMenuActivity.this, FirebaseAuth.getInstance().getCurrentUser().getUid());
        roomsRecView = findViewById(R.id.onlineMenuRecViewRooms);
        btnCreateRoom = findViewById(R.id.onlineMenuBtnCreateRoom);
    }
}