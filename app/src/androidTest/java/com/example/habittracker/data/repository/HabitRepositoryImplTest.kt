
package com.example.habittracker.data.repository

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.habittracker.data.local.AppDatabase
import com.example.habittracker.domain.model.Habit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HabitRepositoryImplTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: HabitRepositoryImpl

    @Before
    fun setup() {
        val context = InstrumentationRegistry
            .getInstrumentation()
            .targetContext

        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()

        repository = HabitRepositoryImpl(database.habitDao())
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertHabit_andReadHabit() = runBlocking {
        val habit = Habit(
            title = "Morning exercise",
            description = "Exercise every morning",
            targetDays = listOf("Monday", "Wednesday"),
            createdAt = 1000L
        )

        repository.insertHabit(habit)

        val result = repository.getAllHabits().first()

        assertEquals(1, result.size)
        assertEquals(habit.title, result[0].title)
        assertEquals(habit.description, result[0].description)
        assertEquals(habit.targetDays, result[0].targetDays)
        assertTrue(result[0].id > 0)
    }

    @Test
    fun updateHabit_updatesExistingHabit() = runBlocking {
        val habit = Habit(
            title = "Reading",
            description = "Read every day",
            targetDays = listOf("Monday", "Tuesday"),
            createdAt = 2000L
        )

        repository.insertHabit(habit)

        val insertedHabit = repository.getAllHabits()
            .first()
            .first()

        val updatedHabit = insertedHabit.copy(
            title = "Reading books",
            description = "Read for 30 minutes"
        )

        repository.updateHabit(updatedHabit)

        val result = repository.getAllHabits().first()

        assertEquals(1, result.size)
        assertEquals("Reading books", result[0].title)
        assertEquals("Read for 30 minutes", result[0].description)
    }

    @Test
    fun deleteHabit_removesExistingHabit() = runBlocking {
        val habit = Habit(
            title = "Walking",
            description = "Walk 5000 steps",
            targetDays = listOf("Saturday"),
            createdAt = 3000L
        )

        repository.insertHabit(habit)

        val insertedHabit = repository.getAllHabits()
            .first()
            .first()

        repository.deleteHabit(insertedHabit)

        val result = repository.getAllHabits().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun getAllHabits_returnsEmptyList_whenDatabaseIsEmpty() =
        runBlocking {
            val result = repository.getAllHabits().first()

            assertTrue(result.isEmpty())
        }
}
