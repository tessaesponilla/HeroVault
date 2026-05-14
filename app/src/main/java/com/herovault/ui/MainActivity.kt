package com.herovault.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
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
        setupNavHeaderClick()

        // Only show initial fragment if there's no saved state
        if (savedInstanceState == null) {
            if (!viewModel.isOnboardingCompleted()) {
                showFragment(OnboardingFragment())
            } else if (viewModel.selectedHero.value == null) {
                showFragment(HeroListFragment())
            }
        }

        observeHeroSelection()

        supportFragmentManager.addOnBackStackChangedListener {
            updateNavIcon()
            updateToolbarAndDrawerVisibility()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "HeroVault"
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
                R.id.nav_loot -> {
                    if (viewModel.selectedHero.value != null) {
                        showFragment(LootFragment(), true)
                    }
                }
                R.id.nav_achievements -> {
                    if (viewModel.selectedHero.value != null) {
                        showFragment(AchievementsFragment(), true)
                    }
                }
                R.id.nav_about -> {
                    showFragment(AboutFragment(), true)
                }
                R.id.nav_reset -> {
                    android.app.AlertDialog.Builder(this)
                        .setTitle("Reset Progress")
                        .setMessage("Are you sure? This will wipe all progress for your hero!")
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

    // Add this new function
    private fun setupNavHeaderClick() {
        val headerView = binding.navView.getHeaderView(0)
        headerView.setOnClickListener {
            // Close drawer first
            binding.drawerLayout.closeDrawer(GravityCompat.START)

            // Delay slightly to let drawer close animation finish
            binding.drawerLayout.postDelayed({
                val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
                if (currentFragment !is HeroDetailFragment) {
                    // Clear back stack and go to hero detail
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    showFragment(HeroDetailFragment())
                }
            }, 200)
        }
    }

    private fun updateToolbarAndDrawerVisibility() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment is OnboardingFragment) {
            supportActionBar?.hide()
            binding.toolbar.visibility = android.view.View.GONE
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            toggle.isDrawerIndicatorEnabled = false
        } else {
            supportActionBar?.show()
            binding.toolbar.visibility = android.view.View.VISIBLE
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            toggle.isDrawerIndicatorEnabled = true
        }
    }

    private fun updateNavIcon() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment is OnboardingFragment) {
            return
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        toggle.isDrawerIndicatorEnabled = true
        binding.toolbar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        toggle.syncState()
    }

    private fun observeHeroSelection() {
        viewModel.selectedHero.observe(this) { hero ->
            if (hero != null) {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
                if (currentFragment !is HeroDetailFragment && currentFragment !is LootFragment && currentFragment !is AchievementsFragment && currentFragment !is AboutFragment) {
                    if (currentFragment is OnboardingFragment) {
                        showFragment(HeroDetailFragment(), false)
                    } else if (currentFragment !is AboutFragment) {
                        showFragment(HeroDetailFragment(), true)
                    }
                }
                supportActionBar?.title = "HeroVault"
                updateNavHeader(hero)
            } else {
                supportActionBar?.title = "HeroVault"
            }
        }
    }

    private fun updateNavHeader(hero: com.herovault.model.Hero) {
        try {
            val headerView = binding.navView.getHeaderView(0)
            val heroImage = headerView.findViewById<android.widget.ImageView>(R.id.nav_header_image)
            val heroName = headerView.findViewById<android.widget.TextView>(R.id.nav_header_name)
            val heroTitle = headerView.findViewById<android.widget.TextView>(R.id.nav_header_title)

            heroImage?.setImageResource(hero.portraitRes)
            heroName?.text = hero.name
            heroTitle?.text = "${hero.getEvolutionTitle()} - Level ${hero.level}"
        } catch (e: Exception) {
            // Header views might not exist yet
        }
    }

    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = false) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)

        if (addToBackStack) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
        supportFragmentManager.executePendingTransactions()
        updateToolbarAndDrawerVisibility()
        updateNavIcon()
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment is OnboardingFragment) {
            return
        }

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if (currentFragment is LootFragment || currentFragment is AchievementsFragment || currentFragment is AboutFragment) {
                supportFragmentManager.popBackStack()
            } else {
                super.onBackPressed()
            }
        }
    }
}