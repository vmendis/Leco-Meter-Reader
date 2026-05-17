package com.leco.meterreader.data.local.dao

import androidx.room.*
import com.leco.meterreader.data.local.entity.MeterReadingEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Data Access Object for meter reading operations
 * Provides methods for querying and modifying meter reading data
 */
@Dao
interface MeterReadingDao {
    
    /**
     * Insert a new meter reading
     * @param meterReading The meter reading to insert
     * @return The row ID of the inserted reading
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeterReading(meterReading: MeterReadingEntity): Long
    
    /**
     * Get all meter readings ordered by timestamp descending
     * @return Flow emitting list of all readings
     */
    @Query("SELECT * FROM meter_readings ORDER BY timestamp DESC")
    fun getAllMeterReadings(): Flow<List<MeterReadingEntity>>
    
    /**
     * Get the latest meter reading
     * @return The latest reading or null if none exists
     */
    @Query("SELECT * FROM meter_readings ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestMeterReading(): MeterReadingEntity?
    
    /**
     * Get readings within a specific date range
     * @param start Start date string (inclusive)
     * @param end End date string (inclusive)
     * @return List of readings in the specified range
     */
    @Query("SELECT * FROM meter_readings WHERE timestamp >= :start AND timestamp <= :end ORDER BY timestamp DESC")
    fun getReadingsByDateRange(start: String, end: String): Flow<List<MeterReadingEntity>>
    
    /**
     * Get readings for a specific month
     * @param year Year of the month
     * @param month Month (1-12)
     * @return List of readings for the specified month
     */
    @Query("SELECT * FROM meter_readings WHERE strftime('%Y', timestamp) = :year AND strftime('%m', timestamp) = :month ORDER BY timestamp DESC")
    fun getReadingsByMonth(year: String, month: String): Flow<List<MeterReadingEntity>>
    
    /**
     * Get a specific meter reading by ID
     * @param id The ID of the reading to retrieve
     * @return The meter reading or null if not found
     */
    @Query("SELECT * FROM meter_readings WHERE id = :id")
    suspend fun getMeterReadingById(id: Long): MeterReadingEntity?
    
    /**
     * Delete a meter reading by ID
     * @param id The ID of the reading to delete
     */
    @Delete
    suspend fun deleteMeterReading(meterReading: MeterReadingEntity)
    
    /**
     * Delete all meter readings
     */
    @Query("DELETE FROM meter_readings")
    suspend fun deleteAllMeterReadings()
    
    /**
     * Get count of total meter readings
     * @return The count of readings
     */
    @Query("SELECT COUNT(*) FROM meter_readings")
    suspend fun getMeterReadingCount(): Int
    
    /**
     * Check if a reading with the given timestamp already exists
     * @param timestamp The timestamp to check
     * @return True if reading exists, false otherwise
     */
    @Query("SELECT EXISTS(SELECT 1 FROM meter_readings WHERE timestamp = :timestamp)")
    suspend fun readingExists(timestamp: String): Boolean
    
    /**
     * Get readings after a specific timestamp
     * @param timestamp The timestamp to search from
     * @return List of readings after the specified timestamp
     */
    @Query("SELECT * FROM meter_readings WHERE timestamp > :timestamp ORDER BY timestamp ASC")
    fun getReadingsAfter(timestamp: String): Flow<List<MeterReadingEntity>>
    
    /**
     * Get readings before a specific timestamp
     * @param timestamp The timestamp to search to
     * @return List of readings before the specified timestamp
     */
    @Query("SELECT * FROM meter_readings WHERE timestamp < :timestamp ORDER BY timestamp DESC")
    fun getReadingsBefore(timestamp: String): Flow<List<MeterReadingEntity>>
}