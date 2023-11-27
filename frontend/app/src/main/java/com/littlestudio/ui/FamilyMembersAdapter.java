package com.littlestudio.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.littlestudio.R;
import com.littlestudio.data.model.User;

import java.util.List;

public class FamilyMembersAdapter extends RecyclerView.Adapter<FamilyMembersAdapter.ViewHolder> {

    private List<User> familyMembers;
    private User me;

    public FamilyMembersAdapter(List<User> familyMembers, User me) {
        this.familyMembers = familyMembers;
        this.me = me;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.join_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User participant = familyMembers.get(position);
        holder.bind(participant);
    }

    @Override
    public int getItemCount() {
        return familyMembers == null ? 0 : familyMembers.size();
    }

    public void setFamilyMembers(List<User> familyMembers) {
        this.familyMembers = familyMembers;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView familyMember_tv;

        public ViewHolder(View itemView) {
            super(itemView);
            familyMember_tv = itemView.findViewById(R.id.particiant);
        }

        public void bind(User familyMember) {
            familyMember_tv.setText(familyMember.getFamilyDisplayName(me));
        }
    }
}