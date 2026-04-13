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
import com.herovault.ui.adapter.TabPagerAdapter
import com.herovault.viewmodel.HeroViewModel

class HeroDetailFragment : Fragment() {

    private var _binding: FragmentHeroDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HeroViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHeroDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomNav()
        observeHero()
    }

    private fun setupBottomNav() {
        val adapter = TabPagerAdapter(requireActivity())
        binding.viewPagerDetail.adapter = adapter
        binding.viewPagerDetail.isUserInputEnabled = false

        binding.bottomNavigationDetail.setOnItemSelectedListener { item ->
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
                binding.heroPortraitDetail.setImageResource(it.portraitRes)
                binding.heroClassDetail.text = it.className
                binding.heroPillarDetail.text = it.pillar
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
