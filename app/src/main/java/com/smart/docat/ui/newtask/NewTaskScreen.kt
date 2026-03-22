package com.smart.docat.ui.newtask

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskScreen(
    taskId: Long?, // Recibe el ID si es edición, o null si es nueva
    viewModel: NewTaskViewModel,
    onNavigateBack: () -> Unit
) {
    // Cargar la tarea si venimos en modo edición (se ejecuta solo una vez)
    LaunchedEffect(taskId) {
        if (taskId != null) {
            viewModel.loadTask(taskId)
        }
    }

    // Observar estados
    val taskName by viewModel.taskName.collectAsState()
    val repetitions by viewModel.repetitions.collectAsState()
    val restTime by viewModel.restTime.collectAsState()
    val subTasks by viewModel.subTasks.collectAsState()

    // Estados temporales para el mini-formulario de agregar subtarea
    var newSubTaskName by remember { mutableStateOf("") }
    var newSubTaskTime by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (taskId == null) "Nueva Misión" else "Editar Misión") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = MaterialTheme.colorScheme.surface) {
                Button(
                    onClick = { viewModel.saveTask(onSuccess = onNavigateBack) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    enabled = taskName.isNotBlank() && subTasks.isNotEmpty() // Validación básica
                ) {
                    Text("GUARDAR MISIÓN")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Nombre de la Tarea
            item {
                OutlinedTextField(
                    value = taskName,
                    onValueChange = { viewModel.onNameChange(it) },
                    label = { Text("Nombre de la tarea") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // 2. Fila para Repeticiones y Descanso
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = repetitions,
                        onValueChange = { viewModel.onRepetitionsChange(it) },
                        label = { Text("Repeticiones") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = restTime,
                        onValueChange = { viewModel.onRestTimeChange(it) },
                        label = { Text("Descanso (min)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            }

            // 3. Sección de Subtareas
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Subtareas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                // Mini-formulario para agregar subtarea
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newSubTaskName,
                        onValueChange = { newSubTaskName = it },
                        label = { Text("¿Qué harás?") },
                        modifier = Modifier.weight(2f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newSubTaskTime,
                        onValueChange = { newSubTaskTime = it },
                        label = { Text("Min") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            viewModel.addSubTask(newSubTaskName, newSubTaskTime)
                            newSubTaskName = ""
                            newSubTaskTime = ""
                        },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Agregar subtarea")
                    }
                }
            }

            // 4. Lista de Subtareas agregadas
            items(subTasks) { subTask ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = subTask.nombre, style = MaterialTheme.typography.bodyLarge)
                            Text(text = "${subTask.tiempoAsignado} min", style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { viewModel.removeSubTask(subTask) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Eliminar subtarea", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}