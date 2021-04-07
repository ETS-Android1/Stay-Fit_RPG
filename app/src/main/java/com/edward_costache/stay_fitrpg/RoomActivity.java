package com.edward_costache.stay_fitrpg;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.edward_costache.stay_fitrpg.exercises.PushupExerciseActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RoomActivity extends AppCompatActivity {
    public static final String TAG = "user";

    private TextView txtRoomName, txtPlayer1Username, txtPlayer2Username, txtPlayer1Status, txtPlayer2Status, txtRoomCapacity;
    private Button btnGuestReady_Unready, btnHostStart;

    private String userID1, userID2, roomID, role;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef, roomRef;

    private User user1, user2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        initViews();

        mAuth = FirebaseAuth.getInstance();
        role = getIntent().getStringExtra("role");
        userRef = FirebaseDatabase.getInstance().getReference("users");
        roomRef = FirebaseDatabase.getInstance().getReference("rooms");
        if(role.equals("host"))
        {
            userID1 = getIntent().getStringExtra("userID1");
            Log.d(TAG, "onCreate: "+userID1);
        }
        else if(role.equals("guest"))
        {
            userID1 = getIntent().getStringExtra("userID1");
            userID2 = getIntent().getStringExtra("userID2");
            Log.d(TAG, "onCreate: "+userID2);
        }
        else
        {
            Log.d(TAG, "onCreate: FATAL ERROR");
        }
        roomID = userID1+"ROOM";

        /*

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");
        roomRef = FirebaseDatabase.getInstance().getReference("rooms");

        userID1 = getIntent().getStringExtra("userID");
        roomID = userID1+"ROOM";

        // Get the user using the userID from the firebase database and store it in a User object
        userRef.child(userID1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user1 = snapshot.getValue(User.class);
                txtPlayer1Username.setText(user1.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        txtRoomName.setText(getIntent().getStringExtra("roomName"));

         */
    }

    private void initViews()
    {
        txtRoomName = findViewById(R.id.roomTxtName);
        txtRoomCapacity = findViewById(R.id.roomTxtRoomCapacity);
        txtPlayer1Username = findViewById(R.id.roomTxtPlayer1Username);
        txtPlayer2Username = findViewById(R.id.roomTxtPlayer2Username);
        txtPlayer1Status = findViewById(R.id.roomTxtPlayer1Ready);
        txtPlayer2Status = findViewById(R.id.roomTxtPlayer2Ready);

        btnGuestReady_Unready = findViewById(R.id.roomBtnGuestReady_Unready);
        btnHostStart = findViewById(R.id.roomBtnHostStart);
    }

    @Override
    public void onBackPressed() {
        if(role.equals("host")) {
            new AlertDialog.Builder(RoomActivity.this, R.style.MyDialogTheme)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Exiting the Room")
                    .setMessage("If you exit the room, the room will be destroyed. Do you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            roomRef.child(roomID).removeValue();
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
        else if(role.equals("guest"))
        {
            roomRef.child(roomID).child("userID2").removeValue();
            finish();
        }
    }
}