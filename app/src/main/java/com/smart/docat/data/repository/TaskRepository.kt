package com.smart.docat.data.repository

import com.smart.docat.data.local.dao.SubTaskDao
import com.smart.docat.data.local.dao.TaskDao
import com.smart.docat.data.local.entity.SubTaskEntity
import com.smart.docat.data.local.entity.TaskEntity
import com.smart.docat.domain.model.SubTask
import com.smart.docat.domain.model.Task
import com.smart.docat.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class TaskRepository(
    private val taskDao: TaskDao,
    private val subTaskDao: SubTaskDao
) {

    fun getTasksForDate(fecha: String): Flow<List<Task>> {
        return taskDao.getTasksForDate(fecha).flatMapLatest { tasks ->
            if (tasks.isEmpty()) {
                flowOf(emptyList())
            } else {
                val subTaskFlows = tasks.map { task ->
                    subTaskDao.getSubTasksForTask(task.id).map { subTasks ->
                        task.toDomain(subTasks)
                    }
                }
                combine(subTaskFlows) { it.toList() }
            }
        }
    }

    suspend fun getTaskById(id: Long): Task? {
        val taskEntity = taskDao.getTaskById(id) ?: return null
        val subTasks = mutableListOf<SubTaskEntity>()
        subTaskDao.getSubTasksForTask(taskEntity.id).collect { subTasks.addAll(it) }
        return taskEntity.toDomain(subTasks)
    }

    suspend fun saveTask(task: Task) {
        val taskEntity = task.toEntity()
        val generatedId = taskDao.insertTask(taskEntity)
        val subTaskEntities = task.subTareas.mapIndexed { index, subTask ->
            subTask.toEntity(tareaId = generatedId, orden = index)
        }
        subTaskDao.insertSubTasks(subTaskEntities)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
        subTaskDao.deleteSubTasksForTask(task.id)
        val subTaskEntities = task.subTareas.mapIndexed { index, subTask ->
            subTask.toEntity(tareaId = task.id, orden = index)
        }
        subTaskDao.insertSubTasks(subTaskEntities)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
    }

    suspend fun updateTaskStatus(task: Task, status: TaskStatus) {
        taskDao.updateTask(task.toEntity().copy(estado = status.name))
    }
}

private fun TaskEntity.toDomain(subTaskEntities: List<SubTaskEntity>): Task {
    return Task(
        id = id,
        nombre = nombre,
        tiempoTrabajo = tiempoTrabajo,
        tiempoDescanso = tiempoDescanso,
        repeticiones = repeticiones,
        estado = TaskStatus.valueOf(estado),
        fecha = fecha,
        subTareas = subTaskEntities.map { it.toDomain() }
    )
}

private fun SubTaskEntity.toDomain(): SubTask {
    return SubTask(
        id = id,
        tareaId = tareaId,
        nombre = nombre,
        tiempoAsignado = tiempoAsignado,
        orden = orden
    )
}

private fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        nombre = nombre,
        tiempoTrabajo = tiempoTrabajo,
        tiempoDescanso = tiempoDescanso,
        repeticiones = repeticiones,
        estado = estado.name,
        fecha = fecha
    )
}

private fun SubTask.toEntity(tareaId: Long, orden: Int): SubTaskEntity {
    return SubTaskEntity(
        id = id,
        tareaId = tareaId,
        nombre = nombre,
        tiempoAsignado = tiempoAsignado,
        orden = orden
    )
}