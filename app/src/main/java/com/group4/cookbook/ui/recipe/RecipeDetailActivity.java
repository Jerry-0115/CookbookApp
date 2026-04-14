package com.group4.cookbook.ui.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.group4.cookbook.R;
import com.group4.cookbook.data.models.Recipe;
import com.group4.cookbook.data.repository.RecipeRepository;

public class RecipeDetailActivity extends AppCompatActivity {
    private ImageView imageRecipe;
    private TextView textTitle, textUserName, textTime, textServings, textDifficulty, textCuisine;
    private TextView textLikes, textSaves, textViews, textComments;
    private Button buttonLike, buttonSave, buttonShare;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private RecipeRepository recipeRepository;
    private FirebaseAuth auth;
    private String recipeId;
    private Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Get recipe ID from intent
        recipeId = getIntent().getStringExtra("recipeId");
        if (recipeId == null) {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize
        recipeRepository = new RecipeRepository();
        auth = FirebaseAuth.getInstance();

        // Initialize views
        initViews();

        // Load recipe data
        loadRecipe();

        // Setup like button
        setupLikeButton();

        // Setup save button
        if (buttonSave != null) {
            buttonSave.setOnClickListener(v -> saveRecipe());
        }

        // Setup share button
        if (buttonShare != null) {
            buttonShare.setOnClickListener(v -> shareRecipe());
        }
    }

    private void initViews() {
        imageRecipe = findViewById(R.id.imageRecipe);
        textTitle = findViewById(R.id.textTitle);
        textUserName = findViewById(R.id.textUserName);
        textTime = findViewById(R.id.textTime);
        textServings = findViewById(R.id.textServings);
        textDifficulty = findViewById(R.id.textDifficulty);
        textCuisine = findViewById(R.id.textCuisine);
        textLikes = findViewById(R.id.textLikes);
        textSaves = findViewById(R.id.textSaves);
        textViews = findViewById(R.id.textViews);
        textComments = findViewById(R.id.textComments);
        buttonLike = findViewById(R.id.buttonLike);
        buttonSave = findViewById(R.id.buttonSave);
        buttonShare = findViewById(R.id.buttonShare);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Setup ViewPager with tabs
        setupViewPager();
    }

    private void setupViewPager() {
        RecipeDetailPagerAdapter pagerAdapter = new RecipeDetailPagerAdapter(this, recipeId);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Ingredients");
                    break;
                case 1:
                    tab.setText("Steps");
                    break;
                case 2:
                    tab.setText("Comments");
                    break;
            }
        }).attach();
    }

    private void loadRecipe() {
        recipeRepository.getRecipe(recipeId)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        recipe = documentSnapshot.toObject(Recipe.class);
                        if (recipe != null) {
                            updateUI();

                            // Check if current user liked this recipe
                            checkIfLiked();
                        } else {
                            Toast.makeText(this, "Failed to parse recipe data", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load recipe: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void updateUI() {
        if (recipe == null) return;

        textTitle.setText(recipe.getTitle());
        textUserName.setText("By " + recipe.getUserName());
        textTime.setText(recipe.getTotalTime() + " min");
        textServings.setText(recipe.getServings() + " servings");
        textDifficulty.setText(recipe.getDifficulty());
        textCuisine.setText(recipe.getCuisine());
        textLikes.setText(String.valueOf(recipe.getLikes()));
        textSaves.setText(String.valueOf(recipe.getSaves()));
        textViews.setText(String.valueOf(recipe.getViews()));
        textComments.setText(String.valueOf(recipe.getComments()));

        // Load image if available - using placeholder for now
        // In production, you would use Glide or Picasso
        imageRecipe.setImageResource(R.drawable.placeholder_recipe);
    }

    private void setupLikeButton() {
        if (buttonLike == null) return;

        buttonLike.setOnClickListener(v -> {
            if (auth.getCurrentUser() == null) {
                Toast.makeText(this, "Please login to like recipes", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = auth.getCurrentUser().getUid();

            recipeRepository.checkIfLiked(recipeId, userId)
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Unlike
                            recipeRepository.unlikeRecipe(recipeId, userId)
                                    .addOnSuccessListener(aVoid -> {
                                        if (recipe != null) {
                                            recipe.setLikes(recipe.getLikes() - 1);
                                            updateUI();
                                            buttonLike.setText("Like");
                                            Toast.makeText(this, "Recipe unliked", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to unlike recipe", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Like
                            recipeRepository.likeRecipe(recipeId, userId)
                                    .addOnSuccessListener(aVoid -> {
                                        if (recipe != null) {
                                            recipe.setLikes(recipe.getLikes() + 1);
                                            updateUI();
                                            buttonLike.setText("Liked");
                                            Toast.makeText(this, "Recipe liked!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to like recipe", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to check like status", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void checkIfLiked() {
        if (auth.getCurrentUser() == null || buttonLike == null) return;

        String userId = auth.getCurrentUser().getUid();
        recipeRepository.checkIfLiked(recipeId, userId)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        buttonLike.setText("Liked");
                    } else {
                        buttonLike.setText("Like");
                    }
                })
                .addOnFailureListener(e -> {
                    // Ignore error, keep default "Like" text
                });
    }

    private void saveRecipe() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login to save recipes", Toast.LENGTH_SHORT).show();
            return;
        }

        recipeRepository.saveRecipe(recipeId)
                .addOnSuccessListener(aVoid -> {
                    if (recipe != null) {
                        recipe.setSaves(recipe.getSaves() + 1);
                        updateUI();
                        Toast.makeText(this, "Recipe saved to collections", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save recipe", Toast.LENGTH_SHORT).show();
                });
    }

    private void shareRecipe() {
        if (recipe == null) return;

        String shareText = "Check out this recipe: " + recipe.getTitle() +
                "\nTotal time: " + recipe.getTotalTime() + " minutes";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share Recipe"));
    }
}