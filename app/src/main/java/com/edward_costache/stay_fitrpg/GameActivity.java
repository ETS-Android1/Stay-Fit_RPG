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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edward_costache.stay_fitrpg.exercises.PushupExerciseActivity;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ExecutionException;

public class GameActivity extends AppCompatActivity {

    public static final String TAG = "GAME ACTIVITY";
    private TextView txtUsername1, txtUsername2, txtHealthUser1, txtHealthUser2, txtPlayerTurn, txtGameOverInfo, txtRematchInfo;
    private ProgressBar progressBarUser1Health, progressBarUser2Health;
    private com.google.android.material.card.MaterialCardView cardAttack, cardDefend, cardRest, cardMenu;
    private androidx.constraintlayout.widget.ConstraintLayout gameOverConstraint, rematchConstraint;
    private Button btnRematch, btnExit, btnAccept, btnDecline;

    private String role, userID1, userID2, gameID;
    private DatabaseReference gameRef;

    private User user1, user2;

    private boolean gameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initViews();

        role = getIntent().getStringExtra("role");
        userID1 = getIntent().getStringExtra("userID1");
        userID2 = getIntent().getStringExtra("userID2");
        gameID = getIntent().getStringExtra("gameID");
        gameRef = FirebaseDatabase.getInstance().getReference("games");

        gameOverConstraint.setVisibility(View.GONE);
        rematchConstraint.setVisibility(View.GONE);
        //-------------------------------------------------------------------- HOST ----------------------------------------------------------------------------------------------------//
        if (role.equals("host")) {
            gameRef.child(gameID).child("user1").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        user1 = snapshot.getValue(User.class);
                        txtUsername1.setText(user1.getUsername());
                        txtHealthUser1.setText(Integer.toString(user1.getHealth()));
                        progressBarUser1Health.setProgress(user1.getHealth());

                        //host lost
                        if (user1.getHealth() <= 0) {
                            gameOver = true;
                            gameOverConstraint.setVisibility(View.VISIBLE);
                            txtGameOverInfo.setText("YOU LOST!");
                            cardEnable(false);

                            //host requests rematch
                            btnRematch.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (user2.isLeft()) {
                                        Toast.makeText(GameActivity.this, user2.getUsername() + " already left the game!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        //send rematch request from host
                                        gameRef.child(gameID).child("rematch").setValue(user1.getUsername());
                                        Toast.makeText(GameActivity.this, "Waiting for response from " + user2.getUsername(), Toast.LENGTH_SHORT).show();
                                    }
                                    btnRematch.setEnabled(false);
                                    btnRematch.setBackgroundColor(getResources().getColor(R.color.purple_700));
                                }
                            });

                            //host exits the game
                            btnExit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    gameRef.child(gameID).child("user1").child("left").setValue(true);
                                    startActivity(new Intent(GameActivity.this, ProfileActivity.class));
                                    finish();
                                }
                            });
                        }
                    } else {
                        Log.d(TAG, "onDataChange: USER1 NOT FOUND ON DATABASE");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            gameRef.child(gameID).child("user2").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        user2 = snapshot.getValue(User.class);
                        txtUsername2.setText(user2.getUsername());
                        txtHealthUser2.setText(Integer.toString(user2.getHealth()));
                        progressBarUser2Health.setProgress(user2.getHealth());

                        //host wins
                        if (user2.getHealth() <= 0) {
                            gameOver = true;
                            gameOverConstraint.setVisibility(View.VISIBLE);
                            btnRematch.setEnabled(false);
                            btnRematch.setBackgroundColor(getResources().getColor(R.color.purple_700));
                            txtGameOverInfo.setText("YOU WON!");
                            cardEnable(false);

                            //host exits the game
                            btnExit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    gameRef.child(gameID).child("user1").child("left").setValue(true);
                                    startActivity(new Intent(GameActivity.this, ProfileActivity.class));
                                    finish();
                                }
                            });
                        }

                        //guest left the game
                        if (user2.isLeft() && user2.getUsername() != null) {
                            if(!gameOver) {
                                gameOver = true;
                                gameOverConstraint.setVisibility(View.VISIBLE);
                                cardEnable(false);
                                btnRematch.setEnabled(false);
                                btnRematch.setBackgroundColor(getResources().getColor(R.color.purple_700));
                                txtGameOverInfo.setText("YOU WON!");
                                Toast.makeText(GameActivity.this, user2.getUsername() + " left the game!", Toast.LENGTH_SHORT).show();

                                btnExit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        gameRef.child(gameID).removeValue();
                                        startActivity(new Intent(GameActivity.this, ProfileActivity.class));
                                        finish();
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(GameActivity.this, user2.getUsername() + " left the game!", Toast.LENGTH_SHORT).show();
                                gameRef.child(gameID).removeValue();
                            }
                        }
                    } else {
                        Log.d(TAG, "onDataChange: USER2 NOT FOUND ON DATABASE");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            gameRef.child(gameID).child("playerTurn").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        if(!gameOver) {
                            if (snapshot.getValue(Integer.class) == 1) {
                                gameRef.child(gameID).child("user1").child("defending").setValue(false);
                                cardEnable(true);
                                txtPlayerTurn.setText("Your Turn!");
                            } else if (snapshot.getValue(Integer.class) == 2) {
                                cardEnable(false);
                                txtPlayerTurn.setText(user2.getUsername() + "'s Turn!");
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            cardMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(GameActivity.this, v);
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.gameMenuItem1) {
                                new AlertDialog.Builder(GameActivity.this)
                                        .setTitle("Leaving the Match")
                                        .setMessage("Are you sure you want to leave?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                gameRef.child(gameID).child("user1").child("left").setValue(true);
                                                dialog.dismiss();
                                                finish();
                                            }
                                        })
                                        .setCancelable(true)
                                        .show();
                            }
                            return true;
                        }
                    });
                    menu.inflate(R.menu.game_menu);
                    menu.show();
                }
            });
            cardAttack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int damage = (int) (user1.getStrength() * 1.3);

                    if (user2.isDefending()) {
                        damage = (damage / 2);
                    }
                    gameRef.child(gameID).child("user2").child("health").setValue(user2.getHealth() - damage);
                    changeTurn(2);
                }
            });
            cardDefend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gameRef.child(gameID).child("user1").child("defending").setValue(true);
                    changeTurn(2);
                }
            });
            cardRest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int healthRecovered = (int) (user1.getStamina() * 0.2);
                    gameRef.child(gameID).child("user1").child("health").setValue(user1.getHealth() + healthRecovered);
                    changeTurn(2);
                }
            });

            gameRef.child(gameID).child("rematch").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        //check if the guest wants a rematch
                        if (snapshot.getValue(String.class).equals(user2.getUsername())) {
                            gameOverConstraint.setVisibility(View.GONE);
                            rematchConstraint.setVisibility(View.VISIBLE);
                            txtRematchInfo.setText(user2.getUsername() + " is requesting a rematch!");
                            btnAccept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //accept rematch
                                    gameRef.child(gameID).child("rematch").setValue(user2.getUsername() + "accept");
                                    Intent intent = new Intent(GameActivity.this, RoomActivity.class);
                                    intent.putExtra("roomName", user1.getUsername() + "' room");
                                    intent.putExtra("role", "host");
                                    intent.putExtra("userID", userID1);
                                    startActivity(intent);
                                    finishAndRemoveTask();
                                }
                            });

                            btnDecline.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    gameRef.child(gameID).child("rematch").setValue(user2.getUsername() + "decline");
                                    gameRef.child(gameID).child("user1").child("left").setValue(true);
                                    finishAndRemoveTask();
                                }
                            });
                        }
                        //check if guest accept host rematch
                        else if (snapshot.getValue(String.class).equals(user1.getUsername() + "accept")) {
                            Intent intent = new Intent(GameActivity.this, RoomActivity.class);
                            intent.putExtra("roomName", user1.getUsername() + "'s room");
                            intent.putExtra("role", "host");
                            intent.putExtra("userID", userID1);
                            intent.putExtra("roomID", userID1+"ROOM");
                            FirebaseDatabase.getInstance().getReference("rooms").child(userID1 + "ROOM").setValue(new Room(user1.getUsername()+"'s room", userID1, userID2));
                            startActivity(intent);
                            gameRef.child(gameID).removeValue();
                            finishAndRemoveTask();
                        }
                        //check if guest declined host rematch
                        else if (snapshot.getValue(String.class).equals(user1.getUsername() + "decline")) {
                            gameRef.child(gameID).removeValue();
                            txtRematchInfo.setText(user2.getUsername() + " declined!");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        //-------------------------------------------------------------------- GUEST --------------------------------------------------------------------------------------//
        else if (role.equals("guest")) {
            OnlineMenuActivity.OMA.finish();
            gameRef.child(gameID).child("user1").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        user1 = snapshot.getValue(User.class);
                        txtUsername2.setText(user1.getUsername());
                        txtHealthUser2.setText(Integer.toString(user1.getHealth()));
                        progressBarUser2Health.setProgress(user1.getHealth());

                        //guest wins
                        if (user1.getHealth() <= 0) {
                            gameOver = true;
                            gameOverConstraint.setVisibility(View.VISIBLE);
                            btnRematch.setEnabled(false);
                            btnRematch.setBackgroundColor(getResources().getColor(R.color.purple_700));
                            cardEnable(false);
                            txtGameOverInfo.setText("YOU WON!");
                            //guest exits
                            btnExit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    gameRef.child(gameID).child("user2").child("left").setValue(true);
                                    startActivity(new Intent(GameActivity.this, ProfileActivity.class));
                                    finishAndRemoveTask();
                                }
                            });
                        }

                        //host leaves the game
                        if (user1.isLeft() && user1.getUsername() != null) {
                            if(!gameOver) {
                                gameOver = true;
                                cardEnable(false);
                                gameOverConstraint.setVisibility(View.VISIBLE);
                                btnRematch.setEnabled(false);
                                btnRematch.setBackgroundColor(getResources().getColor(R.color.purple_700));
                                txtGameOverInfo.setText("YOU WON!");
                                Toast.makeText(GameActivity.this, user1.getUsername() + " left the game!", Toast.LENGTH_SHORT).show();
                                btnExit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        gameRef.child(gameID).removeValue();
                                        startActivity(new Intent(GameActivity.this, ProfileActivity.class));
                                        finishAndRemoveTask();
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(GameActivity.this, user1.getUsername() + " left the game!", Toast.LENGTH_SHORT).show();
                                gameRef.child(gameID).removeValue();
                            }
                        }
                    } else {
                        Log.d(TAG, "onDataChange: USER1 NOT FOUND ON DATABASE");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            gameRef.child(gameID).child("user2").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        user2 = snapshot.getValue(User.class);
                        txtUsername1.setText(user2.getUsername());
                        if (user2.getHealth() < 0) {
                            txtHealthUser1.setText("0");
                            progressBarUser1Health.setProgress(0);
                        } else {
                            txtHealthUser1.setText(Integer.toString(user2.getHealth()));
                            progressBarUser1Health.setProgress(user2.getHealth());

                        }
                        //guest looses
                        if (user2.getHealth() <= 0) {
                            gameOver = true;
                            gameOverConstraint.setVisibility(View.VISIBLE);
                            cardEnable(false);
                            btnRematch.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (user1.isLeft()) {
                                        Toast.makeText(GameActivity.this, user1.getUsername() + " already left the game!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        //send rematch request from guest
                                        gameRef.child(gameID).child("rematch").setValue(user2.getUsername());
                                        cardEnable(false);
                                        Toast.makeText(GameActivity.this, "Waiting for Response", Toast.LENGTH_SHORT).show();
                                        btnRematch.setEnabled(false);
                                        btnRematch.setBackgroundColor(getResources().getColor(R.color.purple_700));
                                    }
                                }
                            });

                            btnExit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    gameRef.child(gameID).child("user2").child("left").setValue(true);

                                    startActivity(new Intent(GameActivity.this, ProfileActivity.class));
                                    finishAndRemoveTask();
                                }
                            });

                            txtGameOverInfo.setText("YOU LOST!");
                        }

                    } else {
                        Log.d(TAG, "onDataChange: USER2 NOT FOUND ON DATABASE");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            gameRef.child(gameID).child("rematch").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        //check if host requested rematch
                        if (snapshot.getValue(String.class).equals(user1.getUsername())) {
                            gameOverConstraint.setVisibility(View.GONE);
                            rematchConstraint.setVisibility(View.VISIBLE);

                            txtRematchInfo.setText(user1.getUsername() + " is requesting a rematch!");
                            btnAccept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //accept rematch
                                    gameRef.child(gameID).child("rematch").setValue(user1.getUsername() + "accept");
                                    Intent intent = new Intent(GameActivity.this, RoomActivity.class);
                                    intent.putExtra("roomName", user1.getUsername() + "' room");
                                    intent.putExtra("role", "guest");
                                    intent.putExtra("userID", userID2);
                                    startActivity(intent);
                                    finishAndRemoveTask();
                                }
                            });

                            btnDecline.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    gameRef.child(gameID).child("rematch").setValue(user1.getUsername() + "decline");
                                    gameRef.child(gameID).child("user2").child("left").setValue(true);
                                    finishAndRemoveTask();
                                }
                            });

                        }
                        //check if host accepted guest rematch
                        else if (snapshot.getValue(String.class).equals(user2.getUsername() + "accept")) {
                            Intent intent = new Intent(GameActivity.this, RoomActivity.class);
                            intent.putExtra("roomName", user1.getUsername() + "'s room");
                            intent.putExtra("role", "guest");
                            intent.putExtra("userID", userID2);
                            intent.putExtra("roomID", userID1+"ROOM");
                            startActivity(intent);
                            gameRef.child(gameID).removeValue();
                            FirebaseDatabase.getInstance().getReference("rooms").child(userID1 + "ROOM").setValue(new Room(user1.getUsername()+"'s room", userID1, userID2));
                            finishAndRemoveTask();
                        }
                        //check if host declined guest rematch
                        else if (snapshot.getValue(String.class).equals(user2.getUsername() + "decline")) {
                            gameRef.child(gameID).removeValue();
                            txtRematchInfo.setText(user1.getUsername() + " declined!");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            cardEnable(false);

            gameRef.child(gameID).child("playerTurn").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        if(!gameOver) {
                            if (snapshot.getValue(Integer.class) == 1) {
                                cardEnable(false);
                                txtPlayerTurn.setText(user1.getUsername() + "'s Turn!");
                            } else if (snapshot.getValue(Integer.class) == 2) {
                                gameRef.child(gameID).child("user2").child("defending").setValue(false);
                                cardEnable(true);
                                txtPlayerTurn.setText("Your Turn!");
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            cardMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(GameActivity.this, v);
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.gameMenuItem1) {
                                new AlertDialog.Builder(GameActivity.this)
                                        .setTitle("Leaving the Match")
                                        .setMessage("Are you sure you want to leave?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                gameRef.child(gameID).child("user2").child("left").setValue(true);

                                                startActivity(new Intent(GameActivity.this, ProfileActivity.class));
                                                finishAffinity();
                                            }
                                        })
                                        .setCancelable(true)
                                        .show();
                            }
                            return true;
                        }
                    });
                    menu.inflate(R.menu.game_menu);
                    menu.show();
                }
            });
            cardAttack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int damage = (int) (user2.getStrength() * 1.3);

                    if (user1.isDefending()) {
                        damage = (damage / 2);
                    }
                    gameRef.child(gameID).child("user1").child("health").setValue(user1.getHealth() - damage);
                    changeTurn(1);
                }
            });
            cardDefend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gameRef.child(gameID).child("user2").child("defending").setValue(true);
                    changeTurn(1);
                }
            });
            cardRest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int healthRecovered = (int) (user2.getStamina() * 0.2);
                    gameRef.child(gameID).child("user2").child("health").setValue(user2.getHealth() + healthRecovered);
                    changeTurn(1);
                }
            });
        } else {
            Log.d(TAG, "onCreate: COULD'T DETERMINE HOST OR GUEST");
        }
    }

    @Override
    public void onBackPressed() {
        if (!GameActivity.this.isFinishing()) {
            if (role.equals("host")) {
                new AlertDialog.Builder(GameActivity.this)
                        .setTitle("Exiting the game!")
                        .setMessage("Are you sure you want to exit the game, this will count as a loss.")

                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                gameRef.child(gameID).child("user1").child("left").setValue(true);

                                startActivity(new Intent(GameActivity.this, ProfileActivity.class));
                                finishAffinity();
                            }
                        })
                        .show();
            } else if (role.equals("guest")) {
                new AlertDialog.Builder(GameActivity.this)
                        .setTitle("Exiting the game!")
                        .setMessage("Are you sure you want to exit the game, this will count as a loss.")

                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                gameRef.child(gameID).child("user2").child("left").setValue(true);

                                startActivity(new Intent(GameActivity.this, ProfileActivity.class));
                                finishAffinity();
                            }
                        })
                        .show();
            }
        }
    }

    private void changeTurn(int user) {
        gameRef.child(gameID).child("playerTurn").setValue(user);
    }

    private void cardEnable(boolean enabled) {
        cardAttack.setEnabled(enabled);
        cardDefend.setEnabled(enabled);
        cardRest.setEnabled(enabled);
        if (!enabled) {
            cardAttack.setCardBackgroundColor(getResources().getColor(R.color.purple_700));
            cardDefend.setCardBackgroundColor(getResources().getColor(R.color.purple_700));
            cardRest.setCardBackgroundColor(getResources().getColor(R.color.purple_700));
        } else {
            cardAttack.setCardBackgroundColor(getResources().getColor(R.color.purple_500));
            cardDefend.setCardBackgroundColor(getResources().getColor(R.color.purple_500));
            cardRest.setCardBackgroundColor(getResources().getColor(R.color.purple_500));
        }
    }

    private void initViews() {
        txtUsername1 = findViewById(R.id.gameTxtUser1Username);
        txtUsername2 = findViewById(R.id.gameTxtUser2Username);
        txtHealthUser1 = findViewById(R.id.gameTxtUser1HealthValue);
        txtHealthUser2 = findViewById(R.id.gameTxtUser2HealthValue);
        txtPlayerTurn = findViewById(R.id.gameTxtTurn);
        txtGameOverInfo = findViewById(R.id.gameTxtGameOverInfo);
        txtRematchInfo = findViewById(R.id.gameTxtRematchInfo);

        progressBarUser1Health = findViewById(R.id.gameProgressBarUser1Health);
        progressBarUser2Health = findViewById(R.id.gameProgressBarUser2Health);

        cardAttack = findViewById(R.id.gameCardViewAttack);
        cardDefend = findViewById(R.id.gameCardViewDefend);
        cardRest = findViewById(R.id.gameCardViewRest);
        cardMenu = findViewById(R.id.gameCardViewMenu);

        gameOverConstraint = findViewById(R.id.gameConstLayoutGameOver);
        rematchConstraint = findViewById(R.id.gameConstLayoutRematch);

        btnRematch = findViewById(R.id.gameBtnRematch);
        btnExit = findViewById(R.id.gameBtnExit);
        btnAccept = findViewById(R.id.gameBtnRematchAccept);
        btnDecline = findViewById(R.id.gameBtnRematchDecline);
    }
}