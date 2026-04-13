package com.herovault.data

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*

class PreferenceManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("herovault_prefs", Context.MODE_PRIVATE)

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
    fun loadSelectedHeroId(): String = prefs.getString("selected_hero", "knight") ?: "knight"

    // --- Quest & Loot Logic ---

    fun isQuestCompleted(questId: String): Boolean {
        checkHourlyReset()
        return prefs.getBoolean("quest_done_$questId", false)
    }

    fun setQuestCompleted(questId: String) {
        prefs.edit().putBoolean("quest_done_$questId", true).apply()
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
        val lastReset = prefs.getString("last_reset_hour", "")
        val currentHour = SimpleDateFormat("yyyyMMddHH", Locale.getDefault()).format(Date())

        if (lastReset != currentHour) {
            val editor = prefs.edit()
            
            // --- Apply Hourly Drain (Survival Mode) ---
            // If it's a new hour, decrease HP and MP for the selected hero
            if (lastReset != "") {
                val heroId = loadSelectedHeroId()
                val currentHp = loadHp(heroId)
                val currentMp = loadMp(heroId)
                
                // Subtract 5 points, minimum 0
                editor.putInt("${heroId}_hp", (currentHp - 5).coerceAtLeast(0))
                editor.putInt("${heroId}_mp", (currentMp - 5).coerceAtLeast(0))
            }

            // Reset hourly quest/claim flags
            val allKeys = prefs.all
            for (key in allKeys.keys) {
                if (key.startsWith("quest_done_") || key.startsWith("loot_claimed_")) {
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