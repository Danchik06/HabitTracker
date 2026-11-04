package com.example.habittracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    // 1. Барлық әдетті алу (жаңадан құрылғанынан бастап сұрыптау)
    // Flow қолданылғандықтан, реактивті түрде деректер өзгерсе автоматты жаңарады
    @Query("SELECT * FROM habits ORDER BY created_at DESC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    // 2. Жаңа әдет қосу
    // OnConflictStrategy.REPLACE — егер ID қайталанса, ескісін жаңасымен алмастырады
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)

    // 3. Әдетті өшіру
    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    // 4. Әдетті жаңарту
    @Update
    suspend fun updateHabit(habit: HabitEntity)
}