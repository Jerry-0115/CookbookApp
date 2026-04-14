package com.group4.cookbook.data.repository;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.firestore.FirebaseFirestore;

import com.group4.cookbook.data.models.User;

import java.util.Map;

public class AuthRepository {
    private static final String TAG = "AuthRepository";
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private final UserRepository userRepository;

    public AuthRepository() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
        this.userRepository = new UserRepository();
    }

    // Get current user
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    // Check if user is logged in
    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    // Login with email and password
    public Task<AuthResult> loginWithEmail(String email, String password) {
        return firebaseAuth.signInWithEmailAndPassword(email, password);
    }

    // Sign up with email and password
    public Task<AuthResult> signUpWithEmail(String email, String password, String username) {
        Log.d(TAG, "Starting sign up for: " + email);

        return firebaseAuth.createUserWithEmailAndPassword(email, password)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        Log.d(TAG, "Firebase Auth user created: " + firebaseUser.getUid());

                        if (firebaseUser != null) {
                            // Create user profile in Firestore
                            return userRepository.createUserProfile(firebaseUser.getUid(), email, username)
                                    .continueWith(profileTask -> {
                                        if (profileTask.isSuccessful()) {
                                            Log.d(TAG, "User profile created in Firestore");
                                            return task.getResult();
                                        } else {
                                            Log.e(TAG, "Failed to create user profile: " +
                                                    profileTask.getException().getMessage());
                                            // If profile creation fails, delete the auth user
                                            firebaseUser.delete();
                                            throw profileTask.getException();
                                        }
                                    });
                        }
                    } else {
                        Log.e(TAG, "Firebase Auth failed: " + task.getException().getMessage());
                    }
                    return task;
                });
    }

    // Logout
    public void logout() {
        firebaseAuth.signOut();
    }

    // Reset password
    public Task<Void> resetPassword(String email) {
        return firebaseAuth.sendPasswordResetEmail(email);
    }

    // Update user profile
    public Task<Void> updateUserProfile(String userId, Map<String, Object> updates) {
        return firestore.collection("users").document(userId).update(updates);
    }

    // Get user profile
    public Task<User> getUserProfile(String userId) {
        return userRepository.getUserById(userId);
    }
}