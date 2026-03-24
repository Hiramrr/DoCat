package com.smart.docat.ui.ambient

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smart.docat.core.audio.AmbientSound
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmbientSoundScreen(
    viewModel: AmbientSoundViewModel = hiltViewModel()
) {
    val selectedSound by viewModel.selectedSound.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val volume by viewModel.volume.collectAsState()
    val sounds = viewModel.availableSounds
    val volumeInt = (volume * 100).roundToInt()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Sonidos Ambientales",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // ── Sección de volumen ─────────────────────────────────────
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Volumen: $volumeInt",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Slider(
                value = volume,
                onValueChange = { viewModel.setVolume(it) },
                valueRange = 0f..1f,
                modifier = Modifier.fillMaxWidth()
            )

            // ── Botones de control ─────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Silenciar
                FilledTonalIconButton(onClick = { viewModel.mute() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeOff,
                        contentDescription = "Silenciar"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Disminuir
                FilledTonalIconButton(onClick = { viewModel.decreaseVolume() }) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Disminuir volumen"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Aumentar
                FilledTonalIconButton(onClick = { viewModel.increaseVolume() }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Aumentar volumen"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Reiniciar
                FilledTonalIconButton(onClick = { viewModel.resetVolume() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reiniciar volumen"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()

            // ── Lista de sonidos ───────────────────────────────────────
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(sounds) { sound ->
                    val isSelected = (selectedSound == sound) && isPlaying
                    SoundListItem(
                        sound = sound,
                        isSelected = isSelected,
                        onClick = { viewModel.selectSound(sound) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SoundListItem(
    sound: AmbientSound,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        headlineContent = {
            Text(
                text = stringResource(id = sound.nameResId),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        },
        leadingContent = {
            Icon(
                imageVector = getSoundIcon(sound),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Seleccionado",
                    tint = Color(0xFF4CAF50) // Verde como en la referencia
                )
            }
        }
    )
}

/**
 * Retorna un icono representativo para cada sonido ambiental.
 */
private fun getSoundIcon(sound: AmbientSound): ImageVector {
    return when (sound) {
        AmbientSound.LLUVIA -> Icons.Default.WaterDrop
        AmbientSound.RIO -> Icons.Default.Water
        AmbientSound.BOSQUE -> Icons.Default.Forest
        AmbientSound.CAFE -> Icons.Default.LocalCafe
        AmbientSound.OLAS -> Icons.Default.Waves
        AmbientSound.VIENTO -> Icons.Default.Air
        AmbientSound.FUEGO -> Icons.Default.LocalFireDepartment
        AmbientSound.NOCHE -> Icons.Default.DarkMode
        AmbientSound.CIUDAD -> Icons.Default.LocationCity
        AmbientSound.PAJAROS -> Icons.Default.EmojiNature
    }
}
