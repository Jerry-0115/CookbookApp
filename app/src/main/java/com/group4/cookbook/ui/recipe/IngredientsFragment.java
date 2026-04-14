package com.group4.cookbook.ui.recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.group4.cookbook.R;
import com.group4.cookbook.data.models.Recipe;
import com.group4.cookbook.data.repository.RecipeRepository;

import java.util.List;
import java.util.Map;

public class IngredientsFragment extends Fragment {
    private static final String ARG_RECIPE_ID = "recipeId";
    private String recipeId;
    private RecipeRepository recipeRepository;
    private LinearLayout layoutIngredients;

    public static IngredientsFragment newInstance(String recipeId) {
        IngredientsFragment fragment = new IngredientsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RECIPE_ID, recipeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipeId = getArguments().getString(ARG_RECIPE_ID);
        }
        recipeRepository = new RecipeRepository();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredients, container, false);
        layoutIngredients = view.findViewById(R.id.layoutIngredients);
        loadIngredients();
        return view;
    }

    private void loadIngredients() {
        if (recipeId == null) return;

        recipeRepository.getRecipe(recipeId)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Recipe recipe = documentSnapshot.toObject(Recipe.class);
                        if (recipe != null && recipe.getIngredients() != null) {
                            displayIngredients(recipe.getIngredients());
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Show error or empty state
                });
    }

    private void displayIngredients(List<Map<String, Object>> ingredients) {
        if (getContext() == null || layoutIngredients == null) return;

        layoutIngredients.removeAllViews();

        for (Map<String, Object> ingredient : ingredients) {
            String name = (String) ingredient.get("name");
            Object quantityObj = ingredient.get("quantity");
            String unit = (String) ingredient.get("unit");
            String category = (String) ingredient.get("category");

            double quantity = 0;
            if (quantityObj instanceof Double) {
                quantity = (Double) quantityObj;
            } else if (quantityObj instanceof Integer) {
                quantity = ((Integer) quantityObj).doubleValue();
            } else if (quantityObj instanceof Long) {
                quantity = ((Long) quantityObj).doubleValue();
            }

            View ingredientView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_ingredient, layoutIngredients, false);

            TextView textName = ingredientView.findViewById(R.id.textIngredientName);
            TextView textQuantity = ingredientView.findViewById(R.id.textIngredientQuantity);
            TextView textCategory = ingredientView.findViewById(R.id.textIngredientCategory);

            textName.setText(name);
            textQuantity.setText(String.format("%.1f %s", quantity, unit != null ? unit : ""));
            if (category != null) {
                textCategory.setText(category);
            } else {
                textCategory.setVisibility(View.GONE);
            }

            layoutIngredients.addView(ingredientView);
        }
    }
}