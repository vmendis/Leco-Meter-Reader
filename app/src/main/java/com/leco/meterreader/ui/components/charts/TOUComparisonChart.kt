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
import com.leco.meterreader.viewmodel.TOUComparisonData
import com.patrykandpatrick.vico.compose.axis.horizontal
import com.patrykandpatrick.vico.compose.axis.vertical
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.chart.zoom.rememberChartZoomState
import com.patrykandpatrick.vico.core.chart.values.MutableChartValues
import com.patrykandpatrick.vico.core.chart.values.MutableChartValuesEntryModel
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.model.ColumnChartModel
import com.patrykandpatrick.vico.core.model.columnSeries
import com.patrykandpatrick.vico.core.model.StackedColumnModel
import com.patrykandpatrick.vico.core.model.stackedColumnSeries
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Time of Use (TOU) comparison stacked bar chart using Vico library
 */
@Composable
fun TOUComparisonChart(
    data: List<TOUComparisonData>,
    modifier: Modifier = Modifier
) {
    val isTablet = isTablet()
    val chartHeight = if (isTablet) 300.dp else 250.dp
    
    if (data.isEmpty()) {
        EmptyChartPlaceholder(
            message = "No TOU comparison data available",
            modifier = modifier
        )
        return
    }
    
    // TOU colors
    val offPeakColor = Color(0xFF4CAF50) // Green
    val dayColor = Color(0xFF2196F3) // Blue
    val peakColor = Color(0xFFFF9800) // Orange
    
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
                    text = "Time of Use Comparison",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Off-Peak vs Day vs Peak usage",
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
                    text = "Days: ${data.size}",
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
                chart = columnChart(
                    modelProducer = remember(data) {
                        StackedColumnChartModelProducer(
                            buildModel = {
                                stackedColumnSeries(
                                    values = data.map { dayData ->
                                        listOf(
                                            dayData.offPeakUsage,
                                            dayData.dayUsage,
                                            dayData.peakUsage
                                        )
                                    },
                                    colors = listOf(offPeakColor, dayColor, peakColor),
                                    spacing = 8.dp
                                )
                            }
                        )
                    },
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
                            data.getOrNull(dataIndex)?.let { touData ->
                                com.patrykandpatrick.vico.compose.chart.marker.Label(
                                    text = "${touData.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}\n" +
                                            "Total: ${String.format("%.1f", touData.totalUsage)} kWh\n" +
                                            "Off-Peak: ${String.format("%.1f", touData.offPeakUsage)} kWh (${String.format("%.0f", (touData.offPeakUsage / touData.totalUsage) * 100)}%)\n" +
                                            "Day: ${String.format("%.1f", touData.dayUsage)} kWh (${String.format("%.0f", (touData.dayUsage / touData.totalUsage) * 100)}%)\n" +
                                            "Peak: ${String.format("%.1f", touData.peakUsage)} kWh (${String.format("%.0f", (touData.peakUsage / touData.totalUsage) * 100)}%)",
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
        
        // Legend
        ChartLegend(
            items = listOf(
                LegendItem("Off-Peak", offPeakColor),
                LegendItem("Day", dayColor),
                LegendItem("Peak", peakColor)
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        // TOU summary statistics
        TOUSummaryStats(data = data)
    }
}

/**
 * Creates a stacked column chart model for TOU data
 */
@Composable
private fun StackedColumnChartModelProducer(
    buildModel: StackedColumnModel.() -> Unit
): StackedColumnChartModelProducer {
    return remember {
        StackedColumnChartModelProducer(
            buildModel = buildModel
        )
    }
}

/**
 * Interactive TOU comparison chart with enhanced features
 */
@Composable
fun InteractiveTOUComparisonChart(
    data: List<TOUComparisonData>,
    onDaySelected: (TOUComparisonData) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isTablet = isTablet()
    val chartHeight = if (isTablet) 300.dp else 250.dp
    
    if (data.isEmpty()) {
        EmptyChartPlaceholder(
            message = "No TOU comparison data available",
            modifier = modifier
        )
        return
    }
    
    // TOU colors
    val offPeakColor = Color(0xFF4CAF50) // Green
    val dayColor = Color(0xFF2196F3) // Blue
    val peakColor = Color(0xFFFF9800) // Orange
    
    var selectedDay by remember { mutableStateOf<TOUComparisonData?>(null) }
    
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
                    text = "Time of Use Comparison",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Off-Peak vs Day vs Peak usage",
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
                    text = "Days: ${data.size}",
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
                chart = columnChart(
                    modelProducer = remember(data) {
                        StackedColumnChartModelProducer(
                            buildModel = {
                                stackedColumnSeries(
                                    values = data.map { dayData ->
                                        listOf(
                                            dayData.offPeakUsage,
                                            dayData.dayUsage,
                                            dayData.peakUsage
                                        )
                                    },
                                    colors = listOf(offPeakColor, dayColor, peakColor),
                                    spacing = 8.dp
                                )
                            }
                        )
                    },
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
                            data.getOrNull(dataIndex)?.let { touData ->
                                com.patrykandpatrick.vico.compose.chart.marker.Label(
                                    text = "${touData.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}\n" +
                                            "Total: ${String.format("%.1f", touData.totalUsage)} kWh\n" +
                                            "Off-Peak: ${String.format("%.1f", touData.offPeakUsage)} kWh (${String.format("%.0f", (touData.offPeakUsage / touData.totalUsage) * 100)}%)\n" +
                                            "Day: ${String.format("%.1f", touData.dayUsage)} kWh (${String.format("%.0f", (touData.dayUsage / touData.totalUsage) * 100)}%)\n" +
                                            "Peak: ${String.format("%.1f", touData.peakUsage)} kWh (${String.format("%.0f", (touData.peakUsage / touData.totalUsage) * 100)}%)",
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
        
        // Selected day details
        selectedDay?.let { day ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = day.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Total: ${String.format("%.1f", day.totalUsage)} kWh",
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
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // TOU breakdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TOUBreakdownItem(
                            label = "Off-Peak",
                            value = day.offPeakUsage,
                            percentage = (day.offPeakUsage / day.totalUsage) * 100,
                            color = offPeakColor
                        )
                        TOUBreakdownItem(
                            label = "Day",
                            value = day.dayUsage,
                            percentage = (day.dayUsage / day.totalUsage) * 100,
                            color = dayColor
                        )
                        TOUBreakdownItem(
                            label = "Peak",
                            value = day.peakUsage,
                            percentage = (day.peakUsage / day.totalUsage) * 100,
                            color = peakColor
                        )
                    }
                }
            }
        }
        
        // Legend
        ChartLegend(
            items = listOf(
                LegendItem("Off-Peak", offPeakColor),
                LegendItem("Day", dayColor),
                LegendItem("Peak", peakColor)
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        // TOU summary statistics
        TOUSummaryStats(data = data)
    }
}

/**
 * TOU breakdown item component
 */
@Composable
private fun TOUBreakdownItem(
    label: String,
    value: Double,
    percentage: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color, shape = RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = String.format("%.1f kWh", value),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = String.format("%.0f%%", percentage),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

/**
 * TOU summary statistics component
 */
@Composable
private fun TOUSummaryStats(
    data: List<TOUComparisonData>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return
    
    val totalOffPeak = data.sumOf { it.offPeakUsage }
    val totalDay = data.sumOf { it.dayUsage }
    val totalPeak = data.sumOf { it.peakUsage }
    val totalOverall = totalOffPeak + totalDay + totalPeak
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TOUStatItem(
                label = "Off-Peak",
                value = totalOffPeak,
                percentage = (totalOffPeak / totalOverall) * 100,
                color = Color(0xFF4CAF50)
            )
            TOUStatItem(
                label = "Day",
                value = totalDay,
                percentage = (totalDay / totalOverall) * 100,
                color = Color(0xFF2196F3)
            )
            TOUStatItem(
                label = "Peak",
                value = totalPeak,
                percentage = (totalPeak / totalOverall) * 100,
                color = Color(0xFFFF9800)
            )
        }
    }
}

/**
 * TOU statistics item component
 */
@Composable
private fun TOUStatItem(
    label: String,
    value: Double,
    percentage: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = String.format("%.0f kWh", value),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = String.format("%.0f%%", percentage),
            style = MaterialTheme.typography.bodySmall,
            color = color
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
 * Helper function to determine if the screen is tablet-sized
 */
@Composable
private fun isTablet(): Boolean {
    val screenWidth = androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp
    return screenWidth >= 600
}