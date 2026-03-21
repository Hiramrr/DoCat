package com.smart.docat.domain.model

data class SubTask(
    val id: Long,
    val tareaId: Long,
    val nombre: String,
    val tiempoAsignado: Int,
    val orden: Int
)