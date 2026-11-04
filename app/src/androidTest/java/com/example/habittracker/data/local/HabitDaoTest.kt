package com.example.habittracker.data.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HabitDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var habitDao: HabitDao

    @Before
    fun setup() {
        val context = InstrumentationRegistry
            .getInstrumentation()
            .targetContext

        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()

        habitDao = database.habitDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertHabit_andReadHabit() = runBlocking {
        // Arrange
        val habit = HabitEntity(
            title = "Morning exercise",
            description = "Exercise every morning",
            targetDays = listOf("Monday", "Wednesday", "Friday"),
            createdAt = 1000L
        )

        // Act
        habitDao.insertHabit(habit)
        val result = habitDao.getAllHabits().first()

        // Assert
        assertEquals(1, result.size)
        assertEquals("Morning exercise", result[0].title)
        assertEquals(
            listOf("Monday", "Wednesday", "Friday"),
            result[0].targetDays
        )
        assertTrue(result[0].id > 0)
    }

    @Test
    fun updateHabit_updatesExistingHabit() = runBlocking {
        // Arrange
        val habit = HabitEntity(
            title = "Reading",
            description = "Read every day",
            targetDays = listOf("Monday", "Tuesday"),
            createdAt = 2000L
        )

        habitDao.insertHabit(habit)
        val insertedHabit = habitDao.getAllHabits().first().first()

        // Act
        val updatedHabit = insertedHabit.copy(
            title = "Reading books",
            description = "Read for 30 minutes"
        )

        habitDao.updateHabit(updatedHabit)

        val result = habitDao.getAllHabits().first()

        // Assert
        assertEquals(1, result.size)
        assertEquals("Reading books", result[0].title)
        assertEquals("Read for 30 minutes", result[0].description)
    }

    @Test
    fun deleteHabit_removesExistingHabit() = runBlocking {
        // Arrange
        val habit = HabitEntity(
            title = "Walking",
            description = "Walk 5000 steps",
            targetDays = listOf("Saturday"),
            createdAt = 3000L
        )

        habitDao.insertHabit(habit)
        val insertedHabit = habitDao.getAllHabits().first().first()

        // Act
        habitDao.deleteHabit(insertedHabit)

        val result = habitDao.getAllHabits().first()

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun getAllHabits_returnsEmptyList_whenDatabaseIsEmpty() =
        runBlocking {
            val result = habitDao.getAllHabits().first()

            assertTrue(result.isEmpty())
        }
}
