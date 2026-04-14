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

public class StepsFragment extends Fragment {
    private static final String ARG_RECIPE_ID = "recipeId";
    private String recipeId;
    private RecipeRepository recipeRepository;
    private LinearLayout layoutSteps;

    public static StepsFragment newInstance(String recipeId) {
        StepsFragment fragment = new StepsFragment();
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
        View view = inflater.inflate(R.layout.fragment_steps, container, false);
        layoutSteps = view.findViewById(R.id.layoutSteps);
        loadSteps();
        return view;
    }

    private void loadSteps() {
        if (recipeId == null) return;

        recipeRepository.getRecipe(recipeId)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Recipe recipe = documentSnapshot.toObject(Recipe.class);
                        if (recipe != null && recipe.getSteps() != null) {
                            displaySteps(recipe.getSteps());
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Show error or empty state
                });
    }

    private void displaySteps(List<Map<String, Object>> steps) {
        if (getContext() == null || layoutSteps == null) return;

        layoutSteps.removeAllViews();

        for (Map<String, Object> step : steps) {
            String description = (String) step.get("description");
            Object stepNumberObj = step.get("stepNumber");
            Object timerObj = step.get("timer");

            int stepNumber = 1;
            if (stepNumberObj instanceof Integer) {
                stepNumber = (Integer) stepNumberObj;
            } else if (stepNumberObj instanceof Long) {
                stepNumber = ((Long) stepNumberObj).intValue();
            }

            double timer = 0;
            if (timerObj instanceof Double) {
                timer = (Double) timerObj;
            } else if (timerObj instanceof Integer) {
                timer = ((Integer) timerObj).doubleValue();
            } else if (timerObj instanceof Long) {
                timer = ((Long) timerObj).doubleValue();
            }

            View stepView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_step, layoutSteps, false);

            TextView textStepNumber = stepView.findViewById(R.id.textStepNumber);
            TextView textDescription = stepView.findViewById(R.id.textStepDescription);
            TextView textTimer = stepView.findViewById(R.id.textStepTimer);

            textStepNumber.setText(String.valueOf(stepNumber));
            textDescription.setText(description);

            if (timer > 0) {
                textTimer.setText(String.format("%.0f min", timer / 60)); // Convert seconds to minutes
                textTimer.setVisibility(View.VISIBLE);
            } else {
                textTimer.setVisibility(View.GONE);
            }

            layoutSteps.addView(stepView);
        }
    }
}