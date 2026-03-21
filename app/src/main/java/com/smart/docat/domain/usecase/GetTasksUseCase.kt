package com.smart.docat.domain.usecase

import com.smart.docat.data.repository.TaskRepository
import com.smart.docat.domain.model.Task
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(fecha: String): Flow<List<Task>> {
        return taskRepository.getTasksForDate(fecha)
    }
}