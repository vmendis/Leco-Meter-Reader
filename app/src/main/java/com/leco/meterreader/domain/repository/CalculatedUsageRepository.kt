package com.leco.meterreader.domain.repository

import com.leco.meterreader.domain.model.CalculatedUsage
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for calculated usage operations.
 */
interface CalculatedUsageRepository {
    fun getAllCalculatedUsage(): Flow<List<CalculatedUsage>>
    suspend fun getCalculatedUsageById(id: Long): CalculatedUsage?
    suspend fun getCalculatedUsageByFromReading(fromReadingId: Long): CalculatedUsage?
    suspend fun getCalculatedUsageByToReading(toReadingId: Long): CalculatedUsage?
    suspend fun insertCalculatedUsage(usage: CalculatedUsage): Long
    suspend fun updateCalculatedUsage(usage: CalculatedUsage)
    suspend fun deleteCalculatedUsage(usage: CalculatedUsage)
    suspend fun deleteAllCalculatedUsage()
}