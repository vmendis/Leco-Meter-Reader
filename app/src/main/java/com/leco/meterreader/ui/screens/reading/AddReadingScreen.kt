package com.leco.meterreader.ui.screens.reading

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leco.meterreader.R
import com.leco.meterreader.ui.components.*
import com.leco.meterreader.viewmodel.ReadingUiState
import com.leco.meterreader.viewmodel.ReadingViewModel
import java.time.LocalDateTime

/**
 * Main manual reading entry screen with Jetpack Compose
 * 
 * Features:
 * - Large numeric keypad for meter readings
 * - Timestamp selection with smart defaults
 * - Validation logic to prevent decreasing readings
 * - Optional notes field
 * - Save/cancel workflow with error handling
 * - Material 3 UI with proper spacing and typography
 * - Responsive layout for different screen sizes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReadingScreen(
    viewModel: ReadingViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onReadingSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val lastSavedReading by viewModel.lastSavedReading.collectAsStateWithLifecycle()
    
    // Load latest reading on first composition
    LaunchedEffect(Unit) {
        viewModel.loadLatestReading()
    }
    
    // Handle save success
    LaunchedEffect(isLoading) {
        if (!isLoading && lastSavedReading != null) {
            onReadingSaved()
        }
    }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.reading_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.cancelReading() }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear form")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.saveReading(
                        onSuccess = onReadingSaved,
                        onError = { error -> /* Show error toast */ }
                    )
                },
                modifier = Modifier.padding(bottom = 16.dp, end = 16.dp),
                enabled = !isLoading && uiState.totalReading.isNotBlank(),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Check, contentDescription = "Save reading")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error message display
            AnimatedVisibility(
                visible = uiState.errors.isNotEmpty(),
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Please fix the following errors:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        
                        uiState.errors.values.forEach { error ->
                            Text(
                                text = "• $error",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
            
            // Timestamp selection
            DateTimePicker(
                selectedDateTime = uiState.timestamp,
                onDateTimeSelected = { viewModel.onTimestampChange(it) },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Field selector for switching between input fields
            FieldSelector(
                selectedField = uiState.currentInputField,
                onFieldSelected = { viewModel.setCurrentInputField(it) },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Reading displays for each field
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Total reading (required)
                ReadingDisplay(
                    value = uiState.totalReading,
                    label = stringResource(R.string.reading_total),
                    isError = uiState.errors.containsKey("totalReading"),
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Rate readings (optional)
                listOf(
                    "rate1Reading" to stringResource(R.string.reading_rate1),
                    "rate2Reading" to stringResource(R.string.reading_rate2),
                    "rate3Reading" to stringResource(R.string.reading_rate3)
                ).forEach { (field, label) ->
                    ReadingDisplay(
                        value = uiState.rateReadings[field] ?: "",
                        label = label,
                        isError = uiState.errors.containsKey(field),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // Notes field
            NotesField(
                value = uiState.notes,
                onValueChange = { viewModel.onNotesChange(it) },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Numeric keypad
            KeypadComponent(
                onDigitPressed = { digit -> viewModel.addDigit(digit) },
                onBackspacePressed = { viewModel.removeLastDigit() },
                onClearPressed = { viewModel.clearCurrentField() },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.cancelReading()
                        onBack()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(
                        text = stringResource(R.string.reading_cancel),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Button(
                    onClick = {
                        viewModel.saveReading(
                            onSuccess = onReadingSaved,
                            onError = { error -> /* Show error toast */ }
                        )
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading && uiState.totalReading.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.reading_save),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Loading indicator
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

/**
 * Preview for the AddReadingScreen
 */
@Preview(showBackground = true, name = "Add Reading Screen")
@Composable
private fun AddReadingScreenPreview() {
    MaterialTheme {
        AddReadingScreen(
            onBack = {},
            onReadingSaved = {}
        )
    }
}

/**
 * Preview for the AddReadingScreen with error state
 */
@Preview(showBackground = true, name = "Add Reading Screen - Error State")
@Composable
private fun AddReadingScreenErrorPreview() {
    MaterialTheme {
        AddReadingScreen(
            onBack = {},
            onReadingSaved = {}
        )
    }
}