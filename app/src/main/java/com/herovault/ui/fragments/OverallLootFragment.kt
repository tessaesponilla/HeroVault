package com.herovault.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.herovault.databinding.DialogLootDetailBinding
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
        val selectedHero = viewModel.selectedHero.value

        // Loop through ALL heroes and their loot tables to show everything earned
        for (hero in viewModel.heroList) {
            for (loot in hero.lootTable) {
                val count = viewModel.getAchievementCount(loot.id)
                if (count > 0) {
                    val isEquipped = selectedHero?.equippedLootIds?.contains(loot.id) == true
                    claimedItems.add(
                        LootCollectionItem(
                            id = loot.id,
                            icon = loot.icon,
                            name = loot.name,
                            count = count,
                            lore = loot.lore,
                            hpBonus = loot.hpBonus,
                            mpBonus = loot.mpBonus,
                            isEquipped = isEquipped
                        )
                    )
                }
            }
        }

        binding.lootRecyclerView.adapter = LootCollectionAdapter(claimedItems) { item ->
            showLootDetailDialog(item)
        }
    }

    private fun showLootDetailDialog(item: LootCollectionItem) {
        val dialogBinding = DialogLootDetailBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.dialogLootIcon.text = item.icon
        dialogBinding.dialogLootName.text = item.name
        dialogBinding.dialogLootLore.text = item.lore
        dialogBinding.dialogLootStats.text = "Bonus: +${item.hpBonus} HP | +${item.mpBonus} MP"
        
        // Only allow equipping if the item belongs to the currently selected hero's loot table
        val selectedHero = viewModel.selectedHero.value
        val isOwnedByCurrent = selectedHero?.lootTable?.any { it.id == item.id } == true
        
        if (isOwnedByCurrent) {
            dialogBinding.btnEquipAction.text = if (item.isEquipped) "Unequip" else "Equip Item"
            dialogBinding.btnEquipAction.setOnClickListener {
                viewModel.equipItem(item.id)
                dialog.dismiss()
                updateOverallLootList() // Refresh
            }
        } else {
            dialogBinding.btnEquipAction.visibility = View.GONE
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
