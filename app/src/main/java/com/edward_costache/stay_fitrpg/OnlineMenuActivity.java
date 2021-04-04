package com.edward_costache.stay_fitrpg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OnlineMenuActivity extends AppCompatActivity {

    private RecyclerView userRecView;
    private LinearLayout menuLayout, searchPlayerLayout, searchOnlineLayout;
    private Button btnSearchPlayer;

    private ArrayList<User> users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_menu);

        initViews();
        userRecView.setLayoutManager(new LinearLayoutManager(this));

        menuLayout.setVisibility(View.VISIBLE);
        searchOnlineLayout.setVisibility(View.GONE);
        searchPlayerLayout.setVisibility(View.GONE);

        btnSearchPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLayout.setVisibility(View.GONE);
                searchOnlineLayout.setVisibility(View.GONE);
                searchPlayerLayout.setVisibility(View.VISIBLE);

                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference usersRef = rootRef.child("users");

                usersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        users = new ArrayList<>();
                        for(DataSnapshot ss : snapshot.getChildren())
                        {
                            User user = ss.getValue(User.class);
                            Log.d("TAG", "onDataChange: "+ss.getKey());
                            users.add(user);
                        }
                        UsersRecViewAdapter adapter = new UsersRecViewAdapter();
                        adapter.setUsers(users);
                        userRecView.setAdapter(adapter);
                        userRecView.setLayoutManager(new LinearLayoutManager(OnlineMenuActivity.this));

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        //TODO: WHEN SEARCH USER IS PRESSED POPULATE ADAPTER WITH USERS FROM FIREBASE
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    private void initViews()
    {
        userRecView = findViewById(R.id.onlineMenuRecViewUsers);

        menuLayout = findViewById(R.id.onlineMenuLinearLayoutMenu);
        searchPlayerLayout = findViewById(R.id.onlineMenuLinearLayoutSearchPlayer);
        searchOnlineLayout = findViewById(R.id.onlineMenuLinearLayoutSearchOnline);

        btnSearchPlayer = findViewById(R.id.onlineMenuBtnSearchPlayer);
    }

    public void createRoom()
    {

    }
}