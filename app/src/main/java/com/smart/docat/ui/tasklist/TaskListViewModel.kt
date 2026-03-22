package com.smart.docat.ui.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smart.docat.core.utils.TimeFormatter
import com.smart.docat.domain.model.Task
import com.smart.docat.domain.model.TaskStatus
import com.smart.docat.domain.usecase.GetTasksUseCase
import com.smart.docat.domain.usecase.UpdateTaskStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase,
    private val timeFormatter: TimeFormatter
) : ViewModel() {

    private val today = timeFormatter.formatDate()

    val tasks: StateFlow<List<Task>> = getTasksUseCase(today)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun toggleTaskStatus(task: Task) {
        viewModelScope.launch {
            val newStatus = if (task.estado == TaskStatus.COMPLETED) {
                TaskStatus.IN_PROGRESS
            } else {
                TaskStatus.COMPLETED
            }
            // Pasamos el objeto 'task' completo y el nuevo estado
            updateTaskStatusUseCase(task, newStatus)
        }
    }
}