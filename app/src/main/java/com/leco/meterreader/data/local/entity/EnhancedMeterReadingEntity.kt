package com.leco.meterreader.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import androidx.room.ForeignKey
import java.time.LocalDateTime

/**
 * Enhanced Room entity for meter readings that supports empty readings
 * Maps to the enhanced_meter_readings table in the database
 */
@Entity(
    tableName = "enhanced_meter_readings",
    indices = [
        Index(value = ["timestamp"], unique = false),
        Index(value = ["status"]),
        Index(value = ["empty_reason"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = CalculatedUsageEntity::class,
            parentColumns = ["id"],
            childColumns = ["calculated_usage_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class EnhancedMeterReadingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val timestamp: String, // Stored as string for Room compatibility
    
    val status: String = "PENDING", // ReadingStatus enum value
    
    val totalReading: Double?, // Can be null for empty/estimated readings
    
    val rate1Reading: Double?, // Can be null for empty/estimated readings
    
    val rate2Reading: Double?, // Can be null for empty/estimated readings
    
    val rate3Reading: Double?, // Can be null for empty/estimated readings
    
    val notes: String = "",
    
    val emptyReason: String?, // EmptyReadingReason enum value or null
    
    val estimatedReading: Boolean = false,
    
    val estimatedValue: Double?, // Can be null for non-estimated readings
    
    val createdAt: String = LocalDateTime.now().toString(), // Stored as string for Room compatibility
    
    val updatedAt: String = LocalDateTime.now().toString(), // Stored as string for Room compatibility
    
    val calculatedUsageId: Long?, // Foreign key to calculated_usage table
    
    val isComplete: Boolean = false, // Computed field for complete readings
    
    val isEmpty: Boolean = false, // Computed field for empty readings
    
    val isEstimated: Boolean = false // Computed field for estimated readings
) {
    /**
     * Convert entity to enhanced domain model
     */
    fun toDomain(): com.leco.meterreader.data.model.EnhancedMeterReading {
        return com.leco.meterreader.data.model.EnhancedMeterReading(
            id = id.toString(),
            timestamp = LocalDateTime.parse(timestamp),
            status = com.leco.meterreader.data.model.ReadingStatus.valueOf(status),
            totalReading = totalReading,
            rate1Reading = rate1Reading,
            rate2Reading = rate2Reading,
            rate3Reading = rate3Reading,
            notes = notes,
            emptyReason = if (emptyReason != null) com.leco.meterreader.data.model.EmptyReadingReason.valueOf(emptyReason) else null,
            estimatedReading = estimatedReading,
            estimatedValue = estimatedValue,
            createdAt = LocalDateTime.parse(createdAt),
            updatedAt = LocalDateTime.parse(updatedAt)
        )
    }
    
    /**
     * Convert enhanced domain model to entity
     */
    companion object {
        fun fromDomain(reading: com.leco.meterreader.data.model.EnhancedMeterReading): EnhancedMeterReadingEntity {
            return EnhancedMeterReadingEntity(
                id = reading.id.toLongOrNull() ?: 0,
                timestamp = reading.timestamp.toString(),
                status = reading.status.name,
                totalReading = reading.totalReading,
                rate1Reading = reading.rate1Reading,
                rate2Reading = reading.rate2Reading,
                rate3Reading = reading.rate3Reading,
                notes = reading.notes,
                emptyReason = reading.emptyReason?.name,
                estimatedReading = reading.estimatedReading,
                estimatedValue = reading.estimatedValue,
                createdAt = reading.createdAt.toString(),
                updatedAt = reading.updatedAt.toString(),
                calculatedUsageId = null, // Will be set when calculated usage is created
                isComplete = reading.isComplete,
                isEmpty = reading.isEmpty,
                isEstimated = reading.isEstimated
            )
        }
    }
    
    /**
     * Create a complete reading entity from this enhanced entity
     */
    fun toCompleteMeterReadingEntity(): MeterReadingEntity {
        return MeterReadingEntity(
            id = id,
            timestamp = timestamp,
            totalReading = totalReading ?: 0.0,
            rate1Reading = rate1Reading ?: 0.0,
            rate2Reading = rate2Reading ?: 0.0,
            rate3Reading = rate3Reading ?: 0.0,
            notes = notes,
            createdAt = createdAt
        )
    }
    
    /**
     * Update the entity with new values
     */
    fun update(
        status: String = this.status,
        totalReading: Double? = this.totalReading,
        rate1Reading: Double? = this.rate1Reading,
        rate2Reading: Double? = this.rate2Reading,
        rate3Reading: Double? = this.rate3Reading,
        notes: String = this.notes,
        emptyReason: String? = this.emptyReason,
        estimatedReading: Boolean = this.estimatedReading,
        estimatedValue: Double? = this.estimatedValue,
        calculatedUsageId: Long? = this.calculatedUsageId
    ): EnhancedMeterReadingEntity {
        return copy(
            status = status,
            totalReading = totalReading,
            rate1Reading = rate1Reading,
            rate2Reading = rate2Reading,
            rate3Reading = rate3Reading,
            notes = notes,
            emptyReason = emptyReason,
            estimatedReading = estimatedReading,
            estimatedValue = estimatedValue,
            calculatedUsageId = calculatedUsageId,
            updatedAt = LocalDateTime.now().toString(),
            isComplete = (totalReading != null && rate1Reading != null && rate2Reading != null && rate3Reading != null && status == "COMPLETED"),
            isEmpty = (status == "EMPTY" || totalReading == null),
            isEstimated = estimatedReading
        )
    }
}