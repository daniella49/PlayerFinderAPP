package com.example.playerfinderapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChatListAdapter extends BaseAdapter {

    private Context context;
    private String[] usernames; // Array of usernames
    private int[] profileImages; // Array of profile image resources
    private LayoutInflater inflater;

    public ChatListAdapter(Context context, String[] usernames, int[] profileImages) {
        this.context = context;
        this.usernames = usernames;
        this.profileImages = profileImages;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return usernames.length; // Number of items in the list
    }

    @Override
    public Object getItem(int position) {
        return usernames[position]; // Get the item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position; // Return the position as the item ID
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate the chat list item layout if convertView is null
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.chat_list_item, parent, false);
        }

        // Get the views for the current list item
        ImageView profileImage = convertView.findViewById(R.id.profile_image);
        TextView username = convertView.findViewById(R.id.username);
        ImageView trashIcon = convertView.findViewById(R.id.trash_icon);

        // Set the data to the views
        profileImage.setImageResource(profileImages[position]); // Set profile image
        username.setText(usernames[position]); // Set username

        // Set the click listener for the trash icon
        trashIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle chat deletion (you'll implement this logic later)
                // For example, you might remove the chat from the data source
            }
        });

        return convertView; // Return the completed view
    }
}


//This adapter populates the ListView items with user profiles and names using the layout chat_list_item.xml, and a remove chat button.//





