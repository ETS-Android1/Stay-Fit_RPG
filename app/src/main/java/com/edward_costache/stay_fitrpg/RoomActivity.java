package com.edward_costache.stay_fitrpg;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Edward Costache
 */
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

        //the application has to respond differently to a host and a guest. The host creates the room, thus is the first person to join the room.
        //the guest joins a room created by a host, and they will have to ready up in order for the host to start the game.
        //an if statement is used to determine if the user joined a room, meaning they are a guest. Or if they made a room meaning they are a host.
        //within the if statement different code is run to accurately provide the best interface for the different type of users.
        //The role of a user is decided in previous activities. i.e the role of a host is passed to a user who has pressed the 'Create Room' button in
        //the OnlineMenuActivity. The role of a guest is passed to a user that selects one of the available rooms. The passing of data is done by passing
        //values through the intent.

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

            //store the data for the guest in a variable.
            //set up a ValueEventListener to listen for data changes of the guest.
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

            //set up a listener for a variable that shows how many players are in a game.
            roomRef.child(roomID).child("playersReady").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        if (snapshot.getValue(Integer.class) == 2) {        //if there are 2 players, i.e a guest joined the host's room
                            playersReady = 2;
                            txtPlayer2Status.setText("READY");
                            txtPlayer2Status.setTextColor(getResources().getColor(R.color.teal_700));
                        } else if (snapshot.getValue(Integer.class) == 1) { //if there is only 1 player, i.e only the host
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

            //starts the game when if the guest is ready
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

            //get userID1 from the roomID. the roomID is passed to the intent when the user selects a room.
            //by default a roomID is comprised of the ID of the host + "ROOM".
            //here i am splitting the roomID at "ROOM" and getting the left side of the split which leaves the ID of the host
            userID1 = roomID.split("ROOM")[0];

            //fetching the username of the host and displaying it for the guest.
            //NOTE: a ListenerForSingleValueEvent is used because the username of the host will not change while in a room, so we only need to fetch it once.
            userRef.child(userID1).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue() != null) {
                        txtPlayer1Username.setText(snapshot.getValue(String.class));
                    }
                    else
                    {
                        Log.d(TAG, "onDataChange: USERID1 IS NULL");
                    }
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

            //check if the game is started by checking the value inside the Database Reference for the room.
            //the host will create and set this value to 'true', which will cause this code to run once more.
            //NOTE: when adding this ValueEventListener to the Database Reference, the code inside it will trigger.
            //however at the time of the first trigger, the 'gameStarted' reference was not create by the host, resulting in a null snapshot.
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

    /**
     * A function for initializing all Views
     */
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