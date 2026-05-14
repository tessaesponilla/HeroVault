package com.herovault.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private lateinit var adapter: TabPagerAdapter

    // Store current tab position
    private var currentTabPosition = 0

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

        // Restore saved tab position if exists
        currentTabPosition = savedInstanceState?.getInt("selected_tab", 0) ?: 0

        setupViewPager()
        setupBottomNav()
        observeHero()
        observeEvents()
    }

    private fun setupViewPager() {
        // Create new adapter instance
        adapter = TabPagerAdapter(requireActivity())
        binding.viewPagerDetail.adapter = adapter
        binding.viewPagerDetail.isUserInputEnabled = false

        // Restore previous position
        binding.viewPagerDetail.currentItem = currentTabPosition
    }

    private fun setupBottomNav() {
        // Set the correct menu item checked based on current tab
        when (currentTabPosition) {
            0 -> binding.bottomNavigationDetail.menu.getItem(0).isChecked = true
            1 -> binding.bottomNavigationDetail.menu.getItem(1).isChecked = true
            2 -> binding.bottomNavigationDetail.menu.getItem(2).isChecked = true
        }

        binding.bottomNavigationDetail.setOnItemSelectedListener { item ->
            soundManager.playClick()
            when (item.itemId) {
                R.id.nav_stats -> {
                    binding.viewPagerDetail.currentItem = 0
                    currentTabPosition = 0
                    true
                }
                R.id.nav_quests -> {
                    binding.viewPagerDetail.currentItem = 1
                    currentTabPosition = 1
                    true
                }
                R.id.nav_vault -> {
                    binding.viewPagerDetail.currentItem = 2
                    currentTabPosition = 2
                    true
                }
                else -> false
            }
        }

        binding.viewPagerDetail.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentTabPosition = position
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

                // Update Evolution Title
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
        viewModel.levelUpEvent.observe(viewLifecycleOwner) { level ->
            if (level != null) {
                soundManager.playLevelUp()
                viewModel.resetEvents()
            }
        }

        viewModel.lootClaimedEvent.observe(viewLifecycleOwner) { lootId ->
            if (lootId != null) {
                soundManager.playLootClaimed()
                viewModel.resetEvents()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save current tab position
        outState.putInt("selected_tab", currentTabPosition)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}