package com.group4.cookbook.data.models;

import com.google.firebase.Timestamp;
import java.util.List;

public class ShoppingList {
    private String id;
    private String userId;
    private String name;
    private List<ShoppingListItem> items;
    private List<String> recipeIds;
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public ShoppingList() {}

    public ShoppingList(String userId, String name) {
        this.userId = userId;
        this.name = name;
        this.isActive = true;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<ShoppingListItem> getItems() { return items; }
    public void setItems(List<ShoppingListItem> items) { this.items = items; }

    public List<String> getRecipeIds() { return recipeIds; }
    public void setRecipeIds(List<String> recipeIds) { this.recipeIds = recipeIds; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
