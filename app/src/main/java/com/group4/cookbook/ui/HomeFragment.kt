package com.group4.cookbook.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.group4.cookbook.R
import com.group4.cookbook.databinding.FragmentHomeBinding
import com.group4.cookbook.ui.recipe.CreateRecipeActivity   // ← 正确导入

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ============== 暂时注释掉下面这几行（避免索引错误） ==============
        /*
        val recipeList = listOf(
            Recipe("红烧牛肉", "经典家常菜，鲜嫩入味", R.drawable.ic_home),
            Recipe("宫保鸡丁", "酸甜辣，超级下饭", R.drawable.ic_home),
            Recipe("麻婆豆腐", "川菜经典，麻辣鲜香", R.drawable.ic_home)
        )

        val adapter = RecipeAdapter(recipeList)
        binding.recyclerHome.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHome.adapter = adapter
        */

        // 点击 + 按钮跳转创建食谱（Record）保持正常
        binding.fabAddRecipe.setOnClickListener {
            val intent = Intent(requireContext(), CreateRecipeActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// 临时 Recipe 数据类（如果组员已经在其他地方定义了，可以删除这段）
data class Recipe(
    val title: String,
    val description: String,
    val imageRes: Int
)