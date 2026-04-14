package com.group4.cookbook.data.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;

@IgnoreExtraProperties
public class User {
    private String uid;
    private String username;
    private String email;
    private String bio;
    private String profileImageUrl;
    private int followers;
    private int following;
    private int recipeCount;

    @PropertyName("createdAt")
    private Timestamp createdAt;

    @PropertyName("updatedAt")
    private Timestamp updatedAt;

    // Firebase requires empty constructor
    public User() {}

    // Constructor for new users
    public User(String uid, String username, String email) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.bio = "Home cook enthusiast";
        this.profileImageUrl = "";
        this.followers = 0;
        this.following = 0;
        this.recipeCount = 0;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    // Getters and setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public int getFollowers() { return followers; }
    public void setFollowers(int followers) { this.followers = followers; }

    public int getFollowing() { return following; }
    public void setFollowing(int following) { this.following = following; }

    public int getRecipeCount() { return recipeCount; }
    public void setRecipeCount(int recipeCount) { this.recipeCount = recipeCount; }

    @PropertyName("createdAt")
    public Timestamp getCreatedAt() { return createdAt; }

    @PropertyName("createdAt")
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @PropertyName("updatedAt")
    public Timestamp getUpdatedAt() { return updatedAt; }

    @PropertyName("updatedAt")
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}