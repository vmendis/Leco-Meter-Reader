package com.leco.meterreader.domain.repository

import com.leco.meterreader.data.model.MeterReading
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Repository interface for meter reading operations
 * Defines the contract for data access operations
 */
interface MeterReadingRepository {
    /**
     * Save a new meter reading to the database
     * @param reading The meter reading to save
     * @return Result containing the saved reading or error
     */
    suspend fun saveReading(reading: MeterReading): Result<MeterReading>
    
    /**
     * Get the latest meter reading
     * @return Result containing the latest reading or null if none exists
     */
    suspend fun getLatestReading(): Result<MeterReading?>
    
    /**
     * Get all meter readings ordered by timestamp
     * @return Result containing list of all readings
     */
    suspend fun getAllReadings(): Result<List<MeterReading>>
    
    /**
     * Get readings within a specific date range
     * @param start Start date (inclusive)
     * @param end End date (inclusive)
     * @return Result containing list of readings in the range
     */
    suspend fun getReadingsByDateRange(start: LocalDateTime, end: LocalDateTime): Result<List<MeterReading>>
    
    /**
     * Get readings for a specific month
     * @param year Year of the month
     * @param month Month (1-12)
     * @return Result containing list of readings for the month
     */
    suspend fun getReadingsByMonth(year: Int, month: Int): Result<List<MeterReading>>
    
    /**
     * Delete a meter reading
     * @param readingId ID of the reading to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteReading(readingId: String): Result<Unit>
    
    /**
     * Get count of total readings
     * @return Result containing the count of readings
     */
    suspend fun getReadingCount(): Result<Int>
    
    /**
     * Check if a reading with the given timestamp already exists
     * @param timestamp The timestamp to check
     * @return Result containing true if reading exists, false otherwise
     */
    suspend fun readingExists(timestamp: LocalDateTime): Result<Boolean>
}