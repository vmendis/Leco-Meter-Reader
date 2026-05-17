package com.leco.meterreader.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Responsive layout that adapts to different screen sizes
 */
@Composable
fun ResponsiveReadingLayout(
    timestampSection: @Composable () -> Unit,
    fieldSelector: @Composable () -> Unit,
    readingDisplays: @Composable () -> Unit,
    notesSection: @Composable () -> Unit,
    keypadSection: @Composable () -> Unit,
    actionButtons: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTablet = isTablet()
    
    if (isTablet) {
        // Tablet layout - two columns
        Row(
            modifier = modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left column - Input fields and timestamp
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                timestampSection()
                fieldSelector()
                readingDisplays()
                notesSection()
            }
            
            // Right column - Keypad
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                keypadSection()
                actionButtons()
            }
        }
    } else {
        // Phone layout - single column
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                timestampSection()
            }
            item {
                fieldSelector()
            }
            item {
                readingDisplays()
            }
            item {
                notesSection()
            }
            item {
                keypadSection()
            }
            item {
                actionButtons()
            }
        }
    }
}

/**
 * Helper function to determine if the screen is tablet-sized
 */
@Composable
private fun isTablet(): Boolean {
    // This is a simplified check - in a real app, you might want to use
    // WindowMetricsCalculator or similar to get actual screen dimensions
    val screenWidth = androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp
    return screenWidth >= 600
}

/**
 * Responsive reading display for different screen sizes
 */
@Composable
fun ResponsiveReadingDisplay(
    value: String,
    label: String,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isTablet = isTablet()
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = if (isTablet) 18.sp else 16.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = if (value.isBlank()) "0.000" else value,
                style = MaterialTheme.typography.headlineLarge,
                color = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface,
                fontSize = if (isTablet) 36.sp else 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Responsive keypad layout
 */
@Composable
fun ResponsiveKeypad(
    onDigitPressed: (Char) -> Unit,
    onBackspacePressed: () -> Unit,
    onClearPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTablet = isTablet()
    
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(if (isTablet) 12.dp else 8.dp)
    ) {
        // Clear button
        Button(
            onClick = onClearPressed,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isTablet) 64.dp else 56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Text(
                text = "CLEAR",
                fontSize = if (isTablet) 20.sp else 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        // Number buttons (1-9)
        for (row in listOf(listOf('1', '2', '3'), listOf('4', '5', '6'), listOf('7', '8', '9'))) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(if (isTablet) 12.dp else 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                for (digit in row) {
                    Button(
                        onClick = { onDigitPressed(digit) },
                        modifier = Modifier
                            .weight(1f)
                            .height(if (isTablet) 72.dp else 56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text(
                            text = digit.toString(),
                            fontSize = if (isTablet) 28.sp else 24.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
        
        // Bottom row with 0, decimal, and backspace
        Row(
            horizontalArrangement = Arrangement.spacedBy(if (isTablet) 12.dp else 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { onDigitPressed('0') },
                modifier = Modifier
                    .weight(1f)
                    .height(if (isTablet) 72.dp else 56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(
                    text = "0",
                    fontSize = if (isTablet) 28.sp else 24.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Button(
                onClick = { onDigitPressed('.') },
                modifier = Modifier
                    .weight(1f)
                    .height(if (isTablet) 72.dp else 56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text(
                    text = ".",
                    fontSize = if (isTablet) 28.sp else 24.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Button(
                onClick = onBackspacePressed,
                modifier = Modifier
                    .weight(1f)
                    .height(if (isTablet) 72.dp else 56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text(
                    text = "⌫",
                    fontSize = if (isTablet) 28.sp else 24.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Responsive action buttons
 */
@Composable
fun ResponsiveActionButtons(
    onBack: () -> Unit,
    onSave: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isTablet = isTablet()
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = if (isTablet) 24.dp else 16.dp),
        horizontalArrangement = Arrangement.spacedBy(if (isTablet) 16.dp else 12.dp)
    ) {
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.weight(1f),
            enabled = !isLoading,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text(
                text = "Cancel",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                fontSize = if (isTablet) 18.sp else 16.sp
            )
        }
        
        Button(
            onClick = onSave,
            modifier = Modifier.weight(1f),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = "Save",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                fontSize = if (isTablet) 18.sp else 16.sp
            )
        }
    }
}

/**
 * Responsive field selector
 */
@Composable
fun ResponsiveFieldSelector(
    selectedField: String,
    onFieldSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isTablet = isTablet()
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(if (isTablet) 8.dp else 4.dp)
    ) {
        val fields = listOf(
            "totalReading" to "Total",
            "rate1Reading" to "Rate 1",
            "rate2Reading" to "Rate 2", 
            "rate3Reading" to "Rate 3"
        )
        
        fields.forEach { (field, label) ->
            FilterChip(
                selected = selectedField == field,
                onClick = { onFieldSelected(field) },
                label = { 
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (selectedField == field) FontWeight.Bold else FontWeight.Normal,
                        fontSize = if (isTablet) 14.sp else 12.sp
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unselectedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(if (isTablet) 48.dp else 40.dp)
            )
        }
    }
}