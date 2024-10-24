package com.example.playerfinderapp.adapters;

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

        // Show different buttons based on whether it's the user's own profile
        if (isOwnProfile) {
            holder.removeButton.setVisibility(View.VISIBLE);
            holder.addButton.setVisibility(View.GONE);

            // Handle remove friend
            holder.removeButton.setOnClickListener(v -> removeFriend(position, friendId));
        } else {
            holder.removeButton.setVisibility(View.GONE);
            holder.addButton.setVisibility(View.VISIBLE);

            // Handle add friend
            holder.addButton.setOnClickListener(v -> sendFriendRequest(friendId, v));
        }

        // Handle profile click
        holder.itemView.setOnClickListener(v -> {
            // Navigate to friend's profile
            // Implementation depends on your navigation setup
        });
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    private void removeFriend(int position, String friendId) {
        String currentUserId = auth.getCurrentUser().getUid();

        // Remove from both users' friends collections
        db.collection("users").document(currentUserId)
                .collection("friends").document(friendId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    friendsList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, friendsList.size());
                });

        // Also remove from friend's friends list
        db.collection("users").document(friendId)
                .collection("friends").document(currentUserId)
                .delete();
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

    static class ViewHolder extends RecyclerView.ViewHolder {
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
