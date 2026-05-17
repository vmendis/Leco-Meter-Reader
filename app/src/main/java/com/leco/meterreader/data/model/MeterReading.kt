package com.leco.meterreader.data.model

import java.time.LocalDateTime
import java.util.UUID

data class MeterReading(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: LocalDateTime,
    val totalReading: Double,
    val rate1Reading: Double,
    val rate2Reading: Double,
    val rate3Reading: Double,
    val notes: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "timestamp" to timestamp.toString(),
            "totalReading" to totalReading,
            "rate1Reading" to rate1Reading,
            "rate2Reading" to rate2Reading,
            "rate3Reading" to rate3Reading,
            "notes" to notes,
            "createdAt" to createdAt.toString()
        )
    }
    
    companion object {
        fun fromMap(map: Map<String, Any>): MeterReading {
            return MeterReading(
                id = map["id"] as String,
                timestamp = LocalDateTime.parse(map["timestamp"] as String),
                totalReading = (map["totalReading"] as Number).toDouble(),
                rate1Reading = (map["rate1Reading"] as Number).toDouble(),
                rate2Reading = (map["rate2Reading"] as Number).toDouble(),
                rate3Reading = (map["rate3Reading"] as Number).toDouble(),
                notes = map["notes"] as String,
                createdAt = LocalDateTime.parse(map["createdAt"] as String)
            )
        }
    }
}