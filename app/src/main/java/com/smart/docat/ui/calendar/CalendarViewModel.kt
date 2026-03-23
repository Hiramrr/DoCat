package com.smart.docat.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smart.docat.core.utils.TimeFormatter
import com.smart.docat.domain.model.SessionHistory
import com.smart.docat.domain.usecase.GetHistoryByDateUseCase
// Si tienes un repositorio de tareas para traer el nombre, idealmente deberías inyectarlo aquí
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getHistoryByDateUseCase: GetHistoryByDateUseCase,
    private val timeFormatter: TimeFormatter
) : ViewModel() {

    // 1. Guardamos la fecha que el usuario tiene seleccionada (hoy por defecto)
    private val _selectedDate = MutableStateFlow(timeFormatter.formatDate())
    val selectedDate = _selectedDate.asStateFlow()

    // 2. Observamos el historial dependiendo de la fecha seleccionada
    // Asumo que tu GetHistoryByDateUseCase recibe un String (fecha) y devuelve un Flow<List<SessionHistory>>
    val dailyHistory: StateFlow<List<SessionHistory>> = _selectedDate
        .flatMapLatest { date ->
            getHistoryByDateUseCase(date).sessions
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 3. (Opcional) Días activos en el mes para ponerles un puntito.
    // Por ahora pondremos un estado vacío, pero aquí podrías consultar todas las fechas con actividad.
    private val _activeDates = MutableStateFlow<Set<String>>(emptySet())
    val activeDates = _activeDates.asStateFlow()

    // Función para cambiar de día cuando el usuario toca un número en el calendario
    fun onDateSelected(newDate: String) {
        _selectedDate.value = newDate
    }
}