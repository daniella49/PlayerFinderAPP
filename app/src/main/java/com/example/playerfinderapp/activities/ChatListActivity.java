package com.example.playerfinderapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playerfinderapp.models.Chat;
import com.example.playerfinderapp.R;
import com.example.playerfinderapp.adapters.ChatListAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.List;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.Timestamp;


public class ChatListActivity extends AppCompatActivity {
    private ImageButton returnButton;
    private ImageButton newChatButton;
    private RecyclerView recyclerViewChatList;
    private ChatListAdapter chatListAdapter;
    private List<Chat> chatList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize views
        returnButton = findViewById(R.id.return_button);
        newChatButton = findViewById(R.id.new_chat_button);
        recyclerViewChatList = findViewById(R.id.recycler_view_chat_list);

        // Initialize chat list
        chatList = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(chatList, new ChatListAdapter.OnChatClickListener() {
            @Override
            public void onChatClick(Chat chat) {
                openChat(chat);
            }

            @Override
            public void onTrashClick(Chat chat) {
                removeChat(chat);
            }
        });

        // Set up RecyclerView
        recyclerViewChatList.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChatList.setAdapter(chatListAdapter);

        // Set up button listeners
        returnButton.setOnClickListener(v -> finish());
        newChatButton.setOnClickListener(v ->
                startActivity(new Intent(ChatListActivity.this, CreateNewChatActivity.class)));

        // Load chats
        loadChats();
    }

    private void loadChats() {
        String currentUserId = auth.getCurrentUser().getUid();

        db.collection("users")
                .document(currentUserId)
                .collection("chats")
                .orderBy("lastMessageTime", Query.Direction.DESCENDING)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Error loading chats: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    chatList.clear();
                    if (value != null) {
                        for (DocumentSnapshot doc : value) {
                            try {
                                Chat chat = new Chat();

                                // Required fields
                                chat.setChatId(doc.getString("chatId"));
                                chat.setUsername(doc.getString("friendName"));
                                chat.setFriendId(doc.getString("friendId"));

                                // Optional fields with defaults
                                chat.setLastMessage(doc.getString("lastMessage") != null ?
                                        doc.getString("lastMessage") : "No messages yet");

                                Timestamp timestamp = doc.getTimestamp("lastMessageTime");
                                if (timestamp != null) {
                                    chat.setLastMessageTime(timestamp);
                                }

                                // Safely get unreadCount with default value 0
                                Long unreadCount = doc.getLong("unreadCount");
                                chat.setUnreadCount(unreadCount != null ? unreadCount.intValue() : 0);

                                chatList.add(chat);
                            } catch (Exception ex) {
                                Log.e("ChatList", "Error parsing chat document: ", ex);
                            }
                        }
                        chatListAdapter.notifyDataSetChanged();

                        // Show empty state if needed
                        if (chatList.isEmpty()) {
                            // You can show an empty state view here
                            Toast.makeText(this, "No chats yet", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void openChat(Chat chat) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("chatId", chat.getChatId());
        intent.putExtra("friendId", chat.getFriendId());
        intent.putExtra("friendName", chat.getUsername());
        startActivity(intent);
    }

    private void removeChat(Chat chat) {
        String currentUserId = auth.getCurrentUser().getUid();
        String chatId = chat.getChatId();
        String friendId = chat.getFriendId();

        // Create a batch to remove all chat references
        db.runTransaction(transaction -> {
            // Remove from current user's chats
            transaction.delete(
                    db.collection("users")
                            .document(currentUserId)
                            .collection("chats")
                            .document(chatId)
            );

            // Remove from friend's chats
            transaction.delete(
                    db.collection("users")
                            .document(friendId)
                            .collection("chats")
                            .document(chatId)
            );

            // Remove the main chat document
            transaction.delete(db.collection("chats").document(chatId));

            return null;
        }).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Chat removed", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error removing chat: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        });
    }
}