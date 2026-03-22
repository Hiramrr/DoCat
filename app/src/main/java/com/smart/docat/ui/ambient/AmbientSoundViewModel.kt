package com.smart.docat.ui.ambient

import androidx.lifecycle.ViewModel
import com.smart.docat.core.audio.AmbientSound
import com.smart.docat.core.audio.AmbientSoundPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AmbientSoundViewModel @Inject constructor(
    private val audioPlayer: AmbientSoundPlayer
) : ViewModel() {

    private val _selectedSound = MutableStateFlow<AmbientSound?>(null)
    val selectedSound: StateFlow<AmbientSound?> = _selectedSound.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    fun toggleSound(sound: AmbientSound) {
        if (_selectedSound.value?.id == sound.id && _isPlaying.value) {
            stopSound()
        } else {
            playSound(sound)
        }
    }

    private fun playSound(sound: AmbientSound) {
        audioPlayer.play(sound)
        _selectedSound.value = sound
        _isPlaying.value = true
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