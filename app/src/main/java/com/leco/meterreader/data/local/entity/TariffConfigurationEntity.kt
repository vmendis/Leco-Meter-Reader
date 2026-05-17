package com.leco.meterreader.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.time.LocalDateTime

/**
 * Room entity for tariff configuration
 * Stores electricity rate configurations for different time periods
 */
@Entity(
    tableName = "tariff_configuration",
    indices = [
        Index(value = ["effectiveDate"], unique = true)
    ]
)
data class TariffConfigurationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val dayRate: Double, // Day rate (LKR per kWh)
    
    val offPeakRate: Double, // Off-peak rate (LKR per kWh)
    
    val peakRate: Double, // Peak rate (LKR per kWh)
    
    val effectiveDate: String = LocalDateTime.now().toString(), // When this tariff becomes effective
    
    val createdAt: String = LocalDateTime.now().toString(), // When this tariff was created
    
    val updatedAt: String = LocalDateTime.now().toString(), // When this tariff was last updated
    
    val isActive: Boolean = true, // Whether this tariff is currently active
    
    val description: String = "" // Optional description for this tariff configuration
) {
    /**
     * Convert entity to domain model
     */
    fun toDomain(): com.leco.meterreader.data.model.TariffConfiguration {
        return com.leco.meterreader.data.model.TariffConfiguration(
            id = id.toString(),
            dayRate = dayRate,
            offPeakRate = offPeakRate,
            peakRate = peakRate,
            effectiveDate = LocalDateTime.parse(effectiveDate),
            createdAt = LocalDateTime.parse(createdAt),
            updatedAt = LocalDateTime.parse(updatedAt),
            isActive = isActive,
            description = description
        )
    }
    
    /**
     * Convert domain model to entity
     */
    companion object {
        fun fromDomain(tariff: com.leco.meterreader.data.model.TariffConfiguration): TariffConfigurationEntity {
            return TariffConfigurationEntity(
                id = tariff.id.toLongOrNull() ?: 0,
                dayRate = tariff.dayRate,
                offPeakRate = tariff.offPeakRate,
                peakRate = tariff.peakRate,
                effectiveDate = tariff.effectiveDate.toString(),
                createdAt = tariff.createdAt.toString(),
                updatedAt = tariff.updatedAt.toString(),
                isActive = tariff.isActive,
                description = tariff.description
            )
        }
    }
}