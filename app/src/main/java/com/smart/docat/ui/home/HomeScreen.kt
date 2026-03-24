package com.smart.docat.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.smart.docat.ui.components.CatMascot
import com.smart.docat.ui.components.CatMood
import com.smart.docat.ui.components.MotivationalPhrase

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartActivityClick: (List<Long>, Int) -> Unit,
    onNavigateToTasksClick: () -> Unit
) {
    val hasTasksToday by viewModel.hasTasksToday.collectAsState()
    val tasksToday by viewModel.tasksToday.collectAsState()

    var showSetupDialog by remember { mutableStateOf(false) }
    var selectedTaskIds by remember { mutableStateOf(setOf<Long>()) }
    var interTaskRestInput by remember { mutableStateOf("5") }
    var isSecondsMode by remember { mutableStateOf(false) }

    LaunchedEffect(showSetupDialog) {
        if (showSetupDialog) {
            selectedTaskIds = tasksToday.map { it.id }.toSet()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "¡Hola!", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        CatMascot(mood = CatMood.BASE)
        Spacer(modifier = Modifier.height(16.dp))
        MotivationalPhrase(
            phrase = if (hasTasksToday) "¡Tienes misiones para hoy! ¿Empezamos?" else "Día libre... o tal vez es hora de planear."
        )
        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { showSetupDialog = true }, // Abre el diálogo en lugar de iniciar de golpe
            enabled = hasTasksToday,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(text = "INICIAR ACTIVIDAD")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!hasTasksToday) {
            Button(
                onClick = onNavigateToTasksClick,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) { Text(text = "IR A TAREAS") }
        }
    }

    if (showSetupDialog) {
        Dialog(onDismissRequest = { showSetupDialog = false }) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Configurar Sesión", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Selecciona las misiones a realizar:", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Lista de tareas con Checkbox
                    LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                        items(tasksToday) { task ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = selectedTaskIds.contains(task.id),
                                    onCheckedChange = { isChecked ->
                                        selectedTaskIds = if (isChecked) selectedTaskIds + task.id else selectedTaskIds - task.id
                                    }
                                )
                                Text(task.nombre, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Descanso entre misiones:", style = MaterialTheme.typography.bodyMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = interTaskRestInput,
                            onValueChange = { interTaskRestInput = it },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterChip(
                            selected = !isSecondsMode,
                            onClick = { isSecondsMode = false },
                            label = { Text("Min") }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        FilterChip(
                            selected = isSecondsMode,
                            onClick = { isSecondsMode = true },
                            label = { Text("Seg") }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showSetupDialog = false }) { Text("Cancelar") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val multiplier = if (isSecondsMode) 1 else 60
                                val restInSeconds = (interTaskRestInput.toIntOrNull() ?: 0) * multiplier
                                showSetupDialog = false
                                onStartActivityClick(selectedTaskIds.toList(), restInSeconds)
                            },
                            enabled = selectedTaskIds.isNotEmpty() // Debe seleccionar al menos una
                        ) { Text("INICIAR") }
                    }
                }
            }
        }
    }
}