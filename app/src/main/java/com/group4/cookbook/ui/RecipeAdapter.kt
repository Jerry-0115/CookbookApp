package com.group4.cookbook.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.group4.cookbook.databinding.ItemRecipeBinding

class RecipeAdapter(private val recipes: List<Recipe>) :
    RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.binding.tvTitle.text = recipe.title
        holder.binding.tvDescription.text = recipe.description
        holder.binding.ivRecipe.setImageResource(recipe.imageRes)   // 后面会改成 Glide 加载网络图片
    }

    override fun getItemCount() = recipes.size
}