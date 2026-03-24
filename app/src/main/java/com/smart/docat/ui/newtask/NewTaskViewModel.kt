package com.smart.docat.ui.newtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smart.docat.core.utils.TimeFormatter
import com.smart.docat.data.repository.TaskRepository
import com.smart.docat.domain.model.SubTask
import com.smart.docat.domain.model.Task
import com.smart.docat.domain.model.TaskStatus
import com.smart.docat.domain.usecase.SaveTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewTaskViewModel @Inject constructor(
    private val saveTaskUseCase: SaveTaskUseCase,
    private val taskRepository: TaskRepository,
    private val timeFormatter: TimeFormatter
) : ViewModel() {

    private val _taskName = MutableStateFlow("")
    val taskName = _taskName.asStateFlow()

    private val _repetitions = MutableStateFlow("")
    val repetitions = _repetitions.asStateFlow()

    private val _restTime = MutableStateFlow("")
    val restTime = _restTime.asStateFlow()

    private val _subTasks = MutableStateFlow<List<SubTask>>(emptyList())
    val subTasks = _subTasks.asStateFlow()

    // NUEVO: Controla si el usuario está ingresando minutos o segundos
    private val _isSecondsMode = MutableStateFlow(false)
    val isSecondsMode = _isSecondsMode.asStateFlow()

    private var currentTaskId: Long = 0L

    fun onNameChange(newName: String) { _taskName.value = newName }
    fun onRepetitionsChange(newReps: String) { _repetitions.value = newReps }
    fun onRestTimeChange(newTime: String) { _restTime.value = newTime }

    // NUEVO: Cambiar entre minutos y segundos
    fun toggleTimeMode(isSeconds: Boolean) { _isSecondsMode.value = isSeconds }

    fun addSubTask(name: String, time: String) {
        if (name.isNotBlank() && time.isNotBlank()) {
            // Si está en modo minutos, lo multiplicamos por 60 para guardarlo SIEMPRE en segundos
            val multiplier = if (_isSecondsMode.value) 1 else 60
            val timeInSeconds = (time.toIntOrNull() ?: 0) * multiplier

            val nextOrder = _subTasks.value.size + 1
            val newSubTask = SubTask(
                id = 0L,
                tareaId = currentTaskId,
                nombre = name,
                tiempoAsignado = timeInSeconds, // Ahora guarda segundos reales
                orden = nextOrder
            )
            _subTasks.value = _subTasks.value + newSubTask
        }
    }

    fun removeSubTask(subTask: SubTask) {
        _subTasks.value = _subTasks.value - subTask
    }

    fun loadTask(taskId: Long) {
        if (taskId != 0L) {
            viewModelScope.launch {
                val task = taskRepository.getTaskById(taskId)
                task?.let {
                    currentTaskId = it.id
                    _taskName.value = it.nombre
                    _repetitions.value = it.repeticiones.toString()
                    // Al editar, forzamos el modo segundos para mostrar el valor crudo exacto guardado
                    _isSecondsMode.value = true
                    _restTime.value = it.tiempoDescanso.toString()
                    _subTasks.value = it.subTareas
                }
            }
        }
    }

    fun saveTask(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val multiplier = if (_isSecondsMode.value) 1 else 60
            val restTimeInSeconds = (_restTime.value.toIntOrNull() ?: 5) * multiplier
            val totalWorkTime = _subTasks.value.sumOf { it.tiempoAsignado }

            val task = Task(
                id = currentTaskId,
                nombre = _taskName.value,
                tiempoTrabajo = totalWorkTime,
                repeticiones = _repetitions.value.toIntOrNull() ?: 1,
                tiempoDescanso = restTimeInSeconds,
                subTareas = _subTasks.value,
                estado = TaskStatus.IN_PROGRESS,
                fecha = timeFormatter.formatDate()
            )
            saveTaskUseCase(task)
            onSuccess()
        }
    }
}