package com.example.playerfinderapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.playerfinderapp.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playerfinderapp.adapters.FriendsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CreateNewChatActivity extends AppCompatActivity {
    private static final String TAG = "CreateNewChatActivity";

    private List<Map<String, Object>> friendsList;
    private FriendsAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String currentUserId;
    private RecyclerView friendsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_chat);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        // Check if we're coming from a direct chat request
        String friendId = getIntent().getStringExtra("friendId");
        String friendName = getIntent().getStringExtra("friendName");

        if (friendId != null && friendName != null) {
            // Directly create chat with this friend
            createChat(friendId, friendName);
            return; // Skip the rest of the initialization
        }

        // Normal initialization for friend list view
        // Initialize components
        friendsList = new ArrayList<>();
        friendsRecyclerView = findViewById(R.id.friends_recycler_view);
        EditText searchEditText = findViewById(R.id.search_username);
        ImageButton returnButton = findViewById(R.id.return_button);

        // Setup RecyclerView
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FriendsAdapter(friendsList, false) {
            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);

                // Override click listener for chat creation
                holder.itemView.setOnClickListener(v -> {
                    Map<String, Object> friend = friendsList.get(position);
                    createChat((String) friend.get("uid"), (String) friend.get("username"));
                });
            }
        };
        friendsRecyclerView.setAdapter(adapter);

        // Fetch friends
        fetchFriends();

        // Setup search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFriends(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Setup return button
        returnButton.setOnClickListener(v -> finish());
    }

    private void fetchFriends() {
        String currentUserId = auth.getCurrentUser().getUid();

        db.collection("users").document(currentUserId)
                .collection("friends")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    friendsList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Map<String, Object> friend = new HashMap<>();
                        friend.put("uid", doc.getString("uid"));
                        friend.put("username", doc.getString("username"));
                        friendsList.add(friend);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error fetching friends: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }

    private void filterFriends(String searchText) {
        if (searchText.isEmpty()) {
            fetchFriends(); // Reset to original list
            return;
        }

        List<Map<String, Object>> filteredList = new ArrayList<>();
        for (Map<String, Object> friend : friendsList) {
            String username = (String) friend.get("username");
            if (username != null && username.toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(friend);
            }
        }

        friendsList.clear();
        friendsList.addAll(filteredList);
        adapter.notifyDataSetChanged();
    }


    public void createChat(String friendId, String friendName) {
        if (auth.getCurrentUser() == null) {
            Log.d(TAG, "User not authenticated");
            Toast.makeText(this, "Please sign in first", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserId = auth.getCurrentUser().getUid();
        String chatId = generateChatId(currentUserId, friendId);

        Log.d(TAG, "Attempting to create chat with friendId: " + friendId + ", friendName: " + friendName);

        // First check if chat exists
        db.collection("users")
                .document(currentUserId)
                .collection("chats")
                .document(chatId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "Chat already exists, navigating to chat");
                        navigateToChat(chatId, friendId, friendName);
                    } else {
                        Log.d(TAG, "Creating new chat");
                        createNewChat(chatId, currentUserId, friendId, friendName);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking existing chat: ", e);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void createNewChat(String chatId, String currentUserId, String friendId, String friendName) {
        // Get current user's data first
        db.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(currentUserDoc -> {
                    String currentUsername = currentUserDoc.getString("username");
                    Log.d(TAG, "Got current username: " + currentUsername);

                    // Create main chat document
                    Map<String, Object> chatData = new HashMap<>();
                    chatData.put("participants", Arrays.asList(currentUserId, friendId));
                    chatData.put("createdAt", FieldValue.serverTimestamp());
                    chatData.put("lastMessage", "");
                    chatData.put("lastMessageTime", FieldValue.serverTimestamp());

                    db.collection("chats")
                            .document(chatId)
                            .set(chatData)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Main chat document created successfully");
                                createUserChatReferences(chatId, currentUserId, currentUsername, friendId, friendName);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error creating main chat document: ", e);
                                Toast.makeText(this, "Error creating chat: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting current user data: ", e);
                    Toast.makeText(this, "Error getting user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void createUserChatReferences(String chatId, String currentUserId, String currentUsername,
                                          String friendId, String friendName) {
        // Create chat reference for current user
        Map<String, Object> currentUserChat = new HashMap<>();
        currentUserChat.put("chatId", chatId);
        currentUserChat.put("friendId", friendId);
        currentUserChat.put("friendName", friendName);
        currentUserChat.put("lastMessage", "");
        currentUserChat.put("lastMessageTime", FieldValue.serverTimestamp());
        currentUserChat.put("unreadCount", 0);

        db.collection("users")
                .document(currentUserId)
                .collection("chats")
                .document(chatId)
                .set(currentUserChat)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Current user chat reference created");

                    // Create chat reference for friend
                    Map<String, Object> friendChat = new HashMap<>();
                    friendChat.put("chatId", chatId);
                    friendChat.put("friendId", currentUserId);
                    friendChat.put("friendName", currentUsername);
                    friendChat.put("lastMessage", "");
                    friendChat.put("lastMessageTime", FieldValue.serverTimestamp());
                    friendChat.put("unreadCount", 0);

                    db.collection("users")
                            .document(friendId)
                            .collection("chats")
                            .document(chatId)
                            .set(friendChat)
                            .addOnSuccessListener(aVoid1 -> {
                                Log.d(TAG, "Friend chat reference created, navigating to chat");
                                Toast.makeText(this, "Chat created successfully", Toast.LENGTH_SHORT).show();
                                navigateToChat(chatId, friendId, friendName);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error creating friend chat reference: ", e);
                                Toast.makeText(this, "Error creating friend's chat reference: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating current user chat reference: ", e);
                    Toast.makeText(this, "Error creating chat reference: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }


    private String generateChatId(String userId1, String userId2) {
        List<String> ids = Arrays.asList(userId1, userId2);
        Collections.sort(ids);
        return ids.get(0) + "_" + ids.get(1);
    }

    private void navigateToChat(String chatId, String friendId, String friendName) {
        Log.d(TAG, "Navigating to chat with id: " + chatId + ", friend: " + friendName);
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("chatId", chatId);
        intent.putExtra("friendId", friendId);
        intent.putExtra("friendName", friendName);
        startActivity(intent);
        finish();
    }
}
