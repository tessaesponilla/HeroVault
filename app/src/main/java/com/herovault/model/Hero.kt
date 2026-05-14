package com.herovault.model

import com.herovault.R

data class Quest(
    val id: String,
    val title: String,
    val description: String,
    val hpGain: Int,
    val mpGain: Int,
    val xpGain: Int,
    val requiresImage: Boolean = true
)

data class Loot(
    val id: String,
    val name: String,
    val icon: String,
    val lore: String = "",
    val hpBonus: Int = 0,
    val mpBonus: Int = 0
)

data class Skill(
    val name: String,
    val description: String,
    val icon: String,
    val unlockedAtLevel: Int
)

data class Hero(
    val id: String,
    val name: String,
    val className: String,
    val pillar: String,
    val portraitRes: Int,
    val quests: MutableList<Quest> = mutableListOf(),
    val lootTable: List<Loot> = emptyList(),
    var equippedLootIds: List<String> = emptyList(),
    val psychologicalDescription: String = "",
    val skills: List<Skill> = emptyList(),
    var hp: Int = 100,
    var mp: Int = 100,
    var xp: Int = 0,
    var level: Int = 1
) {
    fun getEvolutionTitle(): String {
        return when {
            level >= 20 -> "Excellent"
            level >= 15 -> "Adept"
            level >= 10 -> "Advanced"
            level >= 5  -> "Intermediate"
            else        -> "Novice"
        }
    }

    fun getBorderResource(): Int {
        return when {
            level >= 20 -> R.drawable.border_gold
            level >= 10 -> R.drawable.border_silver
            level >= 5  -> R.drawable.border_bronze
            else        -> 0
        }
    }
}
