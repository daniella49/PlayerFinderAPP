package com.example.playerfinderapp.activities;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playerfinderapp.R;
import com.example.playerfinderapp.adapters.UserAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameUsersActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView usersRecyclerView;
    private UserAdapter userAdapter;
    private List<Map<String, Object>> userList;
    private String gameName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_users);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get game name from intent
        gameName = getIntent().getStringExtra("GAME_NAME");
        if (gameName == null) {
            Toast.makeText(this, "Error: No game selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        ImageButton backButton = findViewById(R.id.back_button);
        TextView titleTextView = findViewById(R.id.title_text);
        usersRecyclerView = findViewById(R.id.users_recyclerview);

        // Set title
        titleTextView.setText("Players of " + gameName);

        // Setup RecyclerView
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersRecyclerView.setAdapter(userAdapter);

        // Setup back button
        backButton.setOnClickListener(v -> finish());

        // Load users who play this game
        loadUsersForGame();
    }

    private void loadUsersForGame() {
        db.collection("users")
                .whereArrayContains("favoriteGames", gameName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Map<String, Object> userData = document.getData();
                        userList.add(userData);
                    }

                    if (userList.isEmpty()) {
                        Toast.makeText(this, "No users found for " + gameName, Toast.LENGTH_SHORT).show();
                    }

                    userAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading users: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}