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
import com.leco.meterreader.viewmodel.WeeklyTrendData
import com.patrykandpatrick.vico.compose.axis.horizontal
import com.patrykandpatrick.vico.compose.axis.vertical
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.composed.ComposedChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.chart.zoom.rememberChartZoomState
import com.patrykandpatrick.vico.core.chart.values.MutableChartValues
import com.patrykandpatrick.vico.core.chart.values.MutableChartValuesEntryModel
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.model.ColumnChartModel
import com.patrykandpatrick.vico.core.model.LineChartModel
import com.patrykandpatrick.vico.core.model.columnSeries
import com.patrykandpatrick.vico.core.model.lineSeries
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Weekly trends bar + line combination chart using Vico library
 */
@Composable
fun WeeklyTrendsChart(
    data: List<WeeklyTrendData>,
    modifier: Modifier = Modifier
) {
    val isTablet = isTablet()
    val chartHeight = if (isTablet) 300.dp else 250.dp
    
    if (data.isEmpty()) {
        EmptyChartPlaceholder(
            message = "No weekly trends data available",
            modifier = modifier
        )
        return
    }
    
    // Chart colors
    val barColor = MaterialTheme.colorScheme.primary
    val lineColor = Color(0xFFE91E63) // Pink for trend line
    val comparisonColor = Color(0xFF9C27B0) // Purple for comparison
    
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
                    text = "Weekly Trends",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Weekly consumption patterns",
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
                    text = "Avg: ${String.format("%.0f", totalAvg)} kWh",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Weeks: ${data.size}",
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
            ComposedChart(
                modifier = Modifier.fillMaxSize(),
                chartScrollState = rememberChartScrollState(),
                chartZoomState = rememberChartZoomState(),
                runInitialAnimation = true,
                charts = listOf(
                    columnChart(
                        modelProducer = remember(data) {
                            ColumnChartModelProducer(
                                buildModel = {
                                    columnSeries(
                                        values = data.map { it.totalUsage },
                                        color = barColor,
                                        thickness = 20.dp,
                                        spacing = 8.dp
                                    )
                                }
                            )
                        },
                        axis = horizontal(
                            valueFormatter = { value, context ->
                                data.getOrNull(value.toInt())?.let { weekData ->
                                    "${weekData.weekStart.format(DateTimeFormatter.ofPattern("MMM dd"))}\n" +
                                    "${weekData.weekEnd.format(DateTimeFormatter.ofPattern("MMM dd"))}"
                                } ?: ""
                            },
                            title = "Week"
                        ) + vertical(
                            valueFormatter = { value, _ ->
                                String.format("%.0f", value)
                            },
                            title = "Usage (kWh)"
                        ),
                        showHorizontalGuidelines = true,
                        showVerticalGuidelines = false
                    ),
                    lineChart(
                        modelProducer = remember(data) {
                            LineChartModelProducer(
                                buildModel = {
                                    lineSeries(
                                        values = data.map { it.averageUsage },
                                        color = lineColor,
                                        thickness = 3.dp,
                                        pointSize = 6.dp,
                                        pointShape = com.patrykandpatrick.vico.core.shape.Shape.round(8.dp)
                                    )
                                }
                            )
                        },
                        axis = empty(),
                        showHorizontalGuidelines = false,
                        showVerticalGuidelines = false
                    )
                ),
                marker = remember {
                    com.patrykandpatrick.vico.compose.chart.marker.DefaultMarker(
                        label = { markerEntry, _ ->
                            val dataIndex = markerEntry.x.toInt()
                            data.getOrNull(dataIndex)?.let { weekData ->
                                com.patrykandpatrick.vico.compose.chart.marker.Label(
                                    text = "${weekData.weekStart.format(DateTimeFormatter.ofPattern("MMM dd"))} - ${weekData.weekEnd.format(DateTimeFormatter.ofPattern("MMM dd"))}\n" +
                                            "Total: ${String.format("%.0f", weekData.totalUsage)} kWh\n" +
                                            "Daily Avg: ${String.format("%.1f", weekData.averageUsage)} kWh\n" +
                                            "${weekData.previousWeekUsage?.let { "Prev Week: ${String.format("%.0f", it)} kWh" } ?: ""}",
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
                LegendItem("Weekly Total", barColor),
                LegendItem("Daily Average", lineColor),
                LegendItem("Previous Week", comparisonColor)
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        // Trend comparison
        if (data.any { it.previousWeekUsage != null }) {
            val trendText = calculateTrendText(data)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
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
                        text = trendText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    val trendPercentage = calculateTrendPercentage(data)
                    Text(
                        text = trendPercentage,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (trendPercentage.startsWith("+")) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                }
            }
        }
    }
}

/**
 * Creates a column chart model for weekly totals
 */
@Composable
private fun ColumnChartModelProducer(
    buildModel: ColumnChartModel.() -> Unit
): ColumnChartModelProducer {
    return remember {
        ColumnChartModelProducer(
            buildModel = buildModel
        )
    }
}

/**
 * Creates a line chart model for weekly averages
 */
@Composable
private fun LineChartModelProducer(
    buildModel: LineChartModel.() -> Unit
): LineChartModelProducer {
    return remember {
        LineChartModelProducer(
            buildModel = buildModel
        )
    }
}

/**
 * Calculates trend text based on weekly data
 */
private fun calculateTrendText(data: List<WeeklyTrendData>): String {
    val weeksWithComparison = data.filter { it.previousWeekUsage != null }
    if (weeksWithComparison.isEmpty()) return "No comparison data available"
    
    val increasing = weeksWithComparison.count { week ->
        week.totalUsage > (week.previousWeekUsage ?: 0)
    }
    val decreasing = weeksWithComparison.count { week ->
        week.totalUsage < (week.previousWeekUsage ?: 0)
    }
    
    return when {
        increasing > decreasing -> "Usage trending upward"
        decreasing > increasing -> "Usage trending downward"
        else -> "Usage stable"
    }
}

/**
 * Calculates trend percentage
 */
private fun calculateTrendPercentage(data: List<WeeklyTrendData>): String {
    val latestWeek = data.lastOrNull() ?: return ""
    val previousWeek = latestWeek.previousWeekUsage ?: return ""
    
    val percentage = ((latestWeek.totalUsage - previousWeek) / previousWeek) * 100
    return if (percentage >= 0) {
        "+${String.format("%.1f", percentage)}%"
    } else {
        String.format("%.1f", percentage) + "%"
    }
}

/**
 * Interactive weekly trends chart with enhanced features
 */
@Composable
fun InteractiveWeeklyTrendsChart(
    data: List<WeeklyTrendData>,
    onWeekSelected: (WeeklyTrendData) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isTablet = isTablet()
    val chartHeight = if (isTablet) 300.dp else 250.dp
    
    if (data.isEmpty()) {
        EmptyChartPlaceholder(
            message = "No weekly trends data available",
            modifier = modifier
        )
        return
    }
    
    // Chart colors
    val barColor = MaterialTheme.colorScheme.primary
    val lineColor = Color(0xFFE91E63) // Pink for trend line
    val comparisonColor = Color(0xFF9C27B0) // Purple for comparison
    
    var selectedWeek by remember { mutableStateOf<WeeklyTrendData?>(null) }
    
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
                    text = "Weekly Trends",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Weekly consumption patterns",
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
                    text = "Avg: ${String.format("%.0f", totalAvg)} kWh",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Weeks: ${data.size}",
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
            ComposedChart(
                modifier = Modifier.fillMaxSize(),
                chartScrollState = rememberChartScrollState(),
                chartZoomState = rememberChartZoomState(),
                runInitialAnimation = true,
                charts = listOf(
                    columnChart(
                        modelProducer = remember(data) {
                            ColumnChartModelProducer(
                                buildModel = {
                                    columnSeries(
                                        values = data.map { it.totalUsage },
                                        color = barColor,
                                        thickness = 20.dp,
                                        spacing = 8.dp
                                    )
                                }
                            )
                        },
                        axis = horizontal(
                            valueFormatter = { value, context ->
                                data.getOrNull(value.toInt())?.let { weekData ->
                                    "${weekData.weekStart.format(DateTimeFormatter.ofPattern("MMM dd"))}\n" +
                                    "${weekData.weekEnd.format(DateTimeFormatter.ofPattern("MMM dd"))}"
                                } ?: ""
                            },
                            title = "Week"
                        ) + vertical(
                            valueFormatter = { value, _ ->
                                String.format("%.0f", value)
                            },
                            title = "Usage (kWh)"
                        ),
                        showHorizontalGuidelines = true,
                        showVerticalGuidelines = false
                    ),
                    lineChart(
                        modelProducer = remember(data) {
                            LineChartModelProducer(
                                buildModel = {
                                    lineSeries(
                                        values = data.map { it.averageUsage },
                                        color = lineColor,
                                        thickness = 3.dp,
                                        pointSize = 6.dp,
                                        pointShape = com.patrykandpatrick.vico.core.shape.Shape.round(8.dp)
                                    )
                                }
                            )
                        },
                        axis = empty(),
                        showHorizontalGuidelines = false,
                        showVerticalGuidelines = false
                    )
                ),
                marker = remember {
                    com.patrykandpatrick.vico.compose.chart.marker.DefaultMarker(
                        label = { markerEntry, _ ->
                            val dataIndex = markerEntry.x.toInt()
                            data.getOrNull(dataIndex)?.let { weekData ->
                                com.patrykandpatrick.vico.compose.chart.marker.Label(
                                    text = "${weekData.weekStart.format(DateTimeFormatter.ofPattern("MMM dd"))} - ${weekData.weekEnd.format(DateTimeFormatter.ofPattern("MMM dd"))}\n" +
                                            "Total: ${String.format("%.0f", weekData.totalUsage)} kWh\n" +
                                            "Daily Avg: ${String.format("%.1f", weekData.averageUsage)} kWh\n" +
                                            "${weekData.previousWeekUsage?.let { "Prev Week: ${String.format("%.0f", it)} kWh" } ?: ""}",
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
        
        // Selected week details
        selectedWeek?.let { week ->
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
                                text = "${week.weekStart.format(DateTimeFormatter.ofPattern("MMM dd"))} - ${week.weekEnd.format(DateTimeFormatter.ofPattern("MMM dd"))}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Total: ${String.format("%.0f", week.totalUsage)} kWh",
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
                    
                    week.previousWeekUsage?.let { prevWeek ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Previous Week: ${String.format("%.0f", prevWeek)} kWh",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            val change = week.totalUsage - prevWeek
                            val percentage = (change / prevWeek) * 100
                            Text(
                                text = "${if (change >= 0) "+" else ""}${String.format("%.1f", percentage)}%",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (change >= 0) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.error
                                }
                            )
                        }
                    }
                }
            }
        }
        
        // Legend
        ChartLegend(
            items = listOf(
                LegendItem("Weekly Total", barColor),
                LegendItem("Daily Average", lineColor),
                LegendItem("Previous Week", comparisonColor)
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        // Trend comparison
        if (data.any { it.previousWeekUsage != null }) {
            val trendText = calculateTrendText(data)
            val trendPercentage = calculateTrendPercentage(data)
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
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
                        text = trendText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = trendPercentage,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (trendPercentage.startsWith("+")) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                }
            }
        }
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