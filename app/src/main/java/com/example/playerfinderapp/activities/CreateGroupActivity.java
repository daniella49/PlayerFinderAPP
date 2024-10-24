package com.example.playerfinderapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playerfinderapp.R;
import com.example.playerfinderapp.adapters.MemberAdapter;
import com.example.playerfinderapp.models.GroupChat;
import com.example.playerfinderapp.models.User; // Ensure you have this User model
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageButton returnButton;
    private EditText groupNameEditText;
    private ImageButton groupImageButton;
    private SearchView memberSearchView;
    private RecyclerView membersRecyclerView;
    private Button createGroupButton;

    private MemberAdapter memberAdapter;
    private List<User> allMembers; // All friends
    private List<User> selectedMembers; // Selected members
    private Uri groupImageUri; // Uri for selected image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        returnButton = findViewById(R.id.return_button);
        groupNameEditText = findViewById(R.id.group_name_edit_text);
        groupImageButton = findViewById(R.id.group_image_button);
        memberSearchView = findViewById(R.id.member_search_view);
        membersRecyclerView = findViewById(R.id.members_recycler_view);
        createGroupButton = findViewById(R.id.create_group_button);

        // Initialize the selected members list
        selectedMembers = new ArrayList<>();

        // Load all members (you need to implement this to fetch users)
        loadAllMembers();

        // Set up RecyclerView
        membersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        memberAdapter = new MemberAdapter(allMembers, selectedMembers);
        membersRecyclerView.setAdapter(memberAdapter);

        // Return button click listener
        returnButton.setOnClickListener(v -> finish());

        // Group image button click listener
        groupImageButton.setOnClickListener(v -> openFileChooser());

        // Search functionality
        memberSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                memberAdapter.filter(newText);
                return true;
            }
        });

        // Create group button click listener
        createGroupButton.setOnClickListener(v -> createGroup());
    }

    private void loadAllMembers() {
        // Load your friends list from Firebase or wherever you're storing them
        // For now, we'll create a mock list
        allMembers = new ArrayList<>();
        // Add logic to fetch members from Firebase Firestore or any other data source
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Group Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            groupImageUri = data.getData();
            // Optionally, you can set the selected image to the ImageButton
            groupImageButton.setImageURI(groupImageUri);
        }
    }

    private void createGroup() {
        String groupName = groupNameEditText.getText().toString().trim();

        if (groupName.isEmpty()) {
            Toast.makeText(this, "Please enter a group name.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare the list of member usernames
        List<String> memberUsernames = new ArrayList<>();
        for (User member : selectedMembers) {
            memberUsernames.add(member.getUsername());
        }

        // Set default image if no image is selected
        Uri finalGroupImageUri;
        if (groupImageUri != null) {
            finalGroupImageUri = groupImageUri; // This should already be a Uri
        } else {
            finalGroupImageUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.ic_profile_picture_default);
        }

        // Create a Group object
        GroupChat newGroup = new GroupChat(groupName, finalGroupImageUri.toString(), memberUsernames);

        // Save the group to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("groups") // Firestore collection name
                .add(newGroup)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(CreateGroupActivity.this, "Group created successfully!", Toast.LENGTH_SHORT).show();
                    // Redirect to the chat page after group creation
                    Intent intent = new Intent(CreateGroupActivity.this, ChatActivity.class); // Adjust with your ChatActivity
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CreateGroupActivity.this, "Error creating group: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


}
