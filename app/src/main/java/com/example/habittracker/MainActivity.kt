package com.example.habittracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.habittracker.presentation.state.HabitUiEvent
import com.example.habittracker.presentation.ui.home.HomeScreen
import com.example.habittracker.presentation.ui.theme.HabitTrackerTheme
import com.example.habittracker.presentation.viewmodel.HabitViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: HabitViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HabitTrackerTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val snackbarHostState = remember {
                    SnackbarHostState()
                }

                LaunchedEffect(viewModel) {
                    viewModel.events.collect { event ->
                        when (event) {
                            is HabitUiEvent.ShowMessage -> {
                                snackbarHostState.showSnackbar(
                                    event.message
                                )
                            }
                        }
                    }
                }

                HomeScreen(
                    uiState = uiState,
                    snackbarHostState = snackbarHostState,
                    onCompleteHabit = viewModel::completeHabit,
                    onDeleteHabit = viewModel::deleteHabit
                )
            }
        }
    }
}