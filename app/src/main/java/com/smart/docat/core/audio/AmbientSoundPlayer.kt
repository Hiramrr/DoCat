package com.smart.docat.core.audio

import android.content.Context
import android.media.MediaPlayer

class AmbientSoundPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    var currentSoundId: Int? = null
        private set

    fun play(sound: AmbientSound) {
        // Si ya está sonando este mismo audio, no hacemos nada
        if (currentSoundId == sound.id && mediaPlayer?.isPlaying == true) {
            return
        }

        // Detenemos cualquier sonido anterior antes de iniciar uno nuevo
        stop()

        mediaPlayer = MediaPlayer.create(context, sound.resourceId).apply {
            isLooping = true // Queremos que el sonido ambiental se repita indefinidamente
            start()
        }
        currentSoundId = sound.id
    }

    fun stop() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release() // Es crucial liberar los recursos del MediaPlayer
        }
        mediaPlayer = null
        currentSoundId = null
    }
}