package com.smart.docat.ui.ambient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smart.docat.core.audio.AmbientSound
import com.smart.docat.core.audio.AmbientSoundPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AmbientSoundViewModel @Inject constructor(
    private val audioPlayer: AmbientSoundPlayer
) : ViewModel() {

    val availableSounds = AmbientSound.entries

    private val _selectedSound = MutableStateFlow<AmbientSound?>(null)
    val selectedSound: StateFlow<AmbientSound?> = _selectedSound.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _volume = MutableStateFlow(0.8f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    init {
        // Inicializar volumen del player
        audioPlayer.setVolume(_volume.value)
    }

    fun selectSound(sound: AmbientSound) {
        audioPlayer.play(sound)
        _selectedSound.value = sound
        _isPlaying.value = true
    }

    fun toggleSound(sound: AmbientSound) {
        if (_selectedSound.value == sound && _isPlaying.value) {
            stopSound()
        } else {
            selectSound(sound)
        }
    }

    fun setVolume(newVolume: Float) {
        val clamped = newVolume.coerceIn(0f, 1f)
        _volume.value = clamped
        audioPlayer.setVolume(clamped)
    }

    fun mute() {
        setVolume(0f)
    }

    fun decreaseVolume() {
        setVolume(_volume.value - 0.1f)
    }

    fun increaseVolume() {
        setVolume(_volume.value + 0.1f)
    }

    fun resetVolume() {
        setVolume(0.8f)
    }

    fun stopSound() {
        audioPlayer.stop()
        _selectedSound.value = null
        _isPlaying.value = false
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
    }
}
