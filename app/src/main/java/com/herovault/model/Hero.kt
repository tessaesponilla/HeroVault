package com.herovault.model

data class Quest(
    val id: String,
    val title: String,
    val description: String,
    val hpGain: Int,
    val mpGain: Int,
    val xpGain: Int
)

data class Loot(
    val id: String,
    val name: String,
    val icon: String
)

data class Hero(
    val id: String,
    val name: String,
    val className: String,
    val pillar: String,
    val portraitRes: Int,
    val quests: List<Quest> = emptyList(),
    val lootTable: List<Loot> = emptyList(),
    var hp: Int = 100,
    var mp: Int = 100,
    var xp: Int = 0,
    var level: Int = 1
)