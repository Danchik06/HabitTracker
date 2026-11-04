package com.example.habittracker.domain.usecase

import com.example.habittracker.domain.model.HabitLog
import com.example.habittracker.domain.repository.HabitLogRepository
import kotlinx.coroutines.flow.Flow

class GetHabitHistoryUseCase(
    private val repository: HabitLogRepository
) {
    operator fun invoke(habitId: Long): Flow<List<HabitLog>> {
        return repository.getLogsForHabit(habitId)
    }
}