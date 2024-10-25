package com.example.playerfinderapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.playerfinderapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFriendsActivity extends AppCompatActivity {
    private LinearLayout friendsLayout;
    private List<String> selectedFriends;
    private String groupId;
    private Map<String, String> friendsMap; // To store friend IDs and names

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        friendsLayout = findViewById(R.id.friends_layout);
        selectedFriends = new ArrayList<>();
        friendsMap = new HashMap<>();
        groupId = getIntent().getStringExtra("groupId");

        // Fetch friends from Firestore
        fetchFriends();

        Button addFriendsButton = findViewById(R.id.add_friends_button);
        addFriendsButton.setOnClickListener(v -> addSelectedFriends());
    }

    private void fetchFriends() {
        // Assume you have a collection called "users" where friend data is stored
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch the user's friends
        db.collection("users").document(currentUserId).collection("friends")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String friendId = document.getId(); // The friend's ID
                        String friendName = document.getString("name"); // The friend's name
                        addFriendCheckbox(friendId, friendName);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching friends.", Toast.LENGTH_SHORT).show();
                });
    }

    private void addFriendCheckbox(String friendId, String friendName) {
        // Create a CheckBox for each friend
        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(friendName);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedFriends.add(friendId); // Add friend ID to the list
            } else {
                selectedFriends.remove(friendId); // Remove friend ID from the list
            }
        });
        friendsLayout.addView(checkBox);
        friendsMap.put(friendId, friendName); // Map to keep track of friend IDs and names
    }

    private void addSelectedFriends() {
        if (selectedFriends.isEmpty()) {
            Toast.makeText(this, "No friends selected.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Add the selected friends to the current user's "friends" collection
        for (String friendId : selectedFriends) {
            db.collection("users").document(currentUserId).collection("friends")
                    .document(friendId)
                    .set(new HashMap<>()) // Optionally, add other info like friendName here
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Friend " + friendsMap.get(friendId) + " added to your friends list.", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error adding friend " + friendsMap.get(friendId), Toast.LENGTH_SHORT).show();
                    });
        }

        // Add friends to the group if necessary
        db.collection("groups").document(groupId)
                .update("members", FieldValue.arrayUnion(selectedFriends))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Friends added to the group.", Toast.LENGTH_SHORT).show();
                    finish(); // Return to GroupDetailsActivity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error adding friends to group.", Toast.LENGTH_SHORT).show();
                });
    }
}
