package com.example.habittracker.domain.usecase

import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow

class GetAllHabitsUseCase(
    private val repository: HabitRepository
) {
    operator fun invoke(): Flow<List<Habit>> {
        return repository.getAllHabits()
    }
}