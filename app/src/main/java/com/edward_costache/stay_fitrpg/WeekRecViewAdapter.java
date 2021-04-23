package com.edward_costache.stay_fitrpg;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Adapted from freeCodeCamp.org, YouTube channel https://www.youtube.com/watch?v=fis26HvvDII&t=36127s
 * Code creator: MeiCode https://www.youtube.com/channel/UCE3wAhsfp4wGRgHXIQjVx0w
 */
public class WeekRecViewAdapter extends RecyclerView.Adapter<WeekRecViewAdapter.ViewHolder>{

    private ArrayList<ProgressWeek> weeks = new ArrayList<>();
    private final Context fromContext;

    WeekRecViewAdapter(Context fromContext)
    {
        this.fromContext = fromContext;

    }

    @NonNull
    @Override
    public WeekRecViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.week_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeekRecViewAdapter.ViewHolder holder, int position) {
        holder.txtWeekName.setText(weeks.get(position).getWeekName());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fromContext, ProgressDaysActivity.class);
                intent.putExtra("weekName", weeks.get(position).getWeekName());
                fromContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return weeks.size();
    }

    public void setWeeks(ArrayList<ProgressWeek> weeks) {
        this.weeks = weeks;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtWeekName;
        private androidx.cardview.widget.CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtWeekName = itemView.findViewById(R.id.weekListTxtWeekName);
            cardView = itemView.findViewById(R.id.weekListCard);
        }
    }
}
