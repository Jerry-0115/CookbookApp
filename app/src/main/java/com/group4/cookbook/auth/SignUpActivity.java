package com.group4.cookbook.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.group4.cookbook.R;
import com.group4.cookbook.data.Result;
import com.group4.cookbook.ui.home.HomeActivity;

public class SignUpActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword, editTextUsername, editTextConfirmPassword;
    private Button buttonSignUp;
    private TextView textViewLogin, textViewError;
    private ProgressBar progressBar;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Initialize views
        initViews();

        // Set up observers
        setupObservers();
    }

    private void initViews() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextUsername = findViewById(R.id.editTextUsername);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        textViewLogin = findViewById(R.id.textViewLogin);
        textViewError = findViewById(R.id.textViewError);
        progressBar = findViewById(R.id.progressBar);

        // Sign up button click
        buttonSignUp.setOnClickListener(v -> attemptSignUp());

        // Login text click
        textViewLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupObservers() {
        // Observe authentication result
        authViewModel.getAuthResult().observe(this, result -> {
            if (result != null && result.isSuccess()) {
                // Sign up successful, navigate to Home
                Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Observe loading state
        authViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                buttonSignUp.setEnabled(!isLoading);
                textViewLogin.setEnabled(!isLoading);
            }
        });

        // Observe error messages
        authViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                textViewError.setText(errorMessage);
                textViewError.setVisibility(View.VISIBLE);
            } else {
                textViewError.setVisibility(View.GONE);
            }
        });
    }

    private void attemptSignUp() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();

        // Clear previous errors
        textViewError.setVisibility(View.GONE);

        // Additional validation for confirm password
        if (!password.equals(confirmPassword)) {
            textViewError.setText("Passwords do not match");
            textViewError.setVisibility(View.VISIBLE);
            return;
        }

        authViewModel.signUp(email, password, username);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}