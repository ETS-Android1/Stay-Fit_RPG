package com.edward_costache.stay_fitrpg;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.edward_costache.stay_fitrpg.exercises.PushupExerciseActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RoomActivity extends AppCompatActivity {
    public static final String TAG = "user";

    private TextView txtRoomName, txtPlayer1Username, txtPlayer2Username, txtPlayer2Status, txtRoomCapacity;
    private Button btnGuestReady_Unready, btnHostStart;

    private String userID1, userID2, roomID, role, roomName, gameID;
    private int playersReady;

    private DatabaseReference userRef, roomRef, gameRef;

    private User user1, user2;

    private boolean player2Ready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        initViews();

        role = getIntent().getStringExtra("role");
        userRef = FirebaseDatabase.getInstance().getReference("users");
        roomRef = FirebaseDatabase.getInstance().getReference("rooms");
        gameRef = FirebaseDatabase.getInstance().getReference("games");
        roomName = getIntent().getStringExtra("roomName");
        txtRoomName.setText(roomName);
        //------------------------------------------------------------------------------- HOST

        if (role.equals("host")) {
            userID1 = getIntent().getStringExtra("userID");
            roomID = userID1 + "ROOM";
            //store the data for user 1 (host) in a User object
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
            roomRef.child(roomID).child("userID2").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        if (!snapshot.getValue(String.class).equals("")) {
                            userID2 = snapshot.getValue(String.class);
                            txtRoomCapacity.setText("2/2");

                            //when another user joins, store that user in another User object
                            userRef.child(userID2).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    user2 = snapshot.getValue(User.class);
                                    txtPlayer2Username.setVisibility(View.VISIBLE);
                                    txtPlayer2Username.setText(user2.getUsername());
                                    txtPlayer2Status.setVisibility(View.VISIBLE);
                                    txtPlayer2Status.setText("NOT READY");
                                    txtPlayer2Status.setTextColor(getResources().getColor(R.color.redText));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            txtPlayer2Username.setVisibility(View.INVISIBLE);
                            txtPlayer2Status.setVisibility(View.INVISIBLE);
                            txtRoomCapacity.setText("1/2");
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            roomRef.child(roomID).child("playersReady").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        if (snapshot.getValue(Integer.class) == 2) {
                            playersReady = 2;
                            txtPlayer2Status.setText("READY");
                            txtPlayer2Status.setTextColor(getResources().getColor(R.color.teal_700));
                        } else if (snapshot.getValue(Integer.class) == 1) {
                            playersReady = 1;
                            txtPlayer2Status.setText("NOT READY");
                            txtPlayer2Status.setTextColor(getResources().getColor(R.color.redText));
                        } else {
                            Log.d(TAG, "onDataChange: DATA REMOVED");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            btnHostStart.setVisibility(View.VISIBLE);
            btnHostStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (playersReady == 2) {
                        gameID = userID1 + "GAME";
                        roomRef.child(roomID).child("gameStarted").setValue(true);

                        gameRef.child(gameID).child("user1").setValue(new User(user1.getUsername(), user1.getLevel(), user1.getStrength(), user1.getAgility(), user1.getStamina(), user1.getHealth(), false));
                        gameRef.child(gameID).child("user2").setValue(new User(user2.getUsername(), user2.getLevel(), user2.getStrength(), user2.getAgility(), user2.getStamina(), user2.getHealth(), false));
                        gameRef.child(gameID).child("playerTurn").setValue(1);

                        Intent intent = new Intent(RoomActivity.this, GameActivity.class);
                        intent.putExtra("userID1", userID1);
                        intent.putExtra("userID2", userID2);
                        intent.putExtra("role", "host");
                        intent.putExtra("gameID", gameID);
                        startActivity(intent);
                        OnlineMenuActivity.OMA.finish();
                        finish();
                    } else if (user2 == null) {
                        Toast.makeText(RoomActivity.this, "You need 2 players to start a game!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RoomActivity.this, "All players must ready up to start a game!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        //------------------------------------------------------------------------------- GUEST
        else if (role.equals("guest")) {
            roomID = getIntent().getStringExtra("roomID");
            userID2 = getIntent().getStringExtra("userID");
            txtRoomCapacity.setText("2/2");
            txtPlayer2Status.setText("NOT READY");
            txtPlayer2Status.setTextColor(getResources().getColor(R.color.redText));

            //get the user data and store it in a user object for the guest
            userRef.child(userID2).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user2 = snapshot.getValue(User.class);
                    txtPlayer2Username.setVisibility(View.VISIBLE);
                    txtPlayer2Username.setText(user2.getUsername());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //get userID1 from the roomID
            userID1 = roomID.split("ROOM")[0];
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

            btnGuestReady_Unready.setVisibility(View.VISIBLE);
            btnGuestReady_Unready.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!player2Ready) {
                        txtPlayer2Status.setText("READY");
                        txtPlayer2Status.setTextColor(getResources().getColor(R.color.teal_700));
                        roomRef.child(roomID).child("playersReady").setValue(2);
                        player2Ready = true;
                        btnGuestReady_Unready.setText("UN-READY");
                    } else {
                        txtPlayer2Status.setText("NOT READY");
                        txtPlayer2Status.setTextColor(getResources().getColor(R.color.redText));
                        roomRef.child(roomID).child("playersReady").setValue(1);
                        player2Ready = false;
                        btnGuestReady_Unready.setText("READY");
                    }
                }
            });

            roomRef.child(roomID).child("gameStarted").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        gameID = userID1 + "GAME";
                        roomRef.child(roomID).removeValue();
                        Intent intent = new Intent(RoomActivity.this, GameActivity.class);
                        intent.putExtra("userID1", userID1);
                        intent.putExtra("userID2", userID2);
                        intent.putExtra("role", "guest");
                        intent.putExtra("gameID", gameID);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            Log.d(TAG, "onCreate: FATAL ERROR");
        }
    }

    private void initViews() {
        txtRoomName = findViewById(R.id.roomTxtName);
        txtRoomCapacity = findViewById(R.id.roomTxtRoomCapacity);
        txtPlayer1Username = findViewById(R.id.roomTxtPlayer1Username);
        txtPlayer2Username = findViewById(R.id.roomTxtPlayer2Username);
        txtPlayer2Status = findViewById(R.id.roomTxtPlayer2Ready);

        btnGuestReady_Unready = findViewById(R.id.roomBtnGuestReady_Unready);
        btnHostStart = findViewById(R.id.roomBtnHostStart);
    }

    @Override
    public void onBackPressed() {
        if (role.equals("host")) {
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
        } else if (role.equals("guest")) {
            Log.d(TAG, "onClick: guest" + roomID);
            roomRef.child(roomID).child("activePlayers").setValue(1);
            roomRef.child(roomID).child("userID2").setValue("");
            finish();
        }
    }
}