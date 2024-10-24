package com.example.playerfinderapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playerfinderapp.R;
import com.example.playerfinderapp.adapters.GamesSelectionAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ImageButton backButton;
    private ImageView profilePicture;
    private EditText usernameInput, bioInput;
    private RecyclerView gamesRecyclerView;
    private Button saveButton;
    private List<String> selectedGames;
    private GamesSelectionAdapter gamesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        selectedGames = new ArrayList<>();

        initializeViews();
        loadCurrentData();
        setupListeners();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.back_button);
        profilePicture = findViewById(R.id.profile_picture);
        usernameInput = findViewById(R.id.username_input);
        bioInput = findViewById(R.id.bio_input);
        gamesRecyclerView = findViewById(R.id.games_recycler_view);
        saveButton = findViewById(R.id.save_button);

        // Setup games RecyclerView
        gamesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        gamesAdapter = new GamesSelectionAdapter(getPopularGames(), selectedGames);
        gamesRecyclerView.setAdapter(gamesAdapter);
    }

    private void loadCurrentData() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        usernameInput.setText(documentSnapshot.getString("username"));
                        bioInput.setText(documentSnapshot.getString("bio"));
                        List<String> games = (List<String>) documentSnapshot.get("favoriteGames");
                        if (games != null) {
                            selectedGames.clear();
                            selectedGames.addAll(games);
                            gamesAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading profile data", Toast.LENGTH_SHORT).show());
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> finish());

        saveButton.setOnClickListener(v -> saveProfileChanges());

        profilePicture.setOnClickListener(v -> {
            // Implement profile picture change functionality
            // This could open gallery or camera
        });
    }

    private void saveProfileChanges() {
        String userId = auth.getCurrentUser().getUid();
        String username = usernameInput.getText().toString().trim();
        String bio = bioInput.getText().toString().trim();

        if (username.isEmpty()) {
            usernameInput.setError("Username cannot be empty");
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("username", username);
        updates.put("bio", bio);
        updates.put("favoriteGames", selectedGames);

        db.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    // Set result OK to trigger refresh in ProfileActivity
                    setResult(Activity.RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error updating profile: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                });
    }

    private List<String> getPopularGames() {
        // Return the same list as in HomeActivity
        List<String> games = new ArrayList<>();
        games.add("ROBLOX");
        games.add("Minecraft");
        games.add("Fortnite");
        games.add("Counter-Strike 2 & GO");
        games.add("Call of Duty: Modern Warfare II/III/Warzone 2.0");
        games.add("The Sims 4");
        games.add("League of Legends");
        games.add("Valorant");
        games.add("Grand Theft Auto V");
        games.add("Overwatch 1 & 2");
        games.add("Rocket League");
        games.add("World of Warcraft");
        games.add("Tom Clancy's Rainbow Six: Siege");
        games.add("Cyberpunk 2077");
        games.add("Diablo IV");
        games.add("Apex Legends");
        games.add("EA Sports FC 24");
        games.add("Genshin Impact");
        games.add("Escape From Tarkov");
        games.add("BeamNG.drive");
        return games;
    }
}