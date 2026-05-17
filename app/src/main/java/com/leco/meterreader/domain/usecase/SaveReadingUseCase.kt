package com.leco.meterreader.domain.usecase

import com.leco.meterreader.data.model.MeterReading
import com.leco.meterreader.domain.repository.MeterReadingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for saving a meter reading
 * Encapsulates the business logic for validating and saving readings
 */
class SaveReadingUseCase @Inject constructor(
    private val repository: MeterReadingRepository
) {
    /**
     * Execute the use case to save a meter reading
     * @param reading The meter reading to save
     * @return Flow emitting the result of the save operation
     */
    operator fun invoke(reading: MeterReading): Flow<Result<MeterReading>> = repository.saveReading(reading)
    
    /**
     * Validate a meter reading before saving
     * @param reading The reading to validate
     * @return Result containing validation errors or success
     */
    suspend fun validateReading(reading: MeterReading): Result<Unit> {
        // Check if timestamp is in the future
        if (reading.timestamp.isAfter(java.time.LocalDateTime.now())) {
            return Result.failure(Exception("Timestamp cannot be in the future"))
        }
        
        // Check if all readings are positive
        if (reading.totalReading < 0 || reading.rate1Reading < 0 || 
            reading.rate2Reading < 0 || reading.rate3Reading < 0) {
            return Result.failure(Exception("All readings must be positive values"))
        }
        
        // Check if total reading is greater than individual rates
        if (reading.totalReading < reading.rate1Reading + reading.rate2Reading + reading.rate3Reading) {
            return Result.failure(Exception("Total reading must be greater than or equal to the sum of individual rates"))
        }
        
        return Result.success(Unit)
    }
}