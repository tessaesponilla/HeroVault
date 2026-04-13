package com.herovault.ui.fragments

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.herovault.R
import com.herovault.databinding.FragmentHeroDetailBinding
import com.herovault.ui.SoundManager
import com.herovault.ui.adapter.TabPagerAdapter
import com.herovault.viewmodel.HeroViewModel

class HeroDetailFragment : Fragment() {

    private var _binding: FragmentHeroDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HeroViewModel by activityViewModels()
    private lateinit var soundManager: SoundManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHeroDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        soundManager = SoundManager(requireContext())

        setupBottomNav()
        observeHero()
        observeEvents()
    }

    private fun setupBottomNav() {
        val adapter = TabPagerAdapter(requireActivity())
        binding.viewPagerDetail.adapter = adapter
        binding.viewPagerDetail.isUserInputEnabled = false

        binding.bottomNavigationDetail.setOnItemSelectedListener { item ->
            soundManager.playClick()
            when (item.itemId) {
                R.id.nav_stats -> {
                    binding.viewPagerDetail.currentItem = 0
                    true
                }
                R.id.nav_quests -> {
                    binding.viewPagerDetail.currentItem = 1
                    true
                }
                R.id.nav_vault -> {
                    binding.viewPagerDetail.currentItem = 2
                    true
                }
                else -> false
            }
        }

        binding.viewPagerDetail.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.bottomNavigationDetail.menu.getItem(position).isChecked = true
            }
        })
    }

    private fun observeHero() {
        viewModel.selectedHero.observe(viewLifecycleOwner) { hero ->
            hero?.let {
                // Update Basic Info
                binding.heroPortraitDetail.setImageResource(it.portraitRes)
                binding.heroClassDetail.text = it.className
                binding.heroPillarDetail.text = it.pillar

                // Update Psychological Meaning
                binding.heroLoreDescription.text = it.psychologicalDescription

                // Update Evolution Title (e.g., Novice -> Adept -> Excellent)
                binding.heroEvolutionTitleDetail.text = it.getEvolutionTitle()

                // Apply Dynamic Border based on Level
                val borderRes = it.getBorderResource()
                if (borderRes != 0) {
                    binding.portraitContainer.setBackgroundResource(borderRes)
                } else {
                    binding.portraitContainer.background = null
                }


            }
        }
    }

    private fun observeEvents() {
        // Observe Level Up Event
        viewModel.levelUpEvent.observe(viewLifecycleOwner) { level ->
            if (level != null) {
                soundManager.playLevelUp()

                // Level Up animation removed - Lottie confetti no longer available
                // You can show a simple Toast or Snackbar instead
                Toast.makeText(context, "LEVEL UP! Reach Level $level", Toast.LENGTH_LONG).show()
                viewModel.resetEvents()
            }
        }

        // Observe Loot Claimed Event
        viewModel.lootClaimedEvent.observe(viewLifecycleOwner) { lootId ->
            if (lootId != null) {
                soundManager.playLootClaimed()

                // Loot claimed animation removed - Lottie glow no longer available
                Toast.makeText(context, "Loot Claimed!", Toast.LENGTH_SHORT).show()

                viewModel.resetEvents()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}