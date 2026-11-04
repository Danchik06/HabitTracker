package com.example.habittracker.domain.model

data class Habit(
    val id: Long = 0,
    val title: String,
    val description: String,
    val targetDays: List<String>,
    val createdAt: Long,
    val isCompleted: Boolean = false
)