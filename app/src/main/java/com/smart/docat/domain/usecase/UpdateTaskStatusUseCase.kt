package com.smart.docat.domain.usecase

import com.smart.docat.data.repository.TaskRepository
import com.smart.docat.domain.model.Task
import com.smart.docat.domain.model.TaskStatus
import jakarta.inject.Inject

class UpdateTaskStatusUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task, status: TaskStatus) {
        taskRepository.updateTaskStatus(task, status)
    }
}