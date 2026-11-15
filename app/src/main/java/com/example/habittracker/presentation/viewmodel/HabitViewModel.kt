
package com.example.habittracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.usecase.AddHabitUseCase
import com.example.habittracker.domain.usecase.CompleteHabitUseCase
import com.example.habittracker.domain.usecase.DeleteHabitUseCase
import com.example.habittracker.domain.usecase.GetAllHabitsUseCase
import com.example.habittracker.domain.usecase.UpdateHabitUseCase
import com.example.habittracker.presentation.state.HabitUiEvent
import com.example.habittracker.presentation.state.HabitUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltViewModel
class HabitViewModel @Inject constructor(
    private val getAllHabitsUseCase: GetAllHabitsUseCase,
    private val addHabitUseCase: AddHabitUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val completeHabitUseCase: CompleteHabitUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HabitUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HabitUiEvent>()
    val events = _events.asSharedFlow()

    init {
        observeHabits()
    }

    private fun observeHabits() {
        viewModelScope.launch {
            getAllHabitsUseCase()
                .catch { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Деректерді жүктеу қатесі"
                        )
                    }
                }
                .collect { habits ->
                    _uiState.update {
                        it.copy(
                            habits = habits,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    fun addHabit(
        title: String,
        description: String,
        targetDays: List<String>
    ) {
        if (title.isBlank()) {
            sendMessage("Әдет атауын енгізіңіз")
            return
        }

        executeOperation("Әдет сәтті қосылды") {
            addHabitUseCase(
                Habit(
                    title = title.trim(),
                    description = description.trim(),
                    targetDays = targetDays,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun updateHabit(habit: Habit) {
        executeOperation("Әдет жаңартылды") {
            updateHabitUseCase(habit)
        }
    }

    fun deleteHabit(habit: Habit) {
        executeOperation("Әдет өшірілді") {
            deleteHabitUseCase(habit)
        }
    }

    fun completeHabit(habitId: Long) {
        val today = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).format(Date())

        executeOperation("Әдет орындалды") {
            completeHabitUseCase(habitId, today)
        }
    }

    private fun executeOperation(
        successMessage: String,
        action: suspend () -> Unit
    ) {
        viewModelScope.launch {
            try {
                action()
                _events.emit(
                    HabitUiEvent.ShowMessage(successMessage)
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _events.emit(
                    HabitUiEvent.ShowMessage(
                        "Операция орындалмады"
                    )
                )
            }
        }
    }

    private fun sendMessage(message: String) {
        viewModelScope.launch {
            _events.emit(HabitUiEvent.ShowMessage(message))
        }
    }
}
