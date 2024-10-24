package com.example.playerfinderapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private static final String TAG = "UserAdapter";
    private List<Map<String, Object>> userList;

    // Constructor
    public UserAdapter(List<Map<String, Object>> userList) {
        this.userList = userList;
        Log.d(TAG, "UserAdapter created with " + userList.size() + " users");
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_list_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Map<String, Object> user = userList.get(position);

        // Set username
        String username = (String) user.get("username");
        holder.usernameTextView.setText(username != null ? username : "");

        // Handle add button click
        holder.addUserButton.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Added " + username, Toast.LENGTH_SHORT).show();
            // Add your logic here for what happens when the add button is clicked
        });

        // Profile image is set to default in XML (ic_profile_picture_default)
        // If you later add profile image URLs, you can load them here using Glide or Picasso
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public void updateUserList(List<Map<String, Object>> newUserList) {
        this.userList = newUserList;
        notifyDataSetChanged();
    }

    // ViewHolder class
    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        ImageView profileImageView;
        ImageView addUserButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views
            usernameTextView = itemView.findViewById(R.id.username);
            profileImageView = itemView.findViewById(R.id.profile_image);
            addUserButton = itemView.findViewById(R.id.add_user_button);
        }
    }
}