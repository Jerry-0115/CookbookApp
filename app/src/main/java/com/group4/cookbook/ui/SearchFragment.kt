package com.group4.cookbook.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.group4.cookbook.R
import com.group4.cookbook.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 示例数据（后面会改成实时搜索 + Firebase 查询）
        val recipeList = listOf(
            Recipe("红烧肉", "肥而不腻，入口即化", R.drawable.ic_home),
            Recipe("清蒸鲈鱼", "鲜嫩原汁，健康低脂", R.drawable.ic_home),
            Recipe("糖醋排骨", "酸甜可口，色泽诱人", R.drawable.ic_home)
        )

        val adapter = RecipeAdapter(recipeList)
        binding.recyclerSearch.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSearch.adapter = adapter

        // 搜索框监听（暂时打印，后面加真正搜索逻辑）
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.etSearch.text.toString().trim()
                // TODO: 后面在这里调用搜索函数
                true
            } else false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}