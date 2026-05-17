package com.leco.meterreader.domain.usecase

import com.leco.meterreader.data.model.MeterReading
import com.leco.meterreader.domain.repository.MeterReadingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting the latest meter reading
 * Encapsulates the business logic for retrieving the most recent reading
 */
class GetLatestReadingUseCase @Inject constructor(
    private val repository: MeterReadingRepository
) {
    /**
     * Execute the use case to get the latest reading
     * @return Flow emitting the latest reading or null
     */
    operator fun invoke(): Flow<Result<MeterReading?>> = repository.getLatestReading()
}