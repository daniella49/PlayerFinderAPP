package com.example.playerfinderapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class CreateNewChatActivity extends AppCompatActivity {

    private EditText searchUsername;
    private ListView friendsListView;
    private Button createGroupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_chat);

        // Return button
        ImageButton returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> finish()); // Return to the chat list

        searchUsername = findViewById(R.id.search_username);
        friendsListView = findViewById(R.id.friends_list_view);
        createGroupButton = findViewById(R.id.create_group_button);

        // Set up the friends list (you can replace this with actual data)
        String[] friendsNames = {"Alice", "Bob", "Charlie", "David"};
        int[] friendsImages = {R.drawable.ic_profile_picture_default, R.drawable.ic_profile_picture_default,
                R.drawable.ic_profile_picture_default, R.drawable.ic_profile_picture_default};

        ChatListAdapter friendsAdapter = new ChatListAdapter(this, friendsNames, friendsImages);
        friendsListView.setAdapter(friendsAdapter);

        // Click listener for friends list item
        friendsListView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            String selectedFriend = friendsNames[position];

            // Open chat window (this will handle both new and existing chats)
            Intent chatIntent = new Intent(CreateNewChatActivity.this, PrivateChatActivity.class);
            chatIntent.putExtra("friendName", selectedFriend); // Pass friend's name to chat window
            startActivity(chatIntent);
        });

        // Click listener for create group button
        createGroupButton.setOnClickListener(v -> {
            Intent createGroupIntent = new Intent(CreateNewChatActivity.this, CreateGroupActivity.class);
            startActivity(createGroupIntent);
        });
    }
}
