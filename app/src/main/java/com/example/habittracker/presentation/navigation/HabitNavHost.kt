package com.example.habittracker.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.habittracker.presentation.state.HabitUiState
import com.example.habittracker.presentation.ui.habit.AddEditHabitScreen
import com.example.habittracker.presentation.ui.home.HomeScreen
import com.example.habittracker.presentation.viewmodel.HabitViewModel

private object HabitRoutes {
    const val HOME = "home"
    const val ADD_HABIT = "habit/add"
    const val EDIT_HABIT = "habit/edit/{habitId}"

    fun editHabit(habitId: Long) = "habit/edit/$habitId"
}

@Composable
fun HabitNavHost(
    viewModel: HabitViewModel,
    uiState: HabitUiState,
    snackbarHostState: SnackbarHostState
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HabitRoutes.HOME
    ) {
        composable(HabitRoutes.HOME) {
            HomeScreen(
                uiState = uiState,
                snackbarHostState = snackbarHostState,
                onCompleteHabit = viewModel::completeHabit,
                onDeleteHabit = viewModel::deleteHabit,
                onAddHabit = {
                    navController.navigate(HabitRoutes.ADD_HABIT)
                },
                onEditHabit = { habit ->
                    navController.navigate(
                        HabitRoutes.editHabit(habit.id)
                    )
                }
            )
        }

        composable(HabitRoutes.ADD_HABIT) {
            AddEditHabitScreen(
                onBack = {
                    navController.popBackStack()
                },
                onSave = { title, description, targetDays ->
                    viewModel.addHabit(
                        title,
                        description,
                        targetDays
                    )
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = HabitRoutes.EDIT_HABIT,
            arguments = listOf(
                navArgument("habitId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val habitId = backStackEntry.arguments
                ?.getLong("habitId")

            val habit = uiState.habits.firstOrNull {
                it.id == habitId
            }

            when {
                habit != null -> {
                    AddEditHabitScreen(
                        initialHabit = habit,
                        onBack = {
                            navController.popBackStack()
                        },
                        onSave = { title, description, targetDays ->
                            viewModel.updateHabit(
                                habit.copy(
                                    title = title,
                                    description = description,
                                    targetDays = targetDays
                                )
                            )
                            navController.popBackStack()
                        }
                    )
                }

                uiState.isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Әдет табылмады")
                        Button(
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Text("Артқа қайту")
                        }
                    }
                }
            }
        }
    }
}