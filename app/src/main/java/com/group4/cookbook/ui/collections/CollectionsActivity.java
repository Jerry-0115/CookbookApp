package com.group4.cookbook.ui.collections;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.group4.cookbook.R;
import com.group4.cookbook.auth.AuthViewModel;
import com.group4.cookbook.auth.LoginActivity;
import com.group4.cookbook.data.models.Recipe;
import com.group4.cookbook.data.models.ShoppingList;
import com.group4.cookbook.data.repository.RecipeRepository;
import com.group4.cookbook.data.repository.ShoppingListRepository;
import com.group4.cookbook.ui.home.HomeActivity;
import com.group4.cookbook.ui.home.RecipeAdapter;
import com.group4.cookbook.ui.recipe.RecipeDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class CollectionsActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {
    private TabLayout tabLayout;
    private RecyclerView recyclerViewCollections;
    private ProgressBar progressBar;
    private TextView textViewEmpty;

    private RecipeRepository recipeRepository;
    private ShoppingListRepository shoppingListRepository;
    private FirebaseAuth auth;
    private AuthViewModel authViewModel;

    private RecipeAdapter recipeAdapter;
    private ShoppingListAdapter shoppingListAdapter;

    private List<Recipe> savedRecipes = new ArrayList<>();
    private List<ShoppingList> shoppingLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Check authentication
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login to view collections", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize repositories
        recipeRepository = new RecipeRepository();
        shoppingListRepository = new ShoppingListRepository();

        // Initialize views
        initViews();

        // Setup tab layout
        setupTabLayout();

        // Load saved recipes by default
        loadSavedRecipes();
    }

    private void initViews() {
        tabLayout = findViewById(R.id.tabLayout);
        recyclerViewCollections = findViewById(R.id.recyclerViewCollections);
        progressBar = findViewById(R.id.progressBar);
        textViewEmpty = findViewById(R.id.textViewEmpty);

        // Setup recipe adapter
        recipeAdapter = new RecipeAdapter(savedRecipes, this);

        // Setup shopping list adapter
        shoppingListAdapter = new ShoppingListAdapter(shoppingLists, this::onShoppingListClick);

        recyclerViewCollections.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCollections.setAdapter(recipeAdapter);
    }

    private void setupTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText("Saved Recipes"));
        tabLayout.addTab(tabLayout.newTab().setText("Shopping Lists"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        recyclerViewCollections.setAdapter(recipeAdapter);
                        loadSavedRecipes();
                        break;
                    case 1:
                        recyclerViewCollections.setAdapter(shoppingListAdapter);
                        loadShoppingLists();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadSavedRecipes() {
        progressBar.setVisibility(View.VISIBLE);
        textViewEmpty.setVisibility(View.GONE);

        // For now, we'll load public recipes as saved recipes
        // In a real app, you would have a "saved" field or separate collection
        recipeRepository.getPublicRecipes(20)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);

                    savedRecipes.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Recipe recipe = document.toObject(Recipe.class);
                        if (recipe != null) {
                            // Filter recipes with saves > 0 (simulating saved recipes)
                            if (recipe.getSaves() > 0) {
                                savedRecipes.add(recipe);
                            }
                        }
                    }

                    recipeAdapter.setRecipeList(savedRecipes);

                    if (savedRecipes.isEmpty()) {
                        textViewEmpty.setText("No saved recipes yet");
                        textViewEmpty.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    textViewEmpty.setText("Failed to load saved recipes");
                    textViewEmpty.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadShoppingLists() {
        progressBar.setVisibility(View.VISIBLE);
        textViewEmpty.setVisibility(View.GONE);

        String userId = auth.getCurrentUser().getUid();

        shoppingListRepository.getShoppingListsByUser(userId)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);

                    shoppingLists.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        ShoppingList shoppingList = document.toObject(ShoppingList.class);
                        if (shoppingList != null && shoppingList.isActive()) {
                            shoppingLists.add(shoppingList);
                        }
                    }

                    shoppingListAdapter.setShoppingLists(shoppingLists);

                    if (shoppingLists.isEmpty()) {
                        textViewEmpty.setText("No shopping lists yet");
                        textViewEmpty.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    textViewEmpty.setText("Failed to load shopping lists");
                    textViewEmpty.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void onShoppingListClick(ShoppingList shoppingList) {
        // Open shopping list detail
        Toast.makeText(this, "Shopping List: " + shoppingList.getName(), Toast.LENGTH_SHORT).show();
        // You can implement shopping list detail activity later
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_collections, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_home) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_create_shopping_list) {
            createNewShoppingList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createNewShoppingList() {
        Toast.makeText(this, "Create shopping list feature coming soon", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("recipeId", recipe.getId());
        startActivity(intent);
    }

    @Override
    public void onLikeClick(Recipe recipe) {
        // Handle like in collections context
        Toast.makeText(this, "Like functionality in collections", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveClick(Recipe recipe) {
        // Remove from saved collections
        Toast.makeText(this, "Remove from saved", Toast.LENGTH_SHORT).show();
    }
}