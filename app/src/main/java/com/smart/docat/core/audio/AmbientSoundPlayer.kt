package com.smart.docat.core.audio

import android.content.Context
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import android.util.Log
import androidx.media3.common.Player

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "docat_preferences")

@Singleton
class AmbientSoundPlayer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val player: ExoPlayer = ExoPlayer.Builder(context).build().apply {
        repeatMode = ExoPlayer.REPEAT_MODE_ONE

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        setAudioAttributes(audioAttributes, false)
        setWakeMode(C.WAKE_MODE_LOCAL)
    }
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    companion object {
        val KEY_SELECTED_SOUND = stringPreferencesKey("selected_ambient_sound")
        val KEY_VOLUME = floatPreferencesKey("ambient_volume")
    }

    init {
        scope.launch {
            val soundName = context.dataStore.data
                .map { it[KEY_SELECTED_SOUND] }
                .first()
            soundName
                ?.let { runCatching { AmbientSound.valueOf(it) }.getOrNull() }
                ?.let { loadSound(it) }
        }
    }

    fun play(sound: AmbientSound) {
        loadSound(sound)
        player.play()
        scope.launch {
            context.dataStore.edit { it[KEY_SELECTED_SOUND] = sound.name }
        }
    }

    fun resume() {
        scope.launch {
            Log.d("AudioDebug", "▶️ Forzando Play seguro")

            val savedSoundName = context.dataStore.data.map { it[KEY_SELECTED_SOUND] }.first()
            val currentSound = savedSoundName?.let { runCatching { AmbientSound.valueOf(it) }.getOrNull() }
                ?: AmbientSound.RIO

            val uri = android.net.Uri.parse("android.resource://${context.packageName}/${currentSound.resId}")
            player.setMediaItem(androidx.media3.common.MediaItem.fromUri(uri))

            player.prepare()
            player.play()
        }
    }

    fun pause() {
        scope.launch {
            Log.d("AudioDebug", "⏸️ Forzando Pausa al sonido ambiental")
            player.pause()
        }
    }

    fun stop() {
        scope.launch {
            Log.d("AudioDebug", "⏹️ Deteniendo el sonido ambiental y limpiando cola")
            player.pause()
            player.stop()
            player.clearMediaItems() // Limpiamos para que no queden zombies
        }
    }

    fun setVolume(volume: Float) {
        player.volume = volume.coerceIn(0f, 1f)
        scope.launch {
            context.dataStore.edit { it[KEY_VOLUME] = volume }
        }
    }

    fun release() {
        player.release()
    }

    private fun loadSound(sound: AmbientSound) {
        val uri = Uri.parse("android.resource://${context.packageName}/${sound.resId}")
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
    }
}