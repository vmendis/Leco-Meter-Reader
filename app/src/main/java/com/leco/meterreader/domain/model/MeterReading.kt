package com.leco.meterreader.domain.model

import java.util.Date

/**
 * Domain model for a meter reading.
 * Represents a single reading entry from the LECO smart meter.
 */
data class MeterReading(
    val id: Long = 0,
    val timestamp: Date,
    val totalReading: Double,
    val rate1Day: Double,
    val rate2OffPeak: Double,
    val rate3Peak: Double,
    val notes: String? = null,
    val createdAt: Date = Date()
) {
    /**
     * Calculate the sum of all rate readings.
     * Used for validation against total reading.
     */
    val rateSum: Double
        get() = rate1Day + rate2OffPeak + rate3Peak
}