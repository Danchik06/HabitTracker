
package com.example.habittracker.domain.repository

import com.example.habittracker.domain.model.HabitLog
import kotlinx.coroutines.flow.Flow

interface HabitLogRepository {

    // Белгілі бір әдеттің барлық тарихын алу
    fun getLogsForHabit(habitId: Long): Flow<List<HabitLog>>

    // Белгілі бір күндегі жазбаны алу
    fun getLogForDate(
        habitId: Long,
        date: String
    ): Flow<HabitLog?>

    // Жаңа тарих жазбасын қосу
    suspend fun insertLog(log: HabitLog): Long

    // Тарих жазбасын жаңарту
    suspend fun updateLog(log: HabitLog)

    // Тарих жазбасын өшіру
    suspend fun deleteLog(log: HabitLog)
}
