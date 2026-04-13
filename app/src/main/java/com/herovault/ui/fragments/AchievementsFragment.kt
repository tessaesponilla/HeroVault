package com.herovault.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        viewModel.selectedHero.observe(viewLifecycleOwner) {
            updateAchievements()
        }
    }

    private fun updateAchievements() {
        binding.achievementContainer.removeAllViews()

        // --- 1. Hero Levels Section ---
        addSectionHeader("Hero Ranks")
        for (hero in viewModel.heroList) {
            val level = viewModel.getHeroLevel(hero.id)
            addHeroLevelStat(hero.name, level)
        }

        // --- 2. Collection Progress Section ---
        val totalClaimed = viewModel.getTotalLootClaimed()
        addSectionHeader("Collection Milestones")
        val milestones = listOf(10, 20, 40, 60, 80, 100)
        for (target in milestones) {
            val isAchieved = totalClaimed >= target
            addMilestoneCard(
                "Collector Rank $target",
                "Claim $target items from the Vault ($totalClaimed/$target)",
                if (isAchieved) "🎖️" else "🔒",
                isAchieved
            )
        }

        // --- 3. Hero Mastery Section ---
        addSectionHeader("Hero Milestones")
        for (hero in viewModel.heroList) {
            val level = viewModel.getHeroLevel(hero.id)
            val isAchieved = level >= 5
            addMilestoneCard(
                "Master ${hero.name}",
                "Reach Level 5 with ${hero.name}",
                if (isAchieved) "⭐" else "🔒",
                isAchieved
            )
        }
    }

    private fun addSectionHeader(title: String) {
        val header = android.widget.TextView(requireContext()).apply {
            text = title
            textSize = 18f
            setPadding(0, 32, 0, 16)
            setTextColor(android.graphics.Color.BLACK)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        binding.achievementContainer.addView(header)
    }

    private fun addHeroLevelStat(heroName: String, level: Int) {
        val card = ItemAchievementCardBinding.inflate(layoutInflater, binding.achievementContainer, false)
        card.achievementTitle.text = heroName
        card.achievementDescription.text = "Current Level: $level"
        card.achievementIcon.text = "⚜️"
        card.achievementStatusIcon.setColorFilter(android.graphics.Color.parseColor("#FFD700"))
        binding.achievementContainer.addView(card.root)
    }

    private fun addMilestoneCard(title: String, desc: String, icon: String, isAchieved: Boolean) {
        val card = ItemAchievementCardBinding.inflate(layoutInflater, binding.achievementContainer, false)
        card.achievementTitle.text = title
        card.achievementDescription.text = desc
        card.achievementIcon.text = icon
        if (isAchieved) {
            card.achievementStatusIcon.setColorFilter(android.graphics.Color.parseColor("#FFD700"))
        } else {
            card.root.alpha = 0.6f
        }
        binding.achievementContainer.addView(card.root)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
