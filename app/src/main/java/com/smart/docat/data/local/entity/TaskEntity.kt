package com.smart.docat.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val tiempoTrabajo: Int,
    val tiempoDescanso: Int,
    val repeticiones: Int,
    val estado: String,
    val fecha: String
)