package com.leco.meterreader.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Room entity for tariff configuration.
 * Maps to the tariff configuration table.
 */
@Entity(tableName = "tariff_config")
data class TariffConfigEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dayRate: Double,
    val offPeakRate: Double,
    val peakRate: Double,
    val fixedCharge: Double = 0.0,
    val effectiveDate: Long = System.currentTimeMillis()
) {
    companion object {
        /**
         * Create an entity from a domain model.
         */
        fun fromDomain(domain: com.leco.meterreader.domain.model.TariffConfig): TariffConfigEntity {
            return TariffConfigEntity(
                id = domain.id,
                dayRate = domain.dayRate,
                offPeakRate = domain.offPeakRate,
                peakRate = domain.peakRate,
                fixedCharge = domain.fixedCharge,
                effectiveDate = domain.effectiveDate.time
            )
        }
    }

    /**
     * Convert to domain model.
     */
    fun toDomain(): com.leco.meterreader.domain.model.TariffConfig {
        return com.leco.meterreader.domain.model.TariffConfig(
            id = id,
            dayRate = dayRate,
            offPeakRate = offPeakRate,
            peakRate = peakRate,
            fixedCharge = fixedCharge,
            effectiveDate = Date(effectiveDate)
        )
    }
}