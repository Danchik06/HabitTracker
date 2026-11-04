
package com.example.habittracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitLogDao {

    @Query("""
        SELECT * FROM habit_logs
        WHERE habit_id = :habitId
        ORDER BY date DESC
    """)
    fun getLogsForHabit(
        habitId: Long
    ): Flow<List<HabitLogEntity>>

    @Query("""
        SELECT * FROM habit_logs
        WHERE habit_id = :habitId
        AND date = :date
        LIMIT 1
    """)
    fun getLogForDate(
        habitId: Long,
        date: String
    ): Flow<HabitLogEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: HabitLogEntity): Long

    @Update
    suspend fun updateLog(log: HabitLogEntity)

    @Delete
    suspend fun deleteLog(log: HabitLogEntity)
}
