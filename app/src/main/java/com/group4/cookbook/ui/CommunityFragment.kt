package com.group4.cookbook.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.group4.cookbook.R
import com.group4.cookbook.databinding.FragmentCommunityBinding

class CommunityFragment : Fragment() {

    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 示例社区分享数据（后面会改成 Firebase 实时数据 + 点赞评论）
        val communityRecipes = listOf(
            Recipe("大厨分享：完美红烧肉", "今天教大家一招超入味的做法！", R.drawable.ic_home),
            Recipe("家常糖醋鱼", "酸甜开胃，5分钟上桌", R.drawable.ic_home),
            Recipe("空气炸锅版薯条", "零失败零油版分享", R.drawable.ic_home)
        )

        val adapter = RecipeAdapter(communityRecipes)
        binding.recyclerCommunity.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCommunity.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}