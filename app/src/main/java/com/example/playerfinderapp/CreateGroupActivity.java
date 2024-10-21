package com.example.playerfinderapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;


public class CreateGroupActivity extends AppCompatActivity {

    private EditText groupNameEditText;
    private ImageButton groupImageButton; // For choosing a group image
    private Button createGroupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        groupNameEditText = findViewById(R.id.group_name_edit_text);
        groupImageButton = findViewById(R.id.group_image_button);
        createGroupButton = findViewById(R.id.create_group_button);
        ImageButton returnButton = findViewById(R.id.return_button);

        // Set click listener for return button
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Return to the previous activity
            }
        });

        // Set click listener for create group button
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroup(); // Handle group creation logic
            }
        });
    }

    private void createGroup() {
        String groupName = groupNameEditText.getText().toString();
        // Add logic to create the group and start the chat

        // For now, just display a message
        if (!groupName.isEmpty()) {
            // Logic to create group goes here
            Toast.makeText(this, "Group \"" + groupName + "\" created!", Toast.LENGTH_SHORT).show();
            finish(); // Return to previous activity after creating the group
        } else {
            Toast.makeText(this, "Please enter a group name.", Toast.LENGTH_SHORT).show();
        }
    }
}
