package com.group4.cookbook.data.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;

import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Recipe {
    private String id;
    private String title;
    private String description;
    private String userId;
    private String userName;
    private String userImageUrl;
    private String imageUrl;
    private String videoUrl;
    private boolean isPublic;
    private List<String> contributors;
    private List<Map<String, Object>> ingredients; // has category, name, quantity, unit
    private List<Map<String, Object>> steps; // has description, imageUrl, stepNumber, timer
    private int prepTime; // in minutes
    private int cookTime;
    private int totalTime;
    private int servings;
    private String difficulty; // Easy, Medium, Hard
    private String cuisine;
    private List<String> dietaryTags;
    private int likes;
    private int saves;
    private int views;
    private int comments;

    @PropertyName("createdAt")
    private Timestamp createdAt;

    @PropertyName("updatedAt")
    private Timestamp updatedAt;

    @PropertyName("lastViewed")
    private Timestamp lastViewed;

    // Firebase requires empty constructor
    public Recipe() {}

    // Constructor for creating new recipes
    public Recipe(String title, String description, String userId, String userName) {
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.userName = userName;
        this.isPublic = true;
        this.likes = 0;
        this.saves = 0;
        this.views = 0;
        this.comments = 0;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
        this.lastViewed = Timestamp.now();
    }

    // Getters and setters for all fields
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserImageUrl() { return userImageUrl; }
    public void setUserImageUrl(String userImageUrl) { this.userImageUrl = userImageUrl; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }

    public List<String> getContributors() { return contributors; }
    public void setContributors(List<String> contributors) { this.contributors = contributors; }

    public List<Map<String, Object>> getIngredients() { return ingredients; }
    public void setIngredients(List<Map<String, Object>> ingredients) { this.ingredients = ingredients; }

    public List<Map<String, Object>> getSteps() { return steps; }
    public void setSteps(List<Map<String, Object>> steps) { this.steps = steps; }

    public int getPrepTime() { return prepTime; }
    public void setPrepTime(int prepTime) { this.prepTime = prepTime; }

    public int getCookTime() { return cookTime; }
    public void setCookTime(int cookTime) { this.cookTime = cookTime; }

    public int getTotalTime() { return totalTime; }
    public void setTotalTime(int totalTime) { this.totalTime = totalTime; }

    public int getServings() { return servings; }
    public void setServings(int servings) { this.servings = servings; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }

    public List<String> getDietaryTags() { return dietaryTags; }
    public void setDietaryTags(List<String> dietaryTags) { this.dietaryTags = dietaryTags; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public int getSaves() { return saves; }
    public void setSaves(int saves) { this.saves = saves; }

    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }

    public int getComments() { return comments; }
    public void setComments(int comments) { this.comments = comments; }

    @PropertyName("createdAt")
    public Timestamp getCreatedAt() { return createdAt; }

    @PropertyName("createdAt")
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @PropertyName("updatedAt")
    public Timestamp getUpdatedAt() { return updatedAt; }

    @PropertyName("updatedAt")
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @PropertyName("lastViewed")
    public Timestamp getLastViewed() { return lastViewed; }

    @PropertyName("lastViewed")
    public void setLastViewed(Timestamp lastViewed) { this.lastViewed = lastViewed; }
}