package com.smart.docat.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smart.docat.ui.components.CatMascot
import com.smart.docat.ui.components.CatMood
import com.smart.docat.ui.components.MotivationalPhrase

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartActivityClick: () -> Unit,
    onNavigateToTasksClick: () -> Unit
) {
    // 1. Escuchamos lo que dice el ViewModel
    val hasTasksToday by viewModel.hasTasksToday.collectAsState()

    // 2. Armamos la pantalla en una columna centrada
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "¡Hola!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Tu gato en estado base
        CatMascot(mood = CatMood.BASE)

        Spacer(modifier = Modifier.height(16.dp))

        // La frase cambia dependiendo de si hay tareas o no
        MotivationalPhrase(
            phrase = if (hasTasksToday) {
                "¡Tienes misiones para hoy! ¿Empezamos?"
            } else {
                "Día libre... o tal vez es hora de planear."
            }
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Botón principal
        Button(
            onClick = onStartActivityClick,
            enabled = hasTasksToday, // Se apaga si no hay tareas
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(text = "INICIAR ACTIVIDAD")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Si no hay tareas, le mostramos un botón para ir a crearlas
        if (!hasTasksToday) {
            Button(
                onClick = onNavigateToTasksClick,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(text = "IR A TAREAS")
            }
        }
    }
}