package com.example.playerfinderapp;

import android.content.Intent;
import android.graphics.Bitmap;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GroupDetailsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText groupBioEditText;
    private ImageView groupProfilePicture;
    private ImageButton editGroupNameButton;
    private ImageButton editGroupBioButton;
    private ImageButton editGroupPictureButton;
    private Button addMemberButton;
    private SearchView memberSearchView;
    private RecyclerView membersRecyclerView;
    private MemberAdapter memberAdapter;

    private String groupName;
    private Bitmap groupImage; // Store the new profile picture here
    private List<String> groupMembers; // List of current group members

    // Add a flag to check if the user is the group creator
    private boolean isGroupCreator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_details);

        // Initialize views
        groupBioEditText = findViewById(R.id.group_bio);
        groupProfilePicture = findViewById(R.id.group_profile_picture);
        editGroupNameButton = findViewById(R.id.edit_name_button);
        editGroupBioButton = findViewById(R.id.edit_bio_button);
        editGroupPictureButton = findViewById(R.id.edit_picture_button);
        addMemberButton = findViewById(R.id.add_member_button);
        memberSearchView = findViewById(R.id.member_search_view);
        membersRecyclerView = findViewById(R.id.members_recycler_view);

        // Get the current group name and bio from intent (pass this when starting the activity)
        Intent intent = getIntent();
        groupName = intent.getStringExtra("GROUP_NAME");
        String groupBio = intent.getStringExtra("GROUP_BIO");
        isGroupCreator = intent.getBooleanExtra("IS_GROUP_CREATOR", false); // Pass this from the previous activity

        // Set initial values
        setTitle(groupName);
        groupBioEditText.setText(groupBio);

        // Initialize the members list (from database)
        groupMembers = new ArrayList<>(); // Populate this with actual group members

        // Setup RecyclerView
        memberAdapter = new MemberAdapter(groupMembers, isGroupCreator, this::removeMemberFromGroup);
        membersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        membersRecyclerView.setAdapter(memberAdapter);

        // Setup listeners
        editGroupNameButton.setOnClickListener(v -> saveGroupName());
        editGroupBioButton.setOnClickListener(v -> saveGroupBio());
        editGroupPictureButton.setOnClickListener(v -> openImageChooser());
        addMemberButton.setOnClickListener(v -> showAddMembersDialog());

        //set up member search functionality
        setupMemberSearch();
    }

    private void saveGroupName() {
        // This should be replaced with your logic to save the group name in your database
        String newGroupName = groupBioEditText.getText().toString();
        Toast.makeText(this, "Group name updated to: " + newGroupName, Toast.LENGTH_SHORT).show();
    }

    private void saveGroupBio() {
        String newGroupBio = groupBioEditText.getText().toString();
        // Save the new group bio in your database
        Toast.makeText(this, "Group bio updated to: " + newGroupBio, Toast.LENGTH_SHORT).show();
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            groupProfilePicture.setImageURI(imageUri);
            // You may want to upload this image and store the URI
        }
    }

    private void showAddMembersDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Members");

        // List of all available friends (replace with actual data)
        List<String> friendsList = new ArrayList<>(); // Populate this with real friend data
        boolean[] checkedItems = new boolean[friendsList.size()];

        builder.setMultiChoiceItems(friendsList.toArray(new CharSequence[0]), checkedItems, (dialog, which, isChecked) -> {
            // Handle checkbox selection
        });

        builder.setPositiveButton("Add", (dialog, which) -> {
            for (int i = 0; i < checkedItems.length; i++) {
                if (checkedItems[i]) {
                    String friendName = friendsList.get(i);
                    if (!groupMembers.contains(friendName)) {
                        groupMembers.add(friendName);
                        memberAdapter.notifyItemInserted(groupMembers.size() - 1); // Notify adapter of the new member
                        Toast.makeText(this, friendName + " added to the group!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, friendName + " is already a member!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void removeMemberFromGroup(String memberName) {
        groupMembers.remove(memberName);
        memberAdapter.notifyDataSetChanged(); // Notify the adapter to refresh the list
        Toast.makeText(this, memberName + " removed from the group!", Toast.LENGTH_SHORT).show();
    }

    private void setupMemberSearch() {
        memberSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search submit
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                memberAdapter.filter(newText); // Implement this method in the adapter
                return false;
            }
        });
    }
}
