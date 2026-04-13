package com.herovault.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.herovault.data.PreferenceManager
import com.herovault.model.Hero
import com.herovault.model.Quest
import com.herovault.model.Loot
import com.herovault.R
import java.util.*

class HeroViewModel(application: Application) : AndroidViewModel(application) {

    private val prefManager = PreferenceManager(application)

    val heroList = listOf(
        Hero(
            id = "knight",
            name = "The Knight",
            className = "The Guardian",
            pillar = "Physical Resilience",
            portraitRes = R.drawable.portrait_knight,
            quests = listOf(
                Quest("k1", "Log 8 Hours of Sleep",       "Rest and recharge your body.",         hpGain = 20, mpGain = 5,  xpGain = 30),
                Quest("k2", "Do 20 Minutes of Exercise",  "Strengthen your physical resilience.", hpGain = 25, mpGain = 5,  xpGain = 25),
                Quest("k3", "Drink 8 Glasses of Water",   "Hydrate your body for the day.",       hpGain = 15, mpGain = 5,  xpGain = 20),
                Quest("k4", "5 Min Breathing",            "Centered focus.",                      hpGain = 10, mpGain = 10, xpGain = 15)
            ),
            lootTable = listOf(
                Loot("knight_1", "Chainmail Vest", "🛡️"),
                Loot("knight_2", "Iron Shield", "🛡️"),
                Loot("knight_3", "Steel Gauntlets", "🧤"),
                Loot("knight_4", "Knight's Sword", "⚔️"),
                Loot("knight_5", "Stone Whetstone", "🪨"),
                Loot("knight_6", "Vanguard Cape", "🧣")
            )
        ),
        Hero(
            id = "wizard",
            name = "The Wizard",
            className = "The Sage",
            pillar = "Mental Focus",
            portraitRes = R.drawable.portrait_wizard,
            quests = listOf(
                Quest("w1", "Complete 1 Pomodoro Session", "Focus deeply for 25 minutes.",          hpGain = 5,  mpGain = 25, xpGain = 30),
                Quest("w2", "Read for 30 Minutes",         "Expand your knowledge and calm mind.",  hpGain = 5,  mpGain = 20, xpGain = 25),
                Quest("w3", "Journal Your Thoughts",       "Clear mental clutter through writing.", hpGain = 5,  mpGain = 25, xpGain = 20),
                Quest("w4", "Solve a Puzzle",              "Keep the mind sharp.",                  hpGain = 5,  mpGain = 15, xpGain = 20)
            ),
            lootTable = listOf(
                Loot("wiz_1", "Old Scroll", "📜"),
                Loot("wiz_2", "Mana Potion", "🧪"),
                Loot("wiz_3", "Sage Tome", "📖"),
                Loot("wiz_4", "Magic Staff", "🪄"),
                Loot("wiz_5", "Star Relic", "🌟"),
                Loot("wiz_6", "MP Crystal", "🔮")
            )
        ),
        Hero(
            id = "king",
            name = "The King",
            className = "The Sovereign",
            pillar = "Organization",
            portraitRes = R.drawable.portrait_king,
            quests = listOf(
                Quest("kg1", "Declutter Your Workspace",    "A clean space = a clear mind.",            hpGain = 10, mpGain = 15, xpGain = 20),
                Quest("kg2", "Plan Tomorrow's Tasks",       "Take control of your schedule.",           hpGain = 5,  mpGain = 20, xpGain = 25),
                Quest("kg3", "Finish 1 Pending Task",       "Complete something you've been avoiding.", hpGain = 10, mpGain = 15, xpGain = 30),
                Quest("kg4", "Clear 5 Emails",              "Micro-task victory.",                      hpGain = 5,  mpGain = 10, xpGain = 15)
            ),
            lootTable = listOf(
                Loot("king_1", "Golden Sceptre", "🔱"),
                Loot("king_2", "Royal Seal", "📜"),
                Loot("king_3", "Silk Robe", "👘"),
                Loot("king_4", "Authority Ring", "💍"),
                Loot("king_5", "Golden Key", "🔑"),
                Loot("king_6", "Castle Deed", "📜")
            )
        ),
        Hero(
            id = "queen",
            name = "The Queen",
            className = "The Empath",
            pillar = "Emotional Health",
            portraitRes = R.drawable.portrait_queen,
            quests = listOf(
                Quest("q1", "Message a Friend",       "Nurture your social connections.",     hpGain = 5,  mpGain = 20, xpGain = 20),
                Quest("q2", "Practice Deep Breathing",          "Calm your nervous system.",            hpGain = 10, mpGain = 20, xpGain = 20),
                Quest("q3", "Do a Random Act of Kindness",       "Boost mood through giving.",           hpGain = 5,  mpGain = 25, xpGain = 25),
                Quest("q4", "5 Minutes Sunshine",                     "Nature's serotonin.",                  hpGain = 10, mpGain = 10, xpGain = 15)
            ),
            lootTable = listOf(
                Loot("queen_1", "Rose Bouquet", "🌹"),
                Loot("queen_2", "Silver Mirror", "🪞"),
                Loot("queen_3", "Heart Amulet", "📿"),
                Loot("queen_4", "Royal Tiara", "👑"),
                Loot("queen_5", "Peace Lily", "🌿"),
                Loot("queen_6", "Velvet Fan", "🪭")
            )
        ),
        Hero(
            id = "citizen_f",
            name = "Woman Citizen",
            className = "The Balanced",
            pillar = "Work-Life Boundaries",
            portraitRes = R.drawable.portrait_citizen_f,
            quests = listOf(
                Quest("cf1", "Log Off Social Media by 6PM", "Protect your personal time.",          hpGain = 10, mpGain = 15, xpGain = 15),
                Quest("cf2", "Cook a Healthy Meal",         "Nourish your body with good food.",    hpGain = 20, mpGain = 10, xpGain = 20),
                Quest("cf3", "Take a 15min Walk Outside",   "Reset with fresh air and movement.",   hpGain = 15, mpGain = 15, xpGain = 20),
                Quest("cf4", "Drink Herbal Tea",            "Gentle relaxation.",                   hpGain = 10, mpGain = 10, xpGain = 10)
            ),
            lootTable = listOf(
                Loot("citf_1", "Garden Spade", "🪴"),
                Loot("citf_2", "Herbal Tea", "🍵"),
                Loot("citf_3", "Sun Hat", "👒"),
                Loot("citf_4", "Handwoven Basket", "🧺"),
                Loot("citf_5", "Cookbook", "📕"),
                Loot("citf_6", "House Key", "🔑")
            )
        ),
        Hero(
            id = "citizen_m",
            name = "Man Citizen",
            className = "The Mindful",
            pillar = "Daily Presence",
            portraitRes = R.drawable.portrait_citizen_m,
            quests = listOf(
                Quest("cm1", "List 3 Things You're Grateful For", "Shift focus to what's good.",        hpGain = 5,  mpGain = 20, xpGain = 20),
                Quest("cm2", "10 Minutes of Meditation",          "Quiet the mind and be present.",     hpGain = 5,  mpGain = 25, xpGain = 25),
                Quest("cm3", "Spend Time in Nature",              "Ground yourself in the present.",    hpGain = 15, mpGain = 15, xpGain = 20),
                Quest("cm4", "Listen to Music",                  "Auditory grounding.",                 hpGain = 5,  mpGain = 15, xpGain = 10)
            ),
            lootTable = listOf(
                Loot("citm_1", "Old Compass", "🧭"),
                Loot("citm_2", "Leather Journal", "📓"),
                Loot("citm_3", "Walking Stick", "🦯"),
                Loot("citm_4", "Small Map", "🗺️"),
                Loot("citm_5", "Binoculars", "🔭"),
                Loot("citm_6", "Canvas Backpack", "🎒")
            )
        )
    )

    private val _selectedHero = MutableLiveData<Hero?>()
    val selectedHero: LiveData<Hero?> get() = _selectedHero

    private val _minutesRemaining = MutableLiveData<Int>()
    val minutesRemaining: LiveData<Int> get() = _minutesRemaining

    init {
        // Load stats for all heroes to ensure the list is accurate
        heroList.forEach { loadHeroStats(it) }
        startTimer()
    }

    private fun startTimer() {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                val calendar = Calendar.getInstance()
                val minutes = 60 - calendar.get(Calendar.MINUTE)
                _minutesRemaining.postValue(minutes)
            }
        }, 0, 60000) // Update every minute
    }

    fun selectHero(heroId: String) {
        val hero = heroList.find { it.id == heroId } ?: return
        loadHeroStats(hero)
        _selectedHero.value = hero
        prefManager.saveSelectedHeroId(heroId)
    }

    fun clearSelectedHero() {
        _selectedHero.value = null
    }

    fun completeQuest(questId: String, hpGain: Int, mpGain: Int, xpGain: Int) {
        val hero = _selectedHero.value ?: return

        prefManager.setQuestCompleted(questId)

        hero.hp = (hero.hp + hpGain).coerceAtMost(100)
        hero.mp = (hero.mp + mpGain).coerceAtMost(100)
        hero.xp += xpGain

        if (hero.xp >= hero.level * 100) {
            hero.xp = 0
            hero.level++
        }

        prefManager.saveHeroStats(hero.id, hero.hp, hero.mp, hero.xp, hero.level)
        _selectedHero.value = hero
    }

    fun isQuestCompleted(questId: String): Boolean = prefManager.isQuestCompleted(questId)

    fun getCompletedQuestsCount(): Int {
        val hero = _selectedHero.value ?: return 0
        return hero.quests.count { prefManager.isQuestCompleted(it.id) }
    }

    fun isLootClaimed(lootId: String): Boolean = prefManager.isLootClaimed(lootId)

    fun claimLoot(lootId: String) {
        prefManager.setLootClaimed(lootId)
        // Refresh UI
        _selectedHero.value = _selectedHero.value
    }

    fun getAchievementCount(lootId: String): Int = prefManager.getAchievementCount(lootId)

    fun getTotalLevel(): Int = heroList.sumOf { prefManager.loadLevel(it.id) }

    fun getHeroLevel(heroId: String): Int = prefManager.loadLevel(heroId)

    fun getUniqueLootTypesClaimed(): Int {
        val allLootIds = heroList.flatMap { it.lootTable.map { loot -> loot.id } }.distinct()
        return allLootIds.count { prefManager.getAchievementCount(it) > 0 }
    }

    fun getTotalLootClaimed(): Int {
        val allLootIds = heroList.flatMap { it.lootTable.map { loot -> loot.id } }.distinct()
        return allLootIds.sumOf { prefManager.getAchievementCount(it) }
    }

    private fun loadHeroStats(hero: Hero) {
        hero.hp    = prefManager.loadHp(hero.id)
        hero.mp    = prefManager.loadMp(hero.id)
        hero.xp    = prefManager.loadXp(hero.id)
        hero.level = prefManager.loadLevel(hero.id)
    }

    fun resetAllProgress() {
        prefManager.resetAllProgress()
        heroList.forEach { loadHeroStats(it) }
        _selectedHero.value = null
    }
}