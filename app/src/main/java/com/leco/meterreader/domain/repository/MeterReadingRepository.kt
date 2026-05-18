package com.leco.meterreader.domain.repository

import com.leco.meterreader.domain.model.MeterReading
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for meter reading operations.
 * Defines the contract for data access in the domain layer.
 */
interface MeterReadingRepository {
    /**
     * Get all meter readings as a Flow.
     */
    fun getAllReadings(): Flow<List<MeterReading>>

    /**
     * Get a specific meter reading by ID.
     */
    suspend fun getReadingById(id: Long): MeterReading?

    /**
     * Get the most recent meter reading.
     */
    suspend fun getLatestReading(): MeterReading?

    /**
     * Insert a new meter reading.
     */
    suspend fun insertReading(reading: MeterReading): Long

    /**
     * Update an existing meter reading.
     */
    suspend fun updateReading(reading: MeterReading)

    /**
     * Delete a meter reading.
     */
    suspend fun deleteReading(reading: MeterReading)

    /**
     * Delete all meter readings.
     */
    suspend fun deleteAllReadings()
}