package com.group4.cookbook.data.repository;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import com.group4.cookbook.data.models.ShoppingList;
import com.group4.cookbook.data.models.ShoppingListItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingListRepository {
    private static final String TAG = "ShoppingListRepo";
    private FirebaseFirestore firestore;
    private CollectionReference shoppingListsCollection;
    private CollectionReference shoppingListItemsCollection;

    public ShoppingListRepository() {
        this.firestore = FirebaseFirestore.getInstance();
        this.shoppingListsCollection = firestore.collection("shoppingLists");
        this.shoppingListItemsCollection = firestore.collection("shoppingListItems");
    }

    // Get shopping lists for a user
    public Task<QuerySnapshot> getShoppingListsByUser(String userId) {
        return shoppingListsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("isActive", true)
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .get();
    }

    // Create new shopping list
    public Task<Void> createShoppingList(ShoppingList shoppingList) {
        String listId = shoppingListsCollection.document().getId();
        shoppingList.setId(listId);

        Map<String, Object> listData = new HashMap<>();
        listData.put("id", listId);
        listData.put("userId", shoppingList.getUserId());
        listData.put("name", shoppingList.getName());
        listData.put("items", shoppingList.getItems());
        listData.put("recipeIds", shoppingList.getRecipeIds());
        listData.put("isActive", shoppingList.isActive());
        listData.put("createdAt", shoppingList.getCreatedAt());
        listData.put("updatedAt", shoppingList.getUpdatedAt());

        return shoppingListsCollection.document(listId).set(listData);
    }

    // Add recipe ingredients to shopping list
    public Task<Void> addRecipeToShoppingList(String listId, String recipeId, List<ShoppingListItem> items) {
        // 1. Update shopping list items
        DocumentReference listRef = shoppingListsCollection.document(listId);

        // 2. Add recipeId to recipeIds array
        Map<String, Object> updates = new HashMap<>();
        updates.put("recipeIds", FieldValue.arrayUnion(recipeId));
        updates.put("updatedAt", FieldValue.serverTimestamp());

        // 3. Merge items (would need more complex logic to combine quantities)
        return listRef.update(updates);
    }

    // Update shopping list item (mark as purchased)
    public Task<Void> updateShoppingListItem(String listId, String itemName, boolean purchased) {
        // This is simplified - in reality you'd have item IDs
        Map<String, Object> updates = new HashMap<>();
        updates.put("updatedAt", FieldValue.serverTimestamp());
        // You would need to update the specific item in the items array

        return shoppingListsCollection.document(listId).update(updates);
    }

    // Delete shopping list (soft delete)
    public Task<Void> deleteShoppingList(String listId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("isActive", false);
        updates.put("updatedAt", FieldValue.serverTimestamp());

        return shoppingListsCollection.document(listId).update(updates);
    }

    // Get shopping list by ID
    public Task<com.google.firebase.firestore.DocumentSnapshot> getShoppingList(String listId) {
        return shoppingListsCollection.document(listId).get();
    }
}