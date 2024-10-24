package com.example.playerfinderapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playerfinderapp.R;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private ListView popularGamesList;
    private ImageButton searchButton, profileButton, chatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // Ensure this matches your XML filename

        // Initialize views
        popularGamesList = findViewById(R.id.popular_games_list);
        searchButton = findViewById(R.id.search_button);
        profileButton = findViewById(R.id.profile_button);
        chatButton = findViewById(R.id.chat_button);

        // Set up listeners for buttons
        setupListeners();

        // Set up click listener for games list
        popularGamesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedGame = (String) parent.getItemAtPosition(position);

                // Navigate to GameUsersActivity with the selected game
                Intent intent = new Intent(HomeActivity.this, GameUsersActivity.class);
                intent.putExtra("GAME_NAME", selectedGame);
                startActivity(intent);
            }
        });

        // Load popular games into the ListView
        loadPopularGames();
    }

    private void setupListeners() {
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Search Activity
                Intent searchIntent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(searchIntent);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Profile Activity
                //Intent profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
                //startActivity(profileIntent);
            }
        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Chat List Activity
                Intent chatIntent = new Intent(HomeActivity.this, ChatListActivity.class);
                startActivity(chatIntent);
            }
        });
    }

    private void loadPopularGames() {
        // Example list of popular games
        ArrayList<String> gameNames = new ArrayList<>();
        gameNames.add("ROBLOX");
        gameNames.add("Minecraft");
        gameNames.add("Fortnite");
        gameNames.add("Counter-Strike 2 & GO");
        gameNames.add("Call of Duty: Modern Warfare II/III/Warzone 2.0");
        gameNames.add("The Sims 4");
        gameNames.add("League of Legends");
        gameNames.add("Valorant");
        gameNames.add("Grand Theft Auto V");
        gameNames.add("Overwatch 1 & 2");
        gameNames.add("Rocket League");
        gameNames.add("World of Warcraft");
        gameNames.add("Tom Clancy's Rainbow Six: Siege");
        gameNames.add("Cyberpunk 2077");
        gameNames.add("Diablo IV");
        gameNames.add("Apex Legends");
        gameNames.add("EA Sports FC 24");
        gameNames.add("Genshin Impact");
        gameNames.add("Escape From Tarkov");
        gameNames.add("BeamNG.drive");

        // Set up the ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, gameNames);
        popularGamesList.setAdapter(adapter);
    }
}
