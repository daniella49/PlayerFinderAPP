package com.example.playerfinderapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playerfinderapp.R;
import com.example.playerfinderapp.activities.GroupDetailsActivity;
import com.example.playerfinderapp.activities.ProfileActivity;
import com.example.playerfinderapp.models.Message;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;
    private Context context;
    private SimpleDateFormat timeFormat;
    private boolean isGroupChat; // Flag to check if it's a group chat

    // Constructor for the adapter
    public MessageAdapter(Context context, List<Message> messages, boolean isGroupChat) {
        this.context = context;
        this.messages = messages;
        this.isGroupChat = isGroupChat;
        this.timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault()); // 12-hour format with AM/PM
    }

    // ViewHolder class for holding the views
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout sentMessageLayout, receivedMessageLayout;
        TextView sentMessageText, receivedMessageText;
        TextView sentMessageTime, receivedMessageTime; // New time TextViews
        TextView sentMessageUsername, receivedMessageUsername; // New username TextViews

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            sentMessageLayout = itemView.findViewById(R.id.sent_message_layout);
            receivedMessageLayout = itemView.findViewById(R.id.received_message_layout);
            sentMessageText = itemView.findViewById(R.id.sent_message_text);
            receivedMessageText = itemView.findViewById(R.id.received_message_text);
            sentMessageTime = itemView.findViewById(R.id.sent_message_time); // New
            receivedMessageTime = itemView.findViewById(R.id.received_message_time); // New
            sentMessageUsername = itemView.findViewById(R.id.sent_message_username); // New
            receivedMessageUsername = itemView.findViewById(R.id.received_message_username); // New
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the chat item layout (chat_item.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);

        if (message != null) { // Check if the message is not null
            String formattedTime = timeFormat.format(message.getTimestamp()); // Format the timestamp
            String username = message.getSenderUsername(); // Fetch the sender's username

            // Check if the message is sent or received and set visibility accordingly
            if (message.isSent()) {
                holder.sentMessageLayout.setVisibility(View.VISIBLE);
                holder.receivedMessageLayout.setVisibility(View.GONE);
                holder.sentMessageText.setText(message.getMessageText()); // Set message text
                holder.sentMessageTime.setText(formattedTime); // Set timestamp
                holder.sentMessageUsername.setText(username); // Set username
            } else {
                holder.sentMessageLayout.setVisibility(View.GONE);
                holder.receivedMessageLayout.setVisibility(View.VISIBLE);
                holder.receivedMessageText.setText(message.getMessageText()); // Set message text
                holder.receivedMessageTime.setText(formattedTime); // Set timestamp
                holder.receivedMessageUsername.setText(username); // Set username
            }

            // Set up click listener on the username (both sent and received)
            View.OnClickListener profileClickListener = v -> {
                if (isGroupChat) {
                    // Navigate to group chat details
                    Intent intent = new Intent(context, GroupDetailsActivity.class);
                    intent.putExtra("GROUP_ID", message.getSenderId()); // Pass the group ID
                    context.startActivity(intent);
                } else {
                    // Navigate to user profile page
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("USER_ID", message.getSenderId()); // Pass the user's ID
                    context.startActivity(intent);
                }
            };

            // Attach the click listeners
            holder.sentMessageUsername.setOnClickListener(profileClickListener);
            holder.receivedMessageUsername.setOnClickListener(profileClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0; // Ensure safe return of item count
    }
}

