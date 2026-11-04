package com.example.habittracker.domain.usecase

import com.example.habittracker.domain.model.HabitLog
import com.example.habittracker.domain.repository.HabitLogRepository
import kotlinx.coroutines.flow.first

class CompleteHabitUseCase(
    private val repository: HabitLogRepository
) {
    suspend operator fun invoke(
        habitId: Long,
        date: String
    ) {
        val existingLog = repository
            .getLogForDate(habitId, date)
            .first()

        if (existingLog == null) {
            repository.insertLog(
                HabitLog(
                    habitId = habitId,
                    date = date,
                    completedCount = 1,
                    isCompleted = true
                )
            )
        } else if (!existingLog.isCompleted) {
            repository.updateLog(
                existingLog.copy(
                    isCompleted = true,
                    completedCount = maxOf(
                        existingLog.completedCount,
                        1
                    )
                )
            )
        }
    }
}