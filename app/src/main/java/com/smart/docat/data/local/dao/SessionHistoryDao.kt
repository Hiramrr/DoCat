package com.smart.docat.data.local.dao

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

    @Query("SELECT DISTINCT fecha FROM session_history")
    suspend fun getAllActiveDates(): List<String>

    @Query("SELECT COUNT(*) FROM session_history WHERE fecha LIKE :monthPrefix || '%'")
    suspend fun getSessionCountForMonth(monthPrefix: String): Int

    @Query("SELECT COALESCE(SUM(tiempo_real), 0) FROM session_history WHERE fecha LIKE :monthPrefix || '%'")
    suspend fun getTotalTimeForMonth(monthPrefix: String): Int

    @Query("SELECT COUNT(DISTINCT fecha) FROM session_history WHERE fecha LIKE :monthPrefix || '%'")
    suspend fun getActiveDayCountForMonth(monthPrefix: String): Int

    @Query("SELECT COALESCE(SUM(tiempo_real), 0) FROM session_history WHERE fecha = :fecha")
    suspend fun getTotalTimeForDateSync(fecha: String): Int

    @Query("SELECT COUNT(*) FROM session_history WHERE fecha = :fecha")
    suspend fun getSessionCountForDate(fecha: String): Int
}