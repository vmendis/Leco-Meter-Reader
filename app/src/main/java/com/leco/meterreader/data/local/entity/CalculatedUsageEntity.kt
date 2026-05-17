package com.leco.meterreader.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Room entity for calculated usage data
 * Stores derived consumption calculations between meter readings
 */
@Entity(
    tableName = "calculated_usage",
    foreignKeys = [
        ForeignKey(
            entity = MeterReadingEntity::class,
            parentColumns = ["id"],
            childColumns = ["fromReadingId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MeterReadingEntity::class,
            parentColumns = ["id"],
            childColumns = ["toReadingId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["fromReadingId"]),
        Index(value = ["toReadingId"]),
        Index(value = ["calculationTimestamp"])
    ]
)
data class CalculatedUsageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val fromReadingId: Long, // Reference to the starting meter reading
    
    val toReadingId: Long, // Reference to the ending meter reading
    
    val totalUsed: Double, // Total consumption between readings
    
    val dayUsed: Double, // Day rate consumption
    
    val offPeakUsed: Double, // Off-peak rate consumption
    
    val peakUsed: Double, // Peak rate consumption
    
    val estimatedCost: Double, // Estimated cost based on tariffs
    
    val calculationTimestamp: String = LocalDateTime.now().toString() // When this calculation was performed
) {
    /**
     * Convert entity to domain model
     */
    fun toDomain(): com.leco.meterreader.data.model.CalculatedUsage {
        return com.leco.meterreader.data.model.CalculatedUsage(
            id = id.toString(),
            fromReadingId = fromReadingId.toString(),
            toReadingId = toReadingId.toString(),
            totalUsed = totalUsed,
            dayUsed = dayUsed,
            offPeakUsed = offPeakUsed,
            peakUsed = peakUsed,
            estimatedCost = estimatedCost,
            calculationTimestamp = LocalDateTime.parse(calculationTimestamp)
        )
    }
    
    /**
     * Convert domain model to entity
     */
    companion object {
        fun fromDomain(usage: com.leco.meterreader.data.model.CalculatedUsage): CalculatedUsageEntity {
            return CalculatedUsageEntity(
                id = usage.id.toLongOrNull() ?: 0,
                fromReadingId = usage.fromReadingId.toLong(),
                toReadingId = usage.toReadingId.toLong(),
                totalUsed = usage.totalUsed,
                dayUsed = usage.dayUsed,
                offPeakUsed = usage.offPeakUsed,
                peakUsed = usage.peakUsed,
                estimatedCost = usage.estimatedCost,
                calculationTimestamp = usage.calculationTimestamp.toString()
            )
        }
    }
}