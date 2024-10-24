package com.example.playerfinderapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.playerfinderapp.R;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView usernameTextView;
    private ImageView profileImageView;
    private TextView bioTextView;
    private TextView favoriteGamesTextView;
    private Button returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        usernameTextView = findViewById(R.id.username_text_view);
        profileImageView = findViewById(R.id.profile_image_view);
        bioTextView = findViewById(R.id.bio_text_view);
        favoriteGamesTextView = findViewById(R.id.favorite_games_text_view);
        returnButton = findViewById(R.id.return_button);

        // Set user data (you should replace this with actual user data)
        String username = "User Name"; // Replace with actual user name
        String bio = "This is a short biography about the user."; // Replace with actual bio
        List<String> favoriteGames = List.of("Game 1", "Game 2", "Game 3"); // Replace with actual favorite games

        // Set data to views
        usernameTextView.setText(username);
        bioTextView.setText(bio);
        favoriteGamesTextView.setText(String.join(", ", favoriteGames)); // Display favorite games as a comma-separated list

        // Set up return button
        returnButton.setOnClickListener(v -> finish()); // Close the activity and return to the previous one
    }
}
