package com.smart.docat.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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

    // 1. Calculamos los datos del mes real actual
    val currentMonth = remember { YearMonth.now() }
    val daysInMonth = currentMonth.lengthOfMonth()
    // Obtenemos en qué día cae el primer día del mes (1 = Lunes, 7 = Domingo)
    val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value
    // Calculamos cuántos espacios vacíos dejar al principio de la cuadrícula
    val emptySpaces = firstDayOfWeek - 1

    val daysOfWeek = listOf("L", "M", "M", "J", "V", "S", "D")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Historial", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 2. Título dinámico (Ej: "Octubre 2023")
            val monthName = currentMonth.month.getDisplayName(TextStyle.FULL, Locale("es", "ES")).replaceFirstChar { it.uppercase() }
            Text(
                text = "$monthName ${currentMonth.year}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 3. Cuadrícula del Calendario
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth().height(280.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // A) Fila de los días de la semana (Encabezados)
                items(daysOfWeek) { dayName ->
                    Text(
                        text = dayName,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // B) Espacios en blanco antes del primer día
                items(emptySpaces) {
                    Spacer(modifier = Modifier.aspectRatio(1f))
                }

                // C) Los días reales del mes
                items(daysInMonth) { index ->
                    val day = index + 1

                    // IMPORTANTE: Aquí armamos la fecha real en String.
                    // Asegúrate de que el formato coincida con el que genera tu TimeFormatter.
                    // Este ejemplo asume que guardas fechas como "yyyy-MM-dd" o "2023-10-05"
                    val dayString = if (day < 10) "0$day" else "$day"
                    val monthString = if (currentMonth.monthValue < 10) "0${currentMonth.monthValue}" else "${currentMonth.monthValue}"

                    // ---> SI TU FORMATO ES dd/MM/yyyy cambia esto a: "$dayString/$monthString/${currentMonth.year}"
                    val realDateString = "${currentMonth.year}-$monthString-$dayString"

                    val isSelected = selectedDate == realDateString
                    val hasActivity = activeDates.contains(realDateString)

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else Color.Transparent
                            )
                            .clickable { viewModel.onDateSelected(realDateString) }
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = day.toString(),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (hasActivity && !isSelected) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider() // Cambiado a Divider si HorizontalDivider te marcaba error
            Spacer(modifier = Modifier.height(16.dp))

            // 4. Detalle de la fecha seleccionada
            Text(
                text = "Detalle del día: $selectedDate",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (dailyHistory.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay misiones registradas este día.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                val totalTime = dailyHistory.sumOf { it.tiempoReal }
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Tiempo total enfocado: $totalTime min",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                LazyColumn {
                    items(dailyHistory) { session ->
                        ListItem(
                            headlineContent = { Text("Misión ID: ${session.tareaId}") },
                            supportingContent = { Text("Tiempo dedicado: ${session.tiempoReal} min") },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }
                }
            }
        }
    }
}