package com.leco.meterreader.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for meter readings.
 */
@Dao
interface MeterReadingDao {
    @Query("SELECT * FROM meter_readings ORDER BY timestamp DESC")
    fun getAllReadings(): Flow<List<MeterReadingEntity>>

    @Query("SELECT * FROM meter_readings WHERE id = :id")
    suspend fun getReadingById(id: Long): MeterReadingEntity?

    @Query("SELECT * FROM meter_readings ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestReading(): MeterReadingEntity?

    @Insert
    suspend fun insertReading(reading: MeterReadingEntity): Long

    @Update
    suspend fun updateReading(reading: MeterReadingEntity)

    @Delete
    suspend fun deleteReading(reading: MeterReadingEntity)

    @Query("DELETE FROM meter_readings")
    suspend fun deleteAllReadings()
}