package com.example.habittracker.domain.repository

import com.example.habittracker.domain.model.Habit
import kotlinx.coroutines.flow.Flow

interface HabitRepository {

    // Барлық әдеттерді бақылау (Domain моделінің тізімін қайтарады)
    fun getAllHabits(): Flow<List<Habit>>

    // Жаңа әдет қосу
    suspend fun insertHabit(habit: Habit)

    // Әдетті өшіру
    suspend fun deleteHabit(habit: Habit)

    // Әдетті жаңарту
    suspend fun updateHabit(habit: Habit)
}