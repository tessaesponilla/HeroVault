package com.herovault.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.herovault.databinding.FragmentOnboardingBinding
import com.herovault.viewmodel.HeroViewModel

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HeroViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnDiscover.setOnClickListener {
            val pScore = when(binding.rgPhysical.checkedRadioButtonId) {
                binding.rbPhy3.id -> 3
                binding.rbPhy2.id -> 2
                binding.rbPhy1.id -> 1
                else -> 0
            }
            val mScore = when(binding.rgMental.checkedRadioButtonId) {
                binding.rbMen3.id -> 3
                binding.rbMen2.id -> 2
                binding.rbMen1.id -> 1
                else -> 0
            }
            val eScore = when(binding.rgEmotional.checkedRadioButtonId) {
                binding.rbEmo3.id -> 3
                binding.rbEmo2.id -> 2
                binding.rbEmo1.id -> 1
                else -> 0
            }

            if (pScore == 0 || mScore == 0 || eScore == 0) {
                Toast.makeText(context, "Please answer all questions to discover your hero.", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.discoverHero(pScore, mScore, eScore)
                // Navigation to Detail will be handled by MainActivity observer
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
