package com.leco.meterreader.domain.usecase

import com.leco.meterreader.data.model.EnhancedMeterReading
import com.leco.meterreader.data.model.EnhancedMeterReading.EmptyReadingReason
import com.leco.meterreader.domain.repository.EnhancedMeterReadingRepository
import javax.inject.Inject

/**
 * Use case for saving enhanced meter readings
 * Handles different types of readings including empty and estimated readings
 */
class SaveEnhancedReadingUseCase @Inject constructor(
    private val enhancedMeterReadingRepository: EnhancedMeterReadingRepository
) {
    
    /**
     * Save a complete meter reading
     * @param timestamp When the reading was taken
     * @param totalReading Total meter reading value
     * @param rate1Reading Rate 1 reading value
     * @param rate2Reading Rate 2 reading value
     * @param rate3Reading Rate 3 reading value
     * @param notes Optional notes about the reading
     * @return Result containing the saved reading or error
     */
    suspend operator fun invoke(
        timestamp: java.time.LocalDateTime,
        totalReading: Double,
        rate1Reading: Double,
        rate2Reading: Double,
        rate3Reading: Double,
        notes: String = ""
    ): Result<EnhancedMeterReading> {
        val reading = EnhancedMeterReading(
            timestamp = timestamp,
            status = com.leco.meterreader.data.model.ReadingStatus.COMPLETED,
            totalReading = totalReading,
            rate1Reading = rate1Reading,
            rate2Reading = rate2Reading,
            rate3Reading = rate3Reading,
            notes = notes
        )
        return enhancedMeterReadingRepository.saveEnhancedReading(reading)
    }
    
    /**
     * Save an empty meter reading with a specific reason
     * @param timestamp When the reading was supposed to be taken
     * @param reason Why the reading is empty
     * @param notes Optional notes about why the reading is empty
     * @return Result containing the saved reading or error
     */
    suspend operator fun invoke(
        timestamp: java.time.LocalDateTime,
        reason: EmptyReadingReason,
        notes: String = ""
    ): Result<EnhancedMeterReading> {
        val reading = EnhancedMeterReading(
            timestamp = timestamp,
            status = com.leco.meterreader.data.model.ReadingStatus.EMPTY,
            notes = notes,
            emptyReason = reason
        )
        return enhancedMeterReadingRepository.saveEnhancedReading(reading)
    }
    
    /**
     * Save an estimated meter reading
     * @param timestamp When the reading was supposed to be taken
     * @param estimatedValue The estimated reading value
     * @param notes Optional notes about the estimation
     * @return Result containing the saved reading or error
     */
    suspend operator fun invoke(
        timestamp: java.time.LocalDateTime,
        estimatedValue: Double,
        notes: String = ""
    ): Result<EnhancedMeterReading> {
        val reading = EnhancedMeterReading(
            timestamp = timestamp,
            status = com.leco.meterreader.data.model.ReadingStatus.ESTIMATED,
            notes = notes,
            estimatedReading = true,
            estimatedValue = estimatedValue
        )
        return enhancedMeterReadingRepository.saveEnhancedReading(reading)
    }
    
    /**
     * Save a pending meter reading
     * @param timestamp When the reading is scheduled
     * @param notes Optional notes about the pending reading
     * @return Result containing the saved reading or error
     */
    suspend operator fun invoke(
        timestamp: java.time.LocalDateTime,
        notes: String = ""
    ): Result<EnhancedMeterReading> {
        val reading = EnhancedMeterReading(
            timestamp = timestamp,
            status = com.leco.meterreader.data.model.ReadingStatus.PENDING,
            notes = notes
        )
        return enhancedMeterReadingRepository.saveEnhancedReading(reading)
    }
}