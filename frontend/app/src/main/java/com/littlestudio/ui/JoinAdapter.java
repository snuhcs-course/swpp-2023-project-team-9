package com.littlestudio.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.littlestudio.R;

import java.util.ArrayList;

public class JoinAdapter extends RecyclerView.Adapter<JoinAdapter.ViewHolder> {

    private ArrayList<String> participants;
    private Context context;

    public JoinAdapter(Context context, ArrayList<String> participants) {
        this.context = context;
        this.participants = participants;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.join_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String participant = participants.get(position);
        holder.bind(participant);
    }

    @Override
    public int getItemCount() {
        return participants == null ? 0 : participants.size();
    }

    public void setParticipants(ArrayList<String> participants) {
        this.participants = participants;
        notifyDataSetChanged();
    }

    public ArrayList<String> getParticipants() {
        return participants;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView participant;

        public ViewHolder(View itemView) {
            super(itemView);
            participant = itemView.findViewById(R.id.particiant);
        }

        public void bind(String participantName) {
            // Bind data to the ViewHolder
            participant.setText(participantName);
        }
    }
}