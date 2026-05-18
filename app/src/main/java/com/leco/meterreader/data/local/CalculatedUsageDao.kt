package com.leco.meterreader.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for calculated usage.
 */
@Dao
interface CalculatedUsageDao {
    @Query("SELECT * FROM calculated_usage ORDER BY calculationTimestamp DESC")
    fun getAllCalculatedUsage(): Flow<List<CalculatedUsageEntity>>

    @Query("SELECT * FROM calculated_usage WHERE id = :id")
    suspend fun getCalculatedUsageById(id: Long): CalculatedUsageEntity?

    @Query("SELECT * FROM calculated_usage WHERE fromReadingId = :fromReadingId")
    suspend fun getCalculatedUsageByFromReading(fromReadingId: Long): CalculatedUsageEntity?

    @Query("SELECT * FROM calculated_usage WHERE toReadingId = :toReadingId")
    suspend fun getCalculatedUsageByToReading(toReadingId: Long): CalculatedUsageEntity?

    @Insert
    suspend fun insertCalculatedUsage(usage: CalculatedUsageEntity): Long

    @Update
    suspend fun updateCalculatedUsage(usage: CalculatedUsageEntity)

    @Delete
    suspend fun deleteCalculatedUsage(usage: CalculatedUsageEntity)

    @Query("DELETE FROM calculated_usage")
    suspend fun deleteAllCalculatedUsage()
}