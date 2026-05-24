package com.herovault.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.herovault.data.PreferenceManager
import com.herovault.model.*
import com.herovault.R
import java.util.*

class HeroViewModel(application: Application) : AndroidViewModel(application) {

    private val prefManager = PreferenceManager(application)
    private var timer: Timer? = null

    // Events
    private val _levelUpEvent = MutableLiveData<Int?>()
    val levelUpEvent: LiveData<Int?> get() = _levelUpEvent

    private val _lootClaimedEvent = MutableLiveData<String?>()
    val lootClaimedEvent: LiveData<String?> get() = _lootClaimedEvent

    private val _maxHp = MutableLiveData<Int>(100)
    val maxHp: LiveData<Int> get() = _maxHp

    private val _maxMp = MutableLiveData<Int>(100)
    val maxMp: LiveData<Int> get() = _maxMp

    private val _minutesRemaining = MutableLiveData<Int>()
    val minutesRemaining: LiveData<Int> get() = _minutesRemaining

    private val _selectedHero = MutableLiveData<Hero?>()
    val selectedHero: LiveData<Hero?> get() = _selectedHero

    val heroList = listOf(
        Hero(
            id = "knight",
            name = "Guardian",
            className = "Guardian",
            pillar = "Physical Resilience",
            portraitRes = R.drawable.portrait_knight,
            quests = mutableListOf(
                Quest("k1", "Hydration Check", "Drink a full glass of water.", 15, 5, 20, QuestCategory.PHYSICAL),
                Quest("k2", "Morning Stretch", "Stretch your body for 5 minutes.", 20, 10, 25, QuestCategory.PHYSICAL),
                Quest("k3", "Walk Outside", "Go for a 10+ minute walk outdoors.", 25, 10, 30, QuestCategory.PHYSICAL),
                Quest("k4", "Home Workout", "Complete a home exercise session.", 30, 15, 40, QuestCategory.PHYSICAL),
                Quest("k5", "Healthy Meal", "Prepare and eat a balanced meal.", 20, 15, 35, QuestCategory.PHYSICAL)
            ),
            lootTable = listOf(
                Loot("knight_1", "Chainmail Vest", "🛡️", lore = "Built through physical consistency.", hpBonus = 15),
                Loot("knight_2", "Iron Shield", "🛡️", lore = "Mental wall against negativity.", hpBonus = 10),
                Loot("knight_3", "Steel Gauntlets", "🥊", lore = "Protects from physical fatigue.", hpBonus = 12),
                Loot("knight_4", "Heavy Greaves", "🦿", lore = "Grounds your presence.", hpBonus = 8, mpBonus = 5),
                Loot("knight_5", "Restorative Elixir", "🧪", lore = "Accelerates biological healing.", hpBonus = 20),
                Loot("knight_6", "Golden Helm", "🪖", lore = "Peak resilience of mind and body.", hpBonus = 15, mpBonus = 10)
            ),
            psychologicalDescription = "The Guardian focuses on developing physical fortitude as a stable foundation for mental health.",
            skills = listOf(Skill("Indomitable Will", "Reduces HP drain by 60%.", "🛡️", 5))
        ),
        Hero(
            id = "citizen_f",
            name = "Balanced",
            className = "Balanced",
            pillar = "Work-Life Boundaries",
            portraitRes = R.drawable.portrait_citizen_f,
            quests = mutableListOf(
                Quest("cf1", "Screen-Free Corner", "Spend 15 minutes in a place without screens (read, drink tea, look out a window).", 10, 15, 15, QuestCategory.EMOTIONAL),
                Quest("cf2", "Work Shutdown Ritual", "Close your laptop and physically step away from your desk.", 15, 10, 20, QuestCategory.EMOTIONAL),
                Quest("cf3", "Nature Pause", "Take a 5-minute break outdoors or by a plant.", 20, 15, 25, QuestCategory.PHYSICAL),
                Quest("cf4", "Evening Walk", "Walk for 15 minutes after your last work task.", 15, 10, 20, QuestCategory.PHYSICAL),
                Quest("cf5", "Boundary Object", "Place a small object on your desk that signals 'work is done'.", 10, 20, 15, QuestCategory.EMOTIONAL)
            ),
            lootTable = listOf(
                Loot("citf_1", "Zen Tea Set", "🍵", lore = "Calming brew to disconnect.", mpBonus = 15),
                Loot("citf_2", "Sleeping Mask", "🪬", lore = "Shields eyes from work light.", hpBonus = 10),
                Loot("citf_3", "Desk Plant", "🪴", lore = "Brings natural life to the desk.", mpBonus = 10),
                Loot("citf_4", "Noise Canceler", "🎧", lore = "Filters external workplace demands.", mpBonus = 12, hpBonus = 8),
                Loot("citf_5", "Boundary Wand", "🪄", lore = "Allows saying 'no' comfortably.", hpBonus = 15),
                Loot("citf_6", "House Key", "🔑", lore = "Power to lock out the world.", hpBonus = 20)
            ),
            psychologicalDescription = "Represents finding harmony by setting boundaries to prevent burnout.",
            skills = listOf(Skill("Boundary Setting", "Reduces drain by 25%.", "🚫", 5))
        ),
        Hero(
            id = "wizard",
            name = "Sage",
            className = "Sage",
            pillar = "Mental Focus",
            portraitRes = R.drawable.portrait_wizard,
            quests = mutableListOf(
                Quest("w1", "Book Page", "Read 10 pages of a book.", 5, 20, 20, QuestCategory.MENTAL),
                Quest("w2", "Completed Puzzle", "Finish a Sudoku, crossword, or brain puzzle.", 10, 15, 25, QuestCategory.MENTAL),
                Quest("w3", "Learning Note", "Write down three things you learned from a podcast, video, or article.", 10, 20, 25, QuestCategory.MENTAL),
                Quest("w4", "Brain Game", "Play a logic or memory game for 15 minutes.", 10, 15, 20, QuestCategory.MENTAL),
                Quest("w5", "Focus Timer", "Concentrate on a single task for 25 minutes without interruption.", 15, 25, 30, QuestCategory.MENTAL)
            ),
            lootTable = listOf(
                Loot("wiz_1", "Clarity Lens", "🔎", lore = "Focuses attention on what matters.", mpBonus = 10),
                Loot("wiz_2", "Parchment Scroll", "📜", lore = "Scribbles to declutter the mind.", mpBonus = 12),
                Loot("wiz_3", "Sage Tome", "📖", lore = "Frameworks for handling complexity.", mpBonus = 15),
                Loot("wiz_4", "Runed Staff", "🪄", lore = "Directs cognitive power effectively.", mpBonus = 18),
                Loot("wiz_5", "Mindful Hourglass", "⏳", lore = "Measures deep focus intervals.", mpBonus = 15, hpBonus = 5),
                Loot("wiz_6", "MP Crystal", "🔮", lore = "Stored cognitive energy.", mpBonus = 20)
            ),
            psychologicalDescription = "The Sage embodies cognitive mastery and filtering mental noise.",
            skills = listOf(Skill("Cognitive Clarity", "Reduces mental MP drain by 60%.", "🔮", 5))
        ),
        Hero(
            id = "king",
            name = "Sovereign",
            className = "Sovereign",
            pillar = "Organization",
            portraitRes = R.drawable.portrait_king,
            quests = mutableListOf(
                Quest("kg1", "Decluttered Desk", "Clear your desk of unnecessary items.", 15, 10, 25, QuestCategory.ORGANIZATION),
                Quest("kg2", "Written Plan", "Write down your three most important tasks for today.", 10, 15, 20, QuestCategory.ORGANIZATION),
                Quest("kg3", "Organised Drawer", "Tidy one drawer or shelf in your home or office.", 15, 10, 25, QuestCategory.ORGANIZATION),
                Quest("kg4", "Digital Cleanup", "Delete or archive 10 old files from your computer desktop.", 10, 15, 20, QuestCategory.ORGANIZATION),
                Quest("kg5", "Weekly Review", "Review your calendar and plan the upcoming week.", 15, 15, 30, QuestCategory.ORGANIZATION)
            ),
            lootTable = listOf(
                Loot("king_1", "Ornate Quill", "🪶", lore = "Organizes daily objectives.", mpBonus = 12),
                Loot("king_2", "Royal Seal", "📜", lore = "Validates structured decisions.", mpBonus = 10),
                Loot("king_3", "Golden Scepter", "👑", lore = "Command over chaotic schedules.", mpBonus = 15),
                Loot("king_4", "Sovereign Map", "🗺️", lore = "Clear vision of your path.", mpBonus = 15, hpBonus = 5),
                Loot("king_5", "Iron Safe", "🗄️", lore = "Secures essential routines.", hpBonus = 10, mpBonus = 10),
                Loot("king_6", "Castle Deed", "📜", lore = "Ownership of boundaries.", mpBonus = 20)
            ),
            psychologicalDescription = "Represents archetypal order, structuring environments to manage chaos.",
            skills = listOf(Skill("Royal Edict", "Daily Vault requirements reduced to 2.", "📜", 5))
        ),
        Hero(
            id = "queen",
            name = "Empath",
            className = "Empath",
            pillar = "Emotional Health",
            portraitRes = R.drawable.portrait_queen,
            quests = mutableListOf(
                Quest("q1", "Gratitude Object", "Identify one thing you're grateful for and hold it in your hands.", 10, 15, 15, QuestCategory.EMOTIONAL),
                Quest("q2", "Support Message", "Send a kind message to a friend or family member.", 15, 10, 20, QuestCategory.EMOTIONAL),
                Quest("q3", "Comfort Kit", "Prepare a small box or bag with items that comfort you (tea, blanket, candle).", 15, 10, 20, QuestCategory.EMOTIONAL),
                Quest("q4", "Social Proof", "Spend 15 minutes with a person or pet you love.", 20, 15, 25, QuestCategory.EMOTIONAL),
                Quest("q5", "Journal Page", "Write one page about how you're feeling right now.", 10, 20, 20, QuestCategory.MENTAL)
            ),
            lootTable = listOf(
                Loot("queen_1", "Compassion Mirror", "🪞", lore = "Reflects internal goodness.", hpBonus = 10),
                Loot("queen_2", "Comforting Shawl", "🧣", lore = "Protective warmth against isolation.", hpBonus = 12),
                Loot("queen_3", "Heart Amulet", "📿", lore = "Protection from external drain.", hpBonus = 15),
                Loot("queen_4", "Royal Tiara", "👑", lore = "Sovereignty over feelings.", hpBonus = 10, mpBonus = 10),
                Loot("queen_5", "Healing Balm", "🧴", lore = "Soothes emotional bruises.", hpBonus = 18),
                Loot("queen_6", "Rose Quartz", "💎", lore = "Promotes interpersonal warmth.", hpBonus = 15, mpBonus = 15)
            ),
            psychologicalDescription = "Symbolizes emotional intelligence, focusing on regulation and healing.",
            skills = listOf(Skill("Benevolence", "Social quests heal +20 HP.", "💖", 5))
        ),
        Hero(
            id = "citizen_m",
            name = "Mindful",
            className = "Mindful",
            pillar = "Daily Presence",
            portraitRes = R.drawable.portrait_citizen_m,
            quests = mutableListOf(
                Quest("cm1", "Meditation Spot", "Sit quietly in a designated spot for 5 minutes.", 10, 15, 15, QuestCategory.MENTAL),
                Quest("cm2", "Mindful Meal", "Eat one meal without any screens or distractions.", 15, 15, 20, QuestCategory.PHYSICAL),
                Quest("cm3", "Nature Moment", "Pause for 10 seconds and observe a natural object (leaf, cloud, water).", 10, 10, 15, QuestCategory.MENTAL),
                Quest("cm4", "Breathing Reminder", "Set a timer to go off every hour – when it rings, take three deep breaths.", 10, 15, 15, QuestCategory.MENTAL),
                Quest("cm5", "Present Object", "Pick an object, hold it, and describe it in detail using your senses.", 15, 15, 20, QuestCategory.MENTAL)
            ),
            lootTable = listOf(
                Loot("citm_1", "Singing Bowl", "🥣", lore = "Rings a tone of pure presence.", mpBonus = 10),
                Loot("citm_2", "Leather Journal", "📓", lore = "Record of miracles.", mpBonus = 15),
                Loot("citm_3", "Incense Burner", "🪔", lore = "Focuses breath in the present.", mpBonus = 12),
                Loot("citm_4", "Polished Pebble", "🪨", lore = "Grounded anchor to touch.", hpBonus = 10),
                Loot("citm_5", "Meditation Cushion", "🧘", lore = "Perfect support for sitting.", hpBonus = 12, mpBonus = 8),
                Loot("citm_6", "Lotus Blossom", "🪷", lore = "Symbol of mindful unfolding.", hpBonus = 15, mpBonus = 15)
            ),
            psychologicalDescription = "Embodies grounded presence to manage cognitive overload and anxiety.",
            skills = listOf(Skill("Zen State", "MP gain increased by 20%.", "🧘", 5))
        )
    )

    init {
        heroList.forEach { loadHeroStats(it) }
        startTimer()
        val savedId = prefManager.loadSelectedHeroId()
        if (savedId != null) {
            val hero = heroList.find { it.id == savedId }
            if (hero != null) {
                _selectedHero.value = hero
                calculateMaxStats(hero)
            }
        }
    }

    private fun startTimer() {
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                val calendar = Calendar.getInstance()
                _minutesRemaining.postValue(60 - calendar.get(Calendar.MINUTE))
                val currentHero = _selectedHero.value
                if (currentHero != null) {
                    prefManager.applyHourlyDrain()
                    loadHeroStats(currentHero)
                    calculateMaxStats(currentHero)
                    _selectedHero.postValue(currentHero)
                }
            }
        }, 0, 60000)
    }

    fun getPsychologicalInsight(): String {
        val h = _selectedHero.value ?: return "Begin discovery path."
        val mood = prefManager.getTodayMood()
        val moodText = if (mood != null) "Clinical Note: Reporting '$mood' requires attention to your ${h.pillar} pillar." else ""

        return when {
            h.hp < 30 -> "$moodText\n\nYour physical anchor is weak. Prioritize biological restoration immediately."
            h.mp < 30 -> "$moodText\n\nCognitive focus is low. Over-stimulation likely; consider silence."
            else -> "$moodText\n\nStatus: Archetype adherence is stable. Progression is on track."
        }
    }

    fun getTodayMood(): String? = prefManager.getTodayMood()
    fun saveDailyMood(mood: String) {
        prefManager.saveDailyMood(mood)
        _selectedHero.value = _selectedHero.value
    }

    fun saveUserName(name: String) = prefManager.saveUserName(name)
    fun getUserName(): String = prefManager.getUserName()

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
            hero.quests.add(Quest("${hero.id}_boss", "⚔️ BOSS BATTLE", "Defeat internal resistance – write a reflective journal entry.", 50, 50, 500, QuestCategory.MENTAL))
        }
    }

    fun clearSelectedHero() { _selectedHero.value = null }

    fun equipItem(lootId: String) {
        val hero = _selectedHero.value ?: return
        val currentEquipped = hero.equippedLootIds.toMutableList()
        if (currentEquipped.contains(lootId)) currentEquipped.remove(lootId)
        else if (currentEquipped.size < 3) currentEquipped.add(lootId)
        hero.equippedLootIds = currentEquipped
        prefManager.saveEquippedItems(hero.id, currentEquipped)
        calculateMaxStats(hero)
        _selectedHero.value = hero
    }

    private fun calculateMaxStats(hero: Hero) {
        var hpB = 0; var mpB = 0
        hero.equippedLootIds.forEach { id ->
            val item = hero.lootTable.find { it.id == id }
            hpB += item?.hpBonus ?: 0
            mpB += item?.mpBonus ?: 0
        }
        _maxHp.postValue(100 + hpB)
        _maxMp.postValue(100 + mpB)
    }

    fun completeQuest(questId: String, hpG: Int, mpG: Int, xpG: Int, uri: String? = null) {
        val hero = _selectedHero.value ?: return
        prefManager.setQuestCompleted(questId)
        uri?.let { prefManager.saveQuestEvidence(questId, it) }

        val quest = hero.quests.find { it.id == questId }
        quest?.let { prefManager.trackQuestCategory(it.category.name) }

        hero.hp = (hero.hp + hpG).coerceAtMost(_maxHp.value ?: 100)
        hero.mp = (hero.mp + mpG).coerceAtMost(_maxMp.value ?: 100)
        hero.xp += xpG
        if (hero.xp >= hero.level * 100) {
            hero.xp = 0; hero.level++
            if (hero.level == 10) injectBossQuestIfNeeded(hero)
            _levelUpEvent.value = hero.level
        }
        prefManager.saveHeroStats(hero.id, hero.hp, hero.mp, hero.xp, hero.level)
        _selectedHero.value = hero
    }

    fun hasEverCompletedQuest(id: String): Boolean = prefManager.hasEverCompletedQuest(id)
    fun getQuestEvidence(id: String): String? = prefManager.getQuestEvidence(id)
    fun isQuestCompleted(id: String): Boolean = prefManager.isQuestCompleted(id)
    fun isLootClaimed(id: String): Boolean = prefManager.isLootClaimed(id)
    fun getCompletedQuestsCount(): Int = _selectedHero.value?.quests?.count { isQuestCompleted(it.id) } ?: 0

    fun claimLoot(id: String) {
        prefManager.setLootClaimed(id)
        _lootClaimedEvent.value = id
        _selectedHero.value = _selectedHero.value
    }
    fun resetEvents() { _levelUpEvent.value = null; _lootClaimedEvent.value = null }
    fun getAchievementCount(id: String): Int = prefManager.getAchievementCount(id)
    fun getTotalLevel(): Int = heroList.sumOf { prefManager.loadLevel(it.id) }
    fun getHeroLevel(id: String): Int = prefManager.loadLevel(id)
    fun getUniqueLootTypesClaimed(): Int {
        val allLootIds = heroList.flatMap { it.lootTable.map { loot -> loot.id } }.distinct()
        return allLootIds.count { prefManager.getAchievementCount(it) > 0 }
    }
    fun getTotalLootClaimed(): Int {
        val allLootIds = heroList.flatMap { it.lootTable.map { loot -> loot.id } }.distinct()
        return allLootIds.sumOf { prefManager.getAchievementCount(it) }
    }
    fun getPillarProgress(pillar: String): Int = prefManager.getCategoryCount(pillar)

    private fun loadHeroStats(h: Hero) {
        h.hp = prefManager.loadHp(h.id); h.mp = prefManager.loadMp(h.id)
        h.xp = prefManager.loadXp(h.id); h.level = prefManager.loadLevel(h.id)
        h.equippedLootIds = prefManager.loadEquippedItems(h.id)
    }

    fun resetAllProgress() { prefManager.resetAllProgress(); heroList.forEach { loadHeroStats(it) }; _selectedHero.value = null; prefManager.setOnboardingCompleted(false) }

    fun discoverHero(p: Int, m: Int, e: Int, s: Int, slp: Int, mot: Int, act: Int, str: Int, rou: Int, car: Int) {
        val scoresMap = mapOf(
            "knight" to (p + act),
            "citizen_f" to (slp + car),
            "wizard" to (m + mot),
            "king" to (rou * 2),
            "queen" to (e * 2),
            "citizen_m" to (str + s)
        )
        val maxScore = scoresMap.values.maxOrNull() ?: 0
        val candidates = scoresMap.filter { it.value == maxScore }.keys.toList()
        val assignedHeroId = candidates.random()
        selectHero(assignedHeroId)
        prefManager.setOnboardingCompleted(true)
    }

    fun isOnboardingCompleted(): Boolean = prefManager.isOnboardingCompleted()
}