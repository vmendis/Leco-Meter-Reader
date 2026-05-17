package com.leco.meterreader.domain.repository

import com.leco.meterreader.data.model.SolarPlanningData
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for solar planning operations
 * Defines the contract for solar planning data access
 */
interface SolarPlanningRepository {
    /**
     * Save solar planning configuration
     * @param data The solar planning data to save
     * @return Result containing the saved data or error
     */
    suspend fun saveSolarPlanningData(data: SolarPlanningData): Result<SolarPlanningData>
    
    /**
     * Get the latest solar planning configuration
     * @return Result containing the latest configuration or null if none exists
     */
    suspend fun getLatestSolarPlanningData(): Result<SolarPlanningData?>
    
    /**
     * Get all saved solar planning configurations
     * @return Result containing list of all configurations
     */
    suspend fun getAllSolarPlanningData(): Result<List<SolarPlanningData>>
    
    /**
     * Get solar planning data by ID
     * @param id The ID of the configuration to retrieve
     * @return Result containing the configuration or null if not found
     */
    suspend fun getSolarPlanningDataById(id: String): Result<SolarPlanningData?>
    
    /**
     * Delete solar planning configuration
     * @param id The ID of the configuration to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteSolarPlanningData(id: String): Result<Unit>
    
    /**
     * Get count of saved solar planning configurations
     * @return Result containing the count of configurations
     */
    suspend fun getSolarPlanningDataCount(): Result<Int>
    
    /**
     * Check if solar planning data exists for the given parameters
     * @param dailyConsumption Daily consumption value
     * @return Result containing true if data exists, false otherwise
     */
    suspend fun solarPlanningDataExists(dailyConsumption: Double): Result<Boolean>
}