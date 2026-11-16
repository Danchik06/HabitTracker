package com.example.habittracker.presentation.ui.habit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.habittracker.domain.model.Habit

private val weekDays = listOf(
    "Дүйсенбі" to "Дс",
    "Сейсенбі" to "Сс",
    "Сәрсенбі" to "Ср",
    "Бейсенбі" to "Бс",
    "Жұма" to "Жм",
    "Сенбі" to "Сб",
    "Жексенбі" to "Жс"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditHabitScreen(
    initialHabit: Habit? = null,
    onBack: () -> Unit,
    onSave: (
        title: String,
        description: String,
        targetDays: List<String>
    ) -> Unit
) {
    var title by rememberSaveable(initialHabit?.id) {
        mutableStateOf(initialHabit?.title.orEmpty())
    }

    var description by rememberSaveable(initialHabit?.id) {
        mutableStateOf(initialHabit?.description.orEmpty())
    }

    var selectedDays by rememberSaveable(initialHabit?.id) {
        mutableStateOf(initialHabit?.targetDays ?: emptyList())
    }

    var titleError by rememberSaveable {
        mutableStateOf(false)
    }

    var daysError by rememberSaveable {
        mutableStateOf(false)
    }

    val isEditing = initialHabit != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Әдетті өңдеу"
                        else "Жаңа әдет"
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Артқа")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = false
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Әдет атауы") },
                placeholder = { Text("Мысалы: Кітап оқу") },
                singleLine = true,
                isError = titleError,
                supportingText = {
                    if (titleError) {
                        Text("Әдет атауын енгізіңіз")
                    } else {
                        Text("${title.length}/60")
                    }
                }
            )

            OutlinedTextField(
                value = description,
                onValueChange = {
                    if (it.length <= 250) description = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Сипаттама") },
                placeholder = { Text("Қосымша ақпарат (міндетті емес)") },
                minLines = 3,
                supportingText = {
                    Text("${description.length}/250")
                }
            )

            Text("Қай күндері қайталанады?")

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                weekDays.chunked(4).forEach { rowDays ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowDays.forEach { (fullName, shortName) ->
                            FilterChip(
                                selected = fullName in selectedDays,
                                onClick = {
                                    selectedDays =
                                        if (fullName in selectedDays) {
                                            selectedDays - fullName
                                        } else {
                                            selectedDays + fullName
                                        }
                                    daysError = false
                                },
                                label = { Text(shortName) }
                            )
                        }
                    }
                }
            }

            if (daysError) {
                Text("Кемінде бір күнді таңдаңыз")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    titleError = title.isBlank()
                    daysError = selectedDays.isEmpty()

                    if (!titleError && !daysError) {
                        onSave(
                            title.trim(),
                            description.trim(),
                            weekDays
                                .map { it.first }
                                .filter { it in selectedDays }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditing) "Сақтау" else "Әдетті қосу")
            }
        }
    }
}