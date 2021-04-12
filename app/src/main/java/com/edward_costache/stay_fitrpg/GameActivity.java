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
import android.widget.PopupMenu;
import android.widget.ProgressBar;
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

public class GameActivity extends AppCompatActivity {

    public static final String TAG = "GAME ACTIVITY";
    private TextView txtUsername1, txtUsername2, txtHealthUser1, txtHealthUser2, txtPlayerTurn;
    private ProgressBar progressBarUser1Health, progressBarUser2Health;
    private com.google.android.material.card.MaterialCardView cardAttack, cardDefend, cardRest, cardMenu;
    private androidx.constraintlayout.widget.ConstraintLayout parentView;

    private String role, userID1, userID2, gameID, roomName;
    private DatabaseReference gameRef;

    private User user1, user2;

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

        gameRef.child(gameID).child("roomName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null)
                {
                    roomName = snapshot.getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //-------------------------------------------------------------------- HOST
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
                            if(!GameActivity.this.isFinishing()) {
                                new AlertDialog.Builder(GameActivity.this)
                                        .setTitle("Game Result")
                                        .setMessage("You Lost!")
                                        .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                finish();
                                            }
                                        })
                                        .setPositiveButton("Rematch", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (user2.isLeft()) {
                                                    Toast.makeText(GameActivity.this, user2.getUsername() + " already left the game!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    //send rematch request from host
                                                    dialog.dismiss();
                                                    gameRef.child(gameID).child("rematch").setValue(user1.getUsername());
                                                    cardEnable(false);
                                                }
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
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
                        txtUsername2.setText(user2.getUsername());
                        txtHealthUser2.setText(Integer.toString(user2.getHealth()));
                        progressBarUser2Health.setProgress(user2.getHealth());

                        //host wins
                        if (user2.getHealth() <= 0) {
                            if(!GameActivity.this.isFinishing()) {
                                new AlertDialog.Builder(GameActivity.this)
                                        .setTitle("Game Result")
                                        .setMessage("You Won!")
                                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                finish();
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                            }
                        }

                        //guest left the game
                        if (user2.isLeft()) {
                            if(!GameActivity.this.isFinishing()) {
                                new AlertDialog.Builder(GameActivity.this)
                                        .setTitle("Game Result")
                                        .setMessage(user2.getUsername() + " left the game, you win by default.")
                                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                finish();
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
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
                            if(!GameActivity.this.isFinishing()) {
                                new AlertDialog.Builder(GameActivity.this)
                                        .setTitle("Rematch")
                                        .setMessage(user2.getUsername() + " wants a rematch!")
                                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //accept rematch
                                                dialog.dismiss();
                                                gameRef.child(gameID).child("rematch").setValue(user2.getUsername() + "accept");
                                                Intent intent = new Intent(GameActivity.this, RoomActivity.class);
                                                intent.putExtra("roomName", roomName);
                                                intent.putExtra("role", "host");
                                                intent.putExtra("userID", userID1);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                gameRef.child(gameID).child("rematch").setValue(user2.getUsername() + "decline");
                                                dialog.dismiss();
                                                finish();
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                            }
                        }
                        //check if guest accept host rematch
                        else if (snapshot.getValue(String.class).equals(user1.getUsername() + "accept")) {
                            Intent intent = new Intent(GameActivity.this, RoomActivity.class);
                            intent.putExtra("roomName", roomName);
                            intent.putExtra("role", "host");
                            intent.putExtra("userID", userID1);
                            startActivity(intent);
                            finish();
                        } else if (snapshot.getValue(String.class).equals(user1.getUsername() + "decline")) {
                            gameRef.child(gameID).removeValue();
                            if(!GameActivity.this.isFinishing()) {
                                new AlertDialog.Builder(GameActivity.this)
                                        .setMessage(user2.getUsername() + " rejected your rematch!")
                                        .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                finish();
                                            }
                                        })
                                        .setPositiveButton("", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .show();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        //-------------------------------------------------------------------- GUEST
        else if (role.equals("guest")) {
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
                            if(!GameActivity.this.isFinishing()) {
                                new AlertDialog.Builder(GameActivity.this)
                                        .setTitle("Game Result")
                                        .setMessage("You Won!")
                                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                            }
                        }

                        //host leaves the game
                        if (user1.isLeft()) {
                            if(!GameActivity.this.isFinishing()) {
                                new AlertDialog.Builder(GameActivity.this)
                                        .setTitle("Game Result")
                                        .setMessage(user1.getUsername() + " left the game, you win by default.")
                                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                gameRef.child(gameID).removeValue();
                                                finish();
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
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
                        txtHealthUser1.setText(Integer.toString(user2.getHealth()));
                        progressBarUser1Health.setProgress(user2.getHealth());

                        //guest looses
                        if (user2.getHealth() <= 0) {
                            if(!GameActivity.this.isFinishing()) {
                                new AlertDialog.Builder(GameActivity.this)
                                        .setTitle("Game Result")
                                        .setMessage("You Lost!")
                                        .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                finish();
                                            }
                                        })
                                        .setPositiveButton("Rematch", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (user1.isLeft()) {
                                                    Toast.makeText(GameActivity.this, user1.getUsername() + " already left the game!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    //send rematch request from guest
                                                    dialog.dismiss();
                                                    gameRef.child(gameID).child("rematch").setValue(user2.getUsername());
                                                    cardEnable(false);
                                                }
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
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

            gameRef.child(gameID).child("rematch").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        //check if host requested rematch
                        if (snapshot.getValue(String.class).equals(user1.getUsername())) {
                            if(!GameActivity.this.isFinishing()) {
                                new AlertDialog.Builder(GameActivity.this)
                                        .setTitle("Rematch")
                                        .setMessage(user1.getUsername() + " wants a rematch!")
                                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //accept rematch
                                                gameRef.child(gameID).child("rematch").setValue(user1.getUsername() + "accept");

                                                FirebaseDatabase.getInstance().getReference("rooms").child(userID1 + "ROOM").child("userID2").setValue(userID2);

                                                Intent intent = new Intent(GameActivity.this, RoomActivity.class);
                                                intent.putExtra("role", "guest");
                                                intent.putExtra("userID", userID2);
                                                intent.putExtra("roomID", userID1 + "ROOM");
                                                intent.putExtra("roomName", roomName);
                                                startActivity(intent);
                                                gameRef.child(gameID).removeValue();
                                                dialog.dismiss();
                                                finish();
                                                //todo: roomname doesnt display for guest when rematch
                                            }
                                        })
                                        .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                gameRef.child(gameID).child("rematch").setValue(user1.getUsername() + "decline");
                                                dialog.dismiss();
                                                finish();
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                            }
                        }
                        //check if host accepted guest rematch
                        else if (snapshot.getValue(String.class).equals(user2.getUsername() + "accept")) {
                            FirebaseDatabase.getInstance().getReference("rooms").child(userID1 + "ROOM").child("userID2").setValue(userID2);

                            Intent intent = new Intent(GameActivity.this, RoomActivity.class);
                            intent.putExtra("role", "guest");
                            intent.putExtra("userID", userID2);
                            intent.putExtra("roomID", userID1 + "ROOM");
                            intent.putExtra("roomName", roomName);
                            startActivity(intent);
                            gameRef.child(gameID).removeValue();
                            finish();
                        } else if (snapshot.getValue(String.class).equals(user2.getUsername() + "decline")) {
                            gameRef.child(gameID).removeValue();
                            if(!GameActivity.this.isFinishing()) {
                                new AlertDialog.Builder(GameActivity.this)
                                        .setMessage(user1.getUsername() + " rejected your rematch!")
                                        .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                finish();
                                            }
                                        })
                                        .setPositiveButton("", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .show();
                            }
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
        if(!GameActivity.this.isFinishing()) {
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
                                finish();
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
                                finish();
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

        progressBarUser1Health = findViewById(R.id.gameProgressBarUser1Health);
        progressBarUser2Health = findViewById(R.id.gameProgressBarUser2Health);

        cardAttack = findViewById(R.id.gameCardViewAttack);
        cardDefend = findViewById(R.id.gameCardViewDefend);
        cardRest = findViewById(R.id.gameCardViewRest);
        cardMenu = findViewById(R.id.gameCardViewMenu);

        parentView = findViewById(R.id.gameParent);
    }
}