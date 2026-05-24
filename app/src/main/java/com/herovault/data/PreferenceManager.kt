package com.herovault.data

import android.content.Context
import android.content.SharedPreferences
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PreferenceManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("herovault_prefs", Context.MODE_PRIVATE)

    fun saveUserName(name: String) = prefs.edit().putString("user_name", name).apply()
    fun getUserName(): String = prefs.getString("user_name", "Hero") ?: "Hero"

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

    // --- Mood & Reflection Logic ---
    fun saveDailyMood(mood: String) {
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        prefs.edit().putString("mood_$today", mood).apply()
        prefs.edit().putString("last_mood_date", today).apply()
    }

    fun getTodayMood(): String? {
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        return prefs.getString("mood_$today", null)
    }

    // --- Analytics Logic ---
    fun trackQuestCategory(category: String) {
        val count = prefs.getInt("stats_category_$category", 0)
        prefs.edit().putInt("stats_category_$category", count + 1).apply()
    }

    fun getCategoryCount(category: String): Int {
        return prefs.getInt("stats_category_$category", 0)
    }

    // --- Equipment Logic ---
    fun saveEquippedItems(heroId: String, itemIds: List<String>) {
        prefs.edit().putString("${heroId}_equipped", itemIds.joinToString(",")).apply()
    }

    fun loadEquippedItems(heroId: String): List<String> {
        val saved = prefs.getString("${heroId}_equipped", "") ?: ""
        return if (saved.isEmpty()) emptyList() else saved.split(",")
    }

    // --- Core Survival Logic (Persistent Hourly Reset) ---
    fun applyHourlyDrain(hpPenalty: Int = 5, mpPenalty: Int = 5) {
        val lastReset = prefs.getString("last_reset_hour", "")
        val currentHour = SimpleDateFormat("yyyyMMddHH", Locale.getDefault()).format(Date())

        if (lastReset != currentHour) {
            val editor = prefs.edit()

            if (lastReset != "") {
                val heroId = loadSelectedHeroId()
                if (heroId != null) {
                    val currentHp = loadHp(heroId)
                    val currentMp = loadMp(heroId)
                    editor.putInt("${heroId}_hp", (currentHp - hpPenalty).coerceAtLeast(0))
                    editor.putInt("${heroId}_mp", (currentMp - mpPenalty).coerceAtLeast(0))
                }

                val allKeys = prefs.all
                for (key in allKeys.keys) {
                    if (key.startsWith("quest_evidence_")) {
                        val path = prefs.getString(key, null)
                        if (path != null) {
                            try { File(path).delete() } catch (e: Exception) {}
                        }
                        editor.remove(key)
                    } else if (key.startsWith("quest_done_") || key.startsWith("loot_claimed_")) {
                        editor.remove(key)
                    }
                }
            }

            editor.putString("last_reset_hour", currentHour)
            editor.commit()
        }
    }

    fun isQuestCompleted(questId: String): Boolean {
        applyHourlyDrain()
        return prefs.getBoolean("quest_done_$questId", false)
    }

    fun setQuestCompleted(questId: String) {
        applyHourlyDrain()
        prefs.edit()
            .putBoolean("quest_done_$questId", true)
            .putBoolean("quest_ever_done_$questId", true)
            .commit()
    }

    fun saveQuestEvidence(questId: String, uri: String) {
        prefs.edit().putString("quest_evidence_$questId", uri).commit()
    }

    fun getQuestEvidence(questId: String): String? {
        return prefs.getString("quest_evidence_$questId", null)
    }

    fun isLootClaimed(lootId: String): Boolean {
        applyHourlyDrain()
        return prefs.getBoolean("loot_claimed_$lootId", false)
    }

    fun setLootClaimed(lootId: String) {
        applyHourlyDrain()
        val count = prefs.getInt("achieve_count_$lootId", 0)
        prefs.edit().apply {
            putBoolean("loot_claimed_$lootId", true)
            putInt("achieve_count_$lootId", count + 1)
            commit()
        }
    }
    fun hasEverCompletedQuest(questId: String): Boolean {
        return prefs.getBoolean("quest_ever_done_$questId", false)
    }

    fun getTotalEverCompletedQuests(heroId: String): Int {
        return prefs.all.keys.count {
            it.startsWith("quest_ever_done_") && prefs.getBoolean(it, false)
        }
    }

    fun getAchievementCount(lootId: String): Int = prefs.getInt("achieve_count_$lootId", 0)

    fun resetAllProgress() {
        prefs.edit().clear().commit()
    }
}