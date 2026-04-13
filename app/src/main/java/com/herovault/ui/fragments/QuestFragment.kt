package com.herovault.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.herovault.R
import com.herovault.model.Quest
import com.herovault.viewmodel.HeroViewModel

class QuestFragment : Fragment() {

    private val viewModel: HeroViewModel by activityViewModels()
    private lateinit var resetTimerText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootContainer = android.widget.ScrollView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setPadding(48, 48, 48, 48)
        }

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        rootContainer.addView(container)

        // Reset Timer Header
        resetTimerText = TextView(requireContext()).apply {
            textSize = 14f
            setTextColor(android.graphics.Color.GRAY)
            setPadding(0, 0, 0, 16)
        }
        container.addView(resetTimerText)

        viewModel.minutesRemaining.observe(viewLifecycleOwner) { minutes ->
            resetTimerText.text = "⏳ Quests reset in $minutes minutes"
        }

        // Observe hero changes and rebuild quest cards dynamically
        viewModel.selectedHero.observe(viewLifecycleOwner) { hero ->
            hero?.let { selectedHero ->
                // Clear existing quest cards (keep the timer)
                val viewsToRemove = mutableListOf<View>()
                for (i in 1 until container.childCount) {
                    viewsToRemove.add(container.getChildAt(i))
                }
                viewsToRemove.forEach { container.removeView(it) }

                // Header
                val header = TextView(requireContext()).apply {
                    text = "Quest Log — ${selectedHero.className}"
                    textSize = 20f
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    setPadding(0, 0, 0, 40)
                }
                container.addView(header)

                // Build a card for each quest
                selectedHero.quests.forEach { quest ->
                    container.addView(buildQuestCard(quest))
                }
            }
        }

        return rootContainer
    }

    private fun buildQuestCard(quest: Quest): CardView {
        val context = requireContext()

        val card = CardView(context).apply {
            radius = 24f
            cardElevation = 8f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 40 }
        }

        val inner = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        val title = TextView(context).apply {
            text = quest.title
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val desc = TextView(context).apply {
            text = quest.description
            textSize = 13f
            setTextColor(android.graphics.Color.GRAY)
            setPadding(0, 8, 0, 8)
        }

        val rewards = TextView(context).apply {
            text = "+${quest.hpGain} HP   +${quest.mpGain} MP   +${quest.xpGain} XP"
            textSize = 13f
            setTextColor(android.graphics.Color.parseColor("#4CAF50"))
            setPadding(0, 0, 0, 20)
        }

        val isDone = viewModel.isQuestCompleted(quest.id)

        val button = Button(context).apply {
            text = if (isDone) "Completed!" else "Complete Quest"
            isEnabled = !isDone
            setOnClickListener {
                viewModel.completeQuest(quest.id, quest.hpGain, quest.mpGain, quest.xpGain)
                isEnabled = false
                text = "Completed!"
            }
        }

        inner.addView(title)
        inner.addView(desc)
        inner.addView(rewards)
        inner.addView(button)
        card.addView(inner)

        return card
    }
}
