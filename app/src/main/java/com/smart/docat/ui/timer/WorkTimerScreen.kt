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
fun WorkTimerScreen(
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
        // Cabecera: Botones y Títulos
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
                // BOTÓN DE CERRAR
                IconButton(onClick = onStopClick) {
                    Icon(Icons.Filled.Close, contentDescription = "Detener Temporizador", tint = MaterialTheme.colorScheme.error)
                }
            }
            Text(
                text = state.currentTaskName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Subtarea: ${state.currentSubTaskName}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Repetición: ${state.currentRepetition + 1} / ${state.totalRepetitions}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Centro: Gato y Reloj
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Si está pausado, el gato regresa a su estado base relajado
            CatMascot(
                mood = if (state.isPaused) CatMood.BASE else CatMood.WORKING,
                modifier = Modifier.size(220.dp)
            )
            TimerDisplay(seconds = state.secondsRemaining)
        }

        // Pie: Frase motivacional
        MotivationalPhrase(
            phrase = if (state.isPaused) "¡Toma un respiro, te espero!" else "¡Sigue así, estás haciendo un gran trabajo!"
        )
    }
}