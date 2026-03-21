package com.smart.docat.data.repository

import com.smart.docat.data.local.dao.SessionHistoryDao
import com.smart.docat.data.local.entity.SessionHistoryEntity
import com.smart.docat.domain.model.SessionHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionHistoryRepository(
    private val sessionHistoryDao: SessionHistoryDao
) {

    fun getHistoryForDate(fecha: String): Flow<List<SessionHistory>> {
        return sessionHistoryDao.getSessionHistoryForDate(fecha).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getTotalTimeForDate(fecha: String): Int {
        return sessionHistoryDao.getTotalTimeForDate(fecha) ?: 0
    }

    suspend fun getTotalTimeForTask(tareaId: Long, fecha: String): Int {
        return sessionHistoryDao.getTotalTimeForTask(tareaId, fecha) ?: 0
    }

    suspend fun saveSession(sessionHistory: SessionHistory) {
        sessionHistoryDao.insertSessionHistory(sessionHistory.toEntity())
    }
}

private fun SessionHistoryEntity.toDomain(): SessionHistory {
    return SessionHistory(
        id = id,
        tareaId = tareaId,
        fecha = fecha,
        tiempoReal = tiempoReal
    )
}

private fun SessionHistory.toEntity(): SessionHistoryEntity {
    return SessionHistoryEntity(
        id = id,
        tareaId = tareaId,
        fecha = fecha,
        tiempoReal = tiempoReal
    )
}