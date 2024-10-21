package com.example.playerfinderapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

public class ChatListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        // Find ListView in the layout
        ListView chatListView = findViewById(R.id.chat_list_view);

        // Example data for chat list (usernames/groups)
        String[] names = {"Jack", "Friends"};
        int[] images = {R.drawable.ic_profile_picture_default, R.drawable.ic_profile_picture_default}; // Use different drawables

        // Set up custom adapter
        ChatListAdapter adapter = new ChatListAdapter(this, names, images);
        chatListView.setAdapter(adapter);

        // Return button
        ImageButton returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatListActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        // New chat button
        ImageButton newChatButton = findViewById(R.id.new_chat_button);
        newChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatListActivity.this, CreateNewChatActivity.class);
                startActivity(intent);
            }
        });
    }
}


