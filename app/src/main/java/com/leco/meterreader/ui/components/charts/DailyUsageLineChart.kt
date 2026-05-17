package com.leco.meterreader.ui.components.charts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leco.meterreader.ui.components.ChartLegend
import com.leco.meterreader.ui.components.LegendItem
import com.leco.meterreader.viewmodel.DailyUsageData
import com.patrykandpatrick.vico.compose.axis.horizontal
import com.patrykandpatrick.vico.compose.axis.vertical
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.chart.zoom.rememberChartZoomState
import com.patrykandpatrick.vico.core.chart.values.MutableChartValues
import com.patrykandpatrick.vico.core.chart.values.MutableChartValuesEntryModel
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.model.LineChartModel
import com.patrykandpatrick.vico.core.model.LineChartModelProducer
import com.patrykandpatrick.vico.core.model.lineSeries
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Daily usage line chart using Vico library
 */
@Composable
fun DailyUsageLineChart(
    data: List<DailyUsageData>,
    modifier: Modifier = Modifier
) {
    val isTablet = isTablet()
    val chartHeight = if (isTablet) 300.dp else 250.dp
    
    if (data.isEmpty()) {
        EmptyChartPlaceholder(
            message = "No daily usage data available",
            modifier = modifier
        )
        return
    }
    
    // Prepare chart data
    val chartModel = remember(data) {
        createLineChartModel(data)
    }
    
    // Chart colors
    val totalUsageColor = MaterialTheme.colorScheme.primary
    val rate1Color = Color(0xFF4CAF50) // Green
    val rate2Color = Color(0xFF2196F3) // Blue
    val rate3Color = Color(0xFFFF9800) // Orange
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Chart title and stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Daily Usage",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Daily consumption breakdown",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Summary stats
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val totalAvg = data.map { it.totalUsage }.average()
                Text(
                    text = "Avg: ${String.format("%.1f", totalAvg)} kWh",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Total: ${String.format("%.0f", data.sumOf { it.totalUsage })} kWh",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight)
        ) {
            Chart(
                chart = lineChart(
                    modelProducer = chartModel,
                    axis = horizontal(
                        valueFormatter = { value, context ->
                            data.getOrNull(value.toInt())?.date?.let { date ->
                                date.format(DateTimeFormatter.ofPattern("MMM dd"))
                            } ?: ""
                        },
                        title = "Date"
                    ) + vertical(
                        valueFormatter = { value, _ ->
                            String.format("%.1f", value)
                        },
                        title = "Usage (kWh)"
                    ),
                    showHorizontalGuidelines = true,
                    showVerticalGuidelines = false,
                    spacing = 8.dp
                ),
                modifier = Modifier.fillMaxSize(),
                chartScrollState = rememberChartScrollState(),
                chartZoomState = rememberChartZoomState(),
                runInitialAnimation = true
            )
        }
        
        // Legend
        ChartLegend(
            items = listOf(
                LegendItem("Total", totalUsageColor),
                LegendItem("Rate 1", rate1Color),
                LegendItem("Rate 2", rate2Color),
                LegendItem("Rate 3", rate3Color)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Creates a line chart model from daily usage data
 */
@Composable
private fun createLineChartModel(data: List<DailyUsageData>): LineChartModelProducer {
    return remember(data) {
        LineChartModelProducer(
            buildModel = {
                val entries = data.mapIndexed { index, dailyData ->
                    mapOf(
                        "total" to dailyData.totalUsage,
                        "rate1" to dailyData.rate1Usage,
                        "rate2" to dailyData.rate2Usage,
                        "rate3" to dailyData.rate3Usage
                    )
                }
                
                lineSeries(
                    series = listOf(
                        lineSeries(
                            values = entries.map { it["total"] ?: 0.0 },
                            color = MaterialTheme.colorScheme.primary,
                            thickness = 3.dp,
                            pointSize = 4.dp,
                            pointShape = com.patrykandpatrick.vico.core.shape.Shape.round(8.dp)
                        ),
                        lineSeries(
                            values = entries.map { it["rate1"] ?: 0.0 },
                            color = Color(0xFF4CAF50),
                            thickness = 2.dp,
                            pointSize = 3.dp,
                            pointShape = com.patrykandpatrick.vico.core.shape.Shape.round(6.dp)
                        ),
                        lineSeries(
                            values = entries.map { it["rate2"] ?: 0.0 },
                            color = Color(0xFF2196F3),
                            thickness = 2.dp,
                            pointSize = 3.dp,
                            pointShape = com.patrykandpatrick.vico.core.shape.Shape.round(6.dp)
                        ),
                        lineSeries(
                            values = entries.map { it["rate3"] ?: 0.0 },
                            color = Color(0xFFFF9800),
                            thickness = 2.dp,
                            pointSize = 3.dp,
                            pointShape = com.patrykandpatrick.vico.core.shape.Shape.round(6.dp)
                        )
                    )
                )
            }
        )
    }
}

/**
 * Empty chart placeholder
 */
@Composable
private fun EmptyChartPlaceholder(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Interactive daily usage chart with tooltips
 */
@Composable
fun InteractiveDailyUsageLineChart(
    data: List<DailyUsageData>,
    onPointSelected: (DailyUsageData) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isTablet = isTablet()
    val chartHeight = if (isTablet) 300.dp else 250.dp
    
    if (data.isEmpty()) {
        EmptyChartPlaceholder(
            message = "No daily usage data available",
            modifier = modifier
        )
        return
    }
    
    // Prepare chart data
    val chartModel = remember(data) {
        createLineChartModel(data)
    }
    
    // Chart colors
    val totalUsageColor = MaterialTheme.colorScheme.primary
    val rate1Color = Color(0xFF4CAF50) // Green
    val rate2Color = Color(0xFF2196F3) // Blue
    val rate3Color = Color(0xFFFF9800) // Orange
    
    var selectedPoint by remember { mutableStateOf<DailyUsageData?>(null) }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Chart title and stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Daily Usage",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Daily consumption breakdown",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Summary stats
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val totalAvg = data.map { it.totalUsage }.average()
                Text(
                    text = "Avg: ${String.format("%.1f", totalAvg)} kWh",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Total: ${String.format("%.0f", data.sumOf { it.totalUsage })} kWh",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight)
        ) {
            Chart(
                chart = lineChart(
                    modelProducer = chartModel,
                    axis = horizontal(
                        valueFormatter = { value, context ->
                            data.getOrNull(value.toInt())?.date?.let { date ->
                                date.format(DateTimeFormatter.ofPattern("MMM dd"))
                            } ?: ""
                        },
                        title = "Date"
                    ) + vertical(
                        valueFormatter = { value, _ ->
                            String.format("%.1f", value)
                        },
                        title = "Usage (kWh)"
                    ),
                    showHorizontalGuidelines = true,
                    showVerticalGuidelines = false,
                    spacing = 8.dp
                ),
                modifier = Modifier.fillMaxSize(),
                chartScrollState = rememberChartScrollState(),
                chartZoomState = rememberChartZoomState(),
                runInitialAnimation = true,
                marker = remember {
                    com.patrykandpatrick.vico.compose.chart.marker.DefaultMarker(
                        label = { markerEntry, _ ->
                            val dataIndex = markerEntry.x.toInt()
                            data.getOrNull(dataIndex)?.let { dailyData ->
                                com.patrykandpatrick.vico.compose.chart.marker.Label(
                                    text = "${dailyData.date.format(DateTimeFormatter.ofPattern("MMM dd"))}\n" +
                                            "Total: ${String.format("%.1f", dailyData.totalUsage)} kWh\n" +
                                            "Rate 1: ${String.format("%.1f", dailyData.rate1Usage)} kWh\n" +
                                            "Rate 2: ${String.format("%.1f", dailyData.rate2Usage)} kWh\n" +
                                            "Rate 3: ${String.format("%.1f", dailyData.rate3Usage)} kWh",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    background = MaterialTheme.colorScheme.surface,
                                    padding = 8.dp,
                                    borderRadius = 8.dp
                                )
                            }
                        }
                    )
                }
            )
        }
        
        // Selected point details
        selectedPoint?.let { point ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = point.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Total: ${String.format("%.1f", point.totalUsage)} kWh",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Text(
                        text = "Tap to close",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
        
        // Legend
        ChartLegend(
            items = listOf(
                LegendItem("Total", totalUsageColor),
                LegendItem("Rate 1", rate1Color),
                LegendItem("Rate 2", rate2Color),
                LegendItem("Rate 3", rate3Color)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Helper function to determine if the screen is tablet-sized
 */
@Composable
private fun isTablet(): Boolean {
    val screenWidth = androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp
    return screenWidth >= 600
}