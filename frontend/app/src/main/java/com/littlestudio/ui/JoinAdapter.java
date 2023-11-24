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

    public JoinAdapter(Context context, ArrayList<String> participants){
        this.context = context;
        this.participants = participants;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.join_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getParticipant().setText(participants.get(position));

    }

    public void setParticipants(ArrayList<String> participants) {
        this.participants = participants;
    }

    @Override
    public int getItemCount() {
        return  participants == null ? 0 : participants.size();
    }

    public ArrayList<String> getParticipants() {
        return participants;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView getParticipant() {
            return participant;
        }

        final TextView participant;

        public ViewHolder(View itemView){
            super(itemView);
            participant = itemView.findViewById(R.id.participant);
        }


    }
}
