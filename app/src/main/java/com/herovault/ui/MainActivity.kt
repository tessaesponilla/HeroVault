package com.herovault.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.herovault.R
import com.herovault.databinding.ActivityMainBinding
import com.herovault.ui.fragments.*
import com.herovault.viewmodel.HeroViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: HeroViewModel by viewModels()
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDrawer()
        
        if (savedInstanceState == null) {
            // Professor's Suggestion: Show Onboarding/Psych Form first if not completed
            if (!viewModel.isOnboardingCompleted()) {
                showFragment(OnboardingFragment())
            } else if (viewModel.selectedHero.value == null) {
                showFragment(HeroListFragment())
            }
            // If hero is already selected, observeHeroSelection() will handle the Detail fragment
        }

        observeHeroSelection()

        supportFragmentManager.addOnBackStackChangedListener {
            updateNavIcon()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupDrawer() {
        toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.nav_open, R.string.nav_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    viewModel.clearSelectedHero()
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    showFragment(HeroListFragment())
                }
                R.id.nav_loot -> showFragment(LootFragment(), true)
                R.id.nav_overall_loot -> showFragment(OverallLootFragment(), true)
                R.id.nav_achievements -> showFragment(AchievementsFragment(), true)
                R.id.nav_about -> showFragment(AboutFragment(), true)
                R.id.nav_reset -> {
                    android.app.AlertDialog.Builder(this)
                        .setTitle("Reset Progress")
                        .setMessage("Are you sure? This will wipe all loot and levels!")
                        .setPositiveButton("Yes") { _, _ ->
                            viewModel.resetAllProgress()
                            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                            showFragment(OnboardingFragment())
                        }
                        .setNegativeButton("No", null)
                        .show()
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun updateNavIcon() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        
        val shouldShowBack = currentFragment is HeroListFragment || 
                             currentFragment is LootFragment || 
                             currentFragment is OverallLootFragment ||
                             currentFragment is AchievementsFragment ||
                             currentFragment is AboutFragment ||
                             currentFragment is OnboardingFragment
        
        if (!shouldShowBack && currentFragment != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            toggle.isDrawerIndicatorEnabled = true
            binding.toolbar.setNavigationOnClickListener {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        } else {
            toggle.isDrawerIndicatorEnabled = false
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            binding.toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
        toggle.syncState()
    }

    private fun observeHeroSelection() {
        viewModel.selectedHero.observe(this) { hero ->
            if (hero != null) {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
                // If we select a hero (e.g. from the quiz or list), move to Detail
                if (currentFragment !is HeroDetailFragment) {
                    // Use a clean transaction (don't add onboarding to backstack if we just finished it)
                    if (currentFragment is OnboardingFragment) {
                        showFragment(HeroDetailFragment(), false)
                    } else {
                        showFragment(HeroDetailFragment(), true)
                    }
                }
                supportActionBar?.title = "${hero.name} — Lv.${hero.level}"
            } else {
                supportActionBar?.title = "HeroVault"
            }
        }
    }

    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = false) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
        
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        
        transaction.commit()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}