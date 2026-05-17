package com.leco.meterreader.data.model

import java.time.LocalDateTime
import java.util.UUID

/**
 * Data model for calculated usage between meter readings
 * Represents derived consumption calculations and cost estimates
 */
data class CalculatedUsage(
    val id: String = UUID.randomUUID().toString(),
    val fromReadingId: String,
    val toReadingId: String,
    val totalUsed: Double,
    val dayUsed: Double,
    val offPeakUsed: Double,
    val peakUsed: Double,
    val estimatedCost: Double,
    val calculationTimestamp: LocalDateTime = LocalDateTime.now()
) {
    /**
     * Calculate cost per kWh for this usage period
     */
    fun costPerKWh(): Double {
        return if (totalUsed > 0) estimatedCost / totalUsed else 0.0
    }
    
    /**
     * Calculate average daily consumption for this period
     */
    fun averageDailyConsumption(fromTimestamp: LocalDateTime, toTimestamp: LocalDateTime): Double {
        val days = java.time.Duration.between(fromTimestamp, toTimestamp).toDays()
        return if (days > 0) totalUsed / days else totalUsed
    }
    
    /**
     * Get usage breakdown as percentages
     */
    fun usageBreakdown(): Map<String, Double> {
        val total = totalUsed
        return if (total > 0) {
            mapOf(
                "day" to (dayUsed / total * 100),
                "offPeak" to (offPeakUsed / total * 100),
                "peak" to (peakUsed / total * 100)
            )
        } else {
            mapOf("day" to 0.0, "offPeak" to 0.0, "peak" to 0.0)
        }
    }
    
    /**
     * Validate the calculated usage data
     */
    fun validate(): Result<Unit> {
        return try {
            // Check if total usage is positive
            if (totalUsed < 0) {
                return Result.failure(Exception("Total used cannot be negative"))
            }
            
            // Check if individual rates are positive
            if (dayUsed < 0 || offPeakUsed < 0 || peakUsed < 0) {
                return Result.failure(Exception("Individual rates cannot be negative"))
            }
            
            // Check if total usage matches sum of individual rates (with small tolerance for floating point)
            val sumOfRates = dayUsed + offPeakUsed + peakUsed
            if (Math.abs(totalUsed - sumOfRates) > 0.001) {
                return Result.failure(Exception("Total used must equal sum of individual rates"))
            }
            
            // Check if estimated cost is positive
            if (estimatedCost < 0) {
                return Result.failure(Exception("Estimated cost cannot be negative"))
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Convert to map for serialization
     */
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "fromReadingId" to fromReadingId,
            "toReadingId" to toReadingId,
            "totalUsed" to totalUsed,
            "dayUsed" to dayUsed,
            "offPeakUsed" to offPeakUsed,
            "peakUsed" to peakUsed,
            "estimatedCost" to estimatedCost,
            "calculationTimestamp" to calculationTimestamp.toString()
        )
    }
    
    /**
     * Create from map for deserialization
     */
    companion object {
        fun fromMap(map: Map<String, Any>): CalculatedUsage {
            return CalculatedUsage(
                id = map["id"] as String,
                fromReadingId = map["fromReadingId"] as String,
                toReadingId = map["toReadingId"] as String,
                totalUsed = (map["totalUsed"] as Number).toDouble(),
                dayUsed = (map["dayUsed"] as Number).toDouble(),
                offPeakUsed = (map["offPeakUsed"] as Number).toDouble(),
                peakUsed = (map["peakUsed"] as Number).toDouble(),
                estimatedCost = (map["estimatedCost"] as Number).toDouble(),
                calculationTimestamp = LocalDateTime.parse(map["calculationTimestamp"] as String)
            )
        }
    }
}