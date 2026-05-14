package com.herovault.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.herovault.R
import com.herovault.databinding.FragmentAchievementsBinding
import com.herovault.databinding.ItemAchievementCardBinding
import com.herovault.viewmodel.HeroViewModel

class AchievementsFragment : Fragment() {

    private var _binding: FragmentAchievementsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HeroViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAchievementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.selectedHero.observe(viewLifecycleOwner) { hero ->
            if (hero != null) {
                // Update header
                binding.textHeroName.text = hero.name
                binding.textHeroRank.text = hero.getEvolutionTitle()
                binding.imageHeroPortrait.setImageResource(hero.portraitRes)

                // Show achievements for current hero only
                updateAchievements(hero)
            } else {
                binding.textHeroName.text = "No Hero Selected"
                binding.textHeroRank.text = "Complete onboarding first"
                binding.achievementsContainer.removeAllViews()
                val emptyView = TextView(requireContext()).apply {
                    text = "No hero discovered yet."
                    textSize = 14f
                    setTextColor(Color.GRAY)
                    gravity = android.view.Gravity.CENTER
                    setPadding(0, 32, 0, 32)
                }
                binding.achievementsContainer.addView(emptyView)
            }
        }
    }

    private fun updateAchievements(hero: com.herovault.model.Hero) {
        binding.achievementsContainer.removeAllViews()

        // Get data for current hero
        val completedQuests = hero.quests.filter { viewModel.isQuestCompleted(it.id) }
        val claimedLoot = hero.lootTable.filter { viewModel.isLootClaimed(it.id) }
        val completedQuestsCount = completedQuests.size
        val claimedLootCount = claimedLoot.size

        // ===== RANK ACHIEVEMENTS (Based on Level) =====
        addSectionHeader("🏆 Rank Achievements")

        // Novice - Level 1 (always unlocked)
        addAchievementCard(
            title = "Novice",
            description = "Reach Level 1",
            icon = "🌟",
            isUnlocked = hero.level >= 1,
            requirement = 1,
            currentValue = hero.level
        )

        // Intermediate - Level 5
        addAchievementCard(
            title = "Intermediate",
            description = "Reach Level 5",
            icon = "⭐",
            isUnlocked = hero.level >= 5,
            requirement = 5,
            currentValue = hero.level
        )

        // Advanced - Level 10
        addAchievementCard(
            title = "Advanced",
            description = "Reach Level 10",
            icon = "💫",
            isUnlocked = hero.level >= 10,
            requirement = 10,
            currentValue = hero.level
        )

        // Adept - Level 15
        addAchievementCard(
            title = "Adept",
            description = "Reach Level 15",
            icon = "✨",
            isUnlocked = hero.level >= 15,
            requirement = 15,
            currentValue = hero.level
        )

        // Excellent - Level 20
        addAchievementCard(
            title = "Excellent",
            description = "Reach Level 20",
            icon = "👑",
            isUnlocked = hero.level >= 20,
            requirement = 20,
            currentValue = hero.level
        )

        // ===== QUEST ACHIEVEMENTS =====
        addSectionHeader("📜 Quest Achievements")

        // Quest Novice - 5 quests
        addAchievementCard(
            title = "Quest Novice",
            description = "Complete 5 quests",
            icon = "📖",
            isUnlocked = completedQuestsCount >= 5,
            requirement = 5,
            currentValue = completedQuestsCount
        )

        // Quest Adept - 10 quests
        addAchievementCard(
            title = "Quest Adept",
            description = "Complete 10 quests",
            icon = "📚",
            isUnlocked = completedQuestsCount >= 10,
            requirement = 10,
            currentValue = completedQuestsCount
        )

        // Quest Master - 20 quests
        addAchievementCard(
            title = "Quest Master",
            description = "Complete 20 quests",
            icon = "🏅",
            isUnlocked = completedQuestsCount >= 20,
            requirement = 20,
            currentValue = completedQuestsCount
        )

        // Quest Legend - 30 quests
        addAchievementCard(
            title = "Quest Legend",
            description = "Complete 30 quests",
            icon = "🏆",
            isUnlocked = completedQuestsCount >= 30,
            requirement = 30,
            currentValue = completedQuestsCount
        )

        // ===== LOOT ACHIEVEMENTS =====
        addSectionHeader("💎 Loot Achievements")

        // Loot Collector - 5 loots
        addAchievementCard(
            title = "Loot Collector",
            description = "Claim 5 loot items",
            icon = "🔑",
            isUnlocked = claimedLootCount >= 5,
            requirement = 5,
            currentValue = claimedLootCount
        )

        // Loot Hoarder - 10 loots
        addAchievementCard(
            title = "Loot Hoarder",
            description = "Claim 10 loot items",
            icon = "💰",
            isUnlocked = claimedLootCount >= 10,
            requirement = 10,
            currentValue = claimedLootCount
        )

        // Loot Enthusiast - 20 loots
        addAchievementCard(
            title = "Loot Enthusiast",
            description = "Claim 20 loot items",
            icon = "💎",
            isUnlocked = claimedLootCount >= 20,
            requirement = 20,
            currentValue = claimedLootCount
        )

        // Loot Master - 30 loots
        addAchievementCard(
            title = "Loot Master",
            description = "Claim 30 loot items",
            icon = "👑",
            isUnlocked = claimedLootCount >= 30,
            requirement = 30,
            currentValue = claimedLootCount
        )

        // Loot Legend - 40 loots
        addAchievementCard(
            title = "Loot Legend",
            description = "Claim 40 loot items",
            icon = "🏆",
            isUnlocked = claimedLootCount >= 40,
            requirement = 40,
            currentValue = claimedLootCount
        )

        // ===== Completed Quests Section (Actual earned) =====
        if (completedQuests.isNotEmpty()) {
            addSectionHeader("✓ Completed Quests")
            completedQuests.forEach { quest ->
                val card = ItemAchievementCardBinding.inflate(layoutInflater, binding.achievementsContainer, false)
                card.achievementTitle.text = quest.title
                card.achievementDescription.text = quest.description
                card.achievementIcon.text = "✓"
                card.achievementStatusIcon.setImageResource(R.drawable.ic_star)
                card.achievementStatusIcon.setColorFilter(Color.parseColor("#FFD700"))
                binding.achievementsContainer.addView(card.root)
            }
        }

        // ===== Claimed Loot Section (Actual earned) =====
        if (claimedLoot.isNotEmpty()) {
            addSectionHeader("📦 Claimed Loot")
            claimedLoot.forEach { loot ->
                val card = ItemAchievementCardBinding.inflate(layoutInflater, binding.achievementsContainer, false)
                card.achievementTitle.text = loot.name
                card.achievementDescription.text = "${loot.icon} +${loot.hpBonus} HP / +${loot.mpBonus} MP"
                card.achievementIcon.text = loot.icon
                card.achievementStatusIcon.setImageResource(R.drawable.ic_star)
                card.achievementStatusIcon.setColorFilter(Color.parseColor("#FFD700"))
                binding.achievementsContainer.addView(card.root)
            }
        }
    }

    private fun addSectionHeader(title: String) {
        val header = TextView(requireContext()).apply {
            text = title
            textSize = 16f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setTextColor(Color.parseColor("#FFD700"))
            setPadding(16, 24, 16, 8)
        }
        binding.achievementsContainer.addView(header)
    }

    private fun addAchievementCard(title: String, description: String, icon: String, isUnlocked: Boolean, requirement: Int, currentValue: Int) {
        val card = ItemAchievementCardBinding.inflate(layoutInflater, binding.achievementsContainer, false)

        if (isUnlocked) {
            // Unlocked achievement
            card.achievementTitle.text = title
            card.achievementDescription.text = description
            card.achievementIcon.text = icon
            card.achievementStatusIcon.setImageResource(R.drawable.ic_star)
            card.achievementStatusIcon.setColorFilter(Color.parseColor("#FFD700"))
            card.root.alpha = 1.0f
        } else {
            // Locked achievement - show progress
            card.achievementTitle.text = "🔒 $title"
            card.achievementDescription.text = "$description ($currentValue/$requirement)"
            card.achievementIcon.text = "❓"
            card.achievementStatusIcon.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            card.achievementStatusIcon.setColorFilter(Color.GRAY)
            card.root.alpha = 0.6f
        }

        binding.achievementsContainer.addView(card.root)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}