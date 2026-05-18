package com.leco.meterreader.domain.calculation

import com.leco.meterreader.domain.model.CalculatedUsage
import com.leco.meterreader.domain.model.MeterReading
import com.leco.meterreader.domain.model.TariffConfig
import java.util.Date
import javax.inject.Inject

/**
 * Calculation engine for usage deltas and cost estimation.
 */
class CalculationEngine @Inject constructor() {

    /**
     * Calculate usage deltas from two consecutive readings.
     */
    fun calculateUsageDeltas(
        previousReading: MeterReading,
        newReading: MeterReading
    ): CalculatedUsage {
        return CalculatedUsage(
            fromReadingId = previousReading.id,
            toReadingId = newReading.id,
            totalUsed = newReading.totalReading - previousReading.totalReading,
            dayUsed = newReading.rate1Day - previousReading.rate1Day,
            offPeakUsed = newReading.rate2OffPeak - previousReading.rate2OffPeak,
            peakUsed = newReading.rate3Peak - previousReading.rate3Peak,
            calculationTimestamp = Date()
        )
    }

    /**
     * Calculate estimated cost based on usage and tariff configuration.
     */
    fun calculateCost(
        calculatedUsage: CalculatedUsage,
        tariff: TariffConfig
    ): Double {
        val dayCost = calculatedUsage.dayUsed * tariff.dayRate
        val offPeakCost = calculatedUsage.offPeakUsed * tariff.offPeakRate
        val peakCost = calculatedUsage.peakUsed * tariff.peakRate
        
        return dayCost + offPeakCost + peakCost
    }

    /**
     * Calculate daily cost estimate.
     */
    fun calculateDailyCost(
        totalUsed: Double,
        dayUsed: Double,
        offPeakUsed: Double,
        peakUsed: Double,
        tariff: TariffConfig
    ): Double {
        return (dayUsed * tariff.dayRate) + 
               (offPeakUsed * tariff.offPeakRate) + 
               (peakUsed * tariff.peakRate)
    }

    /**
     * Get TOU category for a specific time.
     */
    fun getTouCategory(timestamp: Date): TouCategory {
        val calendar = java.util.Calendar.getInstance().apply { time = timestamp }
        val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = calendar.get(java.util.Calendar.MINUTE)
        return getTouCategoryForHour(hour, minute)
    }
}