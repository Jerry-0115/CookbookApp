package com.group4.cookbook.ui.recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.group4.cookbook.R;

public class CommentsFragment extends Fragment {
    private static final String ARG_RECIPE_ID = "recipeId";
    private String recipeId;

    public static CommentsFragment newInstance(String recipeId) {
        CommentsFragment fragment = new CommentsFragment();
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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Create a simple TextView for now
        TextView textView = new TextView(getContext());
        textView.setText("Comments feature coming soon!\n\nRecipe ID: " + recipeId);
        textView.setPadding(32, 32, 32, 32);
        textView.setTextSize(16);
        return textView;
    }
}