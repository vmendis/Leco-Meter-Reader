package com.leco.meterreader.data.local.dao

import androidx.room.*
import com.leco.meterreader.data.local.entity.TariffConfigurationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for tariff configuration operations
 * Provides methods for querying and modifying tariff configuration data
 */
@Dao
interface TariffConfigurationDao {
    
    /**
     * Insert a new tariff configuration
     * @param tariffConfiguration The tariff configuration to insert
     * @return The row ID of the inserted configuration
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTariffConfiguration(tariffConfiguration: TariffConfigurationEntity): Long
    
    /**
     * Get all tariff configurations ordered by effective date descending
     * @return Flow emitting list of all tariff configurations
     */
    @Query("SELECT * FROM tariff_configuration ORDER BY effectiveDate DESC")
    fun getAllTariffConfigurations(): Flow<List<TariffConfigurationEntity>>
    
    /**
     * Get tariff configuration by ID
     * @param id The ID of the tariff configuration to retrieve
     * @return The tariff configuration or null if not found
     */
    @Query("SELECT * FROM tariff_configuration WHERE id = :id")
    suspend fun getTariffConfigurationById(id: Long): TariffConfigurationEntity?
    
    /**
     * Get the currently active tariff configuration
     * @return The active tariff configuration or null if none exists
     */
    @Query("SELECT * FROM tariff_configuration WHERE isActive = 1 ORDER BY effectiveDate DESC LIMIT 1")
    suspend fun getActiveTariffConfiguration(): TariffConfigurationEntity?
    
    /**
     * Get tariff configurations effective at a specific date
     * @param date The date to check for effective tariffs
     * @return List of tariff configurations effective at the specified date
     */
    @Query("SELECT * FROM tariff_configuration WHERE effectiveDate <= :date AND isActive = 1 ORDER BY effectiveDate DESC")
    fun getTariffConfigurationsAtDate(date: String): Flow<List<TariffConfigurationEntity>>
    
    /**
     * Get tariff configurations for a specific date range
     * @param start Start date string (inclusive)
     * @param end End date string (inclusive)
     * @return List of tariff configurations in the specified range
     */
    @Query("SELECT * FROM tariff_configuration WHERE effectiveDate >= :start AND effectiveDate <= :end ORDER BY effectiveDate DESC")
    fun getTariffConfigurationsByDateRange(start: String, end: String): Flow<List<TariffConfigurationEntity>>
    
    /**
     * Get tariff configurations for a specific year
     * @param year The year to filter by
     * @return List of tariff configurations for the specified year
     */
    @Query("SELECT * FROM tariff_configuration WHERE strftime('%Y', effectiveDate) = :year ORDER BY effectiveDate DESC")
    fun getTariffConfigurationsByYear(year: String): Flow<List<TariffConfigurationEntity>>
    
    /**
     * Get active tariff configurations only
     * @return Flow emitting list of active tariff configurations
     */
    @Query("SELECT * FROM tariff_configuration WHERE isActive = 1 ORDER BY effectiveDate DESC")
    fun getActiveTariffConfigurations(): Flow<List<TariffConfigurationEntity>>
    
    /**
     * Get inactive tariff configurations only
     * @return Flow emitting list of inactive tariff configurations
     */
    @Query("SELECT * FROM tariff_configuration WHERE isActive = 0 ORDER BY effectiveDate DESC")
    fun getInactiveTariffConfigurations(): Flow<List<TariffConfigurationEntity>>
    
    /**
     * Update a tariff configuration
     * @param tariffConfiguration The tariff configuration to update
     */
    @Update
    suspend fun updateTariffConfiguration(tariffConfiguration: TariffConfigurationEntity)
    
    /**
     * Update tariff configuration activity status
     * @param id The ID of the tariff configuration to update
     * @param isActive The new activity status
     */
    @Query("UPDATE tariff_configuration SET isActive = :isActive, updatedAt = datetime('now') WHERE id = :id")
    suspend fun updateTariffConfigurationStatus(id: Long, isActive: Boolean)
    
    /**
     * Deactivate all tariff configurations (make one active at a time)
     */
    @Query("UPDATE tariff_configuration SET isActive = 0, updatedAt = datetime('now')")
    suspend fun deactivateAllTariffConfigurations()
    
    /**
     * Delete a tariff configuration
     * @param tariffConfiguration The tariff configuration to delete
     */
    @Delete
    suspend fun deleteTariffConfiguration(tariffConfiguration: TariffConfigurationEntity)
    
    /**
     * Delete tariff configuration by ID
     * @param id The ID of the tariff configuration to delete
     */
    @Query("DELETE FROM tariff_configuration WHERE id = :id")
    suspend fun deleteTariffConfigurationById(id: Long)
    
    /**
     * Delete all tariff configurations
     */
    @Query("DELETE FROM tariff_configuration")
    suspend fun deleteAllTariffConfigurations()
    
    /**
     * Get count of total tariff configurations
     * @return The count of tariff configurations
     */
    @Query("SELECT COUNT(*) FROM tariff_configuration")
    suspend fun getTariffConfigurationCount(): Int
    
    /**
     * Get count of active tariff configurations
     * @return The count of active tariff configurations
     */
    @Query("SELECT COUNT(*) FROM tariff_configuration WHERE isActive = 1")
    suspend fun getActiveTariffConfigurationCount(): Int
    
    /**
     * Check if tariff configuration exists for the given effective date
     * @param effectiveDate The effective date to check
     * @return True if tariff configuration exists, false otherwise
     */
    @Query("SELECT EXISTS(SELECT 1 FROM tariff_configuration WHERE effectiveDate = :effectiveDate)")
    suspend fun tariffConfigurationExists(effectiveDate: String): Boolean
    
    /**
     * Get tariff configurations created after a specific timestamp
     * @param timestamp The timestamp to search from
     * @return List of tariff configurations created after the specified timestamp
     */
    @Query("SELECT * FROM tariff_configuration WHERE createdAt > :timestamp ORDER BY createdAt ASC")
    fun getTariffConfigurationsAfter(timestamp: String): Flow<List<TariffConfigurationEntity>>
    
    /**
     * Get tariff configurations created before a specific timestamp
     * @param timestamp The timestamp to search to
     * @return List of tariff configurations created before the specified timestamp
     */
    @Query("SELECT * FROM tariff_configuration WHERE createdAt < :timestamp ORDER BY createdAt DESC")
    fun getTariffConfigurationsBefore(timestamp: String): Flow<List<TariffConfigurationEntity>>
    
    /**
     * Get tariff configurations with peak rate greater than specified amount
     * @param minPeakRate Minimum peak rate threshold
     * @return List of tariff configurations with peak rate greater than threshold
     */
    @Query("SELECT * FROM tariff_configuration WHERE peakRate > :minPeakRate ORDER BY peakRate DESC")
    fun getTariffConfigurationsByMinPeakRate(minPeakRate: Double): Flow<List<TariffConfigurationEntity>>
    
    /**
     * Get tariff configurations sorted by day rate ascending
     * @return List of tariff configurations sorted by day rate
     */
    @Query("SELECT * FROM tariff_configuration ORDER BY dayRate ASC")
    fun getTariffConfigurationsByDayRate(): Flow<List<TariffConfigurationEntity>>
    
    /**
     * Get the latest tariff configuration
     * @return The latest tariff configuration or null if none exists
     */
    @Query("SELECT * FROM tariff_configuration ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestTariffConfiguration(): TariffConfigurationEntity?
    
    /**
     * Get tariff configurations by description (search)
     * @param description The description text to search for
     * @return List of tariff configurations matching the description
     */
    @Query("SELECT * FROM tariff_configuration WHERE description LIKE '%' || :description || '%' ORDER BY effectiveDate DESC")
    fun getTariffConfigurationsByDescription(description: String): Flow<List<TariffConfigurationEntity>>
}