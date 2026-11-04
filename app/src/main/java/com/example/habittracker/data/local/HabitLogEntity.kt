
package com.example.habittracker.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_logs",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habit_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(
            value = ["habit_id", "date"],
            unique = true
        )
    ]
)
data class HabitLogEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "habit_id")
    val habitId: Long,

    // Күн форматы: yyyy-MM-dd
    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "completed_count")
    val completedCount: Int = 0,

    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false
)
