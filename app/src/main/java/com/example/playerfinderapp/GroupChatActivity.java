package com.example.playerfinderapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity {

    private TextView chatTitle;
    private ImageButton returnButton, sendButton;
    private RecyclerView messagesRecyclerView;
    private MessageAdapter messageAdapter; // Custom adapter for RecyclerView
    private List<Message> messageList; // List to hold messages
    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistration; // For Firestore updates
    private EditText messageInput;
    private ImageView groupProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize views
        returnButton = findViewById(R.id.return_button);
        chatTitle = findViewById(R.id.chat_title);
        sendButton = findViewById(R.id.send_button);
        messageInput = findViewById(R.id.message_input);
        groupProfilePicture = findViewById(R.id.profile_picture); // Profile picture reference
        messagesRecyclerView = findViewById(R.id.messages_recycler_view);

        // Set up RecyclerView
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messageList);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(messageAdapter);

        db = FirebaseFirestore.getInstance();

        // Get the data from the intent
        Intent intent = getIntent();
        if (intent.hasExtra("groupName")) {
            String groupName = intent.getStringExtra("groupName");
            chatTitle.setText(groupName); // Set the group name as title

            // Load chat messages
            loadGroupChatMessages(groupName);
            // Load group profile picture
            loadGroupProfile(groupName);

            // Set up click listener for group name/picture
            groupProfilePicture.setOnClickListener(v -> {
                Intent groupDetailsIntent = new Intent(GroupChatActivity.this, GroupDetailsActivity.class);
                groupDetailsIntent.putExtra("groupName", groupName);
                startActivity(groupDetailsIntent);
            });

            chatTitle.setOnClickListener(v -> {
                Intent groupDetailsIntent = new Intent(GroupChatActivity.this, GroupDetailsActivity.class);
                groupDetailsIntent.putExtra("groupName", groupName);
                startActivity(groupDetailsIntent);
            });
        }

        // Return button action
        returnButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(GroupChatActivity.this, ChatListActivity.class);
            startActivity(backIntent);
            finish();
        });

        // Send button action
        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessageToFirestore(messageText, intent.getStringExtra("groupName"));
                messageInput.setText(""); // Clear the input field
            }
        });
    }

    private void loadGroupChatMessages(String groupName) {
        String currentUserId = "user_id"; // Replace with the actual current user ID
        String chatId = getGroupChatId(currentUserId, groupName);

        // Reference to the messages sub-collection
        CollectionReference messagesRef = db.collection("groupChats").document(chatId).collection("messages");

        listenerRegistration = messagesRef.orderBy("timestamp").addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Toast.makeText(this, "Error loading messages: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (queryDocumentSnapshots != null) {
                messageList.clear(); // Clear existing messages
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    Message message = document.toObject(Message.class);
                    messageList.add(message);
                }
                messageAdapter.notifyDataSetChanged(); // Notify adapter of data change
                messagesRecyclerView.scrollToPosition(messageList.size() - 1); // Scroll to the latest message
            }
        });
    }

    private void sendMessageToFirestore(String messageText, String groupName) {
        String currentUserId = "user_id"; // Replace with actual current user ID
        String chatId = getGroupChatId(currentUserId, groupName);

        // Create a new message object
        Message message = new Message();
        message.setText(messageText);
        message.setSenderId(currentUserId);
        message.setSent(true); // Assuming this indicates it's sent by the current user

        // Set the current timestamp as a Date object
        message.setTimestamp(new Date(System.currentTimeMillis()));  // Set current time as Date

        // Add message to Firestore
        db.collection("groupChats")
                .document(chatId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    // Optionally handle success (e.g., logging)
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to send message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadGroupProfile(String groupName) {
        // This assumes you have a method to fetch the group's profile information
        db.collection("groups")
                .document(groupName)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String profilePicUrl = documentSnapshot.getString("profilePictureUrl");
                        if (profilePicUrl != null) {
                            // Load the profile picture into the ImageView using Glide
                            Glide.with(this)
                                    .load(profilePicUrl)
                                    .placeholder(R.drawable.ic_profile_picture_default) // Placeholder image
                                    .into(groupProfilePicture);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load group profile picture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Remove listener to prevent memory leaks
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }

    private String getGroupChatId(String userId, String groupName) {
        return userId + "_" + groupName; // Unique identifier for group chats
    }
}
