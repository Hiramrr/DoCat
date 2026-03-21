package com.smart.docat.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "nombre")
    val nombre: String,

    @ColumnInfo(name = "tiempo_trabajo")
    val tiempoTrabajo: Int,

    @ColumnInfo(name = "tiempo_descanso")
    val tiempoDescanso: Int,

    @ColumnInfo(name = "repeticiones")
    val repeticiones: Int,

    @ColumnInfo(name = "estado")
    val estado: String,

    @ColumnInfo(name = "fecha")
    val fecha: String
)