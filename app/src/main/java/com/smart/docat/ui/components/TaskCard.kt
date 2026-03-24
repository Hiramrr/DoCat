package com.smart.docat.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smart.docat.domain.model.Task
import com.smart.docat.domain.model.TaskStatus

@Composable
fun TaskCard(
    task: Task,
    onStatusToggle: (Task) -> Unit,
    onEditClick: (Task) -> Unit,
    onPlayClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val isCompleted = task.estado == TaskStatus.COMPLETED

    val formatTime = { totalSeconds: Int ->
        if (totalSeconds >= 60 && totalSeconds % 60 == 0) {
            "${totalSeconds / 60} min"
        } else {
            "$totalSeconds seg"
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted)
                MaterialTheme.colorScheme.surfaceContainerHigh
            else
                MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { onStatusToggle(task) }) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = if (isCompleted) "Completada" else "Pendiente",
                        tint = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold // Un toque extra para que destaque
                    )
                    Text(
                        // Usamos la función formatTime aquí
                        text = "${task.repeticiones} rep · ${formatTime(task.tiempoDescanso)} descanso",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (!isCompleted) {

                    // BOTÓN NUEVO: PLAY
                    IconButton(
                        onClick = { onPlayClick(task.id) },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Iniciar esta misión",
                            modifier = Modifier.size(28.dp) // Un poco más grande para invitar al tap
                        )
                    }

                    IconButton(onClick = { onEditClick(task) }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Editar tarea",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (task.subTareas.isNotEmpty()) {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (expanded) "Colapsar" else "Expandir",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    task.subTareas.forEach { subTask ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp, horizontal = 8.dp) // Un pequeño padding extra a los lados
                        ) {
                            Text(
                                text = "• ${subTask.nombre}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formatTime(subTask.tiempoAsignado),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}