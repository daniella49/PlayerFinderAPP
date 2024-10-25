package com.example.playerfinderapp.models;

import com.google.firebase.Timestamp;

public class Chat {
    private String chatId;
    private String username;
    private String lastMessage = "";
    private Timestamp lastMessageTime;
    private String friendId;
    private int unreadCount = 0;

    // Required empty constructor for Firestore
    public Chat() {}

    // Getters and Setters with null safety
    public String getChatId() {
        return chatId != null ? chatId : "";
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUsername() {
        return username != null ? username : "";
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastMessage() {
        return lastMessage != null ? lastMessage : "";
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Timestamp getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Timestamp lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public String getFriendId() {
        return friendId != null ? friendId : "";
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}