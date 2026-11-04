package com.example.habittracker.data.repository

import com.example.habittracker.data.local.HabitDao
import com.example.habittracker.data.mapper.toDomain
import com.example.habittracker.data.mapper.toEntity
import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HabitRepositoryImpl(
    private val habitDao: HabitDao
) : HabitRepository {

    // 1. DAO-дан ағынды алып, Entity тізімін Domain тізіміне айналдырамыз
    override fun getAllHabits(): Flow<List<Habit>> {
        return habitDao.getAllHabits().map { entityList ->
            entityList.map { entity -> entity.toDomain() }
        }
    }

    // 2. Жаңа әдетті қосу командасын DAO-ға жіберу (Domain -> Entity)
    override suspend fun insertHabit(habit: Habit) {
        habitDao.insertHabit(habit.toEntity())
    }

    // 3. Әдетті өшіру командасы (Domain -> Entity)
    override suspend fun deleteHabit(habit: Habit) {
        habitDao.deleteHabit(habit.toEntity())
    }

    // 4. Әдетті жаңарту командасы (Domain -> Entity)
    override suspend fun updateHabit(habit: Habit) {
        habitDao.updateHabit(habit.toEntity())
    }
}