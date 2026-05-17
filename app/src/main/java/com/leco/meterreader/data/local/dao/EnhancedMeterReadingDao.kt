package com.leco.meterreader.data.local.dao

import androidx.room.*
import com.leco.meterreader.data.local.entity.EnhancedMeterReadingEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Enhanced DAO for meter readings that supports empty readings
 * Provides comprehensive CRUD operations with support for different reading statuses
 */
@Dao
interface EnhancedMeterReadingDao {
    
    /**
     * Insert a new enhanced meter reading
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnhancedReading(reading: EnhancedMeterReadingEntity): Long
    
    /**
     * Insert multiple enhanced meter readings
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnhancedReadings(readings: List<EnhancedMeterReadingEntity>): List<Long>
    
    /**
     * Update an existing enhanced meter reading
     */
    @Update
    suspend fun updateEnhancedReading(reading: EnhancedMeterReadingEntity)
    
    /**
     * Update multiple enhanced meter readings
     */
    @Update
    suspend fun updateEnhancedReadings(readings: List<EnhancedMeterReadingEntity>)
    
    /**
     * Delete an enhanced meter reading
     */
    @Delete
    suspend fun deleteEnhancedReading(reading: EnhancedMeterReadingEntity)
    
    /**
     * Delete multiple enhanced meter readings
     */
    @Delete
    suspend fun deleteEnhancedReadings(readings: List<EnhancedMeterReadingEntity>)
    
    /**
     * Get all enhanced meter readings ordered by timestamp
     */
    @Query("SELECT * FROM enhanced_meter_readings ORDER BY timestamp DESC")
    fun getAllEnhancedReadings(): Flow<List<EnhancedMeterReadingEntity>>
    
    /**
     * Get the latest enhanced meter reading
     */
    @Query("SELECT * FROM enhanced_meter_readings ORDER BY timestamp DESC LIMIT 1")
    fun getLatestEnhancedReading(): Flow<EnhancedMeterReadingEntity?>
    
    /**
     * Get enhanced readings by status
     */
    @Query("SELECT * FROM enhanced_meter_readings WHERE status = :status ORDER BY timestamp DESC")
    fun getEnhancedReadingsByStatus(status: String): Flow<List<EnhancedMeterReadingEntity>>
    
    /**
     * Get enhanced readings within a specific date range
     */
    @Query("SELECT * FROM enhanced_meter_readings WHERE timestamp >= :start AND timestamp <= :end ORDER BY timestamp DESC")
    fun getEnhancedReadingsByDateRange(start: String, end: String): Flow<List<EnhancedMeterReadingEntity>>
    
    /**
     * Get enhanced readings for a specific month
     */
    @Query("SELECT * FROM enhanced_meter_readings WHERE strftime('%Y-%m', timestamp) = :monthYear ORDER BY timestamp DESC")
    fun getEnhancedReadingsByMonth(monthYear: String): Flow<List<EnhancedMeterReadingEntity>>
    
    /**
     * Get complete readings only
     */
    @Query("SELECT * FROM enhanced_meter_readings WHERE status = 'COMPLETED' ORDER BY timestamp DESC")
    fun getCompleteReadings(): Flow<List<EnhancedMeterReadingEntity>>
    
    /**
     * Get empty readings only
     */
    @Query("SELECT * FROM enhanced_meter_readings WHERE status = 'EMPTY' ORDER BY timestamp DESC")
    fun getEmptyReadings(): Flow<List<EnhancedMeterReadingEntity>>
    
    /**
     * Get estimated readings only
     */
    @Query("SELECT * FROM enhanced_meter_readings WHERE status = 'ESTIMATED' ORDER BY timestamp DESC")
    fun getEstimatedReadings(): Flow<List<EnhancedMeterReadingEntity>>
    
    /**
     * Get pending readings only
     */
    @Query("SELECT * FROM enhanced_meter_readings WHERE status = 'PENDING' ORDER BY timestamp DESC")
    fun getPendingReadings(): Flow<List<EnhancedMeterReadingEntity>>
    
    /**
     * Get readings by empty reason
     */
    @Query("SELECT * FROM enhanced_meter_readings WHERE empty_reason = :reason ORDER BY timestamp DESC")
    fun getReadingsByEmptyReason(reason: String): Flow<List<EnhancedMeterReadingEntity>>
    
    /**
     * Get readings that need completion (pending or empty)
     */
    @Query("SELECT * FROM enhanced_meter_readings WHERE status IN ('PENDING', 'EMPTY') ORDER BY timestamp ASC")
    fun getReadingsNeedingCompletion(): Flow<List<EnhancedMeterReadingEntity>>
    
    /**
     * Get readings with calculated usage
     */
    @Query("SELECT * FROM enhanced_meter_readings WHERE calculated_usage_id IS NOT NULL ORDER BY timestamp DESC")
    fun getReadingsWithCalculatedUsage(): Flow<List<EnhancedMeterReadingEntity>>
    
    /**
     * Get readings without calculated usage
     */
    @Query("SELECT * FROM enhanced_meter_readings WHERE calculated_usage_id IS NULL AND status = 'COMPLETED' ORDER BY timestamp DESC")
    fun getReadingsWithoutCalculatedUsage(): Flow<List<EnhancedMeterReadingEntity>>
    
    /**
     * Count total enhanced readings
     */
    @Query("SELECT COUNT(*) FROM enhanced_meter_readings")
    suspend fun getEnhancedReadingCount(): Int
    
    /**
     * Count readings by status
     */
    @Query("SELECT status, COUNT(*) as count FROM enhanced_meter_readings GROUP BY status")
    suspend fun getReadingCountByStatus(): List<ReadingCount>
    
    /**
     * Count readings by empty reason
     */
    @Query("SELECT empty_reason, COUNT(*) as count FROM enhanced_meter_readings WHERE empty_reason IS NOT NULL GROUP BY empty_reason")
    suspend fun getReadingCountByEmptyReason(): List<ReadingCount>
    
    /**
     * Check if an enhanced reading with the given timestamp exists
     */
    @Query("SELECT EXISTS(SELECT 1 FROM enhanced_meter_readings WHERE timestamp = :timestamp)")
    suspend fun enhancedReadingExists(timestamp: String): Boolean
    
    /**
     * Get reading statistics for a date range
     */
    @Query("""
        SELECT 
            COUNT(*) as totalReadings,
            SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) as completeReadings,
            SUM(CASE WHEN status = 'EMPTY' THEN 1 ELSE 0 END) as emptyReadings,
            SUM(CASE WHEN status = 'ESTIMATED' THEN 1 ELSE 0 END) as estimatedReadings,
            SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) as pendingReadings
        FROM enhanced_meter_readings 
        WHERE timestamp >= :start AND timestamp <= :end
    """)
    suspend fun getReadingStatsForDateRange(start: String, end: String): ReadingStats
    
    /**
     * Get the most recent empty reading
     */
    @Query("SELECT * FROM enhanced_meter_readings WHERE status = 'EMPTY' ORDER BY timestamp DESC LIMIT 1")
    fun getLatestEmptyReading(): Flow<EnhancedMeterReadingEntity?>
    
    /**
     * Get readings that can be converted to complete readings
     */
    @Query("SELECT * FROM enhanced_meter_readings WHERE status = 'ESTIMATED' ORDER BY timestamp DESC")
    fun getEstimatedReadingsForConversion(): Flow<List<EnhancedMeterReadingEntity>>
    
    /**
     * Data class for reading count statistics
     */
    data class ReadingCount(
        val status: String,
        val count: Int
    )
    
    /**
     * Data class for reading statistics
     */
    data class ReadingStats(
        val totalReadings: Int,
        val completeReadings: Int,
        val emptyReadings: Int,
        val estimatedReadings: Int,
        val pendingReadings: Int
    )
}