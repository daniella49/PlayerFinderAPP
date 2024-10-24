package com.example.playerfinderapp.models;

public class PrivateChat {
    private String chatId; // Unique identifier for the chat session
    private String userId; // ID of the user initiating the chat
    private String recipientUserId; // ID of the recipient user
    private String userName; // Name of the recipient user
    private String lastMessage; // Last message in the chat
    private String profileImageUrl; // URL for the profile image

    // Constructor
    public PrivateChat(String chatId, String userId, String recipientUserId, String userName, String lastMessage, String profileImageUrl) {
        this.chatId = chatId;
        this.userId = userId;
        this.recipientUserId = recipientUserId;
        this.userName = userName;
        this.lastMessage = lastMessage;
        this.profileImageUrl = profileImageUrl;
    }

    // No-argument constructor required by Firestore
    public PrivateChat() {
    }

    public PrivateChat(String userName, String lastMessage, String profileImageUrl) {
        this.chatId = chatId;
        this.userId = userId;
        this.recipientUserId = recipientUserId;
        this.userName = userName;
        this.lastMessage = lastMessage;
        this.profileImageUrl = profileImageUrl;
    }

    // Getters
    public String getChatId() {
        return chatId;
    }

    public String getUserId() {
        return userId;
    }

    public String getRecipientUserId() {
        return recipientUserId;
    }

    public String getUserName() {
        return userName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    // Setters
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRecipientUserId(String recipientUserId) {
        this.recipientUserId = recipientUserId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
