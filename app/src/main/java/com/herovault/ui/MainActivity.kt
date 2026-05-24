package com.herovault.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
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
    val viewModel: HeroViewModel by viewModels()
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDrawer()
        
        if (savedInstanceState == null) {
            showFragment(SplashFragment(), false)
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
                    val hero = viewModel.selectedHero.value
                    if (hero != null) {
                        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        showFragment(HeroDetailFragment())
                    } else {
                        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        showFragment(SplashFragment())
                    }
                }
                R.id.nav_loot -> showFragment(LootFragment(), true)
                R.id.nav_achievements -> showFragment(AchievementsFragment(), true)
                R.id.nav_about -> showFragment(AboutFragment(), true)
                R.id.nav_reset -> {
                    android.app.AlertDialog.Builder(this)
                        .setTitle("Reset Progress")
                        .setMessage("Are you sure? This will wipe all data and psychological history!")
                        .setPositiveButton("Yes") { _, _ ->
                            viewModel.resetAllProgress()
                            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                            showFragment(SplashFragment())
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
                             currentFragment is AchievementsFragment ||
                             currentFragment is AboutFragment ||
                             currentFragment is OnboardingFragment ||
                             currentFragment is SplashFragment ||
                             currentFragment is DiscoveryResultFragment
        
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

        val isDrawerLockedScreen = currentFragment is SplashFragment || 
                                   currentFragment is OnboardingFragment || 
                                   currentFragment is DiscoveryResultFragment
        
        if (isDrawerLockedScreen) {
            binding.drawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        } else {
            binding.drawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED)
        }

        toggle.syncState()
    }

    private fun observeHeroSelection() {
        viewModel.selectedHero.observe(this) { hero ->
            updateDrawerHeader(hero)
            if (hero != null) {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
                if (currentFragment !is HeroDetailFragment && 
                    currentFragment !is SplashFragment && 
                    currentFragment !is OnboardingFragment && 
                    currentFragment !is DiscoveryResultFragment) {
                    showFragment(HeroDetailFragment(), currentFragment !is OnboardingFragment)
                }
                supportActionBar?.title = "HeroVault"
            }
        }
    }

    private fun updateDrawerHeader(hero: com.herovault.model.Hero?) {
        val headerView = binding.navView.getHeaderView(0)
        val headerName = headerView.findViewById<TextView>(R.id.nav_header_name)
        val headerTitle = headerView.findViewById<TextView>(R.id.nav_header_title)
        val headerImage = headerView.findViewById<ImageView>(R.id.nav_header_image)

        val userName = viewModel.getUserName()
        headerName.text = if (hero != null) "$userName the ${hero.className}" else userName
        
        if (hero != null) {
            headerTitle.text = "${hero.getEvolutionTitle()} Rank | Lv.${hero.level}"
            headerImage.setImageResource(hero.portraitRes)
            headerImage.visibility = View.VISIBLE
        } else {
            headerTitle.text = "Select your Health Companion"
            headerImage.visibility = View.GONE
        }
    }

    fun showFragment(fragment: Fragment, addToBackStack: Boolean = false) {
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
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }
}