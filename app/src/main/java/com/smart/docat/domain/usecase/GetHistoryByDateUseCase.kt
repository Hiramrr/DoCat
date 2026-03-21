package com.smart.docat.domain.usecase

import com.smart.docat.data.repository.SessionHistoryRepository
import com.smart.docat.domain.model.SessionHistory
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

data class HistoryByDateResult(
    val sessions: Flow<List<SessionHistory>>,
    val totalTime: Int
)

class GetHistoryByDateUseCase @Inject constructor(
    private val  sessionHistoryRepository: SessionHistoryRepository
) {
    suspend operator fun invoke(fecha: String): HistoryByDateResult{
        return HistoryByDateResult(
            sessions = sessionHistoryRepository.getHistoryForDate(fecha),
            totalTime = sessionHistoryRepository.getTotalTimeForDate(fecha)
        )
    }
}
