package com.example.habittracker

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.example.habittracker.data.local.AppDatabase
import com.example.habittracker.data.local.HabitDao
import com.example.habittracker.data.local.HabitEntity

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: HabitDao

    @Before
    fun setup() {
        val context = ApplicationProvider
            .getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        dao = database.habitDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun crudOperationsWork() = runBlocking {

        // 1. CREATE — әдет қосу
        val habit = HabitEntity(
            title = "Кітап оқу",
            description = "Күніне 10 бет оқу",
            targetDays = listOf("MONDAY", "FRIDAY"),
            createdAt = System.currentTimeMillis(),
            isCompleted = false
        )

        dao.insertHabit(habit)

        // 2. READ — әдетті оқу
        val habits = dao.getAllHabits().first()

        assertEquals(1, habits.size)
        assertEquals("Кітап оқу", habits[0].title)

        // 3. UPDATE — әдетті өзгерту
        val savedHabit = habits[0]
        val updatedHabit = savedHabit.copy(
            title = "Күніне кітап оқу"
        )

        dao.updateHabit(updatedHabit)

        val updatedHabits = dao.getAllHabits().first()

        assertEquals(
            "Күніне кітап оқу",
            updatedHabits[0].title
        )

        // 4. DELETE — әдетті өшіру
        dao.deleteHabit(updatedHabit)

        val remainingHabits = dao.getAllHabits().first()

        assertTrue(remainingHabits.isEmpty())
    }
}