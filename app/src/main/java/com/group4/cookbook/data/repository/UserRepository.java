package com.group4.cookbook.data.repository;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.group4.cookbook.data.models.User;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private CollectionReference usersCollection;
    private CollectionReference followsCollection;

    public UserRepository() {
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        usersCollection = firestore.collection("users");
        followsCollection = firestore.collection("follows");
    }

    // Get current user
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    // Get user document from Firestore
    public Task<DocumentSnapshot> getUser(String userId) {
        return usersCollection.document(userId).get();
    }

    // Get user by ID (for AuthRepository compatibility)
    public Task<User> getUserById(String userId) {
        return usersCollection.document(userId).get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        return task.getResult().toObject(User.class);
                    }
                    return null;
                });
    }

    // Create user profile (for AuthRepository compatibility)
    public Task<Void> createUserProfile(String uid, String email, String username) {
        User user = new User(uid, username, email);
        return createOrUpdateUser(user);
    }

    // Check if username is available
    public Task<Boolean> isUsernameAvailable(String username) {
        return usersCollection.whereEqualTo("username", username).get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        return querySnapshot != null && querySnapshot.isEmpty();
                    }
                    return false;
                });
    }

    // Create or update user document
    public Task<Void> createOrUpdateUser(User user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", user.getUid());
        userData.put("username", user.getUsername());
        userData.put("email", user.getEmail());
        userData.put("bio", user.getBio());
        userData.put("profileImageUrl", user.getProfileImageUrl());
        userData.put("followers", user.getFollowers());
        userData.put("following", user.getFollowing());
        userData.put("recipeCount", user.getRecipeCount());
        userData.put("createdAt", user.getCreatedAt());
        userData.put("updatedAt", FieldValue.serverTimestamp());

        return usersCollection.document(user.getUid()).set(userData, SetOptions.merge());
    }

    // Follow a user
    public Task<Void> followUser(String followerId, String followedId) {
        String followId = followerId + "_" + followedId;

        Map<String, Object> followData = new HashMap<>();
        followData.put("followerId", followerId);
        followData.put("followedId", followedId);
        followData.put("followedAt", FieldValue.serverTimestamp());

        // Update follower's following count
        Task<Void> updateFollower = usersCollection.document(followerId)
                .update("following", FieldValue.increment(1));

        // Update followed user's followers count
        Task<Void> updateFollowed = usersCollection.document(followedId)
                .update("followers", FieldValue.increment(1));

        // Create follow document
        Task<Void> createFollow = followsCollection.document(followId).set(followData);

        // Execute all tasks
        return Tasks.whenAll(updateFollower, updateFollowed, createFollow);
    }

    // Unfollow a user
    public Task<Void> unfollowUser(String followerId, String followedId) {
        String followId = followerId + "_" + followedId;

        // Update follower's following count
        Task<Void> updateFollower = usersCollection.document(followerId)
                .update("following", FieldValue.increment(-1));

        // Update followed user's followers count
        Task<Void> updateFollowed = usersCollection.document(followedId)
                .update("followers", FieldValue.increment(-1));

        // Delete follow document
        Task<Void> deleteFollow = followsCollection.document(followId).delete();

        // Execute all tasks
        return Tasks.whenAll(updateFollower, updateFollowed, deleteFollow);
    }

    // Check if user is following another user
    public Task<DocumentSnapshot> checkIfFollowing(String followerId, String followedId) {
        String followId = followerId + "_" + followedId;
        return followsCollection.document(followId).get();
    }

    // Increment user's recipe count
    public Task<Void> incrementRecipeCount(String userId) {
        return usersCollection.document(userId)
                .update("recipeCount", FieldValue.increment(1));
    }

    // Update user profile
    public Task<Void> updateProfile(String userId, String username, String bio) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("username", username);
        updates.put("bio", bio);
        updates.put("updatedAt", FieldValue.serverTimestamp());

        return usersCollection.document(userId).update(updates);
    }
}