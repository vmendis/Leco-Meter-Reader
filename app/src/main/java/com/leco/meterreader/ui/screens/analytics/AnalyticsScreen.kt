package com.leco.meterreader.ui.screens.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leco.meterreader.R
import com.leco.meterreader.ui.components.ChartContainer
import com.leco.meterreader.ui.components.DateRangeSelector
import com.leco.meterreader.ui.components.charts.DailyUsageLineChart
import com.leco.meterreader.ui.components.charts.TOUComparisonChart
import com.leco.meterreader.ui.components.charts.WeeklyTrendsChart
import com.leco.meterreader.ui.components.isTablet
import com.leco.meterreader.util.CSVExportUtils
import com.leco.meterreader.viewmodel.AnalyticsViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onBack: () -> Unit,
    viewModel: AnalyticsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isTablet = isTablet()
    
    // Load chart data when screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.loadChartData()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.analytics_title),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.loadChartData()
                        },
                        enabled = !uiState.isLoading
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh data")
                    }
                    IconButton(
                        onClick = {
                            CSVExportUtils.shareChartData(
                                context = context,
                                dailyData = uiState.dailyUsageData,
                                weeklyData = uiState.weeklyTrendData,
                                touData = uiState.touComparisonData,
                                dateRange = uiState.selectedDateRange
                            )
                        },
                        enabled = !uiState.isLoading && !uiState.hasError
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Export data")
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = if (isTablet) 32.dp else 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Date Range Selector
            DateRangeSelector(
                selectedRange = uiState.selectedDateRange,
                onRangeSelected = { dateRange ->
                    viewModel.updateDateRange(dateRange)
                    viewModel.loadChartData()
                },
                isLoading = uiState.isLoading
            )
            
            // Summary Cards
            if (uiState.summaryData != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SummaryCard(
                        title = "Total Usage",
                        value = "${uiState.summaryData.totalUsage} kWh",
                        icon = Icons.Default.Download,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = "Avg Daily",
                        value = "${uiState.summaryData.avgDailyUsage} kWh",
                        icon = Icons.Default.Download,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = "Peak Rate",
                        value = "₹${uiState.summaryData.peakRate}",
                        icon = Icons.Default.Download,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Daily Usage Line Chart
            ChartContainer(
                title = "Daily Usage Trend",
                isLoading = uiState.isLoading,
                error = uiState.error,
                onRetry = { viewModel.loadChartData() }
            ) {
                DailyUsageLineChart(
                    data = uiState.dailyUsageData,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Weekly Trends Chart
            ChartContainer(
                title = "Weekly Usage Trends",
                isLoading = uiState.isLoading,
                error = uiState.error,
                onRetry = { viewModel.loadChartData() }
            ) {
                WeeklyTrendsChart(
                    data = uiState.weeklyTrendData,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // TOU Comparison Chart
            ChartContainer(
                title = "Time of Use Comparison",
                isLoading = uiState.isLoading,
                error = uiState.error,
                onRetry = { viewModel.loadChartData() }
            ) {
                TOUComparisonChart(
                    data = uiState.touComparisonData,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Export Options
            if (!uiState.isLoading && !uiState.hasError) {
                ExportOptionsSection(
                    onExportDaily = {
                        CSVExportUtils.exportDailyData(
                            context = context,
                            dailyData = uiState.dailyUsageData,
                            dateRange = uiState.selectedDateRange
                        )
                    },
                    onExportWeekly = {
                        CSVExportUtils.exportWeeklyData(
                            context = context,
                            weeklyData = uiState.weeklyTrendData,
                            dateRange = uiState.selectedDateRange
                        )
                    },
                    onExportTOU = {
                        CSVExportUtils.exportTOUData(
                            context = context,
                            touData = uiState.touComparisonData,
                            dateRange = uiState.selectedDateRange
                        )
                    },
                    onExportAll = {
                        CSVExportUtils.shareChartData(
                            context = context,
                            dailyData = uiState.dailyUsageData,
                            weeklyData = uiState.weeklyTrendData,
                            touData = uiState.touComparisonData,
                            dateRange = uiState.selectedDateRange
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ExportOptionsSection(
    onExportDaily: () -> Unit,
    onExportWeekly: () -> Unit,
    onExportTOU: () -> Unit,
    onExportAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Export Options",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onExportDaily,
                modifier = Modifier.weight(1f)
            ) {
                Text("Daily Data")
            }
            OutlinedButton(
                onClick = onExportWeekly,
                modifier = Modifier.weight(1f)
            ) {
                Text("Weekly Data")
            }
            OutlinedButton(
                onClick = onExportTOU,
                modifier = Modifier.weight(1f)
            ) {
                Text("TOU Data")
            }
            OutlinedButton(
                onClick = onExportAll,
                modifier = Modifier.weight(1f)
            ) {
                Text("All Data")
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun AnalyticsScreenPreview() {
    MaterialTheme {
        AnalyticsScreen(onBack = {})
    }
}

@Preview(showBackground = true, device = "spec:width=840dp,height=1200dp")
@Composable
fun AnalyticsScreenTabletPreview() {
    MaterialTheme {
        AnalyticsScreen(onBack = {})
    }
}