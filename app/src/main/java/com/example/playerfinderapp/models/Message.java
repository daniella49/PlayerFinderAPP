package com.example.playerfinderapp.models;

import com.google.firebase.Timestamp;

public class Message {
    private String text;
    private String senderId;
    private Timestamp timestamp;

    // Required empty constructor for Firestore
    public Message() {}

    public Message(String text, String senderId, Timestamp timestamp) {
        this.text = text;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    // Getters
    public String getText() { return text != null ? text : ""; }
    public String getSenderId() { return senderId != null ? senderId : ""; }
    public Timestamp getTimestamp() { return timestamp; }

    // Setters
    public void setText(String text) { this.text = text; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}