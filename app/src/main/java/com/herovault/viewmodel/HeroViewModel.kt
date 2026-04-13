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
    private var timer: Timer? = null

    // Events for UI (Animations/Sounds)
    private val _levelUpEvent = MutableLiveData<Int?>()
    val levelUpEvent: LiveData<Int?> get() = _levelUpEvent

    private val _lootClaimedEvent = MutableLiveData<String?>()
    val lootClaimedEvent: LiveData<String?> get() = _lootClaimedEvent

    // LiveData for Stat Buffs
    private val _maxHp = MutableLiveData<Int>(100)
    val maxHp: LiveData<Int> get() = _maxHp

    private val _maxMp = MutableLiveData<Int>(100)
    val maxMp: LiveData<Int> get() = _maxMp

    val heroList = listOf(
        Hero(
            id = "knight",
            name = "The Knight",
            className = "The Guardian",
            pillar = "Physical Resilience",
            portraitRes = R.drawable.portrait_knight,
            quests = mutableListOf(
                Quest("k1", "Log 8 Hours of Sleep", "Rest and recharge your body.", hpGain = 20, mpGain = 5, xpGain = 30),
                Quest("k2", "Do 20 Minutes of Exercise", "Strengthen your physical resilience.", hpGain = 25, mpGain = 5, xpGain = 25),
                Quest("k3", "Drink 8 Glasses of Water", "Hydrate your body for the day.", hpGain = 15, mpGain = 5, xpGain = 20),
                Quest("k4", "5 Min Breathing", "Centered focus.", hpGain = 10, mpGain = 10, xpGain = 15)
            ),
            lootTable = listOf(
                Loot("knight_1", "Chainmail Vest", "🛡️", hpBonus = 15),
                Loot("knight_2", "Iron Shield", "🛡️", hpBonus = 10),
                Loot("knight_3", "Steel Gauntlets", "🧤", hpBonus = 5),
                Loot("knight_4", "Knight's Sword", "⚔️", hpBonus = 5, mpBonus = 5),
                Loot("knight_5", "Stone Whetstone", "🪨", mpBonus = 5),
                Loot("knight_6", "Vanguard Cape", "🧣", mpBonus = 10),
                Loot("knight_boss", "Aegis of the Sun", "☀️", hpBonus = 50, mpBonus = 20)
            ),
            psychologicalDescription = "The Guardian represents the ego's drive for security and the physical fortitude needed to face life's stresses with a stable foundation."
        ),
        Hero(
            id = "wizard",
            name = "The Wizard",
            className = "The Sage",
            pillar = "Mental Focus",
            portraitRes = R.drawable.portrait_wizard,
            quests = mutableListOf(
                Quest("w1", "Complete 1 Pomodoro Session", "Focus deeply for 25 minutes.", hpGain = 5, mpGain = 25, xpGain = 30),
                Quest("w2", "Read for 30 Minutes", "Expand your knowledge and calm mind.", hpGain = 5, mpGain = 20, xpGain = 25),
                Quest("w3", "Journal Your Thoughts", "Clear mental clutter through writing.", hpGain = 5, mpGain = 25, xpGain = 20),
                Quest("w4", "Solve a Puzzle", "Keep the mind sharp.", hpGain = 5, mpGain = 15, xpGain = 20)
            ),
            lootTable = listOf(
                Loot("wiz_1", "Old Scroll", "📜", mpBonus = 5),
                Loot("wiz_2", "Mana Potion", "🧪", mpBonus = 10),
                Loot("wiz_3", "Sage Tome", "📖", mpBonus = 15),
                Loot("wiz_4", "Magic Staff", "🪄", hpBonus = 5, mpBonus = 10),
                Loot("wiz_5", "Star Relic", "🌟", hpBonus = 10),
                Loot("wiz_6", "MP Crystal", "🔮", mpBonus = 20),
                Loot("wiz_boss", "Eye of Eternity", "🧿", hpBonus = 20, mpBonus = 50)
            ),
            psychologicalDescription = "The Sage embodies cognitive clarity and the analytical mind, focusing on intellectual discipline as a path to psychological stability."
        ),
        Hero(
            id = "king",
            name = "The King",
            className = "The Sovereign",
            pillar = "Organization",
            portraitRes = R.drawable.portrait_king,
            quests = mutableListOf(
                Quest("kg1", "Declutter Your Workspace", "A clean space = a clear mind.", hpGain = 10, mpGain = 15, xpGain = 20),
                Quest("kg2", "Plan Tomorrow's Tasks", "Take control of your schedule.", hpGain = 5, mpGain = 20, xpGain = 25),
                Quest("kg3", "Finish 1 Pending Task", "Complete something you've been avoiding.", hpGain = 10, mpGain = 15, xpGain = 30),
                Quest("kg4", "Clear 5 Emails", "Micro-task victory.", hpGain = 5, mpGain = 10, xpGain = 15)
            ),
            lootTable = listOf(
                Loot("king_1", "Golden Sceptre", "🔱", hpBonus = 10, mpBonus = 10),
                Loot("king_2", "Royal Seal", "📜", mpBonus = 10),
                Loot("king_3", "Silk Robe", "👘", hpBonus = 10),
                Loot("king_4", "Authority Ring", "💍", mpBonus = 15),
                Loot("king_5", "Golden Key", "🔑", hpBonus = 5),
                Loot("king_6", "Castle Deed", "📜", mpBonus = 20),
                Loot("king_boss", "Crown of Harmony", "👑", hpBonus = 35, mpBonus = 35)
            ),
            psychologicalDescription = "The Sovereign represents the archetypal ruler of the self, focusing on structuring internal and external environments to manage chaos."
        ),
        Hero(
            id = "queen",
            name = "The Queen",
            className = "The Empath",
            pillar = "Emotional Health",
            portraitRes = R.drawable.portrait_queen,
            quests = mutableListOf(
                Quest("q1", "Message a Friend", "Nurture your social connections.", hpGain = 5, mpGain = 20, xpGain = 20),
                Quest("q2", "Practice Deep Breathing", "Calm your nervous system.", hpGain = 10, mpGain = 20, xpGain = 20),
                Quest("q3", "Do a Random Act of Kindness", "Boost mood through giving.", hpGain = 5, mpGain = 25, xpGain = 25),
                Quest("q4", "5 Minutes Sunshine", "Nature's serotonin.", hpGain = 10, mpGain = 10, xpGain = 15)
            ),
            lootTable = listOf(
                Loot("queen_1", "Rose Bouquet", "🌹", mpBonus = 5),
                Loot("queen_2", "Silver Mirror", "🪞", mpBonus = 10),
                Loot("queen_3", "Heart Amulet", "📿", hpBonus = 15),
                Loot("queen_4", "Royal Tiara", "👑", hpBonus = 10, mpBonus = 10),
                Loot("queen_5", "Peace Lily", "🌿", hpBonus = 5),
                Loot("queen_6", "Velvet Fan", "🪭", mpBonus = 10),
                Loot("queen_boss", "Chalice of Compassion", "🍷", hpBonus = 35, mpBonus = 35)
            ),
            psychologicalDescription = "The Empath symbolizes emotional intelligence and the healing power of connection, focusing on regulation and interpersonal support."
        ),
        Hero(
            id = "citizen_f",
            name = "Woman Citizen",
            className = "The Balanced",
            pillar = "Work-Life Boundaries",
            portraitRes = R.drawable.portrait_citizen_f,
            quests = mutableListOf(
                Quest("cf1", "Log Off Social Media by 6PM", "Protect your personal time.", hpGain = 10, mpGain = 15, xpGain = 15),
                Quest("cf2", "Cook a Healthy Meal", "Nourish your body with good food.", hpGain = 20, mpGain = 10, xpGain = 20),
                Quest("cf3", "Take a 15min Walk Outside", "Reset with fresh air and movement.", hpGain = 15, mpGain = 15, xpGain = 20),
                Quest("cf4", "Drink Herbal Tea", "Gentle relaxation.", hpGain = 10, mpGain = 10, xpGain = 10)
            ),
            lootTable = listOf(
                Loot("citf_1", "Garden Spade", "🪴", hpBonus = 10),
                Loot("citf_2", "Herbal Tea", "🍵", hpBonus = 5, mpBonus = 5),
                Loot("citf_3", "Sun Hat", "👒", hpBonus = 5),
                Loot("citf_4", "Handwoven Basket", "🧺", mpBonus = 5),
                Loot("citf_5", "Cookbook", "📕", mpBonus = 10),
                Loot("citf_6", "House Key", "🔑", hpBonus = 20),
                Loot("citf_boss", "Amulet of Serenity", "🧿", hpBonus = 30, mpBonus = 40)
            ),
            psychologicalDescription = "The Balanced Citizen represents the integration of self-care into mundane life, focusing on setting boundaries to protect mental energy."
        ),
        Hero(
            id = "citizen_m",
            name = "Man Citizen",
            className = "The Mindful",
            pillar = "Daily Presence",
            portraitRes = R.drawable.portrait_citizen_m,
            quests = mutableListOf(
                Quest("cm1", "List 3 Things You're Grateful For", "Shift focus to what's good.", hpGain = 5, mpGain = 20, xpGain = 20),
                Quest("cm2", "10 Minutes of Meditation", "Quiet the mind and be present.", hpGain = 5, mpGain = 25, xpGain = 25),
                Quest("cm3", "Spend Time in Nature", "Ground yourself in the present.", hpGain = 15, mpGain = 15, xpGain = 20),
                Quest("cm4", "Listen to Music", "Auditory grounding.", hpGain = 5, mpGain = 15, xpGain = 10)
            ),
            lootTable = listOf(
                Loot("citm_1", "Old Compass", "🧭", mpBonus = 10),
                Loot("citm_2", "Leather Journal", "📓", mpBonus = 15),
                Loot("citm_3", "Walking Stick", "🦯", hpBonus = 15),
                Loot("citm_4", "Small Map", "🗺️", mpBonus = 5),
                Loot("citm_5", "Binoculars", "🔭", hpBonus = 5),
                Loot("citm_6", "Canvas Backpack", "🎒", hpBonus = 10, mpBonus = 10),
                Loot("citm_boss", "Scroll of Presence", "📜", hpBonus = 40, mpBonus = 30)
            ),
            psychologicalDescription = "The Mindful Citizen embodies grounded presence and the active practice of mindfulness to manage cognitive overload and stress."
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

        // Initial selected hero from preferences
        val savedId = prefManager.loadSelectedHeroId()
        if (savedId != null) {
            val hero = heroList.find { it.id == savedId }
            if (hero != null) {
                _selectedHero.value = hero
                injectBossQuestIfNeeded(hero)
                calculateMaxStats(hero)
            }
        }
    }

    private fun startTimer() {
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                val calendar = Calendar.getInstance()
                val minutesInHour = calendar.get(Calendar.MINUTE)
                val secondsInMinute = calendar.get(Calendar.SECOND)
                val minutesRemaining = 59 - minutesInHour
                _minutesRemaining.postValue(minutesRemaining)

                // Refresh hero stats every minute
                val currentHero = _selectedHero.value
                if (currentHero != null) {
                    loadHeroStats(currentHero)
                    calculateMaxStats(currentHero)
                    _selectedHero.postValue(currentHero)
                }
            }
        }, 0, 60000)
    }

    fun selectHero(heroId: String) {
        val hero = heroList.find { it.id == heroId } ?: return
        loadHeroStats(hero)
        injectBossQuestIfNeeded(hero)
        _selectedHero.value = hero
        calculateMaxStats(hero)
        prefManager.saveSelectedHeroId(heroId)
    }

    private fun injectBossQuestIfNeeded(hero: Hero) {
        if (hero.level >= 10 && hero.quests.none { it.id.endsWith("_boss") }) {
            val bossQuest = Quest(
                id = "${hero.id}_boss",
                title = "⚔️ BOSS BATTLE: Mastery Challenge",
                description = "Defeat your internal resistance. Complete a difficult 2-hour task of deep growth.",
                hpGain = 50,
                mpGain = 50,
                xpGain = 500
            )
            hero.quests.add(bossQuest)
        }
    }

    fun clearSelectedHero() {
        _selectedHero.value = null
    }

    fun equipItem(lootId: String) {
        val hero = _selectedHero.value ?: return
        val currentEquipped = hero.equippedLootIds.toMutableList()

        if (currentEquipped.contains(lootId)) {
            currentEquipped.remove(lootId)
        } else {
            if (currentEquipped.size < 3) {
                currentEquipped.add(lootId)
            }
        }

        hero.equippedLootIds = currentEquipped
        prefManager.saveEquippedItems(hero.id, currentEquipped)
        calculateMaxStats(hero)
        _selectedHero.value = hero
    }

    private fun calculateMaxStats(hero: Hero) {
        var hpBonus = 0
        var mpBonus = 0
        hero.equippedLootIds.forEach { id ->
            val item = hero.lootTable.find { it.id == id }
            hpBonus += item?.hpBonus ?: 0
            mpBonus += item?.mpBonus ?: 0
        }
        _maxHp.postValue(100 + hpBonus)
        _maxMp.postValue(100 + mpBonus)
    }

    fun completeQuest(questId: String, hpGain: Int, mpGain: Int, xpGain: Int, evidenceUri: String? = null) {
        val hero = _selectedHero.value ?: return

        prefManager.setQuestCompleted(questId)
        evidenceUri?.let { prefManager.saveQuestEvidence(questId, it) }

        if (questId.endsWith("_boss")) {
            val bossLootId = "${hero.id}_boss"
            prefManager.setLootClaimed(bossLootId)
            _lootClaimedEvent.value = bossLootId
        }

        val mHp = _maxHp.value ?: 100
        val mMp = _maxMp.value ?: 100

        hero.hp = (hero.hp + hpGain).coerceAtMost(mHp)
        hero.mp = (hero.mp + mpGain).coerceAtMost(mMp)
        hero.xp += xpGain

        if (hero.xp >= hero.level * 100) {
            hero.xp = 0
            hero.level++
            if (hero.level == 10) injectBossQuestIfNeeded(hero)
            _levelUpEvent.value = hero.level
        }

        prefManager.saveHeroStats(hero.id, hero.hp, hero.mp, hero.xp, hero.level)
        _selectedHero.value = hero
    }

    fun getQuestEvidence(questId: String): String? = prefManager.getQuestEvidence(questId)

    fun isQuestCompleted(questId: String): Boolean = prefManager.isQuestCompleted(questId)

    fun getCompletedQuestsCount(): Int {
        val hero = _selectedHero.value ?: return 0
        return hero.quests.count { prefManager.isQuestCompleted(it.id) }
    }

    fun isLootClaimed(lootId: String): Boolean = prefManager.isLootClaimed(lootId)

    fun claimLoot(lootId: String) {
        prefManager.setLootClaimed(lootId)
        _lootClaimedEvent.value = lootId
        _selectedHero.value = _selectedHero.value
    }

    fun resetEvents() {
        _levelUpEvent.value = null
        _lootClaimedEvent.value = null
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
        hero.hp = prefManager.loadHp(hero.id)
        hero.mp = prefManager.loadMp(hero.id)
        hero.xp = prefManager.loadXp(hero.id)
        hero.level = prefManager.loadLevel(hero.id)
        hero.equippedLootIds = prefManager.loadEquippedItems(hero.id)
    }

    fun resetAllProgress() {
        prefManager.resetAllProgress()
        heroList.forEach { loadHeroStats(it) }
        _selectedHero.value = null
        prefManager.setOnboardingCompleted(false)
    }

    fun discoverHero(physicalScore: Int, mentalScore: Int, emotionalScore: Int) {
        val assignedHeroId = when {
            physicalScore >= mentalScore && physicalScore >= emotionalScore -> "knight"
            mentalScore >= physicalScore && mentalScore >= emotionalScore -> "wizard"
            else -> "queen"
        }
        selectHero(assignedHeroId)
        prefManager.setOnboardingCompleted(true)
    }

    fun isOnboardingCompleted(): Boolean = prefManager.isOnboardingCompleted()

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }
}