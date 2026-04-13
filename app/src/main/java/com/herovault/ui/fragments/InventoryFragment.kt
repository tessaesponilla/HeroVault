package com.herovault.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.herovault.databinding.FragmentInventoryBinding
import com.herovault.viewmodel.HeroViewModel

class InventoryFragment : Fragment() {

    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HeroViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.selectedHero.observe(viewLifecycleOwner) { hero ->
            hero?.let {
                val completedCount = viewModel.getCompletedQuestsCount()
                val isUnlocked = completedCount >= 3

                if (isUnlocked) {
                    binding.vaultStatusText.text = "✨ Vault Unlocked! Click to claim loot."
                    binding.lootGrid.alpha = 1.0f
                } else {
                    binding.vaultStatusText.text = "🔒 Complete 3 quests to unlock ($completedCount/3)"
                    binding.lootGrid.alpha = 0.5f
                }

                // Map all 6 UI slots
                val slots = listOf(
                    Triple(binding.lootItem1, binding.lootIcon1, Pair(binding.lootName1, binding.lootLabel1)),
                    Triple(binding.lootItem2, binding.lootIcon2, Pair(binding.lootName2, binding.lootLabel2)),
                    Triple(binding.lootItem3, binding.lootIcon3, Pair(binding.lootName3, binding.lootLabel3)),
                    Triple(binding.lootItem4, binding.lootIcon4, Pair(binding.lootName4, binding.lootLabel4)),
                    Triple(binding.lootItem5, binding.lootIcon5, Pair(binding.lootName5, binding.lootLabel5)),
                    Triple(binding.lootItem6, binding.lootIcon6, Pair(binding.lootName6, binding.lootLabel6))
                )

                // Dynamic Binding: Match hero loot table to UI slots
                it.lootTable.forEachIndexed { index, loot ->
                    if (index < slots.size) {
                        val (card, iconTv, labels) = slots[index]
                        val (nameTv, statusTv) = labels
                        
                        iconTv.text = loot.icon
                        nameTv.text = loot.name
                        
                        setupLootItem(card, loot.id, isUnlocked, statusTv)
                    }
                }
            }
        }
    }

    private fun setupLootItem(view: View, lootId: String, isUnlocked: Boolean, label: TextView) {
        val isClaimed = viewModel.isLootClaimed(lootId)
        
        view.isEnabled = isUnlocked && !isClaimed
        view.isClickable = isUnlocked && !isClaimed
        
        if (isClaimed) {
            label.text = "CLAIMED"
            view.alpha = 0.6f
        } else {
            label.text = ""
            view.alpha = 1.0f
        }

        view.setOnClickListener {
            if (isUnlocked && !isClaimed) {
                viewModel.claimLoot(lootId)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
