
package com.example.habittracker.data.repository

import com.example.habittracker.data.local.HabitLogDao
import com.example.habittracker.data.mapper.toDomain
import com.example.habittracker.data.mapper.toEntity
import com.example.habittracker.domain.model.HabitLog
import com.example.habittracker.domain.repository.HabitLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HabitLogRepositoryImpl(
    private val habitLogDao: HabitLogDao
) : HabitLogRepository {

    override fun getLogsForHabit(
        habitId: Long
    ): Flow<List<HabitLog>> {
        return habitLogDao.getLogsForHabit(habitId)
            .map { logs ->
                logs.map { it.toDomain() }
            }
    }

    override fun getLogForDate(
        habitId: Long,
        date: String
    ): Flow<HabitLog?> {
        return habitLogDao.getLogForDate(habitId, date)
            .map { it?.toDomain() }
    }

    override suspend fun insertLog(log: HabitLog): Long {
        return habitLogDao.insertLog(log.toEntity())
    }

    override suspend fun updateLog(log: HabitLog) {
        habitLogDao.updateLog(log.toEntity())
    }

    override suspend fun deleteLog(log: HabitLog) {
        habitLogDao.deleteLog(log.toEntity())
    }
}
