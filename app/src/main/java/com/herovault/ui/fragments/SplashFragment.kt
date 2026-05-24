package com.herovault.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.herovault.databinding.FragmentSplashBinding
import com.herovault.viewmodel.HeroViewModel

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HeroViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Professional delay for Splash Screen (2 seconds)
        Handler(Looper.getMainLooper()).postDelayed({
            checkNavigation()
        }, 2000)
    }

    private fun checkNavigation() {
        if (!viewModel.isOnboardingCompleted()) {
            // First time user: Show psychological assessment
            parentFragmentManager.beginTransaction()
                .replace(com.herovault.R.id.fragment_container, OnboardingFragment())
                .commit()
        } else if (viewModel.selectedHero.value == null) {
            // User did quiz but somehow no hero? (Rare case)
            parentFragmentManager.beginTransaction()
                .replace(com.herovault.R.id.fragment_container, HeroListFragment())
                .commit()
        } else {
            // Returning user: Go straight to their health companion
            parentFragmentManager.beginTransaction()
                .replace(com.herovault.R.id.fragment_container, HeroDetailFragment())
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
