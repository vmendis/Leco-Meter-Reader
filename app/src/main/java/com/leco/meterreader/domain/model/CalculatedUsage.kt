package com.leco.meterreader.domain.model

import java.util.Date

/**
 * Domain model for calculated usage derived from consecutive meter readings.
 */
data class CalculatedUsage(
    val id: Long = 0,
    val fromReadingId: Long,
    val toReadingId: Long,
    val totalUsed: Double,
    val dayUsed: Double,
    val offPeakUsed: Double,
    val peakUsed: Double,
    val estimatedCost: Double = 0.0,
    val calculationTimestamp: Date = Date()
)