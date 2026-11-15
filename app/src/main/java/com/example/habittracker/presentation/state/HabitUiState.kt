package com.example.habittracker.presentation.state

import com.example.habittracker.domain.model.Habit

data class HabitUiState(
    val habits: List<Habit> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)