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

        viewModel.selectedHero.observe(viewLifecycleOwner) { hero ->
            hero?.let { h ->
                val maxHp = viewModel.maxHp.value ?: 100
                val maxMp = viewModel.maxMp.value ?: 100

                // Update Rank and Level (separate now)
                binding.textRank.text = h.getEvolutionTitle()  // "Novice", "Adept", etc.
                binding.textLevelNumber.text = "${h.level}"    // "1", "2", "3", etc.

                // Update HP Bar
                binding.progressHp.max = maxHp
                binding.progressHp.progress = h.hp
                binding.textHp.text = "HP: ${h.hp}/$maxHp"

                // Update MP Bar
                binding.progressMp.max = maxMp
                binding.progressMp.progress = h.mp
                binding.textMp.text = "MP: ${h.mp}/$maxMp"

                // Update XP Bar
                binding.progressXp.progress = h.xp
                binding.progressXp.max = h.level * 100
                binding.textXp.text = "XP: ${h.xp}/${h.level * 100}"

                // Update Skills Section
                updateSkills(h)
            }
        }
    }

    private fun updateSkills(hero: com.herovault.model.Hero) {
        binding.skillsContainer.removeAllViews()

        for (skill in hero.skills) {
            val isUnlocked = hero.level >= skill.unlockedAtLevel
            val card = ItemAchievementCardBinding.inflate(layoutInflater, binding.skillsContainer, false)

            card.achievementTitle.text = skill.name
            card.achievementDescription.text = skill.description
            card.achievementIcon.text = skill.icon

            if (isUnlocked) {
                card.achievementStatusIcon.setImageResource(R.drawable.ic_star)
                card.achievementStatusIcon.setColorFilter(Color.parseColor("#FFD700"))
            } else {
                card.achievementTitle.text = "🔒 ${skill.name} (Lv.${skill.unlockedAtLevel})"
                card.root.alpha = 0.5f
            }

            binding.skillsContainer.addView(card.root)
        }

        if (hero.skills.isEmpty()) {
            val emptyTv = TextView(requireContext()).apply {
                text = "No skills discovered yet."
                setPadding(0, 16, 0, 0)
            }
            binding.skillsContainer.addView(emptyTv)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}