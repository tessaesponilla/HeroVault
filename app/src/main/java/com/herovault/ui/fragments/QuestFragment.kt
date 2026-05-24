package com.herovault.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.herovault.R
import com.herovault.model.Quest
import com.herovault.ui.SoundManager
import com.herovault.viewmodel.HeroViewModel
import java.io.File
import java.io.FileOutputStream

class QuestFragment : Fragment() {

    private val viewModel: HeroViewModel by activityViewModels()
    private lateinit var resetTimerText: TextView
    private var soundManager: SoundManager? = null
    private var pendingQuestId: String? = null
    private lateinit var linearContainer: LinearLayout

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val questId = pendingQuestId
            val hero = viewModel.selectedHero.value
            if (questId != null && hero != null) {
                val quest = hero.quests.find { it.id == questId }
                quest?.let { q ->
                    // Copy image to internal storage so it persists forever
                    val savedPath = saveImageToInternalStorage(uri)
                    if (savedPath != null) {
                        soundManager?.playLootClaimed()
                        viewModel.completeQuest(q.id, q.hpGain, q.mpGain, q.xpGain, savedPath)
                        Toast.makeText(context, "Proof accepted! Quest Completed.", Toast.LENGTH_SHORT).show()
                        refreshQuests()
                    }
                }
            }
        } else {
            Toast.makeText(context, "Evidence is required to validate.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val file = File(requireContext().filesDir, "proof_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Restore state if process was killed
        pendingQuestId = savedInstanceState?.getString("pending_quest_id")

        val rootContainer = android.widget.ScrollView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setPadding(48, 48, 48, 48)
        }

        linearContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        rootContainer.addView(linearContainer)

        resetTimerText = TextView(requireContext()).apply {
            textSize = 14f
            setTextColor(android.graphics.Color.GRAY)
            setPadding(0, 0, 0, 16)
        }
        linearContainer.addView(resetTimerText)

        viewModel.minutesRemaining.observe(viewLifecycleOwner) { minutes ->
            resetTimerText.text = "⏳ Quests reset in $minutes minutes"
        }

        viewModel.selectedHero.observe(viewLifecycleOwner) {
            refreshQuests()
        }

        return rootContainer
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("pending_quest_id", pendingQuestId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        soundManager = SoundManager(requireContext())
    }

    private fun refreshQuests() {
        val hero = viewModel.selectedHero.value ?: return
        val viewsToRemove = mutableListOf<View>()
        for (i in 1 until linearContainer.childCount) {
            viewsToRemove.add(linearContainer.getChildAt(i))
        }
        viewsToRemove.forEach { linearContainer.removeView(it) }

        val header = TextView(requireContext()).apply {
            text = "📜 Quest Log — ${hero.className}"
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 40)
        }
        linearContainer.addView(header)

        hero.quests.forEach { quest ->
            linearContainer.addView(buildQuestCard(quest))
        }
    }

    private fun buildQuestCard(quest: Quest): CardView {
        val context = requireContext()
        val card = CardView(context).apply {
            radius = 24f
            cardElevation = 8f
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = 40 }
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

        if (isDone) {
            val evidencePath = viewModel.getQuestEvidence(quest.id)
            if (!evidencePath.isNullOrEmpty()) {
                val file = File(evidencePath)
                if (file.exists()) {
                    val thumbnail = ImageView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(400, 400).apply {
                            topMargin = 16
                            bottomMargin = 16
                            gravity = android.view.Gravity.CENTER_HORIZONTAL
                        }
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        setImageURI(Uri.fromFile(file))
                        clipToOutline = true
                        setBackgroundResource(R.drawable.border_bronze)
                    }
                    inner.addView(thumbnail)
                }
            }
        }

        val button = Button(context).apply {
            text = if (isDone) "Proof Submitted" else "Upload Proof & Complete"
            isEnabled = !isDone
            setOnClickListener {
                soundManager?.playClick()
                if (quest.requiresImage) {
                    pendingQuestId = quest.id
                    pickImageLauncher.launch("image/*")
                } else {
                    viewModel.completeQuest(quest.id, quest.hpGain, quest.mpGain, quest.xpGain)
                    refreshQuests()
                }
            }
        }

        inner.addView(title)
        inner.addView(desc)
        inner.addView(rewards)
        inner.addView(button)
        card.addView(inner)
        return card
    }

    override fun onDestroyView() {
        super.onDestroyView()
        soundManager = null
    }
}
