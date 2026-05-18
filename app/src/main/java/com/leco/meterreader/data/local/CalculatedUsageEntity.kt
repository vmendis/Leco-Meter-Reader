package com.leco.meterreader.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.leco.meterreader.domain.model.CalculatedUsage
import java.util.Date

/**
 * Room entity for calculated usage derived from consecutive meter readings.
 */
@Entity(tableName = "calculated_usage")
data class CalculatedUsageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fromReadingId: Long,
    val toReadingId: Long,
    val totalUsed: Double,
    val dayUsed: Double,
    val offPeakUsed: Double,
    val peakUsed: Double,
    val estimatedCost: Double = 0.0,
    val calculationTimestamp: Long = System.currentTimeMillis()
) {
    companion object {
        /**
         * Create an entity from a domain model.
         */
        fun fromDomain(domain: CalculatedUsage): CalculatedUsageEntity {
            return CalculatedUsageEntity(
                id = domain.id,
                fromReadingId = domain.fromReadingId,
                toReadingId = domain.toReadingId,
                totalUsed = domain.totalUsed,
                dayUsed = domain.dayUsed,
                offPeakUsed = domain.offPeakUsed,
                peakUsed = domain.peakUsed,
                estimatedCost = domain.estimatedCost,
                calculationTimestamp = domain.calculationTimestamp.time
            )
        }
    }

    /**
     * Convert to domain model.
     */
    fun toDomain(): CalculatedUsage {
        return CalculatedUsage(
            id = id,
            fromReadingId = fromReadingId,
            toReadingId = toReadingId,
            totalUsed = totalUsed,
            dayUsed = dayUsed,
            offPeakUsed = offPeakUsed,
            peakUsed = peakUsed,
            estimatedCost = estimatedCost,
            calculationTimestamp = Date(calculationTimestamp)
        )
    }
}