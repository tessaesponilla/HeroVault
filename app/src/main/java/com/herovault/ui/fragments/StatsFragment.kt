package com.herovault.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.herovault.databinding.FragmentStatsBinding
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

        // Observe the hero data to update the progress bars and text
        viewModel.selectedHero.observe(viewLifecycleOwner) { hero ->
            hero?.let {
                binding.textLevel.text = "Level ${it.level}"

                binding.progressHp.progress = it.hp
                binding.textHp.text = "HP: ${it.hp}/100"

                binding.progressMp.progress = it.mp
                binding.textMp.text = "MP: ${it.mp}/100"

                binding.progressXp.progress = it.xp
                binding.progressXp.max = it.level * 100
                binding.textXp.text = "XP: ${it.xp}/${it.level * 100}"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
