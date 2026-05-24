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

        // Use permanent tracking for achievements
        val completedQuests = hero.quests.filter { viewModel.hasEverCompletedQuest(it.id) }
        val claimedLoot = hero.lootTable.filter { viewModel.isLootClaimed(it.id) }
        val completedQuestsCount = completedQuests.size
        val claimedLootCount = claimedLoot.size

        // ✅ Fix progress bars
        val totalQuests = hero.quests.size
        val totalLoot = hero.lootTable.size

        binding.textQuestProgress.text = "$completedQuestsCount/$totalQuests"
        binding.progressQuests.max = totalQuests
        binding.progressQuests.progress = completedQuestsCount

        binding.textLootProgress.text = "$claimedLootCount/$totalLoot"
        binding.progressLoot.max = totalLoot
        binding.progressLoot.progress = claimedLootCount

        val totalItems = totalQuests + totalLoot
        val completedItems = completedQuestsCount + claimedLootCount
        binding.progressOverall.max = if (totalItems > 0) totalItems else 1
        binding.progressOverall.progress = completedItems

        // ===== RANK ACHIEVEMENTS =====
        addSectionHeader("🏆 Rank Achievements")
        addAchievementCard("Initiate", "Reach Level 1", "🌟", hero.level >= 1, 1, hero.level)
        addAchievementCard("Aspirant", "Reach Level 2", "⭐", hero.level >= 2, 2, hero.level)
        addAchievementCard("Practitioner", "Reach Level 3", "💫", hero.level >= 3, 3, hero.level)
        addAchievementCard("Steady", "Reach Level 4", "✨", hero.level >= 4, 4, hero.level)
        addAchievementCard("Adept", "Reach Level 5", "🌟", hero.level >= 5, 5, hero.level)
        addAchievementCard("Resilient", "Reach Level 6", "🛡️", hero.level >= 6, 6, hero.level)
        addAchievementCard("Disciplined", "Reach Level 7", "⚔️", hero.level >= 7, 7, hero.level)
        addAchievementCard("Skilled", "Reach Level 8", "🎯", hero.level >= 8, 8, hero.level)
        addAchievementCard("Vanguard", "Reach Level 9", "🛡️", hero.level >= 9, 9, hero.level)
        addAchievementCard("Elite", "Reach Level 10", "👑", hero.level >= 10, 10, hero.level)
        addAchievementCard("Expert", "Reach Level 11", "📚", hero.level >= 11, 11, hero.level)
        addAchievementCard("Superior", "Reach Level 15", "🏅", hero.level >= 15, 15, hero.level)
        addAchievementCard("Legendary", "Reach Level 20", "🏆", hero.level >= 20, 20, hero.level)
        addAchievementCard("Grandmaster", "Reach Level 25", "🔱", hero.level >= 25, 25, hero.level)
        addAchievementCard("Zen Master", "Reach Level 30", "☯️", hero.level >= 30, 30, hero.level)

        // ===== QUEST ACHIEVEMENTS =====
        addSectionHeader("📜 Quest Achievements")
        addAchievementCard("Quest Novice", "Complete 5 quests", "📖", completedQuestsCount >= 5, 5, completedQuestsCount)
        addAchievementCard("Quest Adept", "Complete 10 quests", "📚", completedQuestsCount >= 10, 10, completedQuestsCount)
        addAchievementCard("Quest Master", "Complete 20 quests", "🏅", completedQuestsCount >= 20, 20, completedQuestsCount)
        addAchievementCard("Quest Legend", "Complete 30 quests", "🏆", completedQuestsCount >= 30, 30, completedQuestsCount)

        // ===== LOOT ACHIEVEMENTS =====
        addSectionHeader("💎 Loot Achievements")
        addAchievementCard("Loot Collector", "Claim 5 loot items", "🔑", claimedLootCount >= 5, 5, claimedLootCount)
        addAchievementCard("Loot Hoarder", "Claim 10 loot items", "💰", claimedLootCount >= 10, 10, claimedLootCount)
        addAchievementCard("Loot Enthusiast", "Claim 20 loot items", "💎", claimedLootCount >= 20, 20, claimedLootCount)
        addAchievementCard("Loot Master", "Claim 30 loot items", "👑", claimedLootCount >= 30, 30, claimedLootCount)
        addAchievementCard("Loot Legend", "Claim 40 loot items", "🏆", claimedLootCount >= 40, 40, claimedLootCount)

        // ===== COMPLETED QUESTS (Ever done) =====
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

        // ===== CLAIMED LOOT =====
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