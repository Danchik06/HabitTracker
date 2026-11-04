
package com.example.habittracker.data.mapper

import com.example.habittracker.data.local.HabitLogEntity
import com.example.habittracker.domain.model.HabitLog

// Entity -> Domain
fun HabitLogEntity.toDomain(): HabitLog {
    return HabitLog(
        id = id,
        habitId = habitId,
        date = date,
        completedCount = completedCount,
        isCompleted = isCompleted
    )
}

// Domain -> Entity
fun HabitLog.toEntity(): HabitLogEntity {
    return HabitLogEntity(
        id = id,
        habitId = habitId,
        date = date,
        completedCount = completedCount,
        isCompleted = isCompleted
    )
}
