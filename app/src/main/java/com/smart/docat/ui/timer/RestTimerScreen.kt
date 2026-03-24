package com.smart.docat.ui.timer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smart.docat.core.service.TimerState
import com.smart.docat.ui.components.CatMascot
import com.smart.docat.ui.components.CatMood
import com.smart.docat.ui.components.MotivationalPhrase
import com.smart.docat.ui.components.TimerDisplay

@Composable
fun RestTimerScreen(
    state: TimerState,
    onStopClick: () -> Unit,
    onPauseToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                // BOTÓN DE PAUSA
                IconButton(onClick = onPauseToggle) {
                    Icon(
                        imageVector = if (state.isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                        contentDescription = if (state.isPaused) "Reanudar" else "Pausar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onStopClick) {
                    Icon(Icons.Filled.Close, contentDescription = "Detener Temporizador", tint = MaterialTheme.colorScheme.error)
                }
            }
            Text(
                text = "¡Tiempo de Descanso!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Relájate antes del siguiente bloque",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            CatMascot(mood = CatMood.SLEEPING, modifier = Modifier.size(220.dp))
            TimerDisplay(seconds = state.secondsRemaining)
        }

        MotivationalPhrase(
            phrase = if (state.isPaused) "Descanso en pausa." else "Respira profundo y recupera energía."
        )
    }
}