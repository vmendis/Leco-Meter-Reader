package com.leco.meterreader.domain.repository

import com.leco.meterreader.domain.model.TariffConfig
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for tariff configuration operations.
 * Defines the contract for tariff data access in the domain layer.
 */
interface TariffConfigRepository {
    /**
     * Get all tariff configurations as a Flow.
     */
    fun getAllTariffs(): Flow<List<TariffConfig>>

    /**
     * Get the currently active tariff configuration.
     */
    suspend fun getActiveTariff(): TariffConfig?

    /**
     * Get a specific tariff configuration by ID.
     */
    suspend fun getTariffById(id: Long): TariffConfig?

    /**
     * Insert a new tariff configuration.
     */
    suspend fun insertTariff(tariff: TariffConfig): Long

    /**
     * Update an existing tariff configuration.
     */
    suspend fun updateTariff(tariff: TariffConfig)

    /**
     * Delete a tariff configuration.
     */
    suspend fun deleteTariff(tariff: TariffConfig)
}