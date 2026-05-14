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
            // Question 1: Physical Energy (1=good, 3=poor)
            val pScore = when(binding.rgPhysical.checkedRadioButtonId) {
                binding.rbPhy3.id -> 3
                binding.rbPhy2.id -> 2
                binding.rbPhy1.id -> 1
                else -> 0
            }

            // Question 2: Mental Focus (1=good, 3=poor)
            val mScore = when(binding.rgMental.checkedRadioButtonId) {
                binding.rbMen3.id -> 3
                binding.rbMen2.id -> 2
                binding.rbMen1.id -> 1
                else -> 0
            }

            // Question 3: Emotional Response (1=calm, 3=overwhelmed)
            val eScore = when(binding.rgEmotional.checkedRadioButtonId) {
                binding.rbEmo3.id -> 3
                binding.rbEmo2.id -> 2
                binding.rbEmo1.id -> 1
                else -> 0
            }

            // Question 4: Social Connection (1=energized, 3=draining)
            val sScore = when(binding.rgSocial.checkedRadioButtonId) {
                binding.rbSoc3.id -> 3
                binding.rbSoc2.id -> 2
                binding.rbSoc1.id -> 1
                else -> 0
            }

            // Question 5: Sleep Quality (1=good, 3=poor)
            val sleepScore = when(binding.rgSleep.checkedRadioButtonId) {
                binding.rbSlp3.id -> 3
                binding.rbSlp2.id -> 2
                binding.rbSlp1.id -> 1
                else -> 0
            }

            // Question 6: Motivation (1=high, 3=low)
            val motScore = when(binding.rgMotivation.checkedRadioButtonId) {
                binding.rbMot3.id -> 3
                binding.rbMot2.id -> 2
                binding.rbMot1.id -> 1
                else -> 0
            }

            // Question 7: Physical Activity (1=regular, 3=rare)
            val actScore = when(binding.rgActivity.checkedRadioButtonId) {
                binding.rbAct3.id -> 3
                binding.rbAct2.id -> 2
                binding.rbAct1.id -> 1
                else -> 0
            }

            // Question 8: Stress Management (1=good, 3=poor)
            val stressScore = when(binding.rgStress.checkedRadioButtonId) {
                binding.rbStr3.id -> 3
                binding.rbStr2.id -> 2
                binding.rbStr1.id -> 1
                else -> 0
            }

            // Question 9: Routine Structure (1=structured, 3=flexible)
            val routineScore = when(binding.rgRoutine.checkedRadioButtonId) {
                binding.rbRou3.id -> 3
                binding.rbRou2.id -> 2
                binding.rbRou1.id -> 1
                else -> 0
            }

            // Question 10: Self-Care Priority (1=high, 3=low)
            val selfCareScore = when(binding.rgSelfCare.checkedRadioButtonId) {
                binding.rbCar3.id -> 3
                binding.rbCar2.id -> 2
                binding.rbCar1.id -> 1
                else -> 0
            }

            // Check if all 10 questions are answered
            if (pScore == 0 || mScore == 0 || eScore == 0 || sScore == 0 ||
                sleepScore == 0 || motScore == 0 || actScore == 0 || stressScore == 0 ||
                routineScore == 0 || selfCareScore == 0) {
                Toast.makeText(context, "Please answer all 10 questions to discover your hero.", Toast.LENGTH_LONG).show()
            } else {
                viewModel.discoverHero(
                    pScore = pScore,
                    mScore = mScore,
                    eScore = eScore,
                    sScore = sScore,
                    sleepScore = sleepScore,
                    motScore = motScore,
                    actScore = actScore,
                    stressScore = stressScore,
                    routineScore = routineScore,
                    selfCareScore = selfCareScore
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}