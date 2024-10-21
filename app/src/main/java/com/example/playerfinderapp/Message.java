package com.example.playerfinderapp;

import java.util.Date;

public class Message {
    private String text;
    private String senderId; // ID of the user who sent the message
    private boolean isSent; // true if the message is sent by the user, false if received
    private Date timestamp; // Timestamp of when the message was sent

    // No-argument constructor (required for Firestore)
    public Message() {
    }

    // Constructor with parameters
    public Message(String text, String senderId, boolean isSent, Date timestamp) {
        this.text = text;
        this.senderId = senderId;
        this.isSent = isSent;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    // toString method for easy logging/debugging
    @Override
    public String toString() {
        return "Message{" +
                "text='" + text + '\'' +
                ", senderId='" + senderId + '\'' +
                ", isSent=" + isSent +
                ", timestamp=" + timestamp +
                '}';
    }
}
