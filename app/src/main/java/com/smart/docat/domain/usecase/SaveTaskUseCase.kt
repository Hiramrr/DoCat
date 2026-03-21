package com.smart.docat.domain.usecase

import com.smart.docat.data.repository.TaskRepository
import com.smart.docat.domain.model.Task
import javax.inject.Inject

class SaveTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
){
    suspend operator fun invoke(task: Task) {
        if (task.id == 0L){
            taskRepository.saveTask(task)
        } else {
            taskRepository.updateTask(task)
        }
    }
}
