package com.smart.docat.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.smart.docat.data.local.entity.TaskEntity

@Entity(
    tableName = "session_history",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["tarea_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["tarea_id"])]
)
data class SessionHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "tarea_id")
    val tareaId: Long,

    @ColumnInfo(name = "fecha")
    val fecha: String,

    @ColumnInfo(name = "tiempo_real")
    val tiempoReal: Int,
)