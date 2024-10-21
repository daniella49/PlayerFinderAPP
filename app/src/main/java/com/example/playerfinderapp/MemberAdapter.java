package com.example.playerfinderapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private List<String> memberList;
    private List<String> memberListFull; // For filtering
    private boolean isGroupCreator;
    private OnMemberRemoveListener onMemberRemoveListener;

    public MemberAdapter(List<String> memberList, boolean isGroupCreator, OnMemberRemoveListener onMemberRemoveListener) {
        this.memberList = memberList;
        this.memberListFull = new ArrayList<>(memberList);
        this.isGroupCreator = isGroupCreator;
        this.onMemberRemoveListener = onMemberRemoveListener;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        String memberName = memberList.get(position);
        holder.memberNameTextView.setText(memberName);

        if (isGroupCreator) {
            holder.removeButton.setVisibility(View.VISIBLE);
            holder.removeButton.setOnClickListener(v -> {
                onMemberRemoveListener.onMemberRemove(memberName);
            });
        } else {
            holder.removeButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public void filter(String text) {
        memberList.clear();
        if (text.isEmpty()) {
            memberList.addAll(memberListFull);
        } else {
            String filterPattern = text.toLowerCase().trim();
            for (String member : memberListFull) {
                if (member.toLowerCase().contains(filterPattern)) {
                    memberList.add(member);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView memberNameTextView;
        Button removeButton;

        MemberViewHolder(View itemView) {
            super(itemView);
            memberNameTextView = itemView.findViewById(R.id.member_name_text_view);
            removeButton = itemView.findViewById(R.id.remove_member_button); 
        }
    }

    public interface OnMemberRemoveListener {
        void onMemberRemove(String memberName);
    }
}
