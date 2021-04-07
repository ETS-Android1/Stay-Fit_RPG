package com.edward_costache.stay_fitrpg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    private RelativeLayout roomsLayout;
    private ConstraintLayout makeRoomLayout;

    private EditText editTxtRoomName;
    private Button btnCreateRoom, btnCreateRoomConfirm;

    private ArrayList<Room> rooms;

    private boolean creatingRoom = false;

    private RoomsRecViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_menu);

        initViews();
        roomsRecView.setLayoutManager(new LinearLayoutManager(this));
        rooms = new ArrayList<>();
        displayRooms();

        roomsLayout.setVisibility(View.VISIBLE);
        makeRoomLayout.setVisibility(View.GONE);

        btnCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creatingRoom = true;
                roomsLayout.setVisibility(View.GONE);
                makeRoomLayout.setVisibility(View.VISIBLE);
            }
        });

        btnCreateRoomConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(creatingRoom)
                {
                    String roomName = editTxtRoomName.getText().toString();
                    FirebaseDatabase.getInstance().getReference("rooms").child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"ROOM").setValue(new Room(roomName, FirebaseAuth.getInstance().getCurrentUser().getUid()));
                    refreshRooms();
                    creatingRoom = false;
                    roomsLayout.setVisibility(View.VISIBLE);
                    makeRoomLayout.setVisibility(View.GONE);

                    Intent intent = new Intent(OnlineMenuActivity.this, RoomActivity.class);
                    intent.putExtra("roomName", roomName);
                    intent.putExtra("role", "host");
                    intent.putExtra("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    startActivity(intent);
                }
            }
        });

    }

    private void displayRooms()
    {
        FirebaseDatabase.getInstance().getReference("rooms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rooms.clear();
                if(snapshot.getChildrenCount() != 0)
                {
                    for(DataSnapshot ds : snapshot.getChildren())
                    {
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

    private void refreshRooms()
    {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if(creatingRoom)
        {
            roomsLayout.setVisibility(View.VISIBLE);
            makeRoomLayout.setVisibility(View.GONE);
            editTxtRoomName.setText("");
            creatingRoom = false;
        }
        else
        {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseDatabase.getInstance().getReference("rooms").child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"-").removeValue();
        rooms.clear();
    }

    private void initViews()
    {
        adapter = new RoomsRecViewAdapter(OnlineMenuActivity.this, FirebaseAuth.getInstance().getCurrentUser().getUid());
        roomsRecView = findViewById(R.id.onlineMenuRecViewRooms);

        roomsLayout = findViewById(R.id.onlineMenuRoomsLayout);
        makeRoomLayout = findViewById(R.id.onlineMenuMakeRoomLayout);

        editTxtRoomName = findViewById(R.id.onlineMenuEditTxtRoomName);
        btnCreateRoom = findViewById(R.id.onlineMenuBtnCreateRoom);
        btnCreateRoomConfirm = findViewById(R.id.onlineMenuBtnConfirmRoom);
    }
}