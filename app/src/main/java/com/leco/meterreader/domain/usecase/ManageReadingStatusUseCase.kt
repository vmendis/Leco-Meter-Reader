package com.leco.meterreader.domain.usecase

import com.leco.meterreader.data.model.EnhancedMeterReading
import com.leco.meterreader.data.model.EnhancedMeterReading.EmptyReadingReason
import com.leco.meterreader.domain.repository.EnhancedMeterReadingRepository
import javax.inject.Inject

/**
 * Use case for managing reading status changes
 * Handles transitions between different reading states
 */
class ManageReadingStatusUseCase @Inject constructor(
    private val enhancedMeterReadingRepository: EnhancedMeterReadingRepository
) {
    
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
    ): Result<EnhancedMeterReading> {
        return enhancedMeterReadingRepository.convertEstimatedToComplete(
            readingId = readingId,
            totalReading = totalReading,
            rate1Reading = rate1Reading,
            rate2Reading = rate2Reading,
            rate3Reading = rate3Reading,
            notes = notes
        )
    }
    
    /**
     * Mark a reading as empty with a specific reason
     * @param readingId ID of the reading to mark as empty
     * @param reason The reason for the empty reading
     * @param notes Optional notes for the empty reading
     * @return Result containing the updated reading or error
     */
    suspend fun markAsEmpty(
        readingId: String,
        reason: EmptyReadingReason,
        notes: String = ""
    ): Result<EnhancedMeterReading> {
        return enhancedMeterReadingRepository.markReadingAsEmpty(
            readingId = readingId,
            reason = reason,
            notes = notes
        )
    }
    
    /**
     * Mark a reading as estimated
     * @param readingId ID of the reading to mark as estimated
     * @param estimatedValue The estimated reading value
     * @param notes Optional notes for the estimated reading
     * @return Result containing the updated reading or error
     */
    suspend fun markAsEstimated(
        readingId: String,
        estimatedValue: Double,
        notes: String = ""
    ): Result<EnhancedMeterReading> {
        return enhancedMeterReadingRepository.markReadingAsEstimated(
            readingId = readingId,
            estimatedValue = estimatedValue,
            notes = notes
        )
    }
    
    /**
     * Get readings that need completion (pending or empty)
     * @return Result containing list of readings that need completion
     */
    suspend fun getReadingsNeedingCompletion(): Result<List<EnhancedMeterReading>> {
        return enhancedMeterReadingRepository.getReadingsNeedingCompletion()
    }
    
    /**
     * Get readings that can be converted to complete readings
     * @return Result containing list of estimated readings that can be converted
     */
    suspend fun getEstimatedReadingsForConversion(): Result<List<EnhancedMeterReading>> {
        return enhancedMeterReadingRepository.getEstimatedReadingsForConversion()
    }
    
    /**
     * Get readings that need calculation (complete but without calculated usage)
     * @return Result containing list of readings that need calculation
     */
    suspend fun getReadingsNeedingCalculation(): Result<List<EnhancedMeterReading>> {
        return enhancedMeterReadingRepository.getReadingsNeedingCalculation()
    }
    
    /**
     * Get statistics about reading status distribution
     * @return Result containing reading statistics
     */
    suspend fun getReadingStatistics(): Result<ReadingStatistics> {
        return try {
            val countByStatus = enhancedMeterReadingRepository.getReadingCountByStatus()
            val totalCount = countByStatus.values.sum()
            
            val stats = ReadingStatistics(
                totalReadings = totalCount,
                completeReadings = countByStatus[EnhancedMeterReading.ReadingStatus.COMPLETED] ?: 0,
                emptyReadings = countByStatus[EnhancedMeterReading.ReadingStatus.EMPTY] ?: 0,
                estimatedReadings = countByStatus[EnhancedMeterReading.ReadingStatus.ESTIMATED] ?: 0,
                pendingReadings = countByStatus[EnhancedMeterReading.ReadingStatus.PENDING] ?: 0,
                completionRate = if (totalCount > 0) {
                    (countByStatus[EnhancedMeterReading.ReadingStatus.COMPLETED] ?: 0).toDouble() / totalCount * 100
                } else 0.0
            )
            
            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get statistics about empty reading reasons
     * @return Result containing empty reading reason statistics
     */
    suspend fun getEmptyReadingReasons(): Result<Map<EmptyReadingReason, Int>> {
        return enhancedMeterReadingRepository.getReadingCountByEmptyReason()
    }
    
    /**
     * Get reading statistics for a specific date range
     * @param start Start date (inclusive)
     * @param end End date (inclusive)
     * @return Result containing reading statistics for the date range
     */
    suspend fun getReadingStatsForDateRange(
        start: java.time.LocalDateTime,
        end: java.time.LocalDateTime
    ): Result<EnhancedMeterReadingRepository.ReadingStats> {
        return enhancedMeterReadingRepository.getReadingStatsForDateRange(start, end)
    }
    
    /**
     * Data class for reading statistics
     */
    data class ReadingStatistics(
        val totalReadings: Int,
        val completeReadings: Int,
        val emptyReadings: Int,
        val estimatedReadings: Int,
        val pendingReadings: Int,
        val completionRate: Double
    )
}