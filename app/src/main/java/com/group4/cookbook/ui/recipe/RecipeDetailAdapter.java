package com.group4.cookbook.ui.recipe;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

class RecipeDetailPagerAdapter extends FragmentStateAdapter {
    private String recipeId;

    public RecipeDetailPagerAdapter(@NonNull FragmentActivity fragmentActivity, String recipeId) {
        super(fragmentActivity);
        this.recipeId = recipeId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return IngredientsFragment.newInstance(recipeId);
            case 1:
                return StepsFragment.newInstance(recipeId);
            case 2:
                return CommentsFragment.newInstance(recipeId);
            default:
                return IngredientsFragment.newInstance(recipeId);
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Ingredients, Steps, Comments
    }
}