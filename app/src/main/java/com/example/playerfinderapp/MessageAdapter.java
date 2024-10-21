package com.example.playerfinderapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;
    private Context context;

    // Constructor for the adapter
    public MessageAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    // ViewHolder class for holding the views
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout sentMessageLayout, receivedMessageLayout;
        TextView sentMessageText, receivedMessageText;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            sentMessageLayout = itemView.findViewById(R.id.sent_message_layout);
            receivedMessageLayout = itemView.findViewById(R.id.received_message_layout);
            sentMessageText = itemView.findViewById(R.id.sent_message_text);
            receivedMessageText = itemView.findViewById(R.id.received_message_text);
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
            // Check if the message is sent or received and set visibility accordingly
            if (message.isSent()) {
                holder.sentMessageLayout.setVisibility(View.VISIBLE);
                holder.receivedMessageLayout.setVisibility(View.GONE);
                holder.sentMessageText.setText(message.getText());
            } else {
                holder.sentMessageLayout.setVisibility(View.GONE);
                holder.receivedMessageLayout.setVisibility(View.VISIBLE);
                holder.receivedMessageText.setText(message.getText());
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0; // Ensure safe return of item count
    }
}
