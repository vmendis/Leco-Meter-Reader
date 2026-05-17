package com.leco.meterreader.ui.screens.solar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.leco.meterreader.R
import com.leco.meterreader.ui.components.SolarPlanningInputs
import com.leco.meterreader.viewmodel.SolarPlanningViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolarScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SolarPlanningViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.solar_title))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        IconButton(
                            onClick = {
                                // TODO: Implement scenario saving dialog
                            },
                            enabled = !uiState.isLoading
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Save Scenario")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (!uiState.isLoading) {
                FloatingActionButton(
                    onClick = {
                        // TODO: Implement calculation trigger or make it automatic
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = "Calculate")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error message if any
            if (uiState.error != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.common_error),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            
            // Input sections
            SolarPlanningInputs(viewModel = viewModel)
            
            // TODO: Add results display section
            // SolarResultsDisplay(viewModel = viewModel)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SolarScreenPreview() {
    MaterialTheme {
        SolarScreen(onBack = {})
    }
}