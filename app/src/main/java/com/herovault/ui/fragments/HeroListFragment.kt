package com.herovault.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.herovault.databinding.FragmentHeroListBinding
import com.herovault.ui.adapter.HeroAdapter
import com.herovault.viewmodel.HeroViewModel

class HeroListFragment : Fragment() {

    private var _binding: FragmentHeroListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HeroViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHeroListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = HeroAdapter(viewModel.heroList) { hero ->
            viewModel.selectHero(hero.id)
            // Navigation logic will be handled by MainActivity observing the selection
        }

        binding.heroRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.heroRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
