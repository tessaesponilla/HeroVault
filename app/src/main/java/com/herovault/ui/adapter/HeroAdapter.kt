package com.herovault.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.herovault.databinding.ItemHeroBinding
import com.herovault.model.Hero

class HeroAdapter(
    private val heroes: List<Hero>,
    private val onHeroClick: (Hero) -> Unit
) : RecyclerView.Adapter<HeroAdapter.HeroViewHolder>() {

    class HeroViewHolder(val binding: ItemHeroBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeroViewHolder {
        val binding = ItemHeroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeroViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeroViewHolder, position: Int) {
        val hero = heroes[position]
        holder.binding.heroName.text = hero.name
        holder.binding.heroPillar.text = hero.pillar
        holder.binding.heroLevel.text = "Lv. ${hero.level}"
        holder.binding.heroImage.setImageResource(hero.portraitRes)
        
        holder.itemView.setOnClickListener { onHeroClick(hero) }
    }

    override fun getItemCount() = heroes.size
}
