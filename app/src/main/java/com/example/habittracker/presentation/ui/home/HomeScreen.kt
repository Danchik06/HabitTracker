package com.example.habittracker.presentation.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.habittracker.domain.model.Habit
import com.example.habittracker.presentation.state.HabitUiState
import com.example.habittracker.presentation.ui.components.HabitCard

@Composable
fun HomeScreen(
    uiState: HabitUiState,
    snackbarHostState: SnackbarHostState,
    onCompleteHabit: (Long) -> Unit,
    onDeleteHabit: (Habit) -> Unit
) {
    var habitToDelete by remember {
        mutableStateOf<Habit?>(null)
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Менің әдеттерім",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 20.dp)
            )

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    Text(
                        text = uiState.error,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                uiState.habits.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Әзірге әдеттер қосылмаған")
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.habits,
                            key = { it.id }
                        ) { habit ->
                            HabitCard(
                                habit = habit,
                                onComplete = {
                                    onCompleteHabit(habit.id)
                                },
                                onDelete = {
                                    habitToDelete = habit
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    habitToDelete?.let { habit ->
        AlertDialog(
            onDismissRequest = {
                habitToDelete = null
            },
            title = {
                Text("Әдетті өшіру")
            },
            text = {
                Text("«${habit.title}» әдетін өшіргіңіз келе ме?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteHabit(habit)
                        habitToDelete = null
                    }
                ) {
                    Text("Өшіру")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        habitToDelete = null
                    }
                ) {
                    Text("Бас тарту")
                }
            }
        )
    }
}