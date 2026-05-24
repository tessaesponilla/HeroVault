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
            val userName = binding.etUserName.text?.toString()?.trim()
            
            val p1 = getScore(binding.rgPhyEnergy.checkedRadioButtonId, binding.rbPhyE1.id, binding.rbPhyE2.id, binding.rbPhyE3.id)
            val m1 = getScore(binding.rgMenFocus.checkedRadioButtonId, binding.rbMenF1.id, binding.rbMenF2.id, binding.rbMenF3.id)
            val e1 = getScore(binding.rgEmoResp.checkedRadioButtonId, binding.rbEmoR1.id, binding.rbEmoR2.id, binding.rbEmoR3.id)
            val s1 = getScore(binding.rgSocial.checkedRadioButtonId, binding.rbSoc1.id, binding.rbSoc2.id, binding.rbSoc3.id)
            val slp = getScore(binding.rgSleep.checkedRadioButtonId, binding.rbSlp1.id, binding.rbSlp2.id, binding.rbSlp3.id)
            val mot = getScore(binding.rgMotivation.checkedRadioButtonId, binding.rbMot1.id, binding.rbMot2.id, binding.rbMot3.id)
            val act = getScore(binding.rgActivity.checkedRadioButtonId, binding.rbAct1.id, binding.rbAct2.id, binding.rbAct3.id)
            val str = getScore(binding.rgStress.checkedRadioButtonId, binding.rbStr1.id, binding.rbStr2.id, binding.rbStr3.id)
            val rou = getScore(binding.rgRoutine.checkedRadioButtonId, binding.rbRou1.id, binding.rbRou2.id, binding.rbRou3.id)
            val car = getScore(binding.rgSelfCare.checkedRadioButtonId, binding.rbCar1.id, binding.rbCar2.id, binding.rbCar3.id)

            if (userName.isNullOrEmpty()) {
                Toast.makeText(context, "Please enter your name.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val scores = listOf(p1, m1, e1, s1, slp, mot, act, str, rou, car)
            if (scores.any { it == 0 }) {
                Toast.makeText(context, "Please answer all questions.", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.saveUserName(userName)
                viewModel.discoverHero(p1, m1, e1, s1, slp, mot, act, str, rou, car)
                parentFragmentManager.beginTransaction()
                    .replace(com.herovault.R.id.fragment_container, DiscoveryResultFragment())
                    .commit()
            }
        }
    }

    private fun getScore(checkedId: Int, id1: Int, id2: Int, id3: Int): Int {
        return when (checkedId) {
            id1 -> 1
            id2 -> 2
            id3 -> 3
            else -> 0
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
