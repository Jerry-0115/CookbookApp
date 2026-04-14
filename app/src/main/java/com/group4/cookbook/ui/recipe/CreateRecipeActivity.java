package com.group4.cookbook.ui.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.group4.cookbook.R;
import com.group4.cookbook.data.models.Recipe;
import com.group4.cookbook.data.repository.RecipeRepository;
import com.group4.cookbook.data.repository.UserRepository;
import com.group4.cookbook.ui.home.HomeActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateRecipeActivity extends AppCompatActivity {
    private TextInputEditText editTextTitle, editTextDescription;
    private Spinner spinnerCuisine, spinnerDifficulty;
    private EditText editTextPrepTime, editTextCookTime, editTextServings;
    private CheckBox checkBoxVegetarian, checkBoxVegan, checkBoxGlutenFree, checkBoxDairyFree;
    private LinearLayout layoutIngredients, layoutSteps;
    private Button buttonAddIngredient, buttonAddStep, buttonCreateRecipe;
    private Switch switchPublic;

    private RecipeRepository recipeRepository;
    private UserRepository userRepository;
    private FirebaseAuth auth;

    private List<View> ingredientViews = new ArrayList<>();
    private List<View> stepViews = new ArrayList<>();

    // Available options
    private String[] cuisines = {"Select Cuisine", "Italian", "Chinese", "Mexican", "Indian",
            "Japanese", "Thai", "French", "American", "Mediterranean", "Other"};
    private String[] difficulties = {"Select Difficulty", "Easy", "Medium", "Hard"};
    private String[] ingredientCategories = {"Select Category", "produce", "dairy", "meat", "pantry", "spices", "other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);

        // Check authentication
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login to create recipes", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize repositories
        recipeRepository = new RecipeRepository();
        userRepository = new UserRepository();

        // Initialize views
        initViews();

        // Setup spinners
        setupSpinners();

        // Setup listeners
        setupListeners();
    }

    private void initViews() {
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        spinnerCuisine = findViewById(R.id.spinnerCuisine);
        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);
        editTextPrepTime = findViewById(R.id.editTextPrepTime);
        editTextCookTime = findViewById(R.id.editTextCookTime);
        editTextServings = findViewById(R.id.editTextServings);
        checkBoxVegetarian = findViewById(R.id.checkBoxVegetarian);
        checkBoxVegan = findViewById(R.id.checkBoxVegan);
        checkBoxGlutenFree = findViewById(R.id.checkBoxGlutenFree);
        checkBoxDairyFree = findViewById(R.id.checkBoxDairyFree);
        layoutIngredients = findViewById(R.id.layoutIngredients);
        layoutSteps = findViewById(R.id.layoutSteps);
        buttonAddIngredient = findViewById(R.id.buttonAddIngredient);
        buttonAddStep = findViewById(R.id.buttonAddStep);
        buttonCreateRecipe = findViewById(R.id.buttonCreateRecipe);
        switchPublic = findViewById(R.id.switchPublic);

        // Add one ingredient and one step by default
        addIngredientView();
        addStepView();
    }

    private void setupSpinners() {
        ArrayAdapter<String> cuisineAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, cuisines);
        cuisineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCuisine.setAdapter(cuisineAdapter);

        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, difficulties);
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(difficultyAdapter);
    }

    private void setupListeners() {
        buttonAddIngredient.setOnClickListener(v -> addIngredientView());
        buttonAddStep.setOnClickListener(v -> addStepView());

        buttonCreateRecipe.setOnClickListener(v -> {
            if (validateForm()) {
                createRecipe();
            }
        });
    }

    private void addIngredientView() {
        // IMPORTANT: Use item_ingredient_input.xml (for INPUT forms)
        View ingredientView = LayoutInflater.from(this)
                .inflate(R.layout.item_ingredient_input, layoutIngredients, false);

        // Setup category spinner for this ingredient
        Spinner spinnerCategory = ingredientView.findViewById(R.id.spinnerIngredientCategory);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, ingredientCategories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Set remove button listener
        ImageButton buttonRemove = ingredientView.findViewById(R.id.buttonRemoveIngredient);
        buttonRemove.setOnClickListener(v -> {
            layoutIngredients.removeView(ingredientView);
            ingredientViews.remove(ingredientView);
        });

        layoutIngredients.addView(ingredientView);
        ingredientViews.add(ingredientView);
    }

    private void addStepView() {
        // IMPORTANT: Use item_step_input.xml (for INPUT forms)
        View stepView = LayoutInflater.from(this)
                .inflate(R.layout.item_step_input, layoutSteps, false);

        // Set remove button listener
        ImageButton buttonRemove = stepView.findViewById(R.id.buttonRemoveStep);
        buttonRemove.setOnClickListener(v -> {
            layoutSteps.removeView(stepView);
            stepViews.remove(stepView);
        });

        layoutSteps.addView(stepView);
        stepViews.add(stepView);
    }

    private boolean validateForm() {
        // Validate title
        String title = editTextTitle.getText().toString().trim();
        if (title.isEmpty()) {
            editTextTitle.setError("Title is required");
            editTextTitle.requestFocus();
            return false;
        }

        // Validate description
        String description = editTextDescription.getText().toString().trim();
        if (description.isEmpty()) {
            editTextDescription.setError("Description is required");
            editTextDescription.requestFocus();
            return false;
        }

        // Validate cuisine
        if (spinnerCuisine.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a cuisine", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate difficulty
        if (spinnerDifficulty.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a difficulty", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate times
        String prepTimeStr = editTextPrepTime.getText().toString().trim();
        String cookTimeStr = editTextCookTime.getText().toString().trim();
        if (prepTimeStr.isEmpty() || cookTimeStr.isEmpty()) {
            Toast.makeText(this, "Please enter prep and cook times", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate at least one ingredient
        if (ingredientViews.isEmpty()) {
            Toast.makeText(this, "Add at least one ingredient", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate at least one step
        if (stepViews.isEmpty()) {
            Toast.makeText(this, "Add at least one step", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void createRecipe() {
        // Get current user
        String userId = auth.getCurrentUser().getUid();
        String userEmail = auth.getCurrentUser().getEmail();
        String userName = userEmail != null ? userEmail.split("@")[0] : "User";

        // Create recipe object
        Recipe recipe = new Recipe(
                editTextTitle.getText().toString().trim(),
                editTextDescription.getText().toString().trim(),
                userId,
                userName
        );

        // Set basic info
        recipe.setCuisine(spinnerCuisine.getSelectedItem().toString());
        recipe.setDifficulty(spinnerDifficulty.getSelectedItem().toString());

        try {
            recipe.setPrepTime(Integer.parseInt(editTextPrepTime.getText().toString().trim()));
            recipe.setCookTime(Integer.parseInt(editTextCookTime.getText().toString().trim()));
            recipe.setTotalTime(recipe.getPrepTime() + recipe.getCookTime());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid time values", Toast.LENGTH_SHORT).show();
            return;
        }

        String servingsStr = editTextServings.getText().toString().trim();
        if (!servingsStr.isEmpty()) {
            try {
                recipe.setServings(Integer.parseInt(servingsStr));
            } catch (NumberFormatException e) {
                recipe.setServings(1);
            }
        } else {
            recipe.setServings(1);
        }

        // Set dietary tags
        List<String> dietaryTags = new ArrayList<>();
        if (checkBoxVegetarian.isChecked()) dietaryTags.add("vegetarian");
        if (checkBoxVegan.isChecked()) dietaryTags.add("vegan");
        if (checkBoxGlutenFree.isChecked()) dietaryTags.add("gluten-free");
        if (checkBoxDairyFree.isChecked()) dietaryTags.add("dairy-free");
        recipe.setDietaryTags(dietaryTags);

        // Set public/private
        recipe.setPublic(switchPublic.isChecked());

        // Collect ingredients
        List<Map<String, Object>> ingredients = new ArrayList<>();
        for (View ingredientView : ingredientViews) {
            TextInputEditText editName = ingredientView.findViewById(R.id.editIngredientName);
            TextInputEditText editQuantity = ingredientView.findViewById(R.id.editIngredientQuantity);
            TextInputEditText editUnit = ingredientView.findViewById(R.id.editIngredientUnit);
            Spinner spinnerCategory = ingredientView.findViewById(R.id.spinnerIngredientCategory);

            String name = editName.getText() != null ? editName.getText().toString().trim() : "";
            String quantityStr = editQuantity.getText() != null ? editQuantity.getText().toString().trim() : "";
            String unit = editUnit.getText() != null ? editUnit.getText().toString().trim() : "";
            String category = spinnerCategory.getSelectedItem() != null ?
                    spinnerCategory.getSelectedItem().toString() : "other";

            if (!name.isEmpty() && !quantityStr.isEmpty()) {
                try {
                    double quantity = Double.parseDouble(quantityStr);
                    Map<String, Object> ingredient = new HashMap<>();
                    ingredient.put("name", name);
                    ingredient.put("quantity", quantity);
                    ingredient.put("unit", unit.isEmpty() ? "pieces" : unit);
                    ingredient.put("category", category.equals("Select Category") ? "other" : category);
                    ingredients.add(ingredient);
                } catch (NumberFormatException e) {
                    // Skip invalid quantity
                }
            }
        }
        recipe.setIngredients(ingredients);

        // Collect steps
        List<Map<String, Object>> steps = new ArrayList<>();
        for (int i = 0; i < stepViews.size(); i++) {
            View stepView = stepViews.get(i);
            TextInputEditText editDescription = stepView.findViewById(R.id.editStepDescription);
            EditText editTimer = stepView.findViewById(R.id.editStepTimer);

            String description = editDescription.getText() != null ?
                    editDescription.getText().toString().trim() : "";
            String timerStr = editTimer.getText() != null ? editTimer.getText().toString().trim() : "";

            if (!description.isEmpty()) {
                Map<String, Object> step = new HashMap<>();
                step.put("description", description);
                step.put("stepNumber", i + 1);
                step.put("imageUrl", ""); // Empty for now

                if (!timerStr.isEmpty()) {
                    try {
                        double timer = Double.parseDouble(timerStr);
                        step.put("timer", timer);
                    } catch (NumberFormatException e) {
                        step.put("timer", 0.0);
                    }
                } else {
                    step.put("timer", 0.0);
                }

                steps.add(step);
            }
        }
        recipe.setSteps(steps);

        // Show loading
        buttonCreateRecipe.setEnabled(false);
        buttonCreateRecipe.setText("Creating...");

        // Save recipe to Firestore
        recipeRepository.createRecipe(recipe)
                .addOnSuccessListener(aVoid -> {
                    // Update user's recipe count
                    userRepository.incrementRecipeCount(userId);

                    Toast.makeText(this, "Recipe created successfully!", Toast.LENGTH_SHORT).show();

                    // Return to home
                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    buttonCreateRecipe.setEnabled(true);
                    buttonCreateRecipe.setText("Create Recipe");
                    Toast.makeText(this, "Failed to create recipe: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}