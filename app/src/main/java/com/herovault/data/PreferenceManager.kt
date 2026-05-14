package com.herovault.data

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*

class PreferenceManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("herovault_prefs", Context.MODE_PRIVATE)

    private var lastCheckedDay: String = ""

    // ==================== HERO STATS (Permanent) ====================
    fun saveHeroStats(heroId: String, hp: Int, mp: Int, xp: Int, level: Int) {
        prefs.edit().apply {
            putInt("${heroId}_hp", hp)
            putInt("${heroId}_mp", mp)
            putInt("${heroId}_xp", xp)
            putInt("${heroId}_level", level)
            apply()
        }
    }

    fun loadHp(heroId: String): Int = prefs.getInt("${heroId}_hp", 100)
    fun loadMp(heroId: String): Int = prefs.getInt("${heroId}_mp", 100)
    fun loadXp(heroId: String): Int = prefs.getInt("${heroId}_xp", 0)
    fun loadLevel(heroId: String): Int = prefs.getInt("${heroId}_level", 1)

    fun saveSelectedHeroId(heroId: String) = prefs.edit().putString("selected_hero", heroId).apply()
    fun loadSelectedHeroId(): String? = prefs.getString("selected_hero", null)

    fun isOnboardingCompleted(): Boolean = prefs.getBoolean("onboarding_complete", false)
    fun setOnboardingCompleted(completed: Boolean) = prefs.edit().putBoolean("onboarding_complete", completed).apply()

    // ==================== EQUIPMENT (Permanent) ====================
    fun saveEquippedItems(heroId: String, itemIds: List<String>) {
        prefs.edit().putString("${heroId}_equipped", itemIds.joinToString(",")).apply()
    }

    fun loadEquippedItems(heroId: String): List<String> {
        val saved = prefs.getString("${heroId}_equipped", "") ?: ""
        return if (saved.isEmpty()) emptyList() else saved.split(",")
    }

    // ==================== QUESTS (Daily Reset) ====================

    private fun getTodayKey(): String {
        return SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    }

    fun isQuestCompleted(questId: String): Boolean {
        checkDailyReset()
        return prefs.getBoolean("quest_done_${getTodayKey()}_$questId", false)
    }

    fun setQuestCompleted(questId: String) {
        prefs.edit().putBoolean("quest_done_${getTodayKey()}_$questId", true).apply()
    }

    fun saveQuestEvidence(questId: String, uri: String) {
        prefs.edit().putString("quest_evidence_${getTodayKey()}_$questId", uri).apply()
    }

    fun getQuestEvidence(questId: String): String? {
        return prefs.getString("quest_evidence_${getTodayKey()}_$questId", null)
    }

    // ==================== LOOT (Permanent - One time only) ====================

    fun isLootClaimed(lootId: String): Boolean {
        return prefs.getBoolean("loot_claimed_$lootId", false)
    }

    fun setLootClaimed(lootId: String) {
        val count = prefs.getInt("achieve_count_$lootId", 0)
        prefs.edit().apply {
            putBoolean("loot_claimed_$lootId", true)
            putInt("achieve_count_$lootId", count + 1)
            apply()
        }
    }

    fun getAchievementCount(lootId: String): Int {
        return prefs.getInt("achieve_count_$lootId", 0)
    }

    // ==================== DAILY RESET & HP/MP DRAIN ====================

    private fun checkDailyReset() {
        val today = getTodayKey()

        if (lastCheckedDay == today) {
            return
        }
        lastCheckedDay = today

        val lastResetDay = prefs.getString("last_reset_day", "") ?: ""

        if (lastResetDay != today) {
            val editor = prefs.edit()

            // Remove OLD quest data (from previous days)
            val allKeys = prefs.all
            for (key in allKeys.keys) {
                if ((key.startsWith("quest_done_") || key.startsWith("quest_evidence_")) && !key.contains(today)) {
                    editor.remove(key)
                }
            }

            // Apply HP/MP drain for the new day
            val heroId = loadSelectedHeroId()
            if (heroId != null && lastResetDay.isNotEmpty()) {
                val currentHp = loadHp(heroId)
                val currentMp = loadMp(heroId)
                editor.putInt("${heroId}_hp", (currentHp - 10).coerceAtLeast(0))
                editor.putInt("${heroId}_mp", (currentMp - 10).coerceAtLeast(0))
            }

            editor.putString("last_reset_day", today)
            editor.apply()
        }
    }

    // Call this from timer for hourly HP/MP drain (smaller drain)
    fun applyHourlyDrain() {
        val heroId = loadSelectedHeroId()
        if (heroId == null) return

        val currentHp = loadHp(heroId)
        val currentMp = loadMp(heroId)

        // Small hourly drain (2 HP/MP per hour)
        prefs.edit().apply {
            putInt("${heroId}_hp", (currentHp - 2).coerceAtLeast(0))
            putInt("${heroId}_mp", (currentMp - 2).coerceAtLeast(0))
            apply()
        }
    }

    // ==================== RESET ALL ====================

    fun resetAllProgress() {
        prefs.edit().clear().apply()
    }
}