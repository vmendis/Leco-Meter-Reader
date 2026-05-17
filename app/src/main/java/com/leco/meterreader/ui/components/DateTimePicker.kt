package com.leco.meterreader.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * DateTime picker component with smart defaults for meter readings
 * 
 * @param selectedDateTime Currently selected datetime
 * @param onDateTimeSelected Callback when datetime is selected
 * @param modifier Modifier for the component
 */
@Composable
fun DateTimePicker(
    selectedDateTime: LocalDateTime,
    onDateTimeSelected: (LocalDateTime) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Current datetime display
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            shadowElevation = 2.dp
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formatDateTime(selectedDateTime),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        // Quick selection buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            QuickTimeButton(
                text = "Now",
                onClick = { onDateTimeSelected(LocalDateTime.now()) },
                modifier = Modifier.weight(1f)
            )
            
            QuickTimeButton(
                text = "15m Ago",
                onClick = { 
                    val newTime = LocalDateTime.now().minusMinutes(15L)
                    onDateTimeSelected(newTime)
                },
                modifier = Modifier.weight(1f)
            )
            
            QuickTimeButton(
                text = "1h Ago",
                onClick = { 
                    val newTime = LocalDateTime.now().minusHours(1L)
                    onDateTimeSelected(newTime)
                },
                modifier = Modifier.weight(1f)
            )
        }
        
        // Detailed time picker
        TimePickerDialog(
            selectedDateTime = selectedDateTime,
            onDateTimeSelected = onDateTimeSelected
        )
    }
}

/**
 * Quick time selection button
 */
@Composable
private fun QuickTimeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Time picker dialog component
 */
@Composable
private fun TimePickerDialog(
    selectedDateTime: LocalDateTime,
    onDateTimeSelected: (LocalDateTime) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    
    // Show dialog button
    OutlinedButton(
        onClick = { showDialog = true },
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text("Select Custom Time")
    }
    
    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            onDateSelected = { date ->
                val newDateTime = LocalDateTime.of(
                    date.year,
                    date.month,
                    date.dayOfMonth,
                    selectedDateTime.hour,
                    selectedDateTime.minute
                )
                onDateTimeSelected(newDateTime)
                showDialog = false
            },
            initialDate = selectedDateTime.toLocalDate()
        )
    }
}

/**
 * Date picker dialog
 */
@Composable
private fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (java.time.LocalDate) -> Unit,
    initialDate: java.time.LocalDate
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.atStartOfDay(java.time.ZoneId.systemDefault()).toEpochMilli()
    )
    
    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val instant = java.time.Instant.ofEpochMilli(millis)
                        val zoneId = java.time.ZoneId.systemDefault()
                        val localDate = java.time.LocalDate.ofInstant(instant, zoneId)
                        onDateSelected(localDate)
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                headlineContentColor = MaterialTheme.colorScheme.onSurface,
                weekdayContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                dayContentColor = MaterialTheme.colorScheme.onSurface,
                selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                todayDateBorderColor = MaterialTheme.colorScheme.primary,
                todayDateContentColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

/**
 * Formats LocalDateTime for display
 */
private fun formatDateTime(dateTime: LocalDateTime): String {
    return dateTime.format(
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
    )
}

/**
 * Smart timestamp utilities
 */
object SmartTimestampUtils {
    
    /**
     * Gets smart default timestamp based on last reading
     */
    fun getSmartDefault(
        lastReading: java.time.LocalDateTime?,
        currentTime: java.time.LocalDateTime = java.time.LocalDateTime.now()
    ): java.time.LocalDateTime {
        return when {
            lastReading == null -> currentTime
            lastReading.isAfter(currentTime.minusHours(1)) -> {
                // If last reading was within the last hour, use current time
                currentTime
            }
            else -> {
                // Otherwise, use 15 minutes after last reading to avoid duplicates
                lastReading.plusMinutes(15L)
            }
        }
    }
    
    /**
     * Rounds timestamp to nearest 15 minutes for consistency
     */
    fun roundToNearest15Minutes(dateTime: java.time.LocalDateTime): java.time.LocalDateTime {
        val minutes = dateTime.minute
        val roundedMinutes = when {
            minutes < 8 -> 0
            minutes < 23 -> 15
            minutes < 38 -> 30
            minutes < 53 -> 45
            else -> 0 // Will roll over to next hour
        }
        
        return dateTime.withMinute(roundedMinutes).withSecond(0).withNano(0)
    }
    
    /**
     * Validates that timestamp is not in the future
     */
    fun isValidTimestamp(timestamp: java.time.LocalDateTime, currentTime: java.time.LocalDateTime = java.time.LocalDateTime.now()): Boolean {
        return !timestamp.isAfter(currentTime)
    }
    
    /**
     * Validates that timestamp is not too far in the past (e.g., more than 30 days)
     */
    fun isReasonableTimestamp(timestamp: java.time.LocalDateTime, currentTime: java.time.LocalDateTime = java.time.LocalDateTime.now()): Boolean {
        val thirtyDaysAgo = currentTime.minusDays(30L)
        return !timestamp.isBefore(thirtyDaysAgo)
    }
}