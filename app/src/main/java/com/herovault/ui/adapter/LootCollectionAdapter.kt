package com.herovault.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.herovault.databinding.ItemLootCollectionBinding

data class LootCollectionItem(val icon: String, val name: String, val count: Int)

class LootCollectionAdapter(private val items: List<LootCollectionItem>) :
    RecyclerView.Adapter<LootCollectionAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemLootCollectionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLootCollectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.lootIcon.text = item.icon
        holder.binding.lootName.text = item.name
        holder.binding.lootCount.text = "Quantity: ${item.count}"
    }

    override fun getItemCount() = items.size
}
