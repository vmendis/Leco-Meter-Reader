package com.leco.meterreader.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.time.LocalDateTime

/**
 * Room entity for meter readings
 * Maps to the meter_readings table in the database
 */
@Entity(
    tableName = "meter_readings",
    indices = [
        Index(value = ["timestamp"], unique = true)
    ]
)
data class MeterReadingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val timestamp: String, // Stored as string for Room compatibility
    
    val totalReading: Double,
    
    val rate1Reading: Double,
    
    val rate2Reading: Double,
    
    val rate3Reading: Double,
    
    val notes: String = "",
    
    val createdAt: String = LocalDateTime.now().toString() // Stored as string for Room compatibility
) {
    /**
     * Convert entity to domain model
     */
    fun toDomain(): com.leco.meterreader.data.model.MeterReading {
        return com.leco.meterreader.data.model.MeterReading(
            id = id.toString(),
            timestamp = LocalDateTime.parse(timestamp),
            totalReading = totalReading,
            rate1Reading = rate1Reading,
            rate2Reading = rate2Reading,
            rate3Reading = rate3Reading,
            notes = notes,
            createdAt = LocalDateTime.parse(createdAt)
        )
    }
    
    /**
     * Convert domain model to entity
     */
    companion object {
        fun fromDomain(reading: com.leco.meterreader.data.model.MeterReading): MeterReadingEntity {
            return MeterReadingEntity(
                id = reading.id.toLongOrNull() ?: 0,
                timestamp = reading.timestamp.toString(),
                totalReading = reading.totalReading,
                rate1Reading = reading.rate1Reading,
                rate2Reading = reading.rate2Reading,
                rate3Reading = reading.rate3Reading,
                notes = reading.notes,
                createdAt = reading.createdAt.toString()
            )
        }
    }
}