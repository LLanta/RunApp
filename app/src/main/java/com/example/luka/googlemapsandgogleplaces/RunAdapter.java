package com.example.luka.googlemapsandgogleplaces;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RunAdapter extends RecyclerView.Adapter<RunAdapter.RunHolder> {
    private List<Run> runs = new ArrayList<>();
    @NonNull
    @Override
    public RunHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.run_item,parent,false);
        return new RunHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RunHolder holder, int position) {
        Run currentRun = runs.get(position);
        holder.tvListDuration.setText(currentRun.runDuration);
        holder.tvListDistance.setText(String.valueOf(currentRun.runDistance)+" m");
    }

    @Override
    public int getItemCount() {
        return runs.size();
    }

    public void setRuns(List<Run> runs){
        this.runs = runs;
        notifyDataSetChanged();
    }

    class RunHolder extends RecyclerView.ViewHolder{
        private TextView tvListDuration;
        private TextView tvListDistance;

        public RunHolder(@NonNull View itemView) {
            super(itemView);
            tvListDuration = itemView.findViewById(R.id.tvListDuration);
            tvListDistance = itemView.findViewById(R.id.tvListDistance);
        }
    }

}
