package com.herovault.data

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*

class PreferenceManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("herovault_prefs", Context.MODE_PRIVATE)
    
    // Cache the last reset hour to avoid redundant preference iterations
    private var lastCheckedHour: String = ""

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

    // --- Equipment Logic ---
    fun saveEquippedItems(heroId: String, itemIds: List<String>) {
        prefs.edit().putString("${heroId}_equipped", itemIds.joinToString(",")).apply()
    }

    fun loadEquippedItems(heroId: String): List<String> {
        val saved = prefs.getString("${heroId}_equipped", "") ?: ""
        return if (saved.isEmpty()) emptyList() else saved.split(",")
    }

    // --- Quest & Loot Logic ---

    fun isQuestCompleted(questId: String): Boolean {
        checkHourlyReset()
        return prefs.getBoolean("quest_done_$questId", false)
    }

    fun setQuestCompleted(questId: String) {
        prefs.edit().putBoolean("quest_done_$questId", true).apply()
    }

    fun saveQuestEvidence(questId: String, uri: String) {
        prefs.edit().putString("quest_evidence_$questId", uri).apply()
    }

    fun getQuestEvidence(questId: String): String? {
        return prefs.getString("quest_evidence_$questId", null)
    }

    fun isLootClaimed(lootId: String): Boolean {
        checkHourlyReset()
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

    private fun checkHourlyReset() {
        val currentHour = SimpleDateFormat("yyyyMMddHH", Locale.getDefault()).format(Date())
        
        // Skip if already checked this hour
        if (lastCheckedHour == currentHour) {
            return
        }
        lastCheckedHour = currentHour
        
        val lastReset = prefs.getString("last_reset_hour", "")

        if (lastReset != currentHour) {
            val editor = prefs.edit()
            
            if (lastReset != "") {
                val heroId = loadSelectedHeroId()
                if (heroId != null) {
                    val currentHp = loadHp(heroId)
                    val currentMp = loadMp(heroId)
                    editor.putInt("${heroId}_hp", (currentHp - 5).coerceAtLeast(0))
                    editor.putInt("${heroId}_mp", (currentMp - 5).coerceAtLeast(0))
                }
            }

            val allKeys = prefs.all
            for (key in allKeys.keys) {
                if (key.startsWith("quest_done_") || key.startsWith("loot_claimed_") || key.startsWith("quest_evidence_")) {
                    editor.remove(key)
                }
            }
            editor.putString("last_reset_hour", currentHour)
            editor.apply()
        }
    }

    fun resetAllProgress() {
        prefs.edit().clear().apply()
    }
}
