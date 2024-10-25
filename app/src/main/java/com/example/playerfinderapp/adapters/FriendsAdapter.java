package com.example.playerfinderapp.adapters;

import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playerfinderapp.R;
import com.example.playerfinderapp.activities.CreateNewChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    private List<Map<String, Object>> friendsList;
    private boolean isOwnProfile;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public FriendsAdapter(List<Map<String, Object>> friendsList, boolean isOwnProfile) {
        this.friendsList = friendsList;
        this.isOwnProfile = isOwnProfile;
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> friend = friendsList.get(position);
        String friendId = (String) friend.get("uid");
        String username = (String) friend.get("username");

        holder.usernameText.setText(username);

        if (isOwnProfile) {
            holder.removeButton.setVisibility(View.VISIBLE);
            holder.addButton.setVisibility(View.GONE);
            holder.removeButton.setOnClickListener(v -> showRemoveConfirmationDialog(position, friendId, v));
        } else {
            holder.removeButton.setVisibility(View.GONE);
            holder.addButton.setVisibility(View.VISIBLE);

            // Handle add button click for chat creation
            holder.addButton.setOnClickListener(v -> {
                try {
                    if (v.getContext() instanceof CreateNewChatActivity) {
                        CreateNewChatActivity activity = (CreateNewChatActivity) v.getContext();
                        activity.createChat(friendId, username);
                    }
                } catch (Exception e) {
                    Log.e("FriendsAdapter", "Error creating chat: ", e);
                    Toast.makeText(v.getContext(), "Error creating chat: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }


    private void removeFriend(int position, String friendId, View view) {
        String currentUserId = auth.getCurrentUser().getUid();

        // Remove from both users' friends collections
        db.collection("users").document(currentUserId)
                .collection("friends").document(friendId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Remove the friend from the local list and update UI
                    friendsList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, friendsList.size());

                    // Show success message
                    Toast.makeText(view.getContext(),
                            "Friend removed successfully", Toast.LENGTH_SHORT).show();

                    // Remove from friend's friends list
                    db.collection("users").document(friendId)
                            .collection("friends").document(currentUserId)
                            .delete()
                            .addOnFailureListener(e ->
                                    Toast.makeText(view.getContext(),
                                            "Error removing from friend's list: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(view.getContext(),
                                "Error removing friend: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }

    private void showRemoveConfirmationDialog(int position, String friendId, View view) {
        new AlertDialog.Builder(view.getContext())
                .setTitle("Remove Friend")
                .setMessage("Are you sure you want to remove this friend?")
                .setPositiveButton("Yes", (dialog, which) -> removeFriend(position, friendId, view))
                .setNegativeButton("No", null)
                .show();
    }

    private void sendFriendRequest(String friendId, View view) {
        String currentUserId = auth.getCurrentUser().getUid();

        Map<String, Object> request = new HashMap<>();
        request.put("from", currentUserId);
        request.put("timestamp", FieldValue.serverTimestamp());

        db.collection("users").document(friendId)
                .collection("friendRequests").document(currentUserId)
                .set(request)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(view.getContext(), "Friend request sent!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(view.getContext(), "Failed to send request", Toast.LENGTH_SHORT).show());
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView usernameText;
        ImageButton removeButton;
        ImageButton addButton;

        ViewHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.friend_profile_image);
            usernameText = itemView.findViewById(R.id.friend_username);
            removeButton = itemView.findViewById(R.id.remove_friend_button);
            addButton = itemView.findViewById(R.id.add_friend_button);
        }
    }
}
