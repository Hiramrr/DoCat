package com.smart.docat.domain.model

data class SessionHistory(
    val id: Long = 0,
    val tareaId: Long,
    val fecha: String,
    val tiempoReal: Int
)