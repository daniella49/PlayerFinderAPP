package com.example.playerfinderapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playerfinderapp.R;
import com.example.playerfinderapp.adapters.FriendsAdapter;
import com.example.playerfinderapp.adapters.GamesAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String userId;
    private boolean isOwnProfile;

    private ImageButton backButton, settingsButton, addFriendButton;
    private ImageView profilePicture;
    private TextView usernameText, bioText;
    private RecyclerView favoriteGamesList, friendsList;
    private GamesAdapter gamesAdapter;
    private FriendsAdapter friendsAdapter;

    private ActivityResultLauncher<Intent> editProfileLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Register activity result launcher for EditProfileActivity
        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Refresh profile data when returning from edit
                        loadProfileData();
                    }
                });

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Get user ID from intent or current user
        userId = getIntent().getStringExtra("USER_ID");
        if (userId == null) {
            userId = auth.getCurrentUser().getUid(); // This can also be null if user is not logged in
            isOwnProfile = true;
        } else {
            isOwnProfile = userId.equals(auth.getCurrentUser().getUid());
        }

        initializeViews();
        setupButtons();
        loadProfileData();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.back_button);
        settingsButton = findViewById(R.id.settings_button);
        addFriendButton = findViewById(R.id.add_friend_button);
        profilePicture = findViewById(R.id.profile_picture);
        usernameText = findViewById(R.id.username_text);
        bioText = findViewById(R.id.bio_text);
        favoriteGamesList = findViewById(R.id.favorite_games_list);
        friendsList = findViewById(R.id.friends_list);


        // Setup RecyclerViews
        favoriteGamesList.setLayoutManager(new LinearLayoutManager(this));
        friendsList.setLayoutManager(new LinearLayoutManager(this));

        // Show appropriate buttons based on profile type
        settingsButton.setVisibility(isOwnProfile ? View.VISIBLE : View.GONE);
        addFriendButton.setVisibility(isOwnProfile ? View.GONE : View.VISIBLE);

        // Check if already friends
        if (!isOwnProfile) {
            checkIfAlreadyFriends();
        }
    }

    // Add this new method to check friendship status
    private void checkIfAlreadyFriends() {
        String currentUserId = auth.getCurrentUser().getUid();

        db.collection("users").document(currentUserId)
                .collection("friends")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Already friends, update UI
                        addFriendButton.setImageResource(R.drawable.ic_check);
                        addFriendButton.setEnabled(false);
                    }
                });
    }

    private void setupButtons() {
        backButton.setOnClickListener(v -> finish());

        settingsButton.setOnClickListener(v -> {
            if (isOwnProfile) {
                showSettingsDialog();
            }
        });

        addFriendButton.setOnClickListener(v -> {
            if (!isOwnProfile) {
                addFriend();
            }
        });
    }

    private void loadProfileData() {
        if (userId == null) {
            Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show();
            return; // Exit the method if userId is null
        }

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        usernameText.setText(documentSnapshot.getString("username"));
                        bioText.setText(documentSnapshot.getString("bio"));

                        // Load favorite games
                        List<String> games = (List<String>) documentSnapshot.get("favoriteGames");
                        if (games != null) {
                            gamesAdapter = new GamesAdapter(games);
                            favoriteGamesList.setAdapter(gamesAdapter);
                        }

                        // Load friends
                        loadFriends();
                    } else {
                        Toast.makeText(this, "User profile does not exist.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Load friends
        loadFriends();
    }

    private void loadFriends() {
        db.collection("users").document(userId)
                .collection("friends")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, Object>> friendsList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        friendsList.add(document.getData());
                    }
                    friendsAdapter = new FriendsAdapter(friendsList, isOwnProfile);
                    this.friendsList.setAdapter(friendsAdapter);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load friends: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private void showSettingsDialog() {
        String[] options = {"Edit Profile", "Log Out", "Delete Account"};

        new AlertDialog.Builder(this)
                .setTitle("Settings")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            // Use the launcher instead of startActivity
                            editProfileLauncher.launch(new Intent(this, EditProfileActivity.class));
                            break;
                        case 1:
                            auth.signOut();
                            startActivity(new Intent(this, MainActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            break;
                        case 2:
                            showDeleteAccountConfirmation();
                            break;
                    }
                })
                .show();
    }

    private void showDeleteAccountConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteAccount())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAccount() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            // First delete user data from Firestore
            db.collection("users").document(uid)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Then delete authentication account
                        user.delete()
                                .addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(this, "Account deleted successfully",
                                            Toast.LENGTH_SHORT).show();
                                    // Change LoginActivity.class to MainActivity.class
                                    Intent intent = new Intent(this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Error deleting account: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error deleting user data: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show());
        }
    }

    private void addFriend() {
        String currentUserId = auth.getCurrentUser().getUid();

        // Skip if trying to add self
        if (currentUserId.equals(userId)) {
            Toast.makeText(this, "Cannot add yourself as friend", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current user's data first
        db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(currentUserDoc -> {
                    String currentUsername = currentUserDoc.getString("username");

                    // Create data for friend's list (current user's data)
                    Map<String, Object> currentUserData = new HashMap<>();
                    currentUserData.put("uid", currentUserId);
                    currentUserData.put("username", currentUsername);

                    // Get friend's data
                    db.collection("users").document(userId)
                            .get()
                            .addOnSuccessListener(friendDoc -> {
                                String friendUsername = friendDoc.getString("username");

                                // Create data for current user's friend list
                                Map<String, Object> friendData = new HashMap<>();
                                friendData.put("uid", userId);
                                friendData.put("username", friendUsername);

                                // Add friend to current user's friends list
                                db.collection("users").document(currentUserId)
                                        .collection("friends")
                                        .document(userId)
                                        .set(friendData)
                                        .addOnSuccessListener(aVoid -> {
                                            // Add current user to friend's friends list
                                            db.collection("users").document(userId)
                                                    .collection("friends")
                                                    .document(currentUserId)
                                                    .set(currentUserData)
                                                    .addOnSuccessListener(aVoid2 -> {
                                                        Toast.makeText(this, "Friend added successfully!", Toast.LENGTH_SHORT).show();

                                                        // Update the UI
                                                        addFriendButton.setImageResource(R.drawable.ic_check);
                                                        addFriendButton.setEnabled(false);

                                                        // Refresh friends list
                                                        loadFriends();
                                                    })
                                                    .addOnFailureListener(e ->
                                                            Toast.makeText(this, "Failed to add to friend's list: " + e.getMessage(),
                                                                    Toast.LENGTH_SHORT).show());
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(this, "Failed to add friend: " + e.getMessage(),
                                                        Toast.LENGTH_SHORT).show());
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Failed to get friend's data: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to get current user's data: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }


}
