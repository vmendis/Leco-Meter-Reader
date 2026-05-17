package com.leco.meterreader.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leco.meterreader.data.model.MeterReading
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.format.DateTimeFormatter
import java.util.Locale

class DashboardViewModel : ViewModel() {
    
    // UI State
    var uiState by mutableStateOf(DashboardUiState())
        private set
    
    // Private state for business logic
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    // Repository would be injected here in a real app
    private val readingRepository = FakeReadingRepository()
    
    /**
     * Load dashboard data
     */
    fun loadDashboardData() {
        if (uiState.isDataLoaded) return // Prevent redundant loading
        
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            try {
                val allReadings = readingRepository.getAllReadings()
                val latestReading = readingRepository.getLatestReading()
                val previousReading = readingRepository.getPreviousReading(latestReading?.id)
                
                val calculations = calculateDashboardMetrics(allReadings, latestReading, previousReading)
                
                uiState = uiState.copy(
                    isDataLoaded = true,
                    latestReading = latestReading,
                    previousReading = previousReading,
                    allReadings = allReadings,
                    dailyUsage = calculations.dailyUsage,
                    estimatedDailyCost = calculations.estimatedDailyCost,
                    quickStats = calculations.quickStats,
                    isLoading = false
                )
            } catch (e: Exception) {
                _error.value = "Failed to load dashboard data: ${e.message}"
                uiState = uiState.copy(isLoading = false)
            }
        }
    }
    
    /**
     * Refresh dashboard data
     */
    fun refreshData() {
        uiState = uiState.copy(isDataLoaded = false)
        loadDashboardData()
    }
    
    /**
     * Clear error state
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Calculate dashboard metrics
     */
    private fun calculateDashboardMetrics(
        allReadings: List<MeterReading>,
        latestReading: MeterReading?,
        previousReading: MeterReading?
    ): DashboardCalculations {
        val quickStats = QuickStats(
            totalReadings = allReadings.size,
            averageDailyUsage = calculateAverageDailyUsage(allReadings),
            averageDailyCost = calculateAverageDailyCost(allReadings),
            timeSpan = calculateTimeSpan(allReadings)
        )
        
        val dailyUsage = if (latestReading != null && previousReading != null) {
            val hoursDiff = ChronoUnit.HOURS.between(
                previousReading.timestamp, 
                latestReading.timestamp
            ).toDouble()
            
            val usage = latestReading.totalReading - previousReading.totalReading
            DailyUsage(
                totalUsage = usage,
                hoursElapsed = hoursDiff,
                usagePerHour = if (hoursDiff > 0) usage / hoursDiff else 0.0,
                period = "${previousReading.timestamp.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm"))} - ${latestReading.timestamp.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm"))}"
            )
        } else {
            DailyUsage(
                totalUsage = 0.0,
                hoursElapsed = 0.0,
                usagePerHour = 0.0,
                period = "No data available"
            )
        }
        
        val estimatedDailyCost = if (latestReading != null) {
            val rate1Cost = latestReading.rate1Reading * 0.15 // $0.15/kWh
            val rate2Cost = latestReading.rate2Reading * 0.20 // $0.20/kWh
            val rate3Cost = latestReading.rate3Reading * 0.25 // $0.25/kWh
            val totalCost = rate1Cost + rate2Cost + rate3Cost
            
            EstimatedDailyCost(
                rate1Cost = rate1Cost,
                rate2Cost = rate2Cost,
                rate3Cost = rate3Cost,
                totalCost = totalCost,
                breakdown = listOf(
                    "Rate 1 (Day): $${String.format(Locale.US, "%.2f", rate1Cost)}",
                    "Rate 2 (Off-Peak): $${String.format(Locale.US, "%.2f", rate2Cost)}",
                    "Rate 3 (Peak): $${String.format(Locale.US, "%.2f", rate3Cost)}"
                )
            )
        } else {
            EstimatedDailyCost(
                rate1Cost = 0.0,
                rate2Cost = 0.0,
                rate3Cost = 0.0,
                totalCost = 0.0,
                breakdown = emptyList()
            )
        }
        
        return DashboardCalculations(
            dailyUsage = dailyUsage,
            estimatedDailyCost = estimatedDailyCost,
            quickStats = quickStats
        )
    }
    
    /**
     * Calculate average daily usage
     */
    private fun calculateAverageDailyUsage(readings: List<MeterReading>): Double {
        if (readings.size < 2) return 0.0
        
        val sortedReadings = readings.sortedBy { it.timestamp }
        var totalUsage = 0.0
        var totalHours = 0.0
        
        for (i in 1 until sortedReadings.size) {
            val current = sortedReadings[i]
            val previous = sortedReadings[i - 1]
            
            val usage = current.totalReading - previous.totalReading
            val hours = ChronoUnit.HOURS.between(previous.timestamp, current.timestamp).toDouble()
            
            if (hours > 0 && usage >= 0) {
                totalUsage += usage
                totalHours += hours
            }
        }
        
        return if (totalHours > 0) totalUsage / (totalHours / 24) else 0.0
    }
    
    /**
     * Calculate average daily cost
     */
    private fun calculateAverageDailyCost(readings: List<MeterReading>): Double {
        if (readings.isEmpty()) return 0.0
        
        val totalCost = readings.sumOf { reading ->
            reading.rate1Reading * 0.15 + 
            reading.rate2Reading * 0.20 + 
            reading.rate3Reading * 0.25
        }
        
        return totalCost / readings.size
    }
    
    /**
     * Calculate time span of readings
     */
    private fun calculateTimeSpan(readings: List<MeterReading>): String {
        if (readings.isEmpty()) return "No data"
        
        val sortedReadings = readings.sortedBy { it.timestamp }
        val first = sortedReadings.first()
        val last = sortedReadings.last()
        
        val days = ChronoUnit.DAYS.between(first.timestamp, last.timestamp)
        return when {
            days == 0L -> "Today"
            days == 1L -> "1 day"
            else -> "${days} days"
        }
    }
}

/**
 * UI State for the dashboard
 */
data class DashboardUiState(
    val isDataLoaded: Boolean = false,
    val isLoading: Boolean = false,
    val latestReading: MeterReading? = null,
    val previousReading: MeterReading? = null,
    val allReadings: List<MeterReading> = emptyList(),
    val dailyUsage: DailyUsage = DailyUsage(),
    val estimatedDailyCost: EstimatedDailyCost = EstimatedDailyCost(),
    val quickStats: QuickStats = QuickStats(),
    val error: String? = null
)

/**
 * Daily usage data
 */
data class DailyUsage(
    val totalUsage: Double = 0.0,
    val hoursElapsed: Double = 0.0,
    val usagePerHour: Double = 0.0,
    val period: String = ""
)

/**
 * Estimated daily cost data
 */
data class EstimatedDailyCost(
    val rate1Cost: Double = 0.0,
    val rate2Cost: Double = 0.0,
    val rate3Cost: Double = 0.0,
    val totalCost: Double = 0.0,
    val breakdown: List<String> = emptyList()
)

/**
 * Quick statistics
 */
data class QuickStats(
    val totalReadings: Int = 0,
    val averageDailyUsage: Double = 0.0,
    val averageDailyCost: Double = 0.0,
    val timeSpan: String = ""
)

/**
 * Dashboard calculations container
 */
data class DashboardCalculations(
    val dailyUsage: DailyUsage,
    val estimatedDailyCost: EstimatedDailyCost,
    val quickStats: QuickStats
)

/**
 * Enhanced fake repository for dashboard data
 */
private class FakeReadingRepository {
    private val readings = mutableListOf<MeterReading>()
    
    init {
        // Add some sample data for demonstration
        val now = LocalDateTime.now()
        readings.add(
            MeterReading(
                timestamp = now.minusHours(12),
                totalReading = 1250.750,
                rate1Reading = 800.250,
                rate2Reading = 300.500,
                rate3Reading = 150.000,
                notes = "Morning reading"
            )
        )
        readings.add(
            MeterReading(
                timestamp = now.minusHours(6),
                totalReading = 1255.200,
                rate1Reading = 820.100,
                rate2Reading = 310.800,
                rate3Reading = 124.300,
                notes = "Afternoon reading"
            )
        )
        readings.add(
            MeterReading(
                timestamp = now.minusHours(1),
                totalReading = 1260.450,
                rate1Reading = 840.200,
                rate2Reading = 315.900,
                rate3Reading = 104.350,
                notes = "Evening reading"
            )
        )
    }
    
    suspend fun getAllReadings(): List<MeterReading> {
        return readings.toList()
    }
    
    suspend fun getLatestReading(): MeterReading? {
        return readings.maxByOrNull { it.timestamp }
    }
    
    suspend fun getPreviousReading(excludeId: String?): MeterReading? {
        return readings
            .filter { it.id != excludeId }
            .maxByOrNull { it.timestamp }
    }
}