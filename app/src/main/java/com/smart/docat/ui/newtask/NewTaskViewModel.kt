package com.smart.docat.ui.newtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smart.docat.core.utils.TimeFormatter
import com.smart.docat.domain.model.SubTask
import com.smart.docat.domain.model.Task
import com.smart.docat.domain.model.TaskStatus
import com.smart.docat.domain.usecase.SaveTaskUseCase
import com.smart.docat.data.repository.TaskRepository // Asumo que lo usas para cargar la tarea si es edición
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

    // Variables de estado para el formulario
    private val _taskName = MutableStateFlow("")
    val taskName = _taskName.asStateFlow()

    private val _repetitions = MutableStateFlow("")
    val repetitions = _repetitions.asStateFlow()

    private val _restTime = MutableStateFlow("")
    val restTime = _restTime.asStateFlow()

    private val _subTasks = MutableStateFlow<List<SubTask>>(emptyList())
    val subTasks = _subTasks.asStateFlow()

    // Variable para saber si estamos editando (guardará el ID de la tarea)
    private var currentTaskId: Long = 0L

    // Funciones para actualizar el estado desde la UI
    fun onNameChange(newName: String) { _taskName.value = newName }
    fun onRepetitionsChange(newReps: String) { _repetitions.value = newReps }
    fun onRestTimeChange(newTime: String) { _restTime.value = newTime }

    fun addSubTask(name: String, time: String) {
        if (name.isNotBlank() && time.isNotBlank()) {
            val timeInt = time.toIntOrNull() ?: 0
            val nextOrder = _subTasks.value.size + 1
            val newSubTask = SubTask(
                id = 0L,
                tareaId = currentTaskId,
                nombre = name,
                tiempoAsignado = timeInt,
                orden = nextOrder
            )
            _subTasks.value = _subTasks.value + newSubTask
        }
    }

    fun removeSubTask(subTask: SubTask) {
        _subTasks.value = _subTasks.value - subTask
    }

    // Cargar tarea si venimos a editar
    fun loadTask(taskId: Long) {
        if (taskId != 0L) {
            viewModelScope.launch {
                val task = taskRepository.getTaskById(taskId) // Ajusta el nombre de la función según tu repositorio
                task?.let {
                    currentTaskId = it.id
                    _taskName.value = it.nombre
                    _repetitions.value = it.repeticiones.toString()
                    _restTime.value = it.tiempoDescanso.toString()
                    _subTasks.value = it.subTareas
                }
            }
        }
    }

    // Guardar la tarea
    fun saveTask(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val totalWorkTime = _subTasks.value.sumOf { it.tiempoAsignado }
            val task = Task(
                id = currentTaskId, // 0L si es nueva, o el ID actual si es edición
                nombre = _taskName.value,
                tiempoTrabajo = totalWorkTime,
                repeticiones = _repetitions.value.toIntOrNull() ?: 1,
                tiempoDescanso = _restTime.value.toIntOrNull() ?: 5,
                subTareas = _subTasks.value,
                estado = TaskStatus.IN_PROGRESS, // O el estado por defecto que uses
                fecha = timeFormatter.formatDate() // Asumiendo que guardas la fecha de hoy
            )
            saveTaskUseCase(task)
            onSuccess()
        }
    }
}