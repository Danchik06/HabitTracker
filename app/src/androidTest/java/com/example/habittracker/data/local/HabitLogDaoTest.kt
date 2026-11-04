
package com.example.habittracker.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.content.Context
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HabitLogDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var habitDao: HabitDao
    private lateinit var habitLogDao: HabitLogDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        habitDao = database.habitDao()
        habitLogDao = database.habitLogDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    private fun insertTestHabit() = runBlocking {
        habitDao.insertHabit(
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
    fun insertAndReadHabitLog() = runBlocking {
        insertTestHabit()

        val log = HabitLogEntity(
            habitId = 1L,
            date = "2026-11-04",
            completedCount = 1,
            isCompleted = true
        )

        habitLogDao.insertLog(log)

        val result = habitLogDao
            .getLogForDate(1L, "2026-11-04")
            .first()

        assertNotNull(result)
        assertEquals(1L, result?.habitId)
        assertTrue(result?.isCompleted == true)
    }

    @Test
    fun updateHabitLog() = runBlocking {
        insertTestHabit()

        val log = HabitLogEntity(
            habitId = 1L,
            date = "2026-11-04",
            completedCount = 1,
            isCompleted = false
        )

        val id = habitLogDao.insertLog(log)

        habitLogDao.updateLog(
            log.copy(
                id = id,
                completedCount = 2,
                isCompleted = true
            )
        )

        val result = habitLogDao
            .getLogForDate(1L, "2026-11-04")
            .first()

        assertEquals(2, result?.completedCount)
        assertTrue(result?.isCompleted == true)
    }

    @Test
    fun deleteHabitLog() = runBlocking {
        insertTestHabit()

        val log = HabitLogEntity(
            habitId = 1L,
            date = "2026-11-04",
            completedCount = 1,
            isCompleted = true
        )

        habitLogDao.insertLog(log)

        val inserted = habitLogDao
            .getLogForDate(1L, "2026-11-04")
            .first()

        assertNotNull(inserted)

        habitLogDao.deleteLog(inserted!!)

        val result = habitLogDao
            .getLogForDate(1L, "2026-11-04")
            .first()

        assertEquals(null, result)
    }

    @Test
    fun deleteHabitShouldCascadeDeleteLogs() = runBlocking {
        insertTestHabit()

        habitLogDao.insertLog(
            HabitLogEntity(
                habitId = 1L,
                date = "2026-11-04",
                completedCount = 1,
                isCompleted = true
            )
        )

        val logsBefore = habitLogDao
            .getLogsForHabit(1L)
            .first()

        assertFalse(logsBefore.isEmpty())

        val habit = habitDao.getAllHabits().first().first()

        habitDao.deleteHabit(habit)

        val logsAfter = habitLogDao
            .getLogsForHabit(1L)
            .first()

        assertTrue(logsAfter.isEmpty())
    }
}
