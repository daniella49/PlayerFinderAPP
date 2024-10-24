package com.example.playerfinderapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playerfinderapp.R;
import com.example.playerfinderapp.adapters.MessageAdapter;
import com.example.playerfinderapp.models.Message;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMessages;
    private EditText editTextMessage;
    private Button buttonSend;
    private MessageAdapter messageAdapter;
    private List<Message> messages;

    private FirebaseFirestore firestore;
    private ListenerRegistration messageListener;

    private String chatId; // Group or Private chat ID
    private String currentUserId; // ID of the logged-in user
    private boolean isGroupChat; // True if this is a group chat, false if private

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize views
        recyclerViewMessages = findViewById(R.id.messages_recycler_view);
        editTextMessage = findViewById(R.id.message_input);
        buttonSend = findViewById(R.id.send_button);
        TextView groupNameTextView = findViewById(R.id.group_name);
        TextView usernameTextView = findViewById(R.id.username);

        firestore = FirebaseFirestore.getInstance();

        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messages, isGroupChat);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);

        // Get chatId and isGroupChat from intent extras
        chatId = getIntent().getStringExtra("CHAT_ID");
        isGroupChat = getIntent().getBooleanExtra("IS_GROUP_CHAT", false);
        currentUserId = ""; // Assign the logged-in user's ID here

        // Load messages
        loadMessages();

        buttonSend.setOnClickListener(view -> sendMessage());

        // Set up click listeners for group name and username
        if (isGroupChat) {
            groupNameTextView.setVisibility(View.VISIBLE);
            groupNameTextView.setOnClickListener(view -> {
                Intent intent = new Intent(ChatActivity.this, GroupDetailsActivity.class);
                intent.putExtra("CHAT_ID", chatId);
                startActivity(intent);
            });
        } else {
            usernameTextView.setVisibility(View.VISIBLE);
            usernameTextView.setOnClickListener(view -> {
                Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
                intent.putExtra("USER_ID", currentUserId); // Pass the user's ID for the profile
                startActivity(intent);
            });
        }
    }

    private void loadMessages() {
        // Listener for real-time updates
        Query query = firestore.collection("chats").document(chatId).collection("messages").orderBy("timestamp");
        messageListener = query.addSnapshotListener((value, error) -> {
            if (error != null || value == null) return;

            messages.clear();
            for (QueryDocumentSnapshot doc : value) {
                Message message = doc.toObject(Message.class);
                messages.add(message);
            }
            messageAdapter.notifyDataSetChanged();
            recyclerViewMessages.scrollToPosition(messages.size() - 1); // Scroll to the bottom
        });
    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (messageText.isEmpty()) return;

        Message message = new Message(messageText, currentUserId, "", true, new Date());
        firestore.collection("chats").document(chatId).collection("messages").add(message);

        editTextMessage.setText(""); // Clear the input field
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (messageListener != null) {
            messageListener.remove(); // Remove the listener when the activity is stopped
        }
    }
}
