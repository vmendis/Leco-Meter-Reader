package com.leco.meterreader.data.repository

import com.leco.meterreader.data.local.AppDatabase
import com.leco.meterreader.data.local.entity.MeterReadingEntity
import com.leco.meterreader.data.model.MeterReading
import com.leco.meterreader.domain.repository.MeterReadingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Implementation of MeterReadingRepository
 * Provides concrete implementation of repository interface using Room database
 */
class MeterReadingRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : MeterReadingRepository {
    
    override suspend fun saveReading(reading: MeterReading): Result<MeterReading> {
        return try {
            val entity = MeterReadingEntity.fromDomain(reading)
            val id = database.meterReadingDao().insertMeterReading(entity)
            Result.success(reading.copy(id = id.toString()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getLatestReading(): Result<MeterReading?> {
        return try {
            val entity = database.meterReadingDao().getLatestMeterReading()
            Result.success(entity?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getAllReadings(): Result<List<MeterReading>> {
        return try {
            val entities = database.meterReadingDao().getAllMeterReadings()
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getReadingsByDateRange(start: LocalDateTime, end: LocalDateTime): Result<List<MeterReading>> {
        return try {
            val entities = database.meterReadingDao().getReadingsByDateRange(
                start = start.toString(),
                end = end.toString()
            )
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getReadingsByMonth(year: Int, month: Int): Result<List<MeterReading>> {
        return try {
            val entities = database.meterReadingDao().getReadingsByMonth(
                year = year.toString(),
                month = month.toString().padStart(2, '0')
            )
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteReading(readingId: String): Result<Unit> {
        return try {
            val entity = database.meterReadingDao().getMeterReadingById(readingId.toLong())
            entity?.let {
                database.meterReadingDao().deleteMeterReading(it)
                Result.success(Unit)
            } ?: Result.failure(Exception("Reading not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getReadingCount(): Result<Int> {
        return try {
            val count = database.meterReadingDao().getMeterReadingCount()
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun readingExists(timestamp: LocalDateTime): Result<Boolean> {
        return try {
            val exists = database.meterReadingDao().readingExists(timestamp.toString())
            Result.success(exists)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}