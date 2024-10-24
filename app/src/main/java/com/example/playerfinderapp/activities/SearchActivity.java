package com.example.playerfinderapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playerfinderapp.adapters.UserAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.playerfinderapp.R;

public class SearchActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private EditText searchEditText;
    private ImageButton searchButton;
    private ImageButton  backButton;
    private RecyclerView searchResultsRecyclerView;
    private UserAdapter userAdapter;
    private List<Map<String, Object>> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userList = new ArrayList<>();

        // Initialize UI elements
        searchEditText = findViewById(R.id.search_input);
        searchButton = findViewById(R.id.search_button);
        backButton = findViewById(R.id.back_button);
        ImageButton clearInputButton = findViewById(R.id.clear_input_button);
        searchResultsRecyclerView = findViewById(R.id.search_results_recyclerview);

        // Set up RecyclerView
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(userList);
        searchResultsRecyclerView.setAdapter(userAdapter);

        // Search button click listener
        searchButton.setOnClickListener(v -> {
            String searchText = searchEditText.getText().toString().trim();
            if (!searchText.isEmpty()) {
                searchUsers(searchText);
            } else {
                Toast.makeText(SearchActivity.this, "Please enter a username to search", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle clear input button click
        clearInputButton.setOnClickListener(v -> {
            searchEditText.setText(""); // Clear the EditText input
        });

        // Handle back button click
        backButton.setOnClickListener(v -> finish());
    }


//    private void searchUsers(String searchText) {
//        // Query Firestore for users
//        db.collection("users")
//                .whereGreaterThanOrEqualTo("username", searchText)
//                .whereLessThanOrEqualTo("username", searchText + "\uf8ff")
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    userList.clear();
//                    for (DocumentSnapshot document : queryDocumentSnapshots) {
//                        Map<String, Object> userData = document.getData();
//                        if (userData != null) {
//                            userList.add(userData);
//                        }
//                    }
//
//                    if (userList.isEmpty()) {
//                        Toast.makeText(SearchActivity.this, "No users found", Toast.LENGTH_SHORT).show();
//                    }
//
//                    userAdapter.notifyDataSetChanged();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(SearchActivity.this, "Error searching users: " + e.getMessage(),
//                            Toast.LENGTH_SHORT).show();
//                    Log.e("SearchActivity", "Error searching users", e);
//                });
//    }

    private void searchUsers(String searchText) {
        // Get current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "You must be logged in to search", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserId = currentUser.getUid();

        db.collection("users")
                .whereGreaterThanOrEqualTo("username", searchText)
                .whereLessThanOrEqualTo("username", searchText + "\uf8ff")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, Object>> searchResults = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        // Get the user ID from the document
                        String userId = document.getString("uid");

                        // Only add to results if it's not the current user
                        if (userId != null && !userId.equals(currentUserId)) {
                            Map<String, Object> userData = document.getData();
                            if (userData != null) {
                                searchResults.add(userData);
                            }
                        }
                    }

                    if (searchResults.isEmpty()) {
                        Toast.makeText(SearchActivity.this, "No users found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Launch SearchResultsActivity with filtered results
                    Intent intent = new Intent(SearchActivity.this, SearchResultsActivity.class);
                    intent.putExtra("searchResults", new ArrayList<>(searchResults));
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SearchActivity.this, "Error searching users: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    Log.e("SearchActivity", "Search error", e);
                });
    }
}
