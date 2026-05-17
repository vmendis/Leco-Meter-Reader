package com.leco.meterreader.data.model

import java.time.LocalDateTime
import java.util.UUID

/**
 * Data model for tariff configuration
 * Stores electricity rates for different time periods
 */
data class TariffConfiguration(
    val id: String = UUID.randomUUID().toString(),
    val dayRate: Double, // Day rate (LKR per kWh)
    val offPeakRate: Double, // Off-peak rate (LKR per kWh)
    val peakRate: Double, // Peak rate (LKR per kWh)
    val effectiveDate: LocalDateTime,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val isActive: Boolean = true,
    val description: String = ""
) {
    /**
     * Calculate total cost for given consumption amounts
     */
    fun calculateCost(dayUsed: Double, offPeakUsed: Double, peakUsed: Double): Double {
        return (dayUsed * dayRate) + (offPeakUsed * offPeakRate) + (peakUsed * peakRate)
    }
    
    /**
     * Get the current active tariff
     */
    fun isActiveAt(date: LocalDateTime): Boolean {
        return isActive && !date.isBefore(effectiveDate)
    }
    
    /**
     * Validate tariff configuration
     */
    fun validate(): Result<Unit> {
        return try {
            // Check if rates are positive
            if (dayRate <= 0 || offPeakRate <= 0 || peakRate <= 0) {
                return Result.failure(Exception("All rates must be positive values"))
            }
            
            // Check if peak rate is higher than day rate (typical tariff structure)
            if (peakRate <= dayRate) {
                return Result.failure(Exception("Peak rate should be higher than day rate"))
            }
            
            // Check if day rate is higher than off-peak rate (typical tariff structure)
            if (dayRate <= offPeakRate) {
                return Result.failure(Exception("Day rate should be higher than off-peak rate"))
            }
            
            // Check if effective date is not in the future
            if (effectiveDate.isAfter(LocalDateTime.now())) {
                return Result.failure(Exception("Effective date cannot be in the future"))
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update the tariff configuration
     */
    fun updateRates(
        dayRate: Double,
        offPeakRate: Double,
        peakRate: Double,
        description: String = this.description
    ): TariffConfiguration {
        return this.copy(
            dayRate = dayRate,
            offPeakRate = offPeakRate,
            peakRate = peakRate,
            description = description,
            updatedAt = LocalDateTime.now()
        )
    }
    
    /**
     * Get tariff summary for display
     */
    fun getSummary(): String {
        return "Day: LKR ${String.format("%.2f", dayRate)}/kWh, " +
                "Off-Peak: LKR ${String.format("%.2f", offPeakRate)}/kWh, " +
                "Peak: LKR ${String.format("%.2f", peakRate)}/kWh"
    }
    
    /**
     * Convert to map for serialization
     */
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "dayRate" to dayRate,
            "offPeakRate" to offPeakRate,
            "peakRate" to peakRate,
            "effectiveDate" to effectiveDate.toString(),
            "createdAt" to createdAt.toString(),
            "updatedAt" to updatedAt.toString(),
            "isActive" to isActive,
            "description" to description
        )
    }
    
    /**
     * Create from map for deserialization
     */
    companion object {
        fun fromMap(map: Map<String, Any>): TariffConfiguration {
            return TariffConfiguration(
                id = map["id"] as String,
                dayRate = (map["dayRate"] as Number).toDouble(),
                offPeakRate = (map["offPeakRate"] as Number).toDouble(),
                peakRate = (map["peakRate"] as Number).toDouble(),
                effectiveDate = LocalDateTime.parse(map["effectiveDate"] as String),
                createdAt = LocalDateTime.parse(map["createdAt"] as String),
                updatedAt = LocalDateTime.parse(map["updatedAt"] as String),
                isActive = map["isActive"] as Boolean,
                description = map["description"] as String
            )
        }
    }
}