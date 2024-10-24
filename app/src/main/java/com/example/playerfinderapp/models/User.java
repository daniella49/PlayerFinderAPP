package com.example.playerfinderapp.models;

public class User {
    private String id; // Unique identifier for the user
    private String username; // Username of the user
    private String profilePictureUrl; // URL of the user's profile picture

    // Default constructor (needed for Firebase)
    public User() {
    }

    // Constructor for creating a user with an ID
    public User(String id, String username, String profilePictureUrl) {
        this.id = id;
        this.username = username;
        this.profilePictureUrl = profilePictureUrl;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}
