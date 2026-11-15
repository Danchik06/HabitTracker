package com.example.habittracker.presentation

import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.model.HabitLog
import com.example.habittracker.domain.repository.HabitLogRepository
import com.example.habittracker.domain.repository.HabitRepository
import com.example.habittracker.domain.usecase.AddHabitUseCase
import com.example.habittracker.domain.usecase.CompleteHabitUseCase
import com.example.habittracker.domain.usecase.DeleteHabitUseCase
import com.example.habittracker.domain.usecase.GetAllHabitsUseCase
import com.example.habittracker.domain.usecase.UpdateHabitUseCase
import com.example.habittracker.presentation.state.HabitUiEvent
import com.example.habittracker.presentation.viewmodel.HabitViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HabitViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val habitRepository = FakeHabitRepository()
    private val habitLogRepository = FakeHabitLogRepository()

    private fun createViewModel(): HabitViewModel {
        return HabitViewModel(
            getAllHabitsUseCase = GetAllHabitsUseCase(habitRepository),
            addHabitUseCase = AddHabitUseCase(habitRepository),
            updateHabitUseCase = UpdateHabitUseCase(habitRepository),
            deleteHabitUseCase = DeleteHabitUseCase(habitRepository),
            completeHabitUseCase = CompleteHabitUseCase(habitLogRepository)
        )
    }

    private fun HabitViewModel.nextEvent() =
        kotlinx.coroutines.CoroutineScope(
            kotlinx.coroutines.Dispatchers.Main
        ).async(start = CoroutineStart.UNDISPATCHED) {
            events.first()
        }

    @Test
    fun `initial state should be loading`() = runTest {
        val viewModel = createViewModel()

        assertTrue(viewModel.uiState.value.isLoading)

        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `habits flow should update ui state`() = runTest {
        val habit = Habit(
            id = 1,
            title = "Reading",
            description = "Read a book",
            targetDays = listOf("MONDAY"),
            createdAt = 1000L
        )

        val viewModel = createViewModel()

        advanceUntilIdle()

        habitRepository.habits.value = listOf(habit)

        advanceUntilIdle()

        assertEquals(
            listOf(habit),
            viewModel.uiState.value.habits
        )
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `flow error should update ui state`() = runTest {
        habitRepository.habitsFlow = flow {
            throw IllegalStateException("Database error")
        }

        val viewModel = createViewModel()

        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(
            "Database error",
            viewModel.uiState.value.error
        )
    }

    @Test
    fun `blank title should emit validation message`() = runTest {
        val viewModel = createViewModel()

        val event = viewModel.nextEvent()

        viewModel.addHabit(
            title = "   ",
            description = "Description",
            targetDays = listOf("MONDAY")
        )

        advanceUntilIdle()

        assertEquals(
            HabitUiEvent.ShowMessage("Әдет атауын енгізіңіз"),
            event.await()
        )

        assertTrue(habitRepository.insertedHabits.isEmpty())
    }

    @Test
    fun `add habit should trim input and emit success message`() = runTest {
        val viewModel = createViewModel()

        val event = viewModel.nextEvent()

        viewModel.addHabit(
            title = "  Reading  ",
            description = "  Read books  ",
            targetDays = listOf("MONDAY", "FRIDAY")
        )

        advanceUntilIdle()

        assertEquals(
            HabitUiEvent.ShowMessage("Әдет сәтті қосылды"),
            event.await()
        )

        val savedHabit = habitRepository.insertedHabits.single()

        assertEquals("Reading", savedHabit.title)
        assertEquals("Read books", savedHabit.description)
        assertEquals(
            listOf("MONDAY", "FRIDAY"),
            savedHabit.targetDays
        )
        assertTrue(savedHabit.createdAt > 0L)
    }

    @Test
    fun `update habit should call repository and emit success`() = runTest {
        val viewModel = createViewModel()

        val habit = Habit(
            id = 1,
            title = "Updated habit",
            description = "Updated description",
            targetDays = listOf("TUESDAY"),
            createdAt = 1000L
        )

        val event = viewModel.nextEvent()

        viewModel.updateHabit(habit)

        advanceUntilIdle()

        assertEquals(habit, habitRepository.updatedHabits.single())
        assertEquals(
            HabitUiEvent.ShowMessage("Әдет жаңартылды"),
            event.await()
        )
    }

    @Test
    fun `delete habit should call repository and emit success`() = runTest {
        val viewModel = createViewModel()

        val habit = Habit(
            id = 1,
            title = "Old habit",
            description = "",
            targetDays = listOf("MONDAY"),
            createdAt = 1000L
        )

        val event = viewModel.nextEvent()

        viewModel.deleteHabit(habit)

        advanceUntilIdle()

        assertEquals(habit, habitRepository.deletedHabits.single())
        assertEquals(
            HabitUiEvent.ShowMessage("Әдет өшірілді"),
            event.await()
        )
    }

    @Test
    fun `complete habit should create today's log`() = runTest {
        val viewModel = createViewModel()

        val event = viewModel.nextEvent()

        viewModel.completeHabit(10L)

        advanceUntilIdle()

        val savedLog = habitLogRepository.insertedLog

        assertNotNull(savedLog)
        assertEquals(10L, savedLog!!.habitId)
        assertTrue(savedLog.isCompleted)
        assertEquals(1, savedLog.completedCount)
        assertTrue(
            Regex("""\d{4}-\d{2}-\d{2}""").matches(savedLog.date)
        )

        assertEquals(
            HabitUiEvent.ShowMessage("Әдет орындалды"),
            event.await()
        )
    }

    @Test
    fun `operation failure should emit error message`() = runTest {
        habitRepository.shouldFailOnInsert = true

        val viewModel = createViewModel()

        val event = viewModel.nextEvent()

        viewModel.addHabit(
            title = "Reading",
            description = "",
            targetDays = listOf("MONDAY")
        )

        advanceUntilIdle()

        assertEquals(
            HabitUiEvent.ShowMessage("Операция орындалмады"),
            event.await()
        )
    }

    private class FakeHabitRepository : HabitRepository {

        val habits = MutableStateFlow<List<Habit>>(emptyList())

        var habitsFlow: Flow<List<Habit>> = habits

        val insertedHabits = mutableListOf<Habit>()
        val updatedHabits = mutableListOf<Habit>()
        val deletedHabits = mutableListOf<Habit>()

        var shouldFailOnInsert = false

        override fun getAllHabits(): Flow<List<Habit>> {
            return habitsFlow
        }

        override suspend fun insertHabit(habit: Habit) {
            if (shouldFailOnInsert) {
                throw IllegalStateException("Insert failed")
            }

            insertedHabits.add(habit)
        }

        override suspend fun updateHabit(habit: Habit) {
            updatedHabits.add(habit)
        }

        override suspend fun deleteHabit(habit: Habit) {
            deletedHabits.add(habit)
        }
    }

    private class FakeHabitLogRepository : HabitLogRepository {

        var insertedLog: HabitLog? = null

        override fun getLogsForHabit(
            habitId: Long
        ): Flow<List<HabitLog>> {
            return flowOf(emptyList())
        }

        override fun getLogForDate(
            habitId: Long,
            date: String
        ): Flow<HabitLog?> {
            return flowOf(null)
        }

        override suspend fun insertLog(log: HabitLog): Long {
            insertedLog = log
            return 1L
        }

        override suspend fun updateLog(log: HabitLog) = Unit

        override suspend fun deleteLog(log: HabitLog) = Unit
    }
}