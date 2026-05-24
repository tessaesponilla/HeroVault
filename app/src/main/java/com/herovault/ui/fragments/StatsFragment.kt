package com.herovault.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.herovault.R
import com.herovault.databinding.FragmentStatsBinding
import com.herovault.databinding.ItemAchievementCardBinding
import com.herovault.viewmodel.HeroViewModel

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HeroViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMoodButtons()

        viewModel.selectedHero.observe(viewLifecycleOwner) { hero ->
            hero?.let { h ->
                val maxHp = viewModel.maxHp.value ?: 100
                val maxMp = viewModel.maxMp.value ?: 100

                // Update Progress Bars
                binding.progressHp.max = maxHp
                binding.progressHp.progress = h.hp
                binding.textHp.text = "HP: ${h.hp}/$maxHp"

                binding.progressMp.max = maxMp
                binding.progressMp.progress = h.mp
                binding.textMp.text = "MP: ${h.mp}/$maxMp"

                binding.progressXp.max = h.level * 100
                binding.progressXp.progress = h.xp
                binding.textXp.text = "XP: ${h.xp}/${h.level * 100}"

                // Update Professional Ranks
                binding.textRank.text = h.getEvolutionTitle()
                binding.textLevelNumber.text = "${h.level}"

                // Clinical Insight Logic (Addressing Professor's Suggestion)
                binding.textClinicalInsight.text = viewModel.getPsychologicalInsight()

                // Display Today's Mood if set
                val mood = viewModel.getTodayMood()
                if (mood != null) {
                    binding.textCurrentMood.visibility = View.VISIBLE
                    binding.textCurrentMood.text = "You feel $mood today."
                    binding.layoutMoodOptions.visibility = View.GONE
                } else {
                    binding.textCurrentMood.visibility = View.GONE
                    binding.layoutMoodOptions.visibility = View.VISIBLE
                }

                updateSkills(h)
            }
        }
    }

    private fun setupMoodButtons() {
        binding.btnMoodHappy.setOnClickListener { viewModel.saveDailyMood("Happy") }
        binding.btnMoodAnxious.setOnClickListener { viewModel.saveDailyMood("Anxious") }
        binding.btnMoodTired.setOnClickListener { viewModel.saveDailyMood("Tired") }
        binding.btnMoodStressed.setOnClickListener { viewModel.saveDailyMood("Stressed") }
    }

    private fun updateSkills(hero: com.herovault.model.Hero) {
        binding.skillsContainer.removeAllViews()
        for (skill in hero.skills) {
            val isUnlocked = hero.level >= skill.unlockedAtLevel
            val card = ItemAchievementCardBinding.inflate(layoutInflater, binding.skillsContainer, false)
            card.achievementTitle.text = if (isUnlocked) skill.name else "🔒 ${skill.name} (Lv.${skill.unlockedAtLevel})"
            card.achievementDescription.text = skill.description
            card.achievementIcon.text = skill.icon
            card.root.alpha = if (isUnlocked) 1.0f else 0.5f
            binding.skillsContainer.addView(card.root)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
