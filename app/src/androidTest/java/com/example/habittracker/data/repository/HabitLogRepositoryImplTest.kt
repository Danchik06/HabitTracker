
package com.example.habittracker.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.habittracker.data.local.AppDatabase
import com.example.habittracker.data.local.HabitEntity
import com.example.habittracker.domain.model.HabitLog
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HabitLogRepositoryImplTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: HabitLogRepositoryImpl

    @Before
    fun setup() {
        val context: Context =
            InstrumentationRegistry.getInstrumentation().targetContext

        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        repository = HabitLogRepositoryImpl(database.habitLogDao())
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    private fun insertTestHabit() = runBlocking {
        database.habitDao().insertHabit(
            HabitEntity(
                id = 1L,
                title = "Reading",
                description = "Read 20 pages",
                targetDays = listOf("MONDAY", "TUESDAY"),
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Test
    fun insertLogAndReadIt() = runBlocking {
        insertTestHabit()

        val log = HabitLog(
            habitId = 1L,
            date = "2026-11-04",
            completedCount = 1,
            isCompleted = true
        )

        val insertedId = repository.insertLog(log)

        val result = repository.getLogsForHabit(1L).first()

        assertEquals(1, result.size)
        assertEquals(insertedId, result[0].id)
        assertEquals("2026-11-04", result[0].date)
        assertTrue(result[0].isCompleted)
    }

    @Test
    fun getLogForSpecificDate() = runBlocking {
        insertTestHabit()

        repository.insertLog(
            HabitLog(
                habitId = 1L,
                date = "2026-11-04",
                completedCount = 2,
                isCompleted = true
            )
        )

        val result = repository
            .getLogForDate(1L, "2026-11-04")
            .first()

        assertNotNull(result)
        assertEquals(2, result?.completedCount)
    }

    @Test
    fun updateLog() = runBlocking {
        insertTestHabit()

        val id = repository.insertLog(
            HabitLog(
                habitId = 1L,
                date = "2026-11-04",
                completedCount = 1,
                isCompleted = false
            )
        )

        repository.updateLog(
            HabitLog(
                id = id,
                habitId = 1L,
                date = "2026-11-04",
                completedCount = 3,
                isCompleted = true
            )
        )

        val result = repository
            .getLogForDate(1L, "2026-11-04")
            .first()

        assertEquals(3, result?.completedCount)
        assertTrue(result?.isCompleted == true)
    }

    @Test
    fun deleteLog() = runBlocking {
        insertTestHabit()

        val id = repository.insertLog(
            HabitLog(
                habitId = 1L,
                date = "2026-11-04",
                completedCount = 1,
                isCompleted = true
            )
        )

        repository.deleteLog(
            HabitLog(
                id = id,
                habitId = 1L,
                date = "2026-11-04",
                completedCount = 1,
                isCompleted = true
            )
        )

        val result = repository.getLogsForHabit(1L).first()

        assertTrue(result.isEmpty())
    }
}
