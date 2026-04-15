package com.group4.cookbook.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.group4.cookbook.R;
import com.group4.cookbook.data.Result;
import com.group4.cookbook.ui.home.HomeActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewSignUp, textViewError, textViewResetPassword;
    private ProgressBar progressBar;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Initialize views
        initViews();

        // ====================== 临时隐藏 recipes 加载错误 ======================
        if (textViewError != null) {
            textViewError.setVisibility(View.GONE);
        }
        // =====================================================================

        // Set up observers
        setupObservers();

        // Check if user is already logged in
        checkExistingSession();
    }

    private void initViews() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewSignUp = findViewById(R.id.textViewSignUp);
        textViewError = findViewById(R.id.textViewError);
        textViewResetPassword = findViewById(R.id.textViewResetPassword);
        progressBar = findViewById(R.id.progressBar);

        // Login button click
        buttonLogin.setOnClickListener(v -> attemptLogin());

        // Sign up text click
        textViewSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        // Reset password click
        textViewResetPassword.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                textViewError.setText("Please enter your email first");
                textViewError.setVisibility(View.VISIBLE);
                return;
            }

            authViewModel.resetPassword(email);
            Toast.makeText(this, "Password reset email sent if account exists",
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void setupObservers() {
        // Observe authentication result
        authViewModel.getAuthResult().observe(this, result -> {
            if (result != null && result.isSuccess()) {
                // Login successful, navigate to Home
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Observe loading state
        authViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                buttonLogin.setEnabled(!isLoading);
                textViewSignUp.setEnabled(!isLoading);
                textViewResetPassword.setEnabled(!isLoading);
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

    private void attemptLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Clear previous errors
        textViewError.setVisibility(View.GONE);

        authViewModel.login(email, password);
    }

    private void checkExistingSession() {
        if (authViewModel.getAuthResult().getValue() != null &&
                authViewModel.getAuthResult().getValue().isSuccess()) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authViewModel.getAuthResult().getValue() != null &&
                authViewModel.getAuthResult().getValue().isSuccess()) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}