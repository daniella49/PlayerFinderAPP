package com.example.playerfinderapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playerfinderapp.models.Chat;
import com.example.playerfinderapp.R;
import com.example.playerfinderapp.adapters.ChatListAdapter;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {

    private ImageButton returnButton;
    private ImageButton newChatButton;
    private RecyclerView recyclerViewChatList;
    private ChatListAdapter chatListAdapter;
    private List<Chat> chatList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list); // Your layout XML

        returnButton = findViewById(R.id.return_button);
        newChatButton = findViewById(R.id.new_chat_button);
        recyclerViewChatList = findViewById(R.id.recycler_view_chat_list);

        db = FirebaseFirestore.getInstance();
        chatList = new ArrayList<>();

        // Set up RecyclerView
        recyclerViewChatList.setLayoutManager(new LinearLayoutManager(this));
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
        recyclerViewChatList.setAdapter(chatListAdapter);

        // Load chats from Firestore
        loadChats();

        // Set return button listener
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // This will redirect back to the home activity
            }
        });

        // Set new chat button listener
        newChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatListActivity.this, CreateNewChatActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadChats() {
        // Load user's chat list from Firestore
        db.collection("chats")
                .whereEqualTo("userId", "userId") // Replace with actual user ID logic
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot value, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(ChatListActivity.this, "Error loading chats: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        chatList.clear();
                        if (value != null) {
                            for (QueryDocumentSnapshot doc : value) {
                                Chat chat = doc.toObject(Chat.class);
                                chatList.add(chat);
                            }
                            chatListAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void openChat(Chat chat) {
        Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
        intent.putExtra("chatId", chat.getChatId()); // Assuming Chat has getChatId() method
        startActivity(intent);
    }

    private void removeChat(Chat chat) {
        db.collection("chats").document(chat.getChatId()).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ChatListActivity.this, "Chat removed", Toast.LENGTH_SHORT).show();
                    loadChats(); // Reload chats after deletion
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ChatListActivity.this, "Error removing chat: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
