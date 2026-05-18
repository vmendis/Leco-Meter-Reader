package com.leco.meterreader.ui.screens.reading

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.ripple
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.leco.meterreader.ui.components.LargeNumericKeypad
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReadingScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddReadingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val activeField by viewModel.activeField.collectAsState()
    val validationErrors by viewModel.validationErrors.collectAsState()
    val validationWarnings by viewModel.validationWarnings.collectAsState()
    val saveResult by viewModel.saveResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    // Date/Time picker state
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = uiState.timestamp.time
    )

    // Handle save result
    LaunchedEffect(saveResult) {
        saveResult?.let { result ->
            when (result) {
                is SaveResult.Success -> {
                    snackbarHostState.showSnackbar("Reading saved successfully")
                    onNavigateBack()
                }
                is SaveResult.Error -> {
                    snackbarHostState.showSnackbar(result.message)
                }
            }
            viewModel.resetSaveResult()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Add Meter Reading",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Validation errors
        if (validationErrors.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    validationErrors.forEach { error ->
                        Text(
                            text = "• $error",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Validation warnings
        if (validationWarnings.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    validationWarnings.forEach { warning ->
                        Text(
                            text = "• $warning",
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Timestamp field with smart defaults
        TimestampField(
            timestamp = uiState.timestamp,
            onTimestampClick = {
                showDatePicker = true
            }
        )

        // Input fields
        ReadingInputField(
            label = "Total Reading (kWh)",
            value = uiState.totalReading,
            field = ReadingField.TOTAL_READING,
            activeField = activeField,
            onFieldClick = { field -> 
                Log.d("AddReadingScreen", "Field clicked: $field")
                viewModel.setActiveField(field) 
            },
            onValueChange = { value -> viewModel.updateField(ReadingField.TOTAL_READING, value) }
        )

        ReadingInputField(
            label = "Rate 1 - Day Usage (kWh)",
            value = uiState.rate1Day,
            field = ReadingField.RATE_1_DAY,
            activeField = activeField,
            onFieldClick = { field -> 
                Log.d("AddReadingScreen", "Field clicked: $field")
                viewModel.setActiveField(field) 
            },
            onValueChange = { value -> viewModel.updateField(ReadingField.RATE_1_DAY, value) }
        )

        ReadingInputField(
            label = "Rate 2 - Off-Peak (kWh)",
            value = uiState.rate2OffPeak,
            field = ReadingField.RATE_2_OFF_PEAK,
            activeField = activeField,
            onFieldClick = { field -> 
                Log.d("AddReadingScreen", "Field clicked: $field")
                viewModel.setActiveField(field) 
            },
            onValueChange = { value -> viewModel.updateField(ReadingField.RATE_2_OFF_PEAK, value) }
        )

        ReadingInputField(
            label = "Rate 3 - Peak (kWh)",
            value = uiState.rate3Peak,
            field = ReadingField.RATE_3_PEAK,
            activeField = activeField,
            onFieldClick = { field -> 
                Log.d("AddReadingScreen", "Field clicked: $field")
                viewModel.setActiveField(field) 
            },
            onValueChange = { value -> viewModel.updateField(ReadingField.RATE_3_PEAK, value) }
        )

        // Notes field
        OutlinedTextField(
            value = uiState.notes,
            onValueChange = { viewModel.updateNotes(it) },
            label = { Text("Notes (optional)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { viewModel.setActiveField(null) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.saveReading()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Save")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large Numeric Keypad
        activeField?.let { field ->
            Log.d("AddReadingScreen", "Showing keypad for field: $field")
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                text = "Enter ${getFieldLabel(field)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LargeNumericKeypad(
                onNumberClick = { viewModel.onNumberInput(it) },
                onClearClick = { viewModel.onClearInput() }
            )
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    val selectedDate = datePickerState.selectedDateMillis
                    if (selectedDate != null) {
                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = selectedDate
                            // Set to suggested time (09:00 or 21:00)
                            val currentHour = getHour(Date())
                            val suggestedHour = if (currentHour >= 18) 21 else 9
                            set(Calendar.HOUR_OF_DAY, suggestedHour)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                        }
                        viewModel.updateTimestamp(calendar.time)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            },
            content = {
                DatePicker(state = datePickerState)
            }
        )
    }
}

@Composable
private fun TimestampField(
    timestamp: Date,
    onTimestampClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { 
                Log.d("AddReadingScreen", "Timestamp field clicked")
                onTimestampClick() 
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        OutlinedTextField(
            value = dateFormat.format(timestamp),
            onValueChange = { },
            label = { Text("Reading Time") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            readOnly = true,
            singleLine = true,
            enabled = false
        )
    }
}

private fun getHour(date: Date): Int {
    val calendar = Calendar.getInstance()
    calendar.time = date
    return calendar.get(Calendar.HOUR_OF_DAY)
}

private fun getMinute(date: Date): Int {
    val calendar = Calendar.getInstance()
    calendar.time = date
    return calendar.get(Calendar.MINUTE)
}

@Composable
private fun ReadingInputField(
    label: String,
    value: String,
    field: ReadingField,
    activeField: ReadingField?,
    onFieldClick: (ReadingField) -> Unit,
    onValueChange: (String) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    // Use clickable on the OutlinedTextField directly with enabled=true and readOnly=true
    // This prevents the keyboard from showing while still allowing click events
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple()
            ) { 
                Log.d("AddReadingScreen", "ReadingInputField clicked: $field")
                onFieldClick(field) 
            },
        readOnly = true,
        singleLine = true,
        enabled = true,
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

private fun getFieldLabel(field: ReadingField): String {
    return when (field) {
        ReadingField.TOTAL_READING -> "Total Reading"
        ReadingField.RATE_1_DAY -> "Rate 1 - Day"
        ReadingField.RATE_2_OFF_PEAK -> "Rate 2 - Off-Peak"
        ReadingField.RATE_3_PEAK -> "Rate 3 - Peak"
    }
}