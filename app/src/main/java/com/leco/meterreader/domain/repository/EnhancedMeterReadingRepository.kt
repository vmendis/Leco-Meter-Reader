package com.leco.meterreader.domain.repository

import com.leco.meterreader.data.model.EnhancedMeterReading
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Enhanced repository interface for meter reading operations
 * Supports empty readings, estimated readings, and various reading statuses
 */
interface EnhancedMeterReadingRepository {
    
    /**
     * Save a new enhanced meter reading
     * @param reading The enhanced meter reading to save
     * @return Result containing the saved reading or error
     */
    suspend fun saveEnhancedReading(reading: EnhancedMeterReading): Result<EnhancedMeterReading>
    
    /**
     * Save multiple enhanced meter readings
     * @param readings List of enhanced meter readings to save
     * @return Result containing the list of saved readings or error
     */
    suspend fun saveEnhancedReadings(readings: List<EnhancedMeterReading>): Result<List<EnhancedMeterReading>>
    
    /**
     * Get the latest enhanced meter reading
     * @return Result containing the latest reading or null if none exists
     */
    suspend fun getLatestEnhancedReading(): Result<EnhancedMeterReading?>
    
    /**
     * Get all enhanced meter readings ordered by timestamp
     * @return Result containing list of all enhanced readings
     */
    suspend fun getAllEnhancedReadings(): Result<List<EnhancedMeterReading>>
    
    /**
     * Get enhanced readings by status
     * @param status The reading status to filter by
     * @return Result containing list of readings with the specified status
     */
    suspend fun getEnhancedReadingsByStatus(status: com.leco.meterreader.data.model.ReadingStatus): Result<List<EnhancedMeterReading>>
    
    /**
     * Get enhanced readings within a specific date range
     * @param start Start date (inclusive)
     * @param end End date (inclusive)
     * @return Result containing list of readings in the range
     */
    suspend fun getEnhancedReadingsByDateRange(start: LocalDateTime, end: LocalDateTime): Result<List<EnhancedMeterReading>>
    
    /**
     * Get enhanced readings for a specific month
     * @param year Year of the month
     * @param month Month (1-12)
     * @return Result containing list of readings for the month
     */
    suspend fun getEnhancedReadingsByMonth(year: Int, month: Int): Result<List<EnhancedMeterReading>>
    
    /**
     * Get complete readings only
     * @return Result containing list of complete readings
     */
    suspend fun getCompleteReadings(): Result<List<EnhancedMeterReading>>
    
    /**
     * Get empty readings only
     * @return Result containing list of empty readings
     */
    suspend fun getEmptyReadings(): Result<List<EnhancedMeterReading>>
    
    /**
     * Get estimated readings only
     * @return Result containing list of estimated readings
     */
    suspend fun getEstimatedReadings(): Result<List<EnhancedMeterReading>>
    
    /**
     * Get pending readings only
     * @return Result containing list of pending readings
     */
    suspend fun getPendingReadings(): Result<List<EnhancedMeterReading>>
    
    /**
     * Get readings by empty reason
     * @param reason The reason for empty readings
     * @return Result containing list of readings with the specified empty reason
     */
    suspend fun getReadingsByEmptyReason(reason: com.leco.meterreader.data.model.EmptyReadingReason): Result<List<EnhancedMeterReading>>
    
    /**
     * Get readings that need completion (pending or empty)
     * @return Result containing list of readings that need completion
     */
    suspend fun getReadingsNeedingCompletion(): Result<List<EnhancedMeterReading>>
    
    /**
     * Get readings with calculated usage
     * @return Result containing list of readings with calculated usage
     */
    suspend fun getReadingsWithCalculatedUsage(): Result<List<EnhancedMeterReading>>
    
    /**
     * Get readings without calculated usage
     * @return Result containing list of readings without calculated usage
     */
    suspend fun getReadingsWithoutCalculatedUsage(): Result<List<EnhancedMeterReading>>
    
    /**
     * Update an existing enhanced meter reading
     * @param reading The enhanced meter reading to update
     * @return Result containing the updated reading or error
     */
    suspend fun updateEnhancedReading(reading: EnhancedMeterReading): Result<EnhancedMeterReading>
    
    /**
     * Delete an enhanced meter reading
     * @param readingId ID of the reading to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteEnhancedReading(readingId: String): Result<Unit>
    
    /**
     * Get count of total enhanced readings
     * @return Result containing the count of enhanced readings
     */
    suspend fun getEnhancedReadingCount(): Result<Int>
    
    /**
     * Get reading count by status
     * @return Result containing map of status to count
     */
    suspend fun getReadingCountByStatus(): Result<Map<com.leco.meterreader.data.model.ReadingStatus, Int>>
    
    /**
     * Get reading count by empty reason
     * @return Result containing map of empty reason to count
     */
    suspend fun getReadingCountByEmptyReason(): Result<Map<com.leco.meterreader.data.model.EmptyReadingReason, Int>>
    
    /**
     * Check if an enhanced reading with the given timestamp exists
     * @param timestamp The timestamp to check
     * @return Result containing true if reading exists, false otherwise
     */
    suspend fun enhancedReadingExists(timestamp: LocalDateTime): Result<Boolean>
    
    /**
     * Get reading statistics for a date range
     * @param start Start date (inclusive)
     * @param end End date (inclusive)
     * @return Result containing reading statistics
     */
    suspend fun getReadingStatsForDateRange(start: LocalDateTime, end: LocalDateTime): Result<ReadingStats>
    
    /**
     * Get the most recent empty reading
     * @return Result containing the latest empty reading or null
     */
    suspend fun getLatestEmptyReading(): Result<EnhancedMeterReading?>
    
    /**
     * Get readings that can be converted to complete readings
     * @return Result containing list of estimated readings that can be converted
     */
    suspend fun getEstimatedReadingsForConversion(): Result<List<EnhancedMeterReading>>
    
    /**
     * Convert an estimated reading to a complete reading
     * @param readingId ID of the estimated reading to convert
     * @param totalReading The total reading value
     * @param rate1Reading The rate 1 reading value
     * @param rate2Reading The rate 2 reading value
     * @param rate3Reading The rate 3 reading value
     * @param notes Optional notes for the completed reading
     * @return Result containing the converted reading or error
     */
    suspend fun convertEstimatedToComplete(
        readingId: String,
        totalReading: Double,
        rate1Reading: Double,
        rate2Reading: Double,
        rate3Reading: Double,
        notes: String = ""
    ): Result<EnhancedMeterReading>
    
    /**
     * Mark a reading as empty with a specific reason
     * @param readingId ID of the reading to mark as empty
     * @param reason The reason for the empty reading
     * @param notes Optional notes for the empty reading
     * @return Result containing the updated reading or error
     */
    suspend fun markReadingAsEmpty(
        readingId: String,
        reason: com.leco.meterreader.data.model.EmptyReadingReason,
        notes: String = ""
    ): Result<EnhancedMeterReading>
    
    /**
     * Mark a reading as estimated
     * @param readingId ID of the reading to mark as estimated
     * @param estimatedValue The estimated reading value
     * @param notes Optional notes for the estimated reading
     * @return Result containing the updated reading or error
     */
    suspend fun markReadingAsEstimated(
        readingId: String,
        estimatedValue: Double,
        notes: String = ""
    ): Result<EnhancedMeterReading>
    
    /**
     * Get readings that are missing data for calculation
     * @return Result containing list of readings that need calculation
     */
    suspend fun getReadingsNeedingCalculation(): Result<List<EnhancedMeterReading>>
    
    /**
     * Data class for reading statistics
     */
    data class ReadingStats(
        val totalReadings: Int,
        val completeReadings: Int,
        val emptyReadings: Int,
        val estimatedReadings: Int,
        val pendingReadings: Int,
        val completionRate: Double // Percentage of complete readings
    )
}