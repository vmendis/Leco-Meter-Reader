package com.leco.meterreader.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.leco.meterreader.viewmodel.DateRange
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Date range selector component with preset options and custom selection
 */
@Composable
fun DateRangeSelector(
    selectedDateRange: DateRange?,
    onDateRangeSelected: (LocalDate, LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val isTablet = isTablet()
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Text(
            text = "Select Date Range",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        // Preset date range buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val presetRanges = listOf(
                "Last 7 days" to { getLastNDays(7) },
                "Last 30 days" to { getLastNDays(30) },
                "Last 3 months" to { getLastNMonths(3) },
                "Last year" to { getLastNYears(1) }
            )
            
            presetRanges.forEach { (label, rangeProvider) ->
                DateRangePresetButton(
                    label = label,
                    isSelected = selectedDateRange?.equals(rangeProvider()) == true,
                    onClick = {
                        val (start, end) = rangeProvider()
                        onDateRangeSelected(start, end)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Custom date range selection
        if (isTablet) {
            // Tablet layout - side by side date pickers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CustomDatePicker(
                    label = "Start Date",
                    selectedDate = selectedDateRange?.start,
                    onDateSelected = { date ->
                        val end = selectedDateRange?.end ?: LocalDate.now()
                        onDateRangeSelected(date, end)
                    },
                    modifier = Modifier.weight(1f)
                )
                
                CustomDatePicker(
                    label = "End Date",
                    selectedDate = selectedDateRange?.end,
                    onDateSelected = { date ->
                        val start = selectedDateRange?.start ?: LocalDate.now().minusDays(30)
                        onDateRangeSelected(start, date)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            // Phone layout - stacked date pickers
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CustomDatePicker(
                    label = "Start Date",
                    selectedDate = selectedDateRange?.start,
                    onDateSelected = { date ->
                        val end = selectedDateRange?.end ?: LocalDate.now()
                        onDateRangeSelected(date, end)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                CustomDatePicker(
                    label = "End Date",
                    selectedDate = selectedDateRange?.end,
                    onDateSelected = { date ->
                        val start = selectedDateRange?.start ?: LocalDate.now().minusDays(30)
                        onDateRangeSelected(start, date)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // Selected date range display
        selectedDateRange?.let { range ->
            SelectedDateRangeDisplay(
                dateRange = range,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Preset date range button
 */
@Composable
private fun DateRangePresetButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            unselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unselectedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .height(40.dp)
    )
}

/**
 * Custom date picker component
 */
@Composable
private fun CustomDatePicker(
    label: String,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(
                    text = selectedDate?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) ?: "Select date",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
    
    // Date picker dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { date ->
                onDateSelected(date)
                showDatePicker = false
            },
            initialDate = selectedDate ?: LocalDate.now()
        )
    }
}

/**
 * Selected date range display
 */
@Composable
private fun SelectedDateRangeDisplay(
    dateRange: DateRange,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Selected Range",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "${dateRange.start.format(DateTimeFormatter.ofPattern("MMM dd"))} - ${dateRange.end.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Text(
                text = "${ChronoUnit.DAYS.between(dateRange.start, dateRange.end) + 1} days",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/**
 * Date picker dialog
 */
@Composable
private fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    initialDate: LocalDate
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDate.toEpochDay())
    
    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = LocalDate.ofEpochDay(millis / 86400000)
                        onDateSelected(date)
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
                weekdayContentColor = MaterialTheme.colorScheme.onSurface,
                dayContentColor = MaterialTheme.colorScheme.onSurface,
                selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                todayContentColor = MaterialTheme.colorScheme.primary,
                todayDateBorderColor = MaterialTheme.colorScheme.primary,
                yearContentColor = MaterialTheme.colorScheme.onSurface,
                selectedYearContainerColor = MaterialTheme.colorScheme.primary,
                selectedYearContentColor = MaterialTheme.colorScheme.onPrimary,
                disabledYearContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                disabledSelectedYearContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f),
                currentYearContentColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

/**
 * Helper function to get last N days
 */
private fun getLastNDays(days: Int): Pair<LocalDate, LocalDate> {
    val end = LocalDate.now()
    val start = end.minusDays(days.toLong() - 1)
    return Pair(start, end)
}

/**
 * Helper function to get last N months
 */
private fun getLastNMonths(months: Int): Pair<LocalDate, LocalDate> {
    val end = LocalDate.now()
    val start = end.minusMonths(months.toLong())
    return Pair(start, end)
}

/**
 * Helper function to get last N years
 */
private fun getLastNYears(years: Int): Pair<LocalDate, LocalDate> {
    val end = LocalDate.now()
    val start = end.minusYears(years.toLong())
    return Pair(start, end)
}

/**
 * Helper function to determine if the screen is tablet-sized
 */
@Composable
private fun isTablet(): Boolean {
    val screenWidth = androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp
    return screenWidth >= 600
}