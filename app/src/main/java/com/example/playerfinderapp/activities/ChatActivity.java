package com.example.playerfinderapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playerfinderapp.R;
import com.example.playerfinderapp.adapters.MessageAdapter;
import com.example.playerfinderapp.models.Message;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private RecyclerView recyclerViewMessages;
    private EditText editTextMessage;
    private ImageButton buttonSend;
    private MessageAdapter messageAdapter;
    private List<Message> messages;
    private TextView usernameTextView;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private ListenerRegistration messageListener;

    private String chatId;
    private String friendId;
    private String friendName;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Get intent extras
        chatId = getIntent().getStringExtra("chatId");
        friendId = getIntent().getStringExtra("friendId");
        friendName = getIntent().getStringExtra("friendName");

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        // Initialize views
        recyclerViewMessages = findViewById(R.id.messages_recycler_view);
        editTextMessage = findViewById(R.id.message_input);
        buttonSend = findViewById(R.id.send_button);
        usernameTextView = findViewById(R.id.username);
        ImageButton returnButton = findViewById(R.id.return_button);

        // Set up username
        usernameTextView.setText(friendName);
        usernameTextView.setVisibility(View.VISIBLE);

        // Set up return button
        returnButton.setOnClickListener(v -> finish());

        // Set up messages
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messages, currentUserId);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);

        // Load messages
        loadMessages();

        // Set up send button
        buttonSend.setOnClickListener(v -> sendMessage());
    }

    private void loadMessages() {
        if (chatId == null) return;

        Query query = firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING);

        messageListener = query.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e(TAG, "Error loading messages:", error);
                return;
            }

            messages.clear();
            if (value != null) {
                for (QueryDocumentSnapshot doc : value) {
                    Message message = doc.toObject(Message.class);
                    messages.add(message);
                }
                messageAdapter.notifyDataSetChanged();
                recyclerViewMessages.scrollToPosition(messages.size() - 1);
            }
        });
    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (messageText.isEmpty() || chatId == null) return;

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("text", messageText);
        messageData.put("senderId", currentUserId);
        messageData.put("timestamp", FieldValue.serverTimestamp());

        firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(messageData)
                .addOnSuccessListener(documentReference -> {
                    editTextMessage.setText("");
                    updateLastMessage(messageText);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error sending message: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }

    private void updateLastMessage(String messageText) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("lastMessage", messageText);
        updates.put("lastMessageTime", FieldValue.serverTimestamp());

        // Update both users' chat references
        firestore.collection("users")
                .document(currentUserId)
                .collection("chats")
                .document(chatId)
                .update(updates);

        firestore.collection("users")
                .document(friendId)
                .collection("chats")
                .document(chatId)
                .update(updates);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (messageListener != null) {
            messageListener.remove();
        }
    }
}