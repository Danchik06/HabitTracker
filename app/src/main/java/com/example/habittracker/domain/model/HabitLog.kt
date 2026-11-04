
package com.example.habittracker.domain.model

data class HabitLog(
    val id: Long = 0,
    val habitId: Long,
    val date: String,
    val completedCount: Int = 0,
    val isCompleted: Boolean = false
)
