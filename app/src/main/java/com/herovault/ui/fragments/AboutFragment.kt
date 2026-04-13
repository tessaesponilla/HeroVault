package com.herovault.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.herovault.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Professional UI: Animate sections to fade and slide in one after another
        animateEntrance()
    }

    private fun animateEntrance() {
        val container = binding.mainContainer
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            child.alpha = 0f
            child.translationY = 48f
            child.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay(i * 120L)
                .start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
