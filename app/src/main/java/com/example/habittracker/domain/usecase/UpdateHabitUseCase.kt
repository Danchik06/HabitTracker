package com.example.habittracker.domain.usecase

import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.repository.HabitRepository

class UpdateHabitUseCase(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit) {
        repository.updateHabit(habit)
    }
}