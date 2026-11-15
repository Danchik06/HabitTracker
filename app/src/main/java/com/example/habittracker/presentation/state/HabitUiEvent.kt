package com.example.habittracker.presentation.state

sealed interface HabitUiEvent {

    data class ShowMessage(
        val message: String
    ) : HabitUiEvent
}