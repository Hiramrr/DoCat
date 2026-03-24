package com.smart.docat.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val dailyHistory by viewModel.dailyHistory.collectAsState()
    val activeDates by viewModel.activeDates.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    val monthlyStats by viewModel.monthlyStats.collectAsState()
    val dailyStats by viewModel.dailyStats.collectAsState()

    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value
    val emptySpaces = firstDayOfWeek - 1

    val daysOfWeek = listOf("L", "M", "M", "J", "V", "S", "D")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Historial", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // ── Navegación del mes ─────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.goToPreviousMonth() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Mes anterior"
                        )
                    }

                    val monthName = currentMonth.month
                        .getDisplayName(TextStyle.FULL, Locale("es", "ES"))
                        .replaceFirstChar { it.uppercase() }
                    Text(
                        text = "${currentMonth.year} / %02d".format(currentMonth.monthValue),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = { viewModel.goToNextMonth() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Mes siguiente"
                        )
                    }
                }
            }

            // ── Cuadrícula del calendario ──────────────────────────
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Encabezados de días
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(7),
                            modifier = Modifier.fillMaxWidth().height(32.dp),
                            userScrollEnabled = false
                        ) {
                            items(daysOfWeek) { dayName ->
                                Text(
                                    text = dayName,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // Días del mes
                        val totalCells = emptySpaces + daysInMonth
                        val gridHeight = ((totalCells + 6) / 7) * 52 // calcular altura
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(7),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(gridHeight.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            userScrollEnabled = false
                        ) {
                            // Espacios vacíos
                            items(emptySpaces) {
                                Spacer(modifier = Modifier.aspectRatio(1f))
                            }

                            // Días reales
                            items(daysInMonth) { index ->
                                val day = index + 1
                                val dayString = "%02d".format(day)
                                val monthString = "%02d".format(currentMonth.monthValue)
                                val realDateString = "${currentMonth.year}-$monthString-$dayString"

                                val isSelected = selectedDate == realDateString
                                val hasActivity = activeDates.contains(realDateString)

                                CalendarDayCell(
                                    day = day,
                                    isSelected = isSelected,
                                    hasActivity = hasActivity,
                                    onClick = { viewModel.onDateSelected(realDateString) }
                                )
                            }
                        }
                    }
                }
            }

            // ── Estadísticas del día seleccionado ──────────────────
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        StatsRow(
                            label = "Sesiones de Enfoque",
                            value = "${dailyStats.focusCount} veces"
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        StatsRow(
                            label = "Tiempo de Enfoque",
                            value = formatTimeFromSeconds(dailyStats.totalTimeSeconds)
                        )
                    }
                }
            }

            // ── Estadísticas mensuales ─────────────────────────────
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        StatsRow(
                            label = "Sesiones del Mes",
                            value = "${monthlyStats.focusCount} veces"
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        StatsRow(
                            label = "Tiempo del Mes",
                            value = formatTimeFromSeconds(monthlyStats.totalTimeSeconds)
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        StatsRow(
                            label = "Días Activos del Mes",
                            value = "${monthlyStats.activeDays} / ${monthlyStats.daysInMonth} días"
                        )
                    }
                }
            }

            // ── Sesiones completadas del día ───────────────────────
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Sesiones Completadas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (dailyHistory.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No hay sesiones registradas este día.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            } else {
                items(dailyHistory) { session ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    "Misión #${session.tareaId}",
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            supportingContent = {
                                Text("Tiempo: ${formatTimeFromSeconds(session.tiempoReal)}")
                            },
                            leadingContent = {
                                Icon(
                                    Icons.Filled.Timer,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    day: Int,
    isSelected: Boolean,
    hasActivity: Boolean,
    onClick: () -> Unit
) {
    val bgColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        else -> Color.Transparent
    }
    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurface
    }
    // Color para el indicador de actividad (amarillo/ámbar como en la referencia)
    val activityColor = Color(0xFFFFC107)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(bgColor)
            .clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day.toString(),
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp
            )
            if (hasActivity && !isSelected) {
                Spacer(modifier = Modifier.height(1.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(activityColor)
                )
            }
        }
    }
}

@Composable
private fun StatsRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Formatea segundos a "Xh Ym" o "Xm"
 */
private fun formatTimeFromSeconds(totalSeconds: Int): String {
    val totalMinutes = totalSeconds / 60
    return if (totalMinutes >= 60) {
        val h = totalMinutes / 60
        val m = totalMinutes % 60
        "${h}h ${m}m"
    } else {
        "${totalMinutes}m"
    }
}