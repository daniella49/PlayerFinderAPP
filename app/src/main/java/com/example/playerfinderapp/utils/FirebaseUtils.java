package com.example.playerfinderapp.utils;

import android.net.Uri;
import android.util.Log;

import com.example.playerfinderapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class FirebaseUtils {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final FirebaseStorage storage = FirebaseStorage.getInstance();

    public interface UsersListCallback {
        void onUsersListLoaded(List<User> users);
    }

    public interface FriendsListCallback {
        void onFriendsListLoaded(List<User> friends);
    }

    public interface ImageUploadCallback {
        void onImageUploaded(String imageUrl);
    }

    // Method to get the list of users from Firestore
    public static void getUsersList(UsersListCallback callback) {
        List<User> users = new ArrayList<>();

        db.collection("users") // Replace "users" with your actual Firestore collection name
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId(); // Get the document ID
                            String username = document.getString("username");
                            String profilePictureUrl = document.getString("profilePictureUrl");

                            // Create a User object and add it to the list
                            users.add(new User(id, username, profilePictureUrl));
                        }
                        // Call the callback method with the retrieved users
                        callback.onUsersListLoaded(users);
                    } else {
                        // Handle the error
                        Log.e("FirebaseUtils", "Error getting users list: ", task.getException());
                        callback.onUsersListLoaded(new ArrayList<>()); // Return empty list on error
                    }
                });
    }

    // Method to get the friends list (similar to getUsersList)
    public static void getFriendsList(String userId, FriendsListCallback callback) {
        List<User> friends = new ArrayList<>();

        db.collection("users") // Replace with your Firestore collection
                .document(userId) // Assuming you store friends list under the user's document
                .collection("friends") // Replace with your friends sub-collection name
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId(); // Get the document ID
                            String username = document.getString("username");
                            String profilePictureUrl = document.getString("profilePictureUrl");

                            // Create a User object and add it to the list
                            friends.add(new User(id, username, profilePictureUrl));
                        }
                        callback.onFriendsListLoaded(friends);
                    } else {
                        Log.e("FirebaseUtils", "Error getting friends list: ", task.getException());
                        callback.onFriendsListLoaded(new ArrayList<>()); // Return empty list on error
                    }
                });
    }

    // Method to upload an image to Firebase Storage
    public static void uploadImage(Uri imageUri, ImageUploadCallback callback) {
        if (imageUri == null) {
            callback.onImageUploaded(null);
            return;
        }

        StorageReference storageRef = storage.getReference().child("group_images/" + imageUri.getLastPathSegment());
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the image URL after uploading
                    storageRef.getDownloadUrl().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String imageUrl = task.getResult().toString();
                            callback.onImageUploaded(imageUrl);
                        } else {
                            Log.e("FirebaseUtils", "Error getting image URL: ", task.getException());
                            callback.onImageUploaded(null);
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseUtils", "Image upload failed: ", e);
                    callback.onImageUploaded(null);
                });
    }
}
