package com.smart.docat.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smart.docat.core.utils.TimeFormatter
import com.smart.docat.data.repository.SessionHistoryRepository
import com.smart.docat.domain.model.SessionHistory
import com.smart.docat.domain.usecase.GetHistoryByDateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

data class MonthlyStats(
    val focusCount: Int = 0,
    val totalTimeSeconds: Int = 0,
    val activeDays: Int = 0,
    val daysInMonth: Int = 30
)

data class DailyStats(
    val focusCount: Int = 0,
    val totalTimeSeconds: Int = 0
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getHistoryByDateUseCase: GetHistoryByDateUseCase,
    private val sessionHistoryRepository: SessionHistoryRepository,
    private val timeFormatter: TimeFormatter
) : ViewModel() {

    // Fecha seleccionada (hoy por defecto)
    private val _selectedDate = MutableStateFlow(timeFormatter.formatDate())
    val selectedDate = _selectedDate.asStateFlow()

    // Mes actual visible en el calendario
    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth = _currentMonth.asStateFlow()

    // Historial del día seleccionado
    @OptIn(ExperimentalCoroutinesApi::class)
    val dailyHistory: StateFlow<List<SessionHistory>> = _selectedDate
        .flatMapLatest { date ->
            getHistoryByDateUseCase(date).sessions
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Días activos (con sesiones registradas)
    private val _activeDates = MutableStateFlow<Set<String>>(emptySet())
    val activeDates = _activeDates.asStateFlow()

    // Estadísticas mensuales
    private val _monthlyStats = MutableStateFlow(MonthlyStats())
    val monthlyStats = _monthlyStats.asStateFlow()

    // Estadísticas del día seleccionado
    private val _dailyStats = MutableStateFlow(DailyStats())
    val dailyStats = _dailyStats.asStateFlow()

    init {
        loadActiveDates()
        loadMonthlyStats()
        loadDailyStats()
    }

    fun onDateSelected(newDate: String) {
        _selectedDate.value = newDate
        loadDailyStats()
    }

    fun goToPreviousMonth() {
        _currentMonth.value = _currentMonth.value.minusMonths(1)
        loadMonthlyStats()
    }

    fun goToNextMonth() {
        _currentMonth.value = _currentMonth.value.plusMonths(1)
        loadMonthlyStats()
    }

    private fun loadActiveDates() {
        viewModelScope.launch {
            _activeDates.value = sessionHistoryRepository.getAllActiveDates()
        }
    }

    private fun loadMonthlyStats() {
        viewModelScope.launch {
            val month = _currentMonth.value
            val monthPrefix = "%04d-%02d".format(month.year, month.monthValue)
            val focusCount = sessionHistoryRepository.getSessionCountForMonth(monthPrefix)
            val totalTime = sessionHistoryRepository.getTotalTimeForMonth(monthPrefix)
            val activeDays = sessionHistoryRepository.getActiveDayCountForMonth(monthPrefix)

            _monthlyStats.value = MonthlyStats(
                focusCount = focusCount,
                totalTimeSeconds = totalTime,
                activeDays = activeDays,
                daysInMonth = month.lengthOfMonth()
            )
        }
    }

    private fun loadDailyStats() {
        viewModelScope.launch {
            val date = _selectedDate.value
            val focusCount = sessionHistoryRepository.getSessionCountForDate(date)
            val totalTime = sessionHistoryRepository.getTotalTimeForDateSync(date)

            _dailyStats.value = DailyStats(
                focusCount = focusCount,
                totalTimeSeconds = totalTime
            )
        }
    }
}