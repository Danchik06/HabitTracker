package com.example.habittracker.data.mapper

import com.example.habittracker.data.local.HabitEntity
import com.example.habittracker.domain.model.Habit

// HabitEntity -> Habit (Domain)
fun HabitEntity.toDomain(): Habit {
    return Habit(
        id = id,
        title = title,
        description = description,
        targetDays = targetDays,
        createdAt = createdAt,
        isCompleted = isCompleted
    )
}

// Habit (Domain) -> HabitEntity
fun Habit.toEntity(): HabitEntity {
    return HabitEntity(
        id = id,
        title = title,
        description = description,
        targetDays = targetDays,
        createdAt = createdAt,
        isCompleted = isCompleted
    )
}