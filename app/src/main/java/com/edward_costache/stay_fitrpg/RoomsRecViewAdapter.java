package com.edward_costache.stay_fitrpg;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RoomsRecViewAdapter extends RecyclerView.Adapter<RoomsRecViewAdapter.ViewHolder>{

    private ArrayList<Room> rooms = new ArrayList<>();
    private Context context;
    private String userID;

    public RoomsRecViewAdapter(Context context, String userID) {
        this.context = context;
        this.userID = userID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_list_item, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtRoomName.setText(rooms.get(position).getRoomName());
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomName = rooms.get(position).getRoomName();
                String roomID = rooms.get(position).getUserID1()+"ROOM";

                FirebaseDatabase.getInstance().getReference("rooms").child(roomID).child("userID2").setValue(userID);

                Intent intent = new Intent(context, RoomActivity.class);
                intent.putExtra("role", "guest");
                intent.putExtra("userID", userID);

                intent.putExtra("roomID", roomID);
                context.startActivity(intent);
                Log.d("TAG", "onClick: "+roomName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtRoomName, txtRoomCapacity;
        private RelativeLayout parentLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRoomName = itemView.findViewById(R.id.itemListTxtRoomName);
            txtRoomCapacity = itemView.findViewById(R.id.itemListTxtRoomCapacity);
            parentLayout = itemView.findViewById(R.id.itemListParentLayout);
        }
    }
}
