package com.smart.docat.domain.model

import com.smart.docat.domain.model.TaskStatus


data class Task(
    val id: Long,
    val nombre: String,
    val tiempoTrabajo: Int,
    val tiempoDescanso: Int,
    val repeticiones: Int,
    val estado: TaskStatus,
    val fecha: String,
    val subTareas: List<SubTask>
)
