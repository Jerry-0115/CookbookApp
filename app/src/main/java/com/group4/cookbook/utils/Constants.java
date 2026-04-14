package com.group4.cookbook.utils;

public class Constants {
    // Firestore Collections
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_RECIPES = "recipes";
    public static final String COLLECTION_COLLECTIONS = "collections";
    public static final String COLLECTION_COMMENTS = "comments";
    public static final String COLLECTION_NOTIFICATIONS = "notifications";
    public static final String COLLECTION_SHOPPINGLIST = "shoppinglist";
    public static final String COLLECTION_FOLLOWS = "follows";

    // Recipe Subcollections
    public static final String SUBCOLLECTION_LIKES = "likes";

    // Shared Preferences
    public static final String PREF_NAME = "CookbookPrefs";
    public static final String PREF_USER_ID = "user_id";
    public static final String PREF_USER_NAME = "user_name";

    // Request Codes
    public static final int RC_SIGN_IN = 1001;
    public static final int RC_IMAGE_PICK = 1002;

    // Intent Extras
    public static final String EXTRA_RECIPE_ID = "recipe_id";
    public static final String EXTRA_USER_ID = "user_id";
}