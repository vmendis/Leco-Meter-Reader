package com.leco.meterreader.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leco.meterreader.data.model.MeterReading
import com.leco.meterreader.util.ChartDataUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AnalyticsViewModel : ViewModel() {
    
    // UI State
    var uiState by mutableStateOf(AnalyticsUiState())
        private set
    
    // Private state for business logic
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    // Repository would be injected here in a real app
    private val readingRepository = FakeAnalyticsRepository()
    
    /**
     * Initializes the analytics data
     */
    fun initializeData() {
        loadChartData()
    }
    
    /**
     * Loads chart data based on selected date range
     */
    fun loadChartData() {
        if (uiState.selectedDateRange == null) return
        
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            try {
                val readings = readingRepository.getReadingsForDateRange(
                    uiState.selectedDateRange!!.start,
                    uiState.selectedDateRange!!.end
                )
                
                val chartData = ChartDataUtils.transformChartData(
                    readings,
                    uiState.selectedDateRange!!
                )
                
                uiState = uiState.copy(
                    dailyUsageData = chartData.dailyUsage,
                    weeklyTrendsData = chartData.weeklyTrends,
                    touComparisonData = chartData.touComparison,
                    totalConsumption = chartData.totalConsumption,
                    averageDailyUsage = chartData.averageDailyUsage
                )
                
            } catch (e: Exception) {
                _error.value = "Failed to load chart data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Updates the selected date range
     */
    fun onDateRangeSelected(start: LocalDate, end: LocalDate) {
        uiState = uiState.copy(
            selectedDateRange = DateRange(start, end)
        )
        loadChartData()
    }
    
    /**
     * Exports chart data to CSV
     */
    fun exportChartData(): String {
        return ChartDataUtils.exportToCSV(
            uiState.dailyUsageData,
            uiState.weeklyTrendsData,
            uiState.touComparisonData,
            uiState.selectedDateRange
        )
    }
    
    /**
     * Clears error state
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Formats date for display
     */
    fun formatDateRange(): String {
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
        return if (uiState.selectedDateRange != null) {
            "${uiState.selectedDateRange!!.start.format(formatter)} - ${uiState.selectedDateRange!!.end.format(formatter)}"
        } else {
            "No date range selected"
        }
    }
}

/**
 * UI State for the analytics screen
 */
data class AnalyticsUiState(
    val selectedDateRange: DateRange? = DateRange(
        LocalDate.now().minusDays(30),
        LocalDate.now()
    ),
    val dailyUsageData: List<DailyUsageData> = emptyList(),
    val weeklyTrendsData: List<WeeklyTrendData> = emptyList(),
    val touComparisonData: List<TOUComparisonData> = emptyList(),
    val totalConsumption: Double = 0.0,
    val averageDailyUsage: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * Date range data class
 */
data class DateRange(
    val start: LocalDate,
    val end: LocalDate
)

/**
 * Daily usage data for line chart
 */
data class DailyUsageData(
    val date: LocalDate,
    val totalUsage: Double,
    val rate1Usage: Double,
    val rate2Usage: Double,
    val rate3Usage: Double
)

/**
 * Weekly trend data for bar + line chart
 */
data class WeeklyTrendData(
    val weekStart: LocalDate,
    val weekEnd: LocalDate,
    val totalUsage: Double,
    val averageUsage: Double,
    val previousWeekUsage: Double? = null
)

/**
 * TOU comparison data for stacked bar chart
 */
data class TOUComparisonData(
    val date: LocalDate,
    val offPeakUsage: Double,
    val dayUsage: Double,
    val peakUsage: Double,
    val totalUsage: Double
)

/**
 * Fake repository for demonstration
 */
private class FakeAnalyticsRepository {
    
    private val readings = mutableListOf<MeterReading>()
    
    init {
        // Generate sample data for testing
        generateSampleData()
    }
    
    private fun generateSampleData() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val now = LocalDateTime.now()
        
        // Generate readings for the last 90 days
        for (i in 0..89) {
            val date = now.minusDays(i.toLong())
            val totalReading = 20.0 + (Math.random() * 15.0) // 20-35 kWh
            val rate1 = totalReading * (0.3 + Math.random() * 0.2) // 30-50%
            val rate2 = totalReading * (0.2 + Math.random() * 0.2) // 20-40%
            val rate3 = totalReading - rate1 - rate2 // remainder
            
            readings.add(
                MeterReading(
                    timestamp = date,
                    totalReading = totalReading,
                    rate1Reading = rate1,
                    rate2Reading = rate2,
                    rate3Reading = rate3,
                    notes = "Sample reading"
                )
            )
        }
    }
    
    suspend fun getReadingsForDateRange(start: LocalDate, end: LocalDate): List<MeterReading> {
        return readings.filter { reading ->
            reading.timestamp.toLocalDate() in start..end
        }.sortedBy { it.timestamp }
    }
}