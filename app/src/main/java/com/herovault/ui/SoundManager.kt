package com.herovault.ui

import android.content.Context
import android.media.MediaPlayer
import com.herovault.R

class SoundManager(private val context: Context) {


    fun playClick() {
        playSound(R.raw.click_sound)
    }

    fun playLevelUp() {
        playSound(R.raw.fanfare_levelup)
    }

    fun playLootClaimed() {
        playSound(R.raw.fanfare_levelup)
    }

    private fun playSound(resId: Int) {
        try {
            val mediaPlayer = MediaPlayer.create(context, resId)
            mediaPlayer.setOnCompletionListener { it.release() }
            mediaPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
