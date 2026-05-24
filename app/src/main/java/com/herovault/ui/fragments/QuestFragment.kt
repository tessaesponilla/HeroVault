package com.herovault.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.herovault.R
import com.herovault.model.ProofType
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
        uri?.let { handleProofUri(it, ProofType.IMAGE) }
            ?: Toast.makeText(context, "No image selected.", Toast.LENGTH_SHORT).show()
    }

    private val pickVideoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { handleProofUri(it, ProofType.VIDEO) }
            ?: Toast.makeText(context, "No video selected.", Toast.LENGTH_SHORT).show()
    }

    private val pickAudioLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { handleProofUri(it, ProofType.AUDIO) }
            ?: Toast.makeText(context, "No audio selected.", Toast.LENGTH_SHORT).show()
    }

    private val pickTextLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { handleProofUri(it, ProofType.TEXT) }
            ?: Toast.makeText(context, "No file selected.", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        pendingQuestId = savedInstanceState?.getString("pending_quest_id")

        val rootContainer = ScrollView(requireContext()).apply {
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

        viewModel.selectedHero.observe(viewLifecycleOwner) { refreshQuests() }

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

    override fun onDestroyView() {
        super.onDestroyView()
        soundManager = null
    }

    private fun refreshQuests() {
        val hero = viewModel.selectedHero.value ?: return
        while (linearContainer.childCount > 1) {
            linearContainer.removeViewAt(1)
        }

        linearContainer.addView(TextView(requireContext()).apply {
            text = "📜 Quest Log — ${hero.className}"
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 40)
        })

        hero.quests.forEach { linearContainer.addView(buildQuestCard(it)) }
    }

    private fun buildQuestCard(quest: Quest): CardView {
        val ctx = requireContext()

        val card = CardView(ctx).apply {
            radius = 24f
            cardElevation = 8f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 40 }
        }

        val inner = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        inner.addView(TextView(ctx).apply {
            text = quest.title
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
        })

        inner.addView(TextView(ctx).apply {
            text = quest.description
            textSize = 13f
            setTextColor(android.graphics.Color.GRAY)
            setPadding(0, 8, 0, 8)
        })

        inner.addView(TextView(ctx).apply {
            text = "+${quest.hpGain} HP   +${quest.mpGain} MP   +${quest.xpGain} XP"
            textSize = 13f
            setTextColor(android.graphics.Color.parseColor("#4CAF50"))
            setPadding(0, 0, 0, 20)
        })

        val isDone = viewModel.isQuestCompleted(quest.id)

        if (isDone) {
            val path = viewModel.getQuestEvidence(quest.id)
            if (!path.isNullOrEmpty() && File(path).exists()) {
                inner.addView(buildProofPreview(path))
            }
        }

        if (isDone) {
            inner.addView(TextView(ctx).apply {
                text = "✔ Proof Submitted"
                textSize = 13f
                setTextColor(android.graphics.Color.parseColor("#4CAF50"))
            })
        } else {
            if (quest.allowedProofTypes.size == 1) {
                inner.addView(buildProofButton(quest, quest.allowedProofTypes.first()))
            } else {
                inner.addView(TextView(ctx).apply {
                    text = "Choose proof type:"
                    textSize = 12f
                    setTextColor(android.graphics.Color.GRAY)
                    setPadding(0, 0, 0, 8)
                })

                val row = LinearLayout(ctx).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }

                quest.allowedProofTypes.forEach { type ->
                    row.addView(buildProofButton(quest, type).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f
                        ).apply { marginEnd = 8 }
                    })
                }

                inner.addView(row)
            }
        }

        card.addView(inner)
        return card
    }

    private fun buildProofButton(quest: Quest, type: ProofType): Button {
        val label = when (type) {
            ProofType.IMAGE -> "📷 Photo"
            ProofType.VIDEO -> "🎥 Video"
            ProofType.AUDIO -> "🎵 Audio"
            ProofType.TEXT  -> "📝 Text"
        }
        return Button(requireContext()).apply {
            text = label
            textSize = 12f
            setOnClickListener {
                soundManager?.playClick()
                pendingQuestId = quest.id
                when (type) {
                    ProofType.IMAGE -> pickImageLauncher.launch("image/*")
                    ProofType.VIDEO -> pickVideoLauncher.launch("video/*")
                    ProofType.AUDIO -> pickAudioLauncher.launch("audio/*")
                    ProofType.TEXT  -> pickTextLauncher.launch("text/plain")
                }
            }
        }
    }

    private fun buildProofPreview(path: String): View {
        val ctx = requireContext()
        return when {
            path.endsWith(".jpg") || path.endsWith(".png") -> {
                ImageView(ctx).apply {
                    layoutParams = LinearLayout.LayoutParams(400, 400).apply {
                        topMargin = 16
                        bottomMargin = 16
                        gravity = android.view.Gravity.CENTER_HORIZONTAL
                    }
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    setImageURI(Uri.fromFile(File(path)))
                    clipToOutline = true
                    setBackgroundResource(R.drawable.border_bronze)
                }
            }
            path.endsWith(".mp4") -> TextView(ctx).apply {
                text = "🎥 Video proof submitted"
                textSize = 13f
                setTextColor(android.graphics.Color.GRAY)
                setPadding(0, 8, 0, 8)
            }
            path.endsWith(".m4a") || path.endsWith(".mp3") -> TextView(ctx).apply {
                text = "🎵 Audio proof submitted"
                textSize = 13f
                setTextColor(android.graphics.Color.GRAY)
                setPadding(0, 8, 0, 8)
            }
            path.endsWith(".txt") -> {
                val snippet = try { File(path).readText().take(120) } catch (e: Exception) { "" }
                TextView(ctx).apply {
                    text = "📝 \"$snippet${if (snippet.length == 120) "…" else ""}\""
                    textSize = 12f
                    setTextColor(android.graphics.Color.GRAY)
                    setPadding(16, 12, 16, 12)
                    setBackgroundColor(android.graphics.Color.parseColor("#1A000000"))
                }
            }
            else -> TextView(ctx).apply { text = "✔ Proof on file" }
        }
    }

    private fun handleProofUri(uri: Uri, type: ProofType) {
        val questId = pendingQuestId ?: return
        val hero = viewModel.selectedHero.value ?: return
        val quest = hero.quests.find { it.id == questId } ?: return

        val maxBytes = if (type == ProofType.VIDEO) 50L * 1024 * 1024 else 10L * 1024 * 1024
        if (getFileSize(uri) > maxBytes) {
            val max = if (type == ProofType.VIDEO) "50MB" else "10MB"
            Toast.makeText(context, "File too large (max $max).", Toast.LENGTH_SHORT).show()
            return
        }

        val extension = when (type) {
            ProofType.IMAGE -> "jpg"
            ProofType.VIDEO -> "mp4"
            ProofType.AUDIO -> "m4a"
            ProofType.TEXT  -> "txt"
        }

        val savedPath = saveFileToInternalStorage(uri, extension)
        if (savedPath == null) {
            Toast.makeText(context, "Failed to save file.", Toast.LENGTH_SHORT).show()
            return
        }

        when (type) {
            ProofType.IMAGE -> verifyImageProof(quest, savedPath)
            ProofType.TEXT  -> verifyTextProof(quest, savedPath)
            ProofType.VIDEO,
            ProofType.AUDIO -> acceptProof(quest, savedPath)
        }
    }

    private fun verifyImageProof(quest: Quest, imagePath: String) {
        val bitmap = android.graphics.BitmapFactory.decodeFile(imagePath) ?: run {
            acceptProof(quest, imagePath)
            return
        }

        Toast.makeText(context, "Verifying photo...", Toast.LENGTH_SHORT).show()

        val inputImage = InputImage.fromBitmap(bitmap, 0)
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

        labeler.process(inputImage)
            .addOnSuccessListener { labels ->
                val detected = labels.map { it.text.lowercase() }
                val approved = checkLabelsMatchQuest(quest.id, detected)
                if (approved) {
                    acceptProof(quest, imagePath)
                } else {
                    File(imagePath).delete()
                    Toast.makeText(
                        context,
                        "✖ Photo doesn't match quest.\nDetected: ${detected.take(3).joinToString()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .addOnFailureListener {
                acceptProof(quest, imagePath)
            }
    }

    private fun verifyTextProof(quest: Quest, textPath: String) {
        val content = try {
            File(textPath).readText().lowercase()
        } catch (e: Exception) {
            acceptProof(quest, textPath)
            return
        }

        if (content.isBlank()) {
            File(textPath).delete()
            Toast.makeText(context, "✖ Text file is empty.", Toast.LENGTH_SHORT).show()
            return
        }

        if (content.length < 30) {
            File(textPath).delete()
            Toast.makeText(context, "✖ Text too short to be valid proof.", Toast.LENGTH_SHORT).show()
            return
        }

        val keywords = getTextKeywordsForQuest(quest.id)
        val approved = if (keywords.isEmpty()) true else keywords.any { content.contains(it) }

        if (approved) {
            acceptProof(quest, textPath)
        } else {
            File(textPath).delete()
            Toast.makeText(
                context,
                "✖ Text doesn't seem related to this quest. Try writing about: ${keywords.take(3).joinToString()}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun acceptProof(quest: Quest, path: String) {
        soundManager?.playLootClaimed()
        viewModel.completeQuest(quest.id, quest.hpGain, quest.mpGain, quest.xpGain, path)
        Toast.makeText(context, "✔ Proof accepted!", Toast.LENGTH_SHORT).show()
        refreshQuests()
    }

    private fun checkLabelsMatchQuest(questId: String, labels: List<String>): Boolean {
        val keywords = when (questId) {
            "k1"  -> listOf("water", "drink", "bottle", "glass", "liquid", "beverage", "cup")
            "k2"  -> listOf("stretching", "exercise", "yoga", "fitness", "sport", "person", "arm", "body")
            "k3"  -> listOf("walking", "road", "path", "outdoor", "nature", "street", "park", "sky", "tree")
            "k4"  -> listOf("exercise", "fitness", "sport", "gym", "workout", "dumbbell", "mat", "person")
            "k5"  -> listOf("food", "meal", "vegetable", "fruit", "plate", "salad", "dish", "cooking", "kitchen")
            "cf1" -> emptyList()
            "cf2" -> listOf("laptop", "computer", "desk", "table", "office", "keyboard", "screen")
            "cf3" -> listOf("plant", "nature", "outdoor", "garden", "tree", "leaf", "flower", "sky", "grass")
            "cf4" -> listOf("walking", "road", "outdoor", "path", "street", "shoe", "nature", "park")
            "cf5" -> emptyList()
            "w1"  -> listOf("book", "reading", "text", "page", "literature", "paper", "novel")
            "w2"  -> listOf("puzzle", "game", "paper", "book", "pen", "grid", "crossword", "writing")
            "w3"  -> listOf("writing", "notebook", "pen", "paper", "notes", "desk", "journal")
            "w4"  -> listOf("phone", "tablet", "game", "screen", "device", "computer")
            "w5"  -> emptyList()
            "kg1" -> listOf("desk", "table", "office", "room", "furniture", "organized", "clean")
            "kg2" -> listOf("writing", "paper", "pen", "notebook", "notes", "planner", "list", "journal")
            "kg3" -> listOf("drawer", "shelf", "storage", "furniture", "box", "cabinet", "organized")
            "kg4" -> listOf("computer", "laptop", "screen", "desktop", "device", "keyboard")
            "kg5" -> listOf("calendar", "planner", "notebook", "phone", "schedule", "desk", "writing")
            "q1"  -> emptyList()
            "q2"  -> emptyList()
            "q3"  -> emptyList()
            "q4"  -> emptyList()
            "q5"  -> emptyList()
            "cm1" -> emptyList()
            "cm2" -> listOf("food", "meal", "eating", "plate", "table", "bowl", "fork", "dish", "drink")
            "cm3" -> listOf("nature", "leaf", "cloud", "plant", "water", "outdoor", "sky", "flower", "tree")
            "cm4" -> emptyList()
            "cm5" -> emptyList()
            else  -> emptyList()
        }
        if (keywords.isEmpty()) return true
        return labels.any { label -> keywords.any { kw -> label.contains(kw) } }
    }

    private fun getTextKeywordsForQuest(questId: String): List<String> {
        return when (questId) {
            "w1"  -> listOf("book", "read", "page", "chapter", "story", "author")
            "w3"  -> listOf("learned", "discovered", "interesting", "today", "note", "idea")
            "w5"  -> listOf("focused", "task", "working", "minutes", "completed", "session")
            "cf1" -> listOf("screen", "break", "relaxed", "quiet", "peaceful", "away")
            "kg2" -> listOf("task", "plan", "today", "priority", "goal", "important", "need to")
            "kg5" -> listOf("week", "plan", "schedule", "monday", "calendar", "review", "next")
            "q1"  -> listOf("grateful", "thankful", "appreciate", "happy", "love", "blessing")
            "q2"  -> listOf("message", "friend", "family", "kind", "wrote", "sent", "care")
            "q5"  -> listOf("feel", "feeling", "today", "emotion", "thought", "mood", "reflect")
            "cm4" -> listOf("breath", "breathe", "calm", "inhale", "exhale", "relax", "pause")
            "cm5" -> listOf("object", "feel", "texture", "smell", "color", "describe", "notice")
            else  -> emptyList()
        }
    }

    private fun saveFileToInternalStorage(uri: Uri, extension: String): String? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val file = File(requireContext().filesDir, "proof_${System.currentTimeMillis()}.$extension")
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

    private fun getFileSize(uri: Uri): Long {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val idx = it.getColumnIndex(android.provider.OpenableColumns.SIZE)
            if (idx != -1 && it.moveToFirst()) return it.getLong(idx)
        }
        return 0L
    }
}