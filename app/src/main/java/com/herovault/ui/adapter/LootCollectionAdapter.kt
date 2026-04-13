package com.herovault.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.herovault.databinding.ItemLootCollectionBinding

data class LootCollectionItem(
    val id: String,
    val icon: String,
    val name: String,
    val count: Int,
    val isEquipped: Boolean = false
)

class LootCollectionAdapter(
    private val items: List<LootCollectionItem>,
    private val onItemClick: (LootCollectionItem) -> Unit
) : RecyclerView.Adapter<LootCollectionAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemLootCollectionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLootCollectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.lootIcon.text = item.icon
        holder.binding.lootName.text = item.name
        
        if (item.isEquipped) {
            holder.binding.lootCount.text = "EQUIPPED"
            holder.binding.root.setStrokeColor(Color.parseColor("#FFD700")) // Gold
            holder.binding.root.setStrokeWidth(4)
        } else {
            holder.binding.lootCount.text = "Quantity: ${item.count}"
            holder.binding.root.setStrokeColor(Color.parseColor("#EEEEEE"))
            holder.binding.root.setStrokeWidth(1)
        }

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount() = items.size
}
