package com.smart.docat.ui.ambient

import androidx.lifecycle.ViewModel
import com.smart.docat.R
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

    // --- NUEVO: Lista de sonidos disponibles ---
    // (Mapeamos tus raw files a nombres legibles)
    val availableSounds = listOf(
        AmbientSound(1, "Lluvia relajante", R.raw.sonido_ambiental_1),
        AmbientSound(2, "Cafetería", R.raw.sonido_ambiental_2),
        AmbientSound(3, "Bosque de noche", R.raw.sonido_ambiental_3),
        AmbientSound(4, "Fuego en chimenea", R.raw.sonido_ambiental_4),
        AmbientSound(5, "Olas del mar", R.raw.sonido_ambiental_5),
        AmbientSound(6, "Viento suave", R.raw.sonido_ambiental_6),
        AmbientSound(7, "Pájaros cantando", R.raw.sonido_ambiental_7),
        AmbientSound(8, "Tormenta", R.raw.sonido_ambiental_8),
        AmbientSound(9, "Ruido blanco", R.raw.sonido_ambiental_9),
        AmbientSound(10, "Río fluyendo", R.raw.sonido_ambiental_10)
    )
    // ------------------------------------------

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