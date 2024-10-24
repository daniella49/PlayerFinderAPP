package com.example.playerfinderapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.playerfinderapp.R;
import com.example.playerfinderapp.models.PrivateChat;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CreateNewChatActivity extends AppCompatActivity {
    private List<PrivateChat> friendsList;
    private FriendsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_chat);

        // Initialize the list of friends
        friendsList = new ArrayList<>();

        // Find ListView in the layout
        ListView friendsListView = findViewById(R.id.friends_list_view);
        EditText searchUsername = findViewById(R.id.search_username);

        // Set up custom adapter
        adapter = new FriendsAdapter(this, friendsList);
        friendsListView.setAdapter(adapter);

        // Fetch friends from Firestore
        fetchFriends();

        // Implement search functionality
        searchUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());  // Filter friends
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Return button logic to go back to Chat List
        ImageButton returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> {
            Intent intent = new Intent(CreateNewChatActivity.this, ChatListActivity.class); // Ensure ChatListActivity is the correct activity
            startActivity(intent);
            finish(); // Finish current activity to prevent returning to it
        });

        // Create group button logic
        findViewById(R.id.create_group_button).setOnClickListener(v -> {
            Intent intent = new Intent(CreateNewChatActivity.this, CreateGroupActivity.class);
            startActivity(intent);
        });
    }

    private void fetchFriends() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")  // Adjust this collection name as per Firestore structure
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            PrivateChat friend = document.toObject(PrivateChat.class);
                            friendsList.add(friend);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error getting friends: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Adapter for friends list
    public class FriendsAdapter extends BaseAdapter {
        private Context context;
        private List<PrivateChat> friends;
        private List<PrivateChat> filteredFriends;

        public FriendsAdapter(Context context, List<PrivateChat> friends) {
            this.context = context;
            this.friends = new ArrayList<>(friends);
            this.filteredFriends = new ArrayList<>(friends);
        }

        @Override
        public int getCount() {
            return filteredFriends.size();
        }

        @Override
        public Object getItem(int position) {
            return filteredFriends.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.friend_list_item, parent, false);
            }

            TextView friendNameTextView = convertView.findViewById(R.id.friend_name);
            ImageView friendProfileImageView = convertView.findViewById(R.id.friend_profile_image);

            PrivateChat friend = filteredFriends.get(position);
            friendNameTextView.setText(friend.getUserName());

            // Load profile image using Glide
            Glide.with(context).load(friend.getProfileImageUrl()).into(friendProfileImageView);

            // Handle friend item click
            convertView.setOnClickListener(v -> {
                // Navigate to chat activity
                startChatActivity(friend);
            });

            return convertView;
        }

        // Filter for search
        public android.widget.Filter getFilter() {
            return new android.widget.Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    List<PrivateChat> filteredList = new ArrayList<>();
                    if (constraint == null || constraint.length() == 0) {
                        filteredList.addAll(friends);
                    } else {
                        String filterPattern = constraint.toString().toLowerCase().trim();
                        for (PrivateChat friend : friends) {
                            if (friend.getUserName().toLowerCase().contains(filterPattern)) {
                                filteredList.add(friend);
                            }
                        }
                    }
                    FilterResults results = new FilterResults();
                    results.values = filteredList;
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredFriends.clear();
                    filteredFriends.addAll((List<PrivateChat>) results.values);
                    notifyDataSetChanged();
                }
            };
        }

        private void startChatActivity(PrivateChat friend) {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("friendId", friend.getUserId());  // Pass necessary data
            context.startActivity(intent);
        }
    }
}
