package com.group4.cookbook.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.group4.cookbook.R                    // ←←← 这一行就是修复关键
import com.group4.cookbook.databinding.FragmentCollectionsBinding

class CollectionsFragment : Fragment() {

    private var _binding: FragmentCollectionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 示例收藏数据（后面会改成从 Firebase 读取用户真实收藏）
        val collectedRecipes = listOf(
            Recipe("红烧牛肉", "已收藏 · 4.8分", R.drawable.ic_home),
            Recipe("宫保鸡丁", "已收藏 · 4.9分", R.drawable.ic_home),
            Recipe("糖醋排骨", "已收藏 · 4.7分", R.drawable.ic_home)
        )

        val adapter = RecipeAdapter(collectedRecipes)
        binding.recyclerCollections.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCollections.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}