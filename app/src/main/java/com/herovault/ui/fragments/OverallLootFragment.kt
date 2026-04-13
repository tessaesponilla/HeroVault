package com.herovault.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.herovault.databinding.FragmentLootBinding
import com.herovault.ui.adapter.LootCollectionAdapter
import com.herovault.ui.adapter.LootCollectionItem
import com.herovault.viewmodel.HeroViewModel

class OverallLootFragment : Fragment() {

    private var _binding: FragmentLootBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HeroViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLootBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lootRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        updateOverallLootList()
    }

    private fun updateOverallLootList() {
        val claimedItems = mutableListOf<LootCollectionItem>()

        // Loop through ALL heroes and their loot tables
        for (hero in viewModel.heroList) {
            for (loot in hero.lootTable) {
                val count = viewModel.getAchievementCount(loot.id)
                if (count > 0) {
                    claimedItems.add(
                        LootCollectionItem(
                            id = loot.id,
                            icon = loot.icon,
                            name = loot.name,
                            count = count,
                            isEquipped = false
                        )
                    )
                }
            }
        }

        binding.lootRecyclerView.adapter = LootCollectionAdapter(
            items = claimedItems,
            onItemClick = { item ->
                Toast.makeText(requireContext(), "${item.name} (x${item.count})", Toast.LENGTH_SHORT).show()
                // You can add more functionality here like:
                // - Show item details dialog
                // - Equip/unequip item
                // - Navigate to item detail fragment
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}