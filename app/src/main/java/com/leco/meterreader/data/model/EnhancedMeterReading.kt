package com.leco.meterreader.data.model

import java.time.LocalDateTime
import java.util.UUID

/**
 * Enhanced meter reading model that supports empty readings
 * Handles scenarios where meter readings might be missing due to:
 * - User forgets to capture data
 * - User is not available to capture data
 * - Meter malfunction
 * - Other unforeseen circumstances
 */
data class EnhancedMeterReading(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: LocalDateTime,
    val status: ReadingStatus = ReadingStatus.PENDING,
    val totalReading: Double? = null,
    val rate1Reading: Double? = null,
    val rate2Reading: Double? = null,
    val rate3Reading: Double? = null,
    val notes: String = "",
    val emptyReason: EmptyReadingReason? = null,
    val estimatedReading: Boolean = false,
    val estimatedValue: Double? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    
    /**
     * Check if this is a complete reading (all values present)
     */
    val isComplete: Boolean
        get() = totalReading != null && 
                rate1Reading != null && 
                rate2Reading != null && 
                rate3Reading != null &&
                status == ReadingStatus.COMPLETED
    
    /**
     * Check if this is an empty reading
     */
    val isEmpty: Boolean
        get() = status == ReadingStatus.EMPTY || totalReading == null
    
    /**
     * Check if this is an estimated reading
     */
    val isEstimated: Boolean
        get() = estimatedReading && estimatedValue != null
    
    /**
     * Get the actual reading value (total or estimated)
     */
    val actualReading: Double?
        get() = totalReading ?: estimatedValue
    
    /**
     * Convert to map for serialization
     */
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "timestamp" to timestamp.toString(),
            "status" to status.name,
            "totalReading" to (totalReading ?: 0.0),
            "rate1Reading" to (rate1Reading ?: 0.0),
            "rate2Reading" to (rate2Reading ?: 0.0),
            "rate3Reading" to (rate3Reading ?: 0.0),
            "notes" to notes,
            "emptyReason" to (emptyReason?.name ?: ""),
            "estimatedReading" to estimatedReading,
            "estimatedValue" to (estimatedValue ?: 0.0),
            "createdAt" to createdAt.toString(),
            "updatedAt" to updatedAt.toString()
        )
    }
    
    /**
     * Create from map for deserialization
     */
    companion object {
        fun fromMap(map: Map<String, Any>): EnhancedMeterReading {
            return EnhancedMeterReading(
                id = map["id"] as String,
                timestamp = LocalDateTime.parse(map["timestamp"] as String),
                status = ReadingStatus.valueOf(map["status"] as String),
                totalReading = if (map["totalReading"] != null) (map["totalReading"] as Number).toDouble() else null,
                rate1Reading = if (map["rate1Reading"] != null) (map["rate1Reading"] as Number).toDouble() else null,
                rate2Reading = if (map["rate2Reading"] != null) (map["rate2Reading"] as Number).toDouble() else null,
                rate3Reading = if (map["rate3Reading"] != null) (map["rate3Reading"] as Number).toDouble() else null,
                notes = map["notes"] as String,
                emptyReason = if (map["emptyReason"] != null && (map["emptyReason"] as String).isNotEmpty()) 
                    EmptyReadingReason.valueOf(map["emptyReason"] as String) else null,
                estimatedReading = map["estimatedReading"] as Boolean,
                estimatedValue = if (map["estimatedValue"] != null) (map["estimatedValue"] as Number).toDouble() else null,
                createdAt = LocalDateTime.parse(map["createdAt"] as String),
                updatedAt = LocalDateTime.parse(map["updatedAt"] as String)
            )
        }
    }
    
    /**
     * Create a complete reading from this enhanced reading
     */
    fun toCompleteMeterReading(): MeterReading? {
        return if (isComplete) {
            MeterReading(
                id = id,
                timestamp = timestamp,
                totalReading = totalReading!!,
                rate1Reading = rate1Reading!!,
                rate2Reading = rate2Reading!!,
                rate3Reading = rate3Reading!!,
                notes = notes,
                createdAt = createdAt
            )
        } else {
            null
        }
    }
    
    /**
     * Mark this reading as completed with actual values
     */
    fun markAsCompleted(
        totalReading: Double,
        rate1Reading: Double,
        rate2Reading: Double,
        rate3Reading: Double,
        notes: String = this.notes
    ): EnhancedMeterReading {
        return copy(
            status = ReadingStatus.COMPLETED,
            totalReading = totalReading,
            rate1Reading = rate1Reading,
            rate2Reading = rate2Reading,
            rate3Reading = rate3Reading,
            notes = notes,
            emptyReason = null,
            estimatedReading = false,
            estimatedValue = null,
            updatedAt = LocalDateTime.now()
        )
    }
    
    /**
     * Mark this reading as empty with a reason
     */
    fun markAsEmpty(reason: EmptyReadingReason, notes: String = this.notes): EnhancedMeterReading {
        return copy(
            status = ReadingStatus.EMPTY,
            totalReading = null,
            rate1Reading = null,
            rate2Reading = null,
            rate3Reading = null,
            notes = notes,
            emptyReason = reason,
            estimatedReading = false,
            estimatedValue = null,
            updatedAt = LocalDateTime.now()
        )
    }
    
    /**
     * Mark this reading as estimated
     */
    fun markAsEstimated(estimatedValue: Double, notes: String = this.notes): EnhancedMeterReading {
        return copy(
            status = ReadingStatus.ESTIMATED,
            totalReading = null,
            rate1Reading = null,
            rate2Reading = null,
            rate3Reading = null,
            notes = notes,
            emptyReason = null,
            estimatedReading = true,
            estimatedValue = estimatedValue,
            updatedAt = LocalDateTime.now()
        )
    }
}

/**
 * Enumeration for reading status
 */
enum class ReadingStatus {
    PENDING,    // Reading scheduled but not yet captured
    COMPLETED,  // Reading successfully captured with all values
    EMPTY,      // Reading intentionally left empty
    ESTIMATED   // Reading estimated due to missing data
}

/**
 * Enumeration for reasons why a reading might be empty
 */
enum class EmptyReadingReason {
    FORGOTTEN,           // User forgot to capture the data
    UNAVAILABLE,         // User was not available to capture data
    MALFUNCTION,        // Meter malfunction or technical issues
    MAINTENANCE,        // Meter under maintenance
    OUT_OF_TOWN,        // User was out of town
    OTHER               // Other unspecified reasons
}