package com.leco.meterreader.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leco.meterreader.R
import com.leco.meterreader.data.model.MeterReading
import com.leco.meterreader.ui.theme.LECOTheme
import com.leco.meterreader.viewmodel.DashboardViewModel
import com.leco.meterreader.viewmodel.DashboardUiState
import com.leco.meterreader.viewmodel.DashboardViewModel as DashboardViewModelType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// Icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SolarPower

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModelType = remember { DashboardViewModelType() },
    onNavigateToReading: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToSolar: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Load data when the screen is first composed
    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }
    
    DashboardScreenContent(
        uiState = viewModel.uiState,
        isLoading = viewModel.isLoading.value,
        error = viewModel.error.value,
        onRefresh = { viewModel.refreshData() },
        onErrorClear = { viewModel.clearError() },
        onNavigateToReading = onNavigateToReading,
        onNavigateToHistory = onNavigateToHistory,
        onNavigateToAnalytics = onNavigateToAnalytics,
        onNavigateToSolar = onNavigateToSolar,
        modifier = modifier
    )
}

@Composable
private fun DashboardScreenContent(
    uiState: DashboardUiState,
    isLoading: Boolean,
    error: String?,
    onRefresh: () -> Unit,
    onErrorClear: () -> Unit,
    onNavigateToReading: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToSolar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with refresh functionality
        DashboardHeader(
            isLoading = isLoading,
            onRefresh = onRefresh,
            error = error,
            onErrorClear = onErrorClear
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (isLoading) {
            LoadingState()
        } else if (error != null) {
            ErrorState(
                error = error,
                onRetry = onRefresh,
                onClearError = onErrorClear
            )
        } else if (!uiState.isDataLoaded) {
            LoadingState()
        } else if (uiState.latestReading == null) {
            EmptyState(
                onAddReading = onNavigateToReading
            )
        } else {
            // Main content with all dashboard cards
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Latest Reading Card
                item {
                    LatestReadingCard(
                        reading = uiState.latestReading,
                        previousReading = uiState.previousReading
                    )
                }
                
                // Daily Usage Card
                item {
                    DailyUsageCard(dailyUsage = uiState.dailyUsage)
                }
                
                // Cost Estimation Card
                item {
                    CostEstimationCard(cost = uiState.estimatedDailyCost)
                }
                
                // Quick Stats Cards
                item {
                    QuickStatsCard(stats = uiState.quickStats)
                }
                
                // Navigation Cards
                item {
                    NavigationCards(
                        onNavigateToReading = onNavigateToReading,
                        onNavigateToHistory = onNavigateToHistory,
                        onNavigateToAnalytics = onNavigateToAnalytics,
                        onNavigateToSolar = onNavigateToSolar
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    isLoading: Boolean,
    onRefresh: () -> Unit,
    error: String?,
    onErrorClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        } else {
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh data",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
    
    if (error != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(
                    onClick = onErrorClear,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text("Dismiss")
                }
            }
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            strokeWidth = 4.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Loading dashboard data...",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterVertically,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Failed to load data",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onRetry,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Retry")
                    }
                    Button(
                        onClick = onClearError,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onErrorContainer,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text("Dismiss")
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    onAddReading: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorically,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.ElectricalServices,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "No meter readings available",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Start by adding your first meter reading to begin tracking your energy usage.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onAddReading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add First Reading")
                }
            }
        }
    }
}

@Composable
private fun LatestReadingCard(
    reading: MeterReading?,
    previousReading: MeterReading?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.dashboard_latest_reading),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (reading != null) {
                Column {
                    // Total Reading
                    ReadingValueRow(
                        label = "Total Reading",
                        value = String.format(Locale.US, "%.3f kWh", reading.totalReading)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Rate Breakdown
                    Text(
                        text = "Rate Breakdown",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    ReadingValueRow(
                        label = "Rate 1 (Day)",
                        value = String.format(Locale.US, "%.3f kWh", reading.rate1Reading)
                    )
                    
                    ReadingValueRow(
                        label = "Rate 2 (Off-Peak)",
                        value = String.format(Locale.US, "%.3f kWh", reading.rate2Reading)
                    )
                    
                    ReadingValueRow(
                        label = "Rate 3 (Peak)",
                        value = String.format(Locale.US, "%.3f kWh", reading.rate3Reading)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Timestamp
                    Text(
                        text = "Timestamp: ${reading.timestamp.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // Usage difference from previous reading
                    previousReading?.let { prev ->
                        val usageDiff = reading.totalReading - prev.totalReading
                        if (usageDiff > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Since last reading: +${String.format(Locale.US, "%.3f kWh", usageDiff)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "No readings available",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ReadingValueRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun DailyUsageCard(
    dailyUsage: com.leco.meterreader.viewmodel.DailyUsage,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.dashboard_daily_usage),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (dailyUsage.totalUsage > 0) {
                Column {
                    ReadingValueRow(
                        label = "Total Usage",
                        value = String.format(Locale.US, "%.3f kWh", dailyUsage.totalUsage)
                    )
                    
                    ReadingValueRow(
                        label = "Usage per Hour",
                        value = String.format(Locale.US, "%.3f kWh/h", dailyUsage.usagePerHour)
                    )
                    
                    ReadingValueRow(
                        label = "Time Period",
                        value = dailyUsage.period
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Usage trend indicator
                    val trend = if (dailyUsage.usagePerHour > 5) "High" else if (dailyUsage.usagePerHour > 2) "Moderate" else "Low"
                    val trendColor = when (trend) {
                        "High" -> MaterialTheme.colorScheme.error
                        "Moderate" -> MaterialTheme.colorScheme.warning
                        else -> MaterialTheme.colorScheme.primary
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Usage Level: ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = trend,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = trendColor
                        )
                    }
                }
            } else {
                Text(
                    text = "No usage data available",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CostEstimationCard(
    cost: com.leco.meterreader.viewmodel.EstimatedDailyCost,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.dashboard_estimated_cost),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (cost.totalCost > 0) {
                Column {
                    ReadingValueRow(
                        label = "Total Daily Cost",
                        value = String.format(Locale.US, "$%.2f", cost.totalCost)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Cost Breakdown",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    cost.breakdown.forEach { breakdown ->
                        Text(
                            text = breakdown,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Text(
                    text = "No cost data available",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun QuickStatsCard(
    stats: com.leco.meterreader.viewmodel.QuickStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Quick Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ReadingValueRow(
                    label = "Total Readings",
                    value = stats.totalReadings.toString()
                )
                
                ReadingValueRow(
                    label = "Average Daily Usage",
                    value = String.format(Locale.US, "%.3f kWh", stats.averageDailyUsage)
                )
                
                ReadingValueRow(
                    label = "Average Daily Cost",
                    value = String.format(Locale.US, "$%.2f", stats.averageDailyCost)
                )
                
                ReadingValueRow(
                    label = "Data Span",
                    value = stats.timeSpan
                )
            }
        }
    }
}

@Composable
private fun NavigationCards(
    onNavigateToReading: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToSolar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        NavigationCard(
            title = stringResource(R.string.nav_reading),
            icon = Icons.Default.Add,
            onClick = onNavigateToReading
        )
        
        NavigationCard(
            title = stringResource(R.string.nav_history),
            icon = Icons.Default.History,
            onClick = onNavigateToHistory
        )
        
        NavigationCard(
            title = stringResource(R.string.nav_analytics),
            icon = Icons.Default.Analytics,
            onClick = onNavigateToAnalytics
        )
        
        NavigationCard(
            title = stringResource(R.string.nav_solar),
            icon = Icons.Default.SolarPower,
            onClick = onNavigateToSolar
        )
    }
}

@Composable
private fun NavigationCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
                
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    LECOTheme {
        DashboardScreen(
            onNavigateToReading = {},
            onNavigateToHistory = {},
            onNavigateToAnalytics = {},
            onNavigateToSolar = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenEmptyStatePreview() {
    LECOTheme {
        DashboardScreenContent(
            uiState = DashboardUiState(),
            isLoading = false,
            error = null,
            onRefresh = {},
            onErrorClear = {},
            onNavigateToReading = {},
            onNavigateToHistory = {},
            onNavigateToAnalytics = {},
            onNavigateToSolar = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenErrorStatePreview() {
    LECOTheme {
        DashboardScreenContent(
            uiState = DashboardUiState(),
            isLoading = false,
            error = "Failed to load dashboard data",
            onRefresh = {},
            onErrorClear = {},
            onNavigateToReading = {},
            onNavigateToHistory = {},
            onNavigateToAnalytics = {},
            onNavigateToSolar = {}
        )
    }
}