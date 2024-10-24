package com.example.playerfinderapp.models;

import java.util.Date;

public class Message {
    private String messageText;
    private String senderId; // User ID of the sender
    private String senderUsername; //  the sender's username
    private boolean isSent; // field to indicate if the message was sent
    private Date timestamp; // Time the message was sent

    public Message() {
        // Required empty constructor for Firestore
    }

    public Message(String messageText, String senderId, String senderUsername, boolean isSent, Date timestamp) {
        this.messageText = messageText;
        this.senderId = senderId;
        this.senderUsername = senderUsername; // Initialize the username
        this.isSent = isSent;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getMessageText() {
        return messageText;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderUsername() {
        return senderUsername; // Getter for the username
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername; // Setter for the username
    }

    public boolean isSent() { // Getter for isSent
        return isSent;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}

