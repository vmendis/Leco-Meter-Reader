package com.leco.meterreader.domain.repository

import com.leco.meterreader.data.model.CalculatedUsage
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Repository interface for calculated usage operations
 * Defines the contract for accessing calculated usage data
 */
interface CalculatedUsageRepository {
    /**
     * Save a calculated usage record
     * @param usage The calculated usage to save
     * @return Result containing the saved usage or error
     */
    suspend fun saveCalculatedUsage(usage: CalculatedUsage): Result<CalculatedUsage>
    
    /**
     * Get the latest calculated usage record
     * @return Result containing the latest usage or null if none exists
     */
    suspend fun getLatestCalculatedUsage(): Result<CalculatedUsage?>
    
    /**
     * Get all calculated usage records ordered by calculation timestamp
     * @return Result containing list of all calculated usage records
     */
    suspend fun getAllCalculatedUsage(): Result<List<CalculatedUsage>>
    
    /**
     * Get calculated usage by ID
     * @param id The ID of the calculated usage to retrieve
     * @return Result containing the calculated usage or null if not found
     */
    suspend fun getCalculatedUsageById(id: String): Result<CalculatedUsage?>
    
    /**
     * Get calculated usage by reading IDs
     * @param fromReadingId The starting reading ID
     * @param toReadingId The ending reading ID
     * @return Result containing the calculated usage or null if not found
     */
    suspend fun getCalculatedUsageByReadingIds(fromReadingId: String, toReadingId: String): Result<CalculatedUsage?>
    
    /**
     * Get calculated usage within a specific date range
     * @param start Start date (inclusive)
     * @param end End date (inclusive)
     * @return Result containing list of calculated usage records in the range
     */
    suspend fun getCalculatedUsageByDateRange(start: LocalDateTime, end: LocalDateTime): Result<List<CalculatedUsage>>
    
    /**
     * Get calculated usage for a specific month
     * @param year Year of the month
     * @param month Month (1-12)
     * @return Result containing list of calculated usage records for the month
     */
    suspend fun getCalculatedUsageByMonth(year: Int, month: Int): Result<List<CalculatedUsage>>
    
    /**
     * Delete a calculated usage record
     * @param id The ID of the calculated usage to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteCalculatedUsage(id: String): Result<Unit>
    
    /**
     * Get count of total calculated usage records
     * @return Result containing the count of calculated usage records
     */
    suspend fun getCalculatedUsageCount(): Result<Int>
    
    /**
     * Check if calculated usage exists for the given reading IDs
     * @param fromReadingId The starting reading ID
     * @param toReadingId The ending reading ID
     * @return Result containing true if calculated usage exists, false otherwise
     */
    suspend fun calculatedUsageExists(fromReadingId: String, toReadingId: String): Result<Boolean>
    
    /**
     * Get Flow of all calculated usage records for reactive updates
     * @return Flow emitting list of all calculated usage records
     */
    fun getCalculatedUsageFlow(): Flow<List<CalculatedUsage>>
    
    /**
     * Get Flow of calculated usage records for a specific reading ID
     * @param readingId The meter reading ID
     * @return Flow emitting list of calculated usage records involving this reading
     */
    fun getCalculatedUsageByReadingId(readingId: String): Flow<List<CalculatedUsage>>
    
    /**
     * Get Flow of calculated usage records with minimum cost
     * @param minCost Minimum cost threshold
     * @return Flow emitting list of calculated usage records with cost greater than threshold
     */
    fun getCalculatedUsageByMinCost(minCost: Double): Flow<List<CalculatedUsage>>
    
    /**
     * Get Flow of calculated usage records sorted by consumption
     * @return Flow emitting list of calculated usage records sorted by consumption
     */
    fun getCalculatedUsageByConsumption(): Flow<List<CalculatedUsage>>
}