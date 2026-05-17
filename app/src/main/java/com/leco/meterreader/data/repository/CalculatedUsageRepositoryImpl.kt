package com.leco.meterreader.data.repository

import com.leco.meterreader.data.local.AppDatabase
import com.leco.meterreader.data.local.entity.CalculatedUsageEntity
import com.leco.meterreader.data.model.CalculatedUsage
import com.leco.meterreader.domain.repository.CalculatedUsageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Implementation of CalculatedUsageRepository
 * Provides concrete implementation of repository interface using Room database
 */
class CalculatedUsageRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : CalculatedUsageRepository {
    
    override suspend fun saveCalculatedUsage(usage: CalculatedUsage): Result<CalculatedUsage> {
        return try {
            val entity = CalculatedUsageEntity.fromDomain(usage)
            val id = database.calculatedUsageDao().insertCalculatedUsage(entity)
            Result.success(usage.copy(id = id.toString()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getLatestCalculatedUsage(): Result<CalculatedUsage?> {
        return try {
            val entity = database.calculatedUsageDao().getLatestCalculatedUsage()
            Result.success(entity?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getAllCalculatedUsage(): Result<List<CalculatedUsage>> {
        return try {
            val entities = database.calculatedUsageDao().getAllCalculatedUsage()
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCalculatedUsageById(id: String): Result<CalculatedUsage?> {
        return try {
            val entity = database.calculatedUsageDao().getCalculatedUsageById(id.toLong())
            Result.success(entity?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCalculatedUsageByReadingIds(fromReadingId: String, toReadingId: String): Result<CalculatedUsage?> {
        return try {
            val entity = database.calculatedUsageDao().getCalculatedUsageByReadingIds(
                fromReadingId = fromReadingId.toLong(),
                toReadingId = toReadingId.toLong()
            )
            Result.success(entity?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCalculatedUsageByDateRange(start: LocalDateTime, end: LocalDateTime): Result<List<CalculatedUsage>> {
        return try {
            val entities = database.calculatedUsageDao().getCalculatedUsageByDateRange(
                start = start.toString(),
                end = end.toString()
            )
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCalculatedUsageByMonth(year: Int, month: Int): Result<List<CalculatedUsage>> {
        return try {
            val entities = database.calculatedUsageDao().getCalculatedUsageByMonth(
                year = year.toString(),
                month = month.toString().padStart(2, '0')
            )
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteCalculatedUsage(id: String): Result<Unit> {
        return try {
            val entity = database.calculatedUsageDao().getCalculatedUsageById(id.toLong())
            entity?.let {
                database.calculatedUsageDao().deleteCalculatedUsage(it)
                Result.success(Unit)
            } ?: Result.failure(Exception("Calculated usage not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCalculatedUsageCount(): Result<Int> {
        return try {
            val count = database.calculatedUsageDao().getCalculatedUsageCount()
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun calculatedUsageExists(fromReadingId: String, toReadingId: String): Result<Boolean> {
        return try {
            val exists = database.calculatedUsageDao().calculatedUsageExists(
                fromReadingId = fromReadingId.toLong(),
                toReadingId = toReadingId.toLong()
            )
            Result.success(exists)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun getCalculatedUsageFlow(): Flow<List<CalculatedUsage>> {
        return database.calculatedUsageDao().getAllCalculatedUsage().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getCalculatedUsageByReadingId(readingId: String): Flow<List<CalculatedUsage>> {
        return database.calculatedUsageDao().getCalculatedUsageByReadingId(readingId.toLong()).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getCalculatedUsageByMinCost(minCost: Double): Flow<List<CalculatedUsage>> {
        return database.calculatedUsageDao().getCalculatedUsageByMinCost(minCost).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getCalculatedUsageByConsumption(): Flow<List<CalculatedUsage>> {
        return database.calculatedUsageDao().getCalculatedUsageByConsumption().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}