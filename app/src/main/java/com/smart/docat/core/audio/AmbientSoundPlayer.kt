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

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "docat_preferences")

@Singleton
class AmbientSoundPlayer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val player: ExoPlayer = ExoPlayer.Builder(context).build().apply {
        repeatMode = ExoPlayer.REPEAT_MODE_ONE
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
        if (!player.isPlaying) player.play()
    }

    fun pause() {
        if (player.isPlaying) player.pause()
    }

    fun stop() {
        player.stop()
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