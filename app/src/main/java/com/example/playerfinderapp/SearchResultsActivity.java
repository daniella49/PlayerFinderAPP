package com.example.playerfinderapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResultsActivity extends AppCompatActivity {

    private RecyclerView resultsRecyclerView;
    private UserAdapter userAdapter;
    private List<Map<String, Object>> userList = new ArrayList<>();
    private List<Map<String, Object>> originalUserList = new ArrayList<>(); // To keep original search results
    private ImageButton backButton;
    private boolean searchByFavoriteGames = false;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize views
        resultsRecyclerView = findViewById(R.id.results_recyclerview);
        backButton = findViewById(R.id.back_button);

        // Set up RecyclerView with proper spacing
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        resultsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);
        resultsRecyclerView.setAdapter(userAdapter);

        // Get search results from intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            ArrayList<HashMap<String, Object>> searchResults =
                    (ArrayList<HashMap<String, Object>>) bundle.getSerializable("searchResults");
            if (searchResults != null && !searchResults.isEmpty()) {
                Log.d("SearchResultsActivity", "Received " + searchResults.size() + " results");
                userList.clear();
                userList.addAll(searchResults);
                userAdapter.notifyDataSetChanged();
            } else {
                Log.d("SearchResultsActivity", "No results received");
                Toast.makeText(this, "No users found", Toast.LENGTH_SHORT).show();
            }
        }

        // Handle back button click
        backButton.setOnClickListener(v -> finish());

        // Handle filter button click
        Button filterButton = findViewById(R.id.filter_button);
        if (filterButton != null) {
            filterButton.setOnClickListener(v -> showFilterDialog());
        }
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter Options");

        final CheckBox favoriteGamesCheckBox = new CheckBox(this);
        favoriteGamesCheckBox.setText("Filter by Favorite Games");
        favoriteGamesCheckBox.setChecked(searchByFavoriteGames);
        builder.setView(favoriteGamesCheckBox);

        builder.setPositiveButton("Apply", (dialog, which) -> {
            searchByFavoriteGames = favoriteGamesCheckBox.isChecked();
            applyFilters();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void applyFilters() {
        if (searchByFavoriteGames) {
            String currentUserId = auth.getCurrentUser().getUid();

            // Get current user's favorite games
            db.collection("users").document(currentUserId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        List<String> currentUserGames = (List<String>) documentSnapshot.get("favoriteGames");

                        if (currentUserGames != null && !currentUserGames.isEmpty()) {
                            // Filter users based on shared games
                            List<Map<String, Object>> filteredList = new ArrayList<>();

                            for (Map<String, Object> user : originalUserList) {
                                List<String> userGames = (List<String>) user.get("favoriteGames");
                                if (userGames != null && !userGames.isEmpty()) {
                                    // Check for any common games
                                    for (String game : currentUserGames) {
                                        if (userGames.contains(game)) {
                                            filteredList.add(user);
                                            break;
                                        }
                                    }
                                }
                            }

                            // Update the display
                            userList.clear();
                            userList.addAll(filteredList);
                            userAdapter.notifyDataSetChanged();

                            if (filteredList.isEmpty()) {
                                Toast.makeText(this, "No users found with similar games",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "You haven't added any favorite games yet",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error fetching your games: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Reset to original search results
            userList.clear();
            userList.addAll(originalUserList);
            userAdapter.notifyDataSetChanged();
        }
    }
}