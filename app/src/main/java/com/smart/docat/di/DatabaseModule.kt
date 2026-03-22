package com.smart.docat.di

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.smart.docat.data.local.entity.SessionHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionHistoryDao{

    @Query("SELECT * FROM session_history WHERE fecha = :fecha")
    fun getSessionHistoryForDate(fecha: String): Flow<List<SessionHistoryEntity>>

    @Query("SELECT SUM(tiempo_real) FROM session_history WHERE fecha = :fecha")
    suspend fun getTotalTimeForDate(fecha: String): Int?

    @Query("SELECT SUM(tiempo_real) FROM session_history")
    suspend fun getTotalTime(): Int?

    @Query("SELECT SUM(tiempo_real) FROM session_history WHERE tarea_id = :tareaId AND fecha = :fecha")
    suspend fun getTotalTimeForTask(tareaId: Long, fecha: String): Int?

    @Insert
    suspend fun insertSessionHistory(sessionHistory: SessionHistoryEntity)

    @Update
    suspend fun updateSessionHistory(sessionHistory: SessionHistoryEntity)

    @Delete
    suspend fun deleteSessionHistory(sessionHistory: SessionHistoryEntity)

}