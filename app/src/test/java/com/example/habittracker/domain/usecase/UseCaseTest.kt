
package com.example.habittracker.domain.usecase

import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.model.HabitLog
import com.example.habittracker.domain.repository.HabitRepository
import com.example.habittracker.domain.repository.HabitLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UseCaseTest {

    // 1. GetAllHabitsUseCase
    @Test
    fun getAllHabits_returnsAllHabits() = runBlocking {
        val habit1 = sampleHabit(id = 1L)
        val habit2 = sampleHabit(id = 2L, title = "Reading")

        val repository = FakeHabitRepository(
            initialHabits = listOf(habit1, habit2)
        )

        val useCase = GetAllHabitsUseCase(repository)

        val result = useCase().first()

        assertEquals(2, result.size)
        assertEquals(listOf(habit1, habit2), result)
    }

    // 2. AddHabitUseCase
    @Test
    fun addHabit_insertsHabitIntoRepository() = runBlocking {
        val repository = FakeHabitRepository()
        val useCase = AddHabitUseCase(repository)
        val habit = sampleHabit(id = 1L)

        useCase(habit)

        assertEquals(listOf(habit), repository.habits.value)
    }

    // 3. UpdateHabitUseCase
    @Test
    fun updateHabit_updatesExistingHabit() = runBlocking {
        val originalHabit = sampleHabit(id = 1L)
        val updatedHabit = originalHabit.copy(
            title = "Updated habit",
            description = "Updated description"
        )

        val repository = FakeHabitRepository(
            initialHabits = listOf(originalHabit)
        )

        val useCase = UpdateHabitUseCase(repository)

        useCase(updatedHabit)

        assertEquals(updatedHabit, repository.habits.value.first())
    }

    // 4. DeleteHabitUseCase
    @Test
    fun deleteHabit_removesHabitFromRepository() = runBlocking {
        val habit = sampleHabit(id = 1L)

        val repository = FakeHabitRepository(
            initialHabits = listOf(habit)
        )

        val useCase = DeleteHabitUseCase(repository)

        useCase(habit)

        assertTrue(repository.habits.value.isEmpty())
    }

    // 5. CompleteHabitUseCase
    @Test
    fun completeHabit_insertsNewLogWhenLogDoesNotExist() = runBlocking {
        val repository = FakeHabitLogRepository()
        val useCase = CompleteHabitUseCase(repository)

        useCase(
            habitId = 1L,
            date = "2026-11-04"
        )

        assertEquals(1, repository.logs.size)

        val insertedLog = repository.logs.first()

        assertEquals(1L, insertedLog.habitId)
        assertEquals("2026-11-04", insertedLog.date)
        assertEquals(1, insertedLog.completedCount)
        assertTrue(insertedLog.isCompleted)
    }

    @Test
    fun completeHabit_updatesExistingIncompleteLog() = runBlocking {
        val existingLog = HabitLog(
            id = 10L,
            habitId = 1L,
            date = "2026-11-04",
            completedCount = 0,
            isCompleted = false
        )

        val repository = FakeHabitLogRepository(
            initialLogs = listOf(existingLog)
        )

        val useCase = CompleteHabitUseCase(repository)

        useCase(
            habitId = 1L,
            date = "2026-11-04"
        )

        assertEquals(1, repository.logs.size)

        val updatedLog = repository.logs.first()

        assertEquals(10L, updatedLog.id)
        assertEquals(1L, updatedLog.habitId)
        assertEquals("2026-11-04", updatedLog.date)
        assertEquals(1, updatedLog.completedCount)
        assertTrue(updatedLog.isCompleted)
    }

    @Test
    fun completeHabit_doesNothingWhenLogAlreadyCompleted() = runBlocking {
        val existingLog = HabitLog(
            id = 10L,
            habitId = 1L,
            date = "2026-11-04",
            completedCount = 1,
            isCompleted = true
        )

        val repository = FakeHabitLogRepository(
            initialLogs = listOf(existingLog)
        )

        val useCase = CompleteHabitUseCase(repository)

        useCase(
            habitId = 1L,
            date = "2026-11-04"
        )

        assertEquals(1, repository.logs.size)
        assertEquals(existingLog, repository.logs.first())
    }

    // 6. GetHabitHistoryUseCase
    @Test
    fun getHabitHistory_returnsLogsForSelectedHabit() = runBlocking {
        val log1 = sampleLog(id = 1L, habitId = 1L)
        val log2 = sampleLog(id = 2L, habitId = 1L)
        val log3 = sampleLog(id = 3L, habitId = 2L)

        val repository = FakeHabitLogRepository(
            initialLogs = listOf(log1, log2, log3)
        )

        val useCase = GetHabitHistoryUseCase(repository)

        val result = useCase(habitId = 1L).first()

        assertEquals(2, result.size)
        assertEquals(listOf(log1, log2), result)
    }

    // Test data
    private fun sampleHabit(
        id: Long = 1L,
        title: String = "Exercise"
    ) = Habit(
        id = id,
        title = title,
        description = "Daily exercise",
        targetDays = listOf("Monday", "Tuesday"),
        createdAt = 1730000000000L,
        isCompleted = false
    )

    private fun sampleLog(
        id: Long = 1L,
        habitId: Long = 1L
    ) = HabitLog(
        id = id,
        habitId = habitId,
        date = "2026-11-04",
        completedCount = 1,
        isCompleted = true
    )
}

// Fake HabitRepository
private class FakeHabitRepository(
    initialHabits: List<Habit> = emptyList()
) : HabitRepository {

    val habits = MutableStateFlow(initialHabits)

    override fun getAllHabits(): Flow<List<Habit>> {
        return habits
    }

    override suspend fun insertHabit(habit: Habit) {
        habits.value = habits.value + habit
    }

    override suspend fun updateHabit(habit: Habit) {
        habits.value = habits.value.map {
            if (it.id == habit.id) habit else it
        }
    }

    override suspend fun deleteHabit(habit: Habit) {
        habits.value = habits.value.filterNot {
            it.id == habit.id
        }
    }
}

// Fake HabitLogRepository
private class FakeHabitLogRepository(
    initialLogs: List<HabitLog> = emptyList()
) : HabitLogRepository {

    val logs = initialLogs.toMutableList()

    override fun getLogsForHabit(
        habitId: Long
    ): Flow<List<HabitLog>> {
        return flowOf(
            logs.filter { it.habitId == habitId }
        )
    }

    override fun getLogForDate(
        habitId: Long,
        date: String
    ): Flow<HabitLog?> {
        return flowOf(
            logs.firstOrNull {
                it.habitId == habitId && it.date == date
            }
        )
    }

    override suspend fun insertLog(log: HabitLog): Long {
        logs.add(log)
        return log.id
    }

    override suspend fun updateLog(log: HabitLog) {
        val index = logs.indexOfFirst {
            it.id == log.id
        }

        if (index != -1) {
            logs[index] = log
        }
    }

    override suspend fun deleteLog(log: HabitLog) {
        logs.removeAll {
            it.id == log.id
        }
    }
}
