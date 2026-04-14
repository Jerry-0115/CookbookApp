package com.group4.cookbook.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.FirebaseNetworkException;
import com.group4.cookbook.data.Result;
import com.group4.cookbook.data.repository.AuthRepository;
import com.group4.cookbook.data.repository.UserRepository;

public class AuthViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private final UserRepository userRepository;

    // LiveData for observing authentication state
    private final MutableLiveData<Result<FirebaseUser>> authResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public AuthViewModel() {
        this.authRepository = new AuthRepository();
        this.userRepository = new UserRepository();
    }

    // Getters for LiveData
    public LiveData<Result<FirebaseUser>> getAuthResult() { return authResult; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    // Login method
    public void login(String email, String password) {
        if (!validateLoginInput(email, password)) {
            return;
        }

        isLoading.setValue(true);
        authResult.setValue(Result.loading());

        authRepository.loginWithEmail(email, password)
                .addOnCompleteListener(task -> {
                    isLoading.setValue(false);

                    if (task.isSuccessful()) {
                        FirebaseUser user = authRepository.getCurrentUser();
                        authResult.setValue(Result.success(user));
                    } else {
                        String error = getAuthErrorMessage(task.getException());
                        errorMessage.setValue(error);
                        authResult.setValue(Result.error(task.getException()));
                    }
                });
    }

    // Sign up method
    public void signUp(String email, String password, String username) {
        if (!validateSignUpInput(email, password, username)) {
            return;
        }

        // Check username availability first
        isLoading.setValue(true);
        authResult.setValue(Result.loading());

        userRepository.isUsernameAvailable(username)
                .addOnCompleteListener(availabilityTask -> {
                    if (availabilityTask.isSuccessful() && availabilityTask.getResult()) {
                        // Username available, proceed with signup
                        authRepository.signUpWithEmail(email, password, username)
                                .addOnCompleteListener(signupTask -> {
                                    isLoading.setValue(false);

                                    if (signupTask.isSuccessful()) {
                                        FirebaseUser user = authRepository.getCurrentUser();
                                        authResult.setValue(Result.success(user));
                                    } else {
                                        String error = getAuthErrorMessage(signupTask.getException());
                                        errorMessage.setValue(error);
                                        authResult.setValue(Result.error(signupTask.getException()));
                                    }
                                });
                    } else {
                        isLoading.setValue(false);
                        if (availabilityTask.isSuccessful()) {
                            errorMessage.setValue("Username is already taken");
                        } else {
                            errorMessage.setValue("Failed to check username availability");
                        }
                        authResult.setValue(Result.error(new Exception("Username check failed")));
                    }
                });
    }

    // Logout method
    public void logout() {
        authRepository.logout();
        authResult.setValue(null);
    }

    // Reset password
    public void resetPassword(String email) {
        if (email.isEmpty()) {
            errorMessage.setValue("Please enter your email");
            return;
        }

        isLoading.setValue(true);
        authRepository.resetPassword(email)
                .addOnCompleteListener(task -> {
                    isLoading.setValue(false);
                    if (task.isSuccessful()) {
                        errorMessage.setValue("Password reset email sent");
                    } else {
                        errorMessage.setValue("Failed to send reset email: " +
                                getAuthErrorMessage(task.getException()));
                    }
                });
    }

    public void clearAuthResult() {
        authResult.setValue(null);
    }

    // Input validation
    private boolean validateLoginInput(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            errorMessage.setValue("Please fill in all fields");
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage.setValue("Please enter a valid email address");
            return false;
        }

        if (password.length() < 6) {
            errorMessage.setValue("Password must be at least 6 characters");
            return false;
        }

        return true;
    }

    private boolean validateSignUpInput(String email, String password, String username) {
        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            errorMessage.setValue("Please fill in all fields");
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage.setValue("Please enter a valid email address");
            return false;
        }

        if (password.length() < 6) {
            errorMessage.setValue("Password must be at least 6 characters");
            return false;
        }

        if (username.length() < 3) {
            errorMessage.setValue("Username must be at least 3 characters");
            return false;
        }

        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            errorMessage.setValue("Username can only contain letters, numbers, and underscores");
            return false;
        }

        return true;
    }

    // Helper method to get user-friendly error messages
    private String getAuthErrorMessage(Exception exception) {
        if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            return "Invalid email or password";
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            return "Email is already registered";
        } else if (exception instanceof FirebaseAuthWeakPasswordException) {
            return "Password is too weak";
        } else if (exception instanceof FirebaseAuthInvalidUserException) {
            return "User not found";
        } else if (exception instanceof FirebaseNetworkException) {
            return "Network error. Please check your connection";
        } else {
            return "Authentication failed: " + (exception != null ? exception.getMessage() : "Unknown error");
        }
    }
}