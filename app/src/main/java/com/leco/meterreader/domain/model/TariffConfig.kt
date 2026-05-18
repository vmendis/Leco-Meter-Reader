package com.leco.meterreader.domain.model

import java.util.Date

/**
 * Domain model for tariff configuration.
 * Represents LECO TOU (Time Of Use) tariff rates.
 */
data class TariffConfig(
    val id: Long = 0,
    val dayRate: Double,
    val offPeakRate: Double,
    val peakRate: Double,
    val fixedCharge: Double = 0.0,
    val effectiveDate: Date = Date()
) {
    /**
     * Calculate the total cost for given usage values.
     */
    fun calculateCost(dayUsage: Double, offPeakUsage: Double, peakUsage: Double): Double {
        return (dayUsage * dayRate) + (offPeakUsage * offPeakRate) + (peakUsage * peakRate)
    }
}