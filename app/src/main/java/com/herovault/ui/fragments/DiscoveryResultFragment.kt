package com.herovault.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.herovault.databinding.FragmentDiscoveryResultBinding
import com.herovault.viewmodel.HeroViewModel

class DiscoveryResultFragment : Fragment() {

    private var _binding: FragmentDiscoveryResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HeroViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiscoveryResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.selectedHero.observe(viewLifecycleOwner) { hero ->
            hero?.let {
                binding.imageHeroResult.setImageResource(it.portraitRes)
                binding.textHeroNameResult.text = it.name
                binding.textHeroPillarResult.text = "Primary Pillar: ${it.pillar}"
                binding.textHeroMeaning.text = it.psychologicalDescription
            }
        }

        binding.btnAcceptCompanion.setOnClickListener {
            // Assessment accepted, proceed to main hero view
            // Navigation is handled by MainActivity's observer or by replacing fragment
            (requireActivity() as? com.herovault.ui.MainActivity)?.showFragment(HeroDetailFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
