package com.group4.cookbook.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group4.cookbook.R;
import com.group4.cookbook.data.models.Recipe;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private List<Recipe> recipeList;
    private OnRecipeClickListener listener;

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
        void onLikeClick(Recipe recipe);
        void onSaveClick(Recipe recipe);
    }

    // Constructor with listener
    public RecipeAdapter(List<Recipe> recipeList, OnRecipeClickListener listener) {
        this.recipeList = recipeList;
        this.listener = listener;
    }

    // Alternative constructor without listener (for flexibility)
    public RecipeAdapter(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    public void setRecipeList(List<Recipe> recipeList) {
        this.recipeList = recipeList;
        notifyDataSetChanged();
    }

    public void setOnRecipeClickListener(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.bind(recipe);

        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecipeClick(recipe);
            }
        });

        if (holder.buttonLike != null) {
            holder.buttonLike.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLikeClick(recipe);
                }
            });
        }

        if (holder.buttonSave != null) {
            holder.buttonSave.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSaveClick(recipe);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return recipeList != null ? recipeList.size() : 0;
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView imageRecipe;
        TextView textTitle;
        TextView textUserName;
        TextView textTime;
        TextView textLikes;
        TextView textSaves;
        View buttonLike;
        View buttonSave;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageRecipe = itemView.findViewById(R.id.imageRecipe);
            textTitle = itemView.findViewById(R.id.textTitle);
            textUserName = itemView.findViewById(R.id.textUserName);
            textTime = itemView.findViewById(R.id.textTime);
            textLikes = itemView.findViewById(R.id.textLikes);
            textSaves = itemView.findViewById(R.id.textSaves);
            buttonLike = itemView.findViewById(R.id.buttonLike);
            buttonSave = itemView.findViewById(R.id.buttonSave);
        }

        public void bind(Recipe recipe) {
            textTitle.setText(recipe.getTitle());
            textUserName.setText("By " + recipe.getUserName());
            textTime.setText(recipe.getTotalTime() + " min");
            textLikes.setText(String.valueOf(recipe.getLikes()));
            textSaves.setText(String.valueOf(recipe.getSaves()));

            // Load image with Glide
            if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(recipe.getImageUrl())
                        .placeholder(R.drawable.placeholder_recipe)
                        .into(imageRecipe);
            } else {
                // Set placeholder image
                imageRecipe.setImageResource(R.drawable.placeholder_recipe);
            }
        }
    }
}