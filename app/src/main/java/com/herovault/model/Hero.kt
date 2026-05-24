package com.herovault.model

import com.herovault.R

enum class QuestCategory { PHYSICAL, MENTAL, EMOTIONAL, ORGANIZATION }

data class Quest(
    val id: String,
    val title: String,
    val description: String,
    val hpGain: Int,
    val mpGain: Int,
    val xpGain: Int,
    val category: QuestCategory,
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
        return when (level) {
            1 -> "Initiate"
            2 -> "Aspirant"
            3 -> "Practitioner"
            4 -> "Steady"
            5 -> "Adept"
            6 -> "Resilient"
            7 -> "Disciplined"
            8 -> "Skilled"
            9 -> "Vanguard"
            10 -> "Elite"
            in 11..14 -> "Expert"
            in 15..19 -> "Superior"
            in 20..24 -> "Legendary"
            in 25..29 -> "Grandmaster"
            else -> "Zen Master (Excellent)"
        }
    }

    fun getBorderResource(): Int {
        return when {
            level >= 30 -> R.drawable.border_mastery
            level >= 25 -> R.drawable.border_diamond
            level >= 20 -> R.drawable.border_gold
            level >= 15 -> R.drawable.border_platinum
            level >= 10 -> R.drawable.border_silver
            level >= 5  -> R.drawable.border_bronze
            else        -> 0
        }
    }
}
