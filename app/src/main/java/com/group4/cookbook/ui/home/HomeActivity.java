package com.group4.cookbook.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.group4.cookbook.R;
import com.group4.cookbook.auth.AuthViewModel;
import com.group4.cookbook.auth.LoginActivity;
import com.group4.cookbook.data.models.Recipe;
import com.group4.cookbook.data.repository.RecipeRepository;
import com.group4.cookbook.ui.recipe.CreateRecipeActivity;
import com.group4.cookbook.ui.recipe.RecipeDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {
    private RecyclerView recyclerViewRecipes;
    private RecipeAdapter recipeAdapter;
    private RecipeRepository recipeRepository;
    private AuthViewModel authViewModel;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize ViewModel for authentication
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Check if user is authenticated
        checkAuthentication();

        // Initialize other components
        recipeRepository = new RecipeRepository();

        recyclerViewRecipes = findViewById(R.id.recyclerViewRecipes);
        recipeAdapter = new RecipeAdapter(new ArrayList<>(), this);

        recyclerViewRecipes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRecipes.setAdapter(recipeAdapter);

        loadRecipes();
    }

    private void checkAuthentication() {
        authViewModel.getAuthResult().observe(this, result -> {
            if (result == null || !result.isSuccess()) {
                // User not authenticated, redirect to login
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Trigger a check
        if (authViewModel.getAuthResult().getValue() == null) {
            // Force check
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            authViewModel.logout();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_create_recipe) {
            // Start CreateRecipeActivity
            Intent intent = new Intent(this, CreateRecipeActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadRecipes() {
        recipeRepository.getPublicRecipes(20)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipe> recipes = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Recipe recipe = document.toObject(Recipe.class);
                        if (recipe != null) {
                            recipes.add(recipe);
                        }
                    }
                    recipeAdapter.setRecipeList(recipes);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load recipes: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        // Open recipe detail
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("recipeId", recipe.getId());
        startActivity(intent);

        // Increment view count
        recipeRepository.incrementViews(recipe.getId());
    }

    @Override
    public void onLikeClick(Recipe recipe) {
        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login to like recipes", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = firebaseAuth.getCurrentUser().getUid();

        // Check if already liked
        recipeRepository.checkIfLiked(recipe.getId(), userId)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Already liked, unlike it
                        recipeRepository.unlikeRecipe(recipe.getId(), userId)
                                .addOnSuccessListener(aVoid -> {
                                    recipe.setLikes(recipe.getLikes() - 1);
                                    recipeAdapter.notifyDataSetChanged();
                                    Toast.makeText(this, "Recipe unliked", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to unlike recipe", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // Not liked, like it
                        recipeRepository.likeRecipe(recipe.getId(), userId)
                                .addOnSuccessListener(aVoid -> {
                                    recipe.setLikes(recipe.getLikes() + 1);
                                    recipeAdapter.notifyDataSetChanged();
                                    Toast.makeText(this, "Recipe liked!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to like recipe", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to check like status", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onSaveClick(Recipe recipe) {
        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login to save recipes", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save to collections
        recipeRepository.saveRecipe(recipe.getId())
                .addOnSuccessListener(aVoid -> {
                    recipe.setSaves(recipe.getSaves() + 1);
                    recipeAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "Recipe saved to collections", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save recipe", Toast.LENGTH_SHORT).show();
                });
    }
}