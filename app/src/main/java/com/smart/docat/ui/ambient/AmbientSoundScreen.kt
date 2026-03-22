package com.smart.docat.ui.ambient

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smart.docat.core.audio.AmbientSound

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmbientSoundScreen(
    viewModel: AmbientSoundViewModel = hiltViewModel()
) {
    // Recolectar el estado desde el ViewModel
    val selectedSound by viewModel.selectedSound.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    // Obtenemos la lista directamente del VM
    val sounds = viewModel.availableSounds

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sonidos Ambientales 🎧") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(sounds) { sound ->
                SoundItem(
                    sound = sound,
                    isSelected = selectedSound?.id == sound.id && isPlaying,
                    onClick = { viewModel.toggleSound(sound) }
                )
            }
        }
    }
}

@Composable
fun SoundItem(
    sound: AmbientSound,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = sound.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Icono dinámico que cambia de Play a Stop
            Icon(
                imageVector = if (isSelected) Icons.Default.Close else Icons.Default.PlayArrow,
                contentDescription = if (isSelected) "Detener" else "Reproducir",
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}