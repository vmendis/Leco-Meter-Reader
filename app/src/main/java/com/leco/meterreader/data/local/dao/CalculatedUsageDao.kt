package com.leco.meterreader.data.local.dao

import androidx.room.*
import com.leco.meterreader.data.local.entity.CalculatedUsageEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for calculated usage operations
 * Provides methods for querying and modifying calculated usage data
 */
@Dao
interface CalculatedUsageDao {
    
    /**
     * Insert a new calculated usage record
     * @param calculatedUsage The calculated usage to insert
     * @return The row ID of the inserted record
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculatedUsage(calculatedUsage: CalculatedUsageEntity): Long
    
    /**
     * Get all calculated usage records ordered by calculation timestamp descending
     * @return Flow emitting list of all calculated usage records
     */
    @Query("SELECT * FROM calculated_usage ORDER BY calculationTimestamp DESC")
    fun getAllCalculatedUsage(): Flow<List<CalculatedUsageEntity>>
    
    /**
     * Get calculated usage by ID
     * @param id The ID of the calculated usage to retrieve
     * @return The calculated usage record or null if not found
     */
    @Query("SELECT * FROM calculated_usage WHERE id = :id")
    suspend fun getCalculatedUsageById(id: Long): CalculatedUsageEntity?
    
    /**
     * Get calculated usage by reading IDs
     * @param fromReadingId The starting reading ID
     * @param toReadingId The ending reading ID
     * @return The calculated usage record or null if not found
     */
    @Query("SELECT * FROM calculated_usage WHERE fromReadingId = :fromReadingId AND toReadingId = :toReadingId")
    suspend fun getCalculatedUsageByReadingIds(fromReadingId: Long, toReadingId: Long): CalculatedUsageEntity?
    
    /**
     * Get calculated usage for a specific meter reading
     * @param readingId The meter reading ID
     * @return List of calculated usage records involving this reading
     */
    @Query("SELECT * FROM calculated_usage WHERE fromReadingId = :readingId OR toReadingId = :readingId ORDER BY calculationTimestamp DESC")
    fun getCalculatedUsageByReadingId(readingId: Long): Flow<List<CalculatedUsageEntity>>
    
    /**
     * Get calculated usage within a specific date range
     * @param start Start date string (inclusive)
     * @param end End date string (inclusive)
     * @return List of calculated usage records in the specified range
     */
    @Query("SELECT * FROM calculated_usage WHERE calculationTimestamp >= :start AND calculationTimestamp <= :end ORDER BY calculationTimestamp DESC")
    fun getCalculatedUsageByDateRange(start: String, end: String): Flow<List<CalculatedUsageEntity>>
    
    /**
     * Get calculated usage for a specific month
     * @param year Year of the month
     * @param month Month (1-12)
     * @return List of calculated usage records for the specified month
     */
    @Query("SELECT * FROM calculated_usage WHERE strftime('%Y', calculationTimestamp) = :year AND strftime('%m', calculationTimestamp) = :month ORDER BY calculationTimestamp DESC")
    fun getCalculatedUsageByMonth(year: String, month: String): Flow<List<CalculatedUsageEntity>>
    
    /**
     * Update a calculated usage record
     * @param calculatedUsage The calculated usage to update
     */
    @Update
    suspend fun updateCalculatedUsage(calculatedUsage: CalculatedUsageEntity)
    
    /**
     * Delete a calculated usage record
     * @param calculatedUsage The calculated usage to delete
     */
    @Delete
    suspend fun deleteCalculatedUsage(calculatedUsage: CalculatedUsageEntity)
    
    /**
     * Delete calculated usage by ID
     * @param id The ID of the calculated usage to delete
     */
    @Query("DELETE FROM calculated_usage WHERE id = :id")
    suspend fun deleteCalculatedUsageById(id: Long)
    
    /**
     * Delete all calculated usage records
     */
    @Query("DELETE FROM calculated_usage")
    suspend fun deleteAllCalculatedUsage()
    
    /**
     * Get count of total calculated usage records
     * @return The count of calculated usage records
     */
    @Query("SELECT COUNT(*) FROM calculated_usage")
    suspend fun getCalculatedUsageCount(): Int
    
    /**
     * Get calculated usage records after a specific timestamp
     * @param timestamp The timestamp to search from
     * @return List of calculated usage records after the specified timestamp
     */
    @Query("SELECT * FROM calculated_usage WHERE calculationTimestamp > :timestamp ORDER BY calculationTimestamp ASC")
    fun getCalculatedUsageAfter(timestamp: String): Flow<List<CalculatedUsageEntity>>
    
    /**
     * Get calculated usage records before a specific timestamp
     * @param timestamp The timestamp to search to
     * @return List of calculated usage records before the specified timestamp
     */
    @Query("SELECT * FROM calculated_usage WHERE calculationTimestamp < :timestamp ORDER BY calculationTimestamp DESC")
    fun getCalculatedUsageBefore(timestamp: String): Flow<List<CalculatedUsageEntity>>
    
    /**
     * Get the latest calculated usage record
     * @return The latest calculated usage record or null if none exists
     */
    @Query("SELECT * FROM calculated_usage ORDER BY calculationTimestamp DESC LIMIT 1")
    suspend fun getLatestCalculatedUsage(): CalculatedUsageEntity?
    
    /**
     * Check if calculated usage exists for the given reading IDs
     * @param fromReadingId The starting reading ID
     * @param toReadingId The ending reading ID
     * @return True if calculated usage exists, false otherwise
     */
    @Query("SELECT EXISTS(SELECT 1 FROM calculated_usage WHERE fromReadingId = :fromReadingId AND toReadingId = :toReadingId)")
    suspend fun calculatedUsageExists(fromReadingId: Long, toReadingId: Long): Boolean
    
    /**
     * Get calculated usage with estimated cost greater than specified amount
     * @param minCost Minimum cost threshold
     * @return List of calculated usage records with cost greater than threshold
     */
    @Query("SELECT * FROM calculated_usage WHERE estimatedCost > :minCost ORDER BY estimatedCost DESC")
    fun getCalculatedUsageByMinCost(minCost: Double): Flow<List<CalculatedUsageEntity>>
    
    /**
     * Get calculated usage sorted by total consumption descending
     * @return List of calculated usage records sorted by consumption
     */
    @Query("SELECT * FROM calculated_usage ORDER BY totalUsed DESC")
    fun getCalculatedUsageByConsumption(): Flow<List<CalculatedUsageEntity>>
}