package com.leco.meterreader.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Room entity for meter readings.
 * Maps to the raw meter readings table.
 */
@Entity(tableName = "meter_readings")
data class MeterReadingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long, // Stored as milliseconds
    val totalReading: Double,
    val rate1Day: Double,
    val rate2OffPeak: Double,
    val rate3Peak: Double,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        /**
         * Create an entity from a domain model.
         */
        fun fromDomain(domain: com.leco.meterreader.domain.model.MeterReading): MeterReadingEntity {
            return MeterReadingEntity(
                id = domain.id,
                timestamp = domain.timestamp.time,
                totalReading = domain.totalReading,
                rate1Day = domain.rate1Day,
                rate2OffPeak = domain.rate2OffPeak,
                rate3Peak = domain.rate3Peak,
                notes = domain.notes,
                createdAt = domain.createdAt.time
            )
        }
    }

    /**
     * Convert to domain model.
     */
    fun toDomain(): com.leco.meterreader.domain.model.MeterReading {
        return com.leco.meterreader.domain.model.MeterReading(
            id = id,
            timestamp = Date(timestamp),
            totalReading = totalReading,
            rate1Day = rate1Day,
            rate2OffPeak = rate2OffPeak,
            rate3Peak = rate3Peak,
            notes = notes,
            createdAt = Date(createdAt)
        )
    }
}