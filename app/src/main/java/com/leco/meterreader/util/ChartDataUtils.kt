package com.leco.meterreader.util

import com.leco.meterreader.data.model.MeterReading
import com.leco.meterreader.viewmodel.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.Locale

/**
 * Utility class for transforming meter reading data into chart-ready formats
 */
object ChartDataUtils {
    
    private val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    private val csvFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    
    /**
     * Transforms raw meter readings into chart data structures
     */
    fun transformChartData(
        readings: List<MeterReading>,
        dateRange: DateRange
    ): ChartDataResult {
        
        val dailyUsage = transformToDailyData(readings, dateRange)
        val weeklyTrends = transformToWeeklyData(readings, dateRange)
        val touComparison = transformToTOUData(readings, dateRange)
        
        val totalConsumption = readings.sumOf { it.totalReading }
        val averageDailyUsage = if (dailyUsage.isNotEmpty()) {
            dailyUsage.sumOf { it.totalUsage } / dailyUsage.size
        } else 0.0
        
        return ChartDataResult(
            dailyUsage = dailyUsage,
            weeklyTrends = weeklyTrends,
            touComparison = touComparison,
            totalConsumption = totalConsumption,
            averageDailyUsage = averageDailyUsage
        )
    }
    
    /**
     * Transforms readings into daily usage data for line chart
     */
    private fun transformToDailyData(
        readings: List<MeterReading>,
        dateRange: DateRange
    ): List<DailyUsageData> {
        
        val dailyMap = mutableMapOf<LocalDate, DailyUsageData>()
        
        // Initialize all dates in range with zero values
        var currentDate = dateRange.start
        while (!currentDate.isAfter(dateRange.end)) {
            dailyMap[currentDate] = DailyUsageData(
                date = currentDate,
                totalUsage = 0.0,
                rate1Usage = 0.0,
                rate2Usage = 0.0,
                rate3Usage = 0.0
            )
            currentDate = currentDate.plusDays(1)
        }
        
        // Aggregate readings by date
        readings.forEach { reading ->
            val date = reading.timestamp.toLocalDate()
            if (dailyMap.containsKey(date)) {
                val existing = dailyMap[date]!!
                dailyMap[date] = existing.copy(
                    totalUsage = existing.totalUsage + reading.totalReading,
                    rate1Usage = existing.rate1Usage + reading.rate1Reading,
                    rate2Usage = existing.rate2Usage + reading.rate2Reading,
                    rate3Usage = existing.rate3Usage + reading.rate3Reading
                )
            }
        }
        
        return dailyMap.values.sortedBy { it.date }
    }
    
    /**
     * Transforms readings into weekly trend data for bar + line chart
     */
    private fun transformToWeeklyData(
        readings: List<MeterReading>,
        dateRange: DateRange
    ): List<WeeklyTrendData> {
        
        val weeklyMap = mutableMapOf<Int, MutableList<MeterReading>>()
        val weekFields = WeekFields.of(Locale.getDefault())
        
        // Group readings by week
        readings.forEach { reading ->
            val week = reading.timestamp.get(weekFields.weekOfWeekBasedYear())
            weeklyMap.getOrPut(week) { mutableListOf() }.add(reading)
        }
        
        val weeklyData = mutableListOf<WeeklyTrendData>()
        var currentWeek = dateRange.start.get(weekFields.weekOfWeekBasedYear())
        
        // Process each week in the date range
        while (currentWeek <= dateRange.end.get(weekFields.weekOfWeekBasedYear())) {
            val weekReadings = weeklyMap[currentWeek] ?: emptyList()
            val weekStart = getWeekStart(currentWeek, readings.firstOrNull()?.timestamp?.year ?: LocalDate.now().year)
            val weekEnd = weekStart.plusDays(6)
            
            if (weekReadings.isNotEmpty()) {
                val totalUsage = weekReadings.sumOf { it.totalReading }
                val averageUsage = totalUsage / weekReadings.size
                
                // Get previous week data for comparison
                val previousWeekUsage = getPreviousWeekUsage(currentWeek, weeklyMap)
                
                weeklyData.add(
                    WeeklyTrendData(
                        weekStart = weekStart,
                        weekEnd = weekEnd,
                        totalUsage = totalUsage,
                        averageUsage = averageUsage,
                        previousWeekUsage = previousWeekUsage
                    )
                )
            }
            
            currentWeek++
        }
        
        return weeklyData.sortedBy { it.weekStart }
    }
    
    /**
     * Transforms readings into TOU comparison data for stacked bar chart
     */
    private fun transformToTOUData(
        readings: List<MeterReading>,
        dateRange: DateRange
    ): List<TOUComparisonData> {
        
        val dailyMap = mutableMapOf<LocalDate, TOUComparisonData>()
        
        // Initialize all dates in range
        var currentDate = dateRange.start
        while (!currentDate.isAfter(dateRange.end)) {
            dailyMap[currentDate] = TOUComparisonData(
                date = currentDate,
                offPeakUsage = 0.0,
                dayUsage = 0.0,
                peakUsage = 0.0,
                totalUsage = 0.0
            )
            currentDate = currentDate.plusDays(1)
        }
        
        // Aggregate readings by date and time period
        readings.forEach { reading ->
            val date = reading.timestamp.toLocalDate()
            if (dailyMap.containsKey(date)) {
                val hour = reading.timestamp.hour
                val timePeriod = getTimePeriod(hour)
                
                val existing = dailyMap[date]!!
                val updated = when (timePeriod) {
                    TimePeriod.OFF_PEAK -> existing.copy(
                        offPeakUsage = existing.offPeakUsage + reading.totalReading,
                        totalUsage = existing.totalUsage + reading.totalReading
                    )
                    TimePeriod.DAY -> existing.copy(
                        dayUsage = existing.dayUsage + reading.totalReading,
                        totalUsage = existing.totalUsage + reading.totalReading
                    )
                    TimePeriod.PEAK -> existing.copy(
                        peakUsage = existing.peakUsage + reading.totalReading,
                        totalUsage = existing.totalUsage + reading.totalReading
                    )
                }
                
                dailyMap[date] = updated
            }
        }
        
        return dailyMap.values.sortedBy { it.date }
    }
    
    /**
     * Exports chart data to CSV format
     */
    fun exportToCSV(
        dailyData: List<DailyUsageData>,
        weeklyData: List<WeeklyTrendData>,
        touData: List<TOUComparisonData>,
        dateRange: DateRange?
    ): String {
        
        val csvBuilder = StringBuilder()
        
        // CSV Header
        csvBuilder.append("LECO Solar Meter Analyzer - Analytics Data Export\n")
        csvBuilder.append("Export Date: ${LocalDate.now().format(formatter)}\n")
        csvBuilder.append("Date Range: ${dateRange?.start?.format(formatter)} - ${dateRange?.end?.format(formatter)}\n\n")
        
        // Daily Usage Data
        csvBuilder.append("=== Daily Usage Data ===\n")
        csvBuilder.append("Date,Total Usage (kWh),Rate 1 (kWh),Rate 2 (kWh),Rate 3 (kWh)\n")
        
        dailyData.forEach { data ->
            csvBuilder.append("${data.date.format(csvFormatter)},")
            csvBuilder.append("${String.format("%.3f", data.totalUsage)},")
            csvBuilder.append("${String.format("%.3f", data.rate1Usage)},")
            csvBuilder.append("${String.format("%.3f", data.rate2Usage)},")
            csvBuilder.append("${String.format("%.3f", data.rate3Usage)}\n")
        }
        
        csvBuilder.append("\n")
        
        // Weekly Trends Data
        csvBuilder.append("=== Weekly Trends Data ===\n")
        csvBuilder.append("Week Start,Week End,Total Usage (kWh),Average Daily (kWh),Previous Week (kWh)\n")
        
        weeklyData.forEach { data ->
            csvBuilder.append("${data.weekStart.format(csvFormatter)},")
            csvBuilder.append("${data.weekEnd.format(csvFormatter)},")
            csvBuilder.append("${String.format("%.3f", data.totalUsage)},")
            csvBuilder.append("${String.format("%.3f", data.averageUsage)},")
            csvBuilder.append("${String.format("%.3f", data.previousWeekUsage ?: 0)}\n")
        }
        
        csvBuilder.append("\n")
        
        // TOU Comparison Data
        csvBuilder.append("=== Time of Use Comparison Data ===\n")
        csvBuilder.append("Date,Off-Peak (kWh),Day (kWh),Peak (kWh),Total (kWh)\n")
        
        touData.forEach { data ->
            csvBuilder.append("${data.date.format(csvFormatter)},")
            csvBuilder.append("${String.format("%.3f", data.offPeakUsage)},")
            csvBuilder.append("${String.format("%.3f", data.dayUsage)},")
            csvBuilder.append("${String.format("%.3f", data.peakUsage)},")
            csvBuilder.append("${String.format("%.3f", data.totalUsage)}\n")
        }
        
        csvBuilder.append("\n")
        
        // Summary Statistics
        csvBuilder.append("=== Summary Statistics ===\n")
        csvBuilder.append("Total Period Consumption: ${String.format("%.3f", dailyData.sumOf { it.totalUsage })} kWh\n")
        csvBuilder.append("Average Daily Usage: ${String.format("%.3f", dailyData.map { it.totalUsage }.average())} kWh\n")
        csvBuilder.append("Number of Days: ${dailyData.size}\n")
        
        return csvBuilder.toString()
    }
    
    /**
     * Helper function to get week start date
     */
    private fun getWeekStart(week: Int, year: Int): LocalDate {
        val firstDayOfYear = LocalDate.of(year, 1, 1)
        val weekFields = WeekFields.of(Locale.getDefault())
        return firstDayOfYear.plusWeeks((week - 1).toLong())
    }
    
    /**
     * Helper function to get previous week usage
     */
    private fun getPreviousWeekUsage(currentWeek: Int, weeklyMap: Map<Int, List<MeterReading>>): Double? {
        val previousWeek = currentWeek - 1
        return weeklyMap[previousWeek]?.sumOf { it.totalReading }?.takeIf { it > 0 }
    }
    
    /**
     * Helper function to determine time period based on hour
     */
    private fun getTimePeriod(hour: Int): TimePeriod {
        return when {
            hour in 22 || hour in 0..5 -> TimePeriod.OFF_PEAK
            hour in 6..18 -> TimePeriod.DAY
            else -> TimePeriod.PEAK
        }
    }
}

/**
 * Result container for transformed chart data
 */
data class ChartDataResult(
    val dailyUsage: List<DailyUsageData>,
    val weeklyTrends: List<WeeklyTrendData>,
    val touComparison: List<TOUComparisonData>,
    val totalConsumption: Double,
    val averageDailyUsage: Double
)

/**
 * Time period enumeration for TOU data
 */
enum class TimePeriod {
    OFF_PEAK, DAY, PEAK
}