package com.example.playerfinderapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playerfinderapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GroupDetailsActivity extends AppCompatActivity {

    private ImageView groupProfilePicture;
    private EditText groupNameEditText, groupBioEditText;
    private Button leaveGroupButton, addFriendsButton;
    private boolean isAdmin; // Check if the user is the admin
    private Uri groupImageUri;
    private String groupId; // Declare groupId here

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_details);

        // Initialize views
        groupProfilePicture = findViewById(R.id.group_profile_picture);
        groupNameEditText = findViewById(R.id.group_name);
        groupBioEditText = findViewById(R.id.group_bio);
        leaveGroupButton = findViewById(R.id.leave_group_button);
        addFriendsButton = findViewById(R.id.add_friends_button);

        // Get the group ID from the Intent extras
        groupId = getIntent().getStringExtra("groupId");

        // Assuming you have a way to determine if the current user is the admin
        isAdmin = true; // Update this based on actual logic

        if (!isAdmin) {
            addFriendsButton.setVisibility(View.GONE); // Hide button if not admin
        }

        // Set up listeners
        leaveGroupButton.setOnClickListener(v -> leaveGroup());
        addFriendsButton.setOnClickListener(v -> addFriends());
    }

    private void leaveGroup() {
        // Get the current user's ID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Retrieve the group members from Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("groups").document(groupId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> members = (List<String>) documentSnapshot.get("members");
                        List<String> admins = (List<String>) documentSnapshot.get("admins");

                        // Check if the current user is an admin
                        if (admins.contains(currentUserId)) {
                            // If the current user is the admin, select a new admin randomly
                            if (members.size() > 1) { // Check if there are other members
                                members.remove(currentUserId); // Remove the current user from members
                                String newAdminId = members.get(new Random().nextInt(members.size()));

                                // Update the admins list
                                admins.remove(currentUserId);
                                admins.add(newAdminId);

                                // Update the group in Firestore
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("members", members);
                                updates.put("admins", admins);
                                db.collection("groups").document(groupId).update(updates)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "You have left the group, and a new admin has been selected.", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(GroupDetailsActivity.this, ChatListActivity.class));
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Error updating group data.", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                // If there are no other members, remove the group completely
                                db.collection("groups").document(groupId).delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "You have left the group, and the group has been deleted.", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(GroupDetailsActivity.this, ChatListActivity.class));
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Error deleting the group.", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            // If the current user is not an admin
                            members.remove(currentUserId); // Remove user from members
                            db.collection("groups").document(groupId).update("members", members)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "You have left the group.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(GroupDetailsActivity.this, ChatListActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Error updating group data.", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error retrieving group data.", Toast.LENGTH_SHORT).show();
                });
    }

    private void addFriends() {
        Intent intent = new Intent(GroupDetailsActivity.this, AddFriendsActivity.class);
        intent.putExtra("groupId", groupId); // Pass the group ID to the next activity
        startActivity(intent);
    }
}
