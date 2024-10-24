package com.example.playerfinderapp.models;

public class Chat {
    private String chatId;
    private String username;      // For private chats, this is the friend's username
    private String groupName;     // For group chats, this is the group's name
    private String lastMessage;
    private String profileImageUrl; // Profile image URL for the user or group
    private boolean isGroupChat;  // Flag to determine if it's a group chat or private chat

    // Constructor for private chat
    public Chat(String chatId, String username, String lastMessage, String profileImageUrl) {
        this.chatId = chatId;
        this.username = username;
        this.lastMessage = lastMessage;
        this.profileImageUrl = profileImageUrl;
        this.isGroupChat = false;
    }

    // Constructor for group chat
    public Chat(String chatId, String groupName, String lastMessage, String profileImageUrl, boolean isGroupChat) {
        this.chatId = chatId;
        this.groupName = groupName;
        this.lastMessage = lastMessage;
        this.profileImageUrl = profileImageUrl;
        this.isGroupChat = true;
    }

    // Getters and setters for chat ID, username, group name, etc.
    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    // For private chats, return the username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // For group chats, return the group name
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public boolean isGroupChat() {
        return isGroupChat;
    }

    public void setGroupChat(boolean groupChat) {
        isGroupChat = groupChat;
    }

    // Return the display name (username for private chat, group name for group chat)
    public String getDisplayName() {
        return isGroupChat ? groupName : username;
    }
}

