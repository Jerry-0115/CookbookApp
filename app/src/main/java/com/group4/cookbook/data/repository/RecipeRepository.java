package com.group4.cookbook.data.repository;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import com.group4.cookbook.data.models.Recipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeRepository {
    private FirebaseFirestore firestore;
    private CollectionReference recipesCollection;
    private AuthRepository authRepository;

    public RecipeRepository() {
        this.firestore = FirebaseFirestore.getInstance();
        this.recipesCollection = firestore.collection("recipes");
        this.authRepository = new AuthRepository();
    }

    private String getCurrentUserId() {
        if (authRepository.getCurrentUser() != null) {
            return authRepository.getCurrentUser().getUid();
        }
        return null;
    }

    // Get all public recipes for home feed
    public Task<QuerySnapshot> getPublicRecipes(int limit) {
        return recipesCollection
                .whereEqualTo("isPublic", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit)
                .get();
    }

    // Get trending recipes (most liked)
    public Task<QuerySnapshot> getTrendingRecipes(int limit) {
        return recipesCollection
                .whereEqualTo("isPublic", true)
                .orderBy("likes", Query.Direction.DESCENDING)
                .limit(limit)
                .get();
    }

    // Get recipes by specific user
    public Task<QuerySnapshot> getRecipesByUser(String userId, int limit) {
        return recipesCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("isPublic", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit)
                .get();
    }

    // Get single recipe by ID
    public Task<DocumentSnapshot> getRecipe(String recipeId) {
        return recipesCollection.document(recipeId).get();
    }

    // Create new recipe
    public Task<Void> createRecipe(Recipe recipe) {
        String recipeId = recipesCollection.document().getId();
        recipe.setId(recipeId);

        Map<String, Object> recipeData = new HashMap<>();
        recipeData.put("id", recipeId);
        recipeData.put("title", recipe.getTitle());
        recipeData.put("description", recipe.getDescription());
        recipeData.put("userId", recipe.getUserId());
        recipeData.put("userName", recipe.getUserName());
        recipeData.put("userImageUrl", recipe.getUserImageUrl());
        recipeData.put("imageUrl", recipe.getImageUrl());
        recipeData.put("videoUrl", recipe.getVideoUrl());
        recipeData.put("isPublic", recipe.isPublic());
        recipeData.put("contributors", recipe.getContributors());
        recipeData.put("ingredients", recipe.getIngredients());
        recipeData.put("steps", recipe.getSteps());
        recipeData.put("prepTime", recipe.getPrepTime());
        recipeData.put("cookTime", recipe.getCookTime());
        recipeData.put("totalTime", recipe.getTotalTime());
        recipeData.put("servings", recipe.getServings());
        recipeData.put("difficulty", recipe.getDifficulty());
        recipeData.put("cuisine", recipe.getCuisine());
        recipeData.put("dietaryTags", recipe.getDietaryTags());
        recipeData.put("likes", recipe.getLikes());
        recipeData.put("saves", recipe.getSaves());
        recipeData.put("views", recipe.getViews());
        recipeData.put("comments", recipe.getComments());
        recipeData.put("createdAt", recipe.getCreatedAt());
        recipeData.put("updatedAt", recipe.getUpdatedAt());
        recipeData.put("lastViewed", recipe.getLastViewed());

        return recipesCollection.document(recipeId).set(recipeData);
    }

    // Like a recipe
    public Task<Void> likeRecipe(String recipeId, String userId) {
        // 1. Add to likes subcollection
        DocumentReference likeRef = recipesCollection
                .document(recipeId)
                .collection("likes")
                .document(userId);

        Map<String, Object> likeData = new HashMap<>();
        likeData.put("userId", userId);
        likeData.put("likedAt", FieldValue.serverTimestamp());

        // 2. Increment likes count in recipe document
        return likeRef.set(likeData)
                .continueWithTask(task ->
                        recipesCollection.document(recipeId)
                                .update("likes", FieldValue.increment(1))
                );
    }

    // Unlike a recipe
    public Task<Void> unlikeRecipe(String recipeId, String userId) {
        // 1. Remove from likes subcollection
        DocumentReference likeRef = recipesCollection
                .document(recipeId)
                .collection("likes")
                .document(userId);

        // 2. Decrement likes count in recipe document
        return likeRef.delete()
                .continueWithTask(task ->
                        recipesCollection.document(recipeId)
                                .update("likes", FieldValue.increment(-1))
                );
    }

    // Check if user already liked a recipe
    public Task<DocumentSnapshot> checkIfLiked(String recipeId, String userId) {
        return recipesCollection
                .document(recipeId)
                .collection("likes")
                .document(userId)
                .get();
    }

    // Save recipe to collections (increment saves count)
    public Task<Void> saveRecipe(String recipeId) {
        return recipesCollection.document(recipeId)
                .update("saves", FieldValue.increment(1));
    }

    // Increment view count
    public Task<Void> incrementViews(String recipeId) {
        return recipesCollection.document(recipeId)
                .update(
                        "views", FieldValue.increment(1),
                        "lastViewed", FieldValue.serverTimestamp()
                );
    }

    // Search recipes by title or ingredients
    public Task<QuerySnapshot> searchRecipes(String query, int limit) {
        // Note: Firestore doesn't support full-text search natively
        // For MVP, we'll search by title (case-insensitive would need workaround)
        return recipesCollection
                .whereEqualTo("isPublic", true)
                .whereGreaterThanOrEqualTo("title", query)
                .whereLessThanOrEqualTo("title", query + "\uf8ff")
                .limit(limit)
                .get();
    }

    // Filter recipes by criteria
    public Task<QuerySnapshot> filterRecipes(String cuisine, String difficulty,
                                             List<String> dietaryTags, int maxTime, int limit) {
        Query query = recipesCollection
                .whereEqualTo("isPublic", true);

        if (cuisine != null && !cuisine.isEmpty()) {
            query = query.whereEqualTo("cuisine", cuisine);
        }

        if (difficulty != null && !difficulty.isEmpty()) {
            query = query.whereEqualTo("difficulty", difficulty);
        }

        if (maxTime > 0) {
            query = query.whereLessThanOrEqualTo("totalTime", maxTime);
        }

        // Note: Firestore doesn't support array-contains-any with other equality filters easily
        // For MVP, we'll handle dietary tags in app logic

        return query.orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit)
                .get();
    }
}