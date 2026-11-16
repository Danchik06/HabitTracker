package com.example.habittracker.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HabitColorScheme = lightColorScheme(
    primary = Color(0xFF386A5A),
    onPrimary = Color.White,
    secondary = Color(0xFF52635A),
    background = Color(0xFFF7F8F5),
    surface = Color(0xFFFFFFFF),
    error = Color(0xFFBA1A1A)
)

@Composable
fun HabitTrackerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HabitColorScheme,
        typography = Typography(),
        content = content
    )
}