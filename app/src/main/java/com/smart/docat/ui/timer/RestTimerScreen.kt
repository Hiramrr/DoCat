package com.smart.docat.ui.timer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
    onStopClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Cabecera
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onStopClick) {
                    Icon(Icons.Filled.Close, contentDescription = "Detener Temporizador")
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

        // Centro: Gato y Reloj
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            CatMascot(mood = CatMood.SLEEPING, modifier = Modifier.size(220.dp))
            TimerDisplay(seconds = state.secondsRemaining)
        }

        // Pie: Frase de descanso
        MotivationalPhrase(phrase = "¡Sigue así, estás haciendo un gran trabajo!")
    }
}