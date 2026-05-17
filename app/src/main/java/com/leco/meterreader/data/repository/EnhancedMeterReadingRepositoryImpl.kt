package com.leco.meterreader.data.repository

import com.leco.meterreader.data.local.dao.EnhancedMeterReadingDao
import com.leco.meterreader.data.local.entity.EnhancedMeterReadingEntity
import com.leco.meterreader.data.model.EnhancedMeterReading
import com.leco.meterreader.data.model.EnhancedMeterReading.Companion.fromMap
import com.leco.meterreader.data.model.EnhancedMeterReading.ReadingStatus
import com.leco.meterreader.data.model.EnhancedMeterReading.EmptyReadingReason
import com.leco.meterreader.domain.repository.EnhancedMeterReadingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Enhanced repository implementation for meter reading operations
 * Handles empty readings, estimated readings, and various reading statuses
 */
class EnhancedMeterReadingRepositoryImpl(
    private val enhancedMeterReadingDao: EnhancedMeterReadingDao
) : EnhancedMeterReadingRepository {
    
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    override suspend fun saveEnhancedReading(reading: EnhancedMeterReading): Result<EnhancedMeterReading> {
        return try {
            val entity = EnhancedMeterReadingEntity.fromDomain(reading)
            val id = enhancedMeterReadingDao.insertEnhancedReading(entity)
            val savedEntity = enhancedMeterReadingDao.getLatestEnhancedReading().first()
            Result.success(savedEntity?.toDomain() ?: reading.copy(id = id.toString()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun saveEnhancedReadings(readings: List<EnhancedMeterReading>): Result<List<EnhancedMeterReading>> {
        return try {
            val entities = readings.map { EnhancedMeterReadingEntity.fromDomain(it) }
            val ids = enhancedMeterReadingDao.insertEnhancedReadings(entities)
            Result.success(readings.mapIndexed { index, reading ->
                reading.copy(id = ids[index].toString())
            })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getLatestEnhancedReading(): Result<EnhancedMeterReading?> {
        return try {
            val entity = enhancedMeterReadingDao.getLatestEnhancedReading().first()
            Result.success(entity?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getAllEnhancedReadings(): Result<List<EnhancedMeterReading>> {
        return try {
            val entities = enhancedMeterReadingDao.getAllEnhancedReadings().first()
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getEnhancedReadingsByStatus(status: ReadingStatus): Result<List<EnhancedMeterReading>> {
        return try {
            val entities = enhancedMeterReadingDao.getEnhancedReadingsByStatus(status.name).first()
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getEnhancedReadingsByDateRange(start: LocalDateTime, end: LocalDateTime): Result<List<EnhancedMeterReading>> {
        return try {
            val entities = enhancedMeterReadingDao.getEnhancedReadingsByDateRange(
                start.format(formatter),
                end.format(formatter)
            ).first()
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getEnhancedReadingsByMonth(year: Int, month: Int): Result<List<EnhancedMeterReading>> {
        return try {
            val monthYear = "${year}-${month.toString().padStart(2, '0')}"
            val entities = enhancedMeterReadingDao.getEnhancedReadingsByMonth(monthYear).first()
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCompleteReadings(): Result<List<EnhancedMeterReading>> {
        return try {
            val entities = enhancedMeterReadingDao.getCompleteReadings().first()
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getEmptyReadings(): Result<List<EnhancedMeterReading>> {
        return try {
            val entities = enhancedMeterReadingDao.getEmptyReadings().first()
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getEstimatedReadings(): Result<List<EnhancedMeterReading>> {
        return try {
            val entities = enhancedMeterReadingDao.getEstimatedReadings().first()
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPendingReadings(): Result<List<EnhancedMeterReading>> {
        return try {
            val entities = enhancedMeterReadingDao.getPendingReadings().first()
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getReadingsByEmptyReason(reason: EmptyReadingReason): Result<List<EnhancedMeterReading>> {
        return try {
            val entities = enhancedMeterReadingDao.getReadingsByEmptyReason(reason.name).first()
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getReadingsNeedingCompletion(): Result<List<EnhancedMeterReading>> {
        return try {
            val entities = enhancedMeterReadingDao.getReadingsNeedingCompletion().first()
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getReadingsWithCalculatedUsage(): Result<List<EnhancedMeterReading>> {
        return try {
            val entities = enhancedMeterReadingDao.getReadingsWithCalculatedUsage().first()
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getReadingsWithoutCalculatedUsage(): Result<List<EnhancedMeterReading>> {
        return try {
            val entities = enhancedMeterReadingDao.getReadingsWithoutCalculatedUsage().first()
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateEnhancedReading(reading: EnhancedMeterReading): Result<EnhancedMeterReading> {
        return try {
            val entity = EnhancedMeterReadingEntity.fromDomain(reading)
            enhancedMeterReadingDao.updateEnhancedReading(entity)
            val updatedEntity = enhancedMeterReadingDao.getLatestEnhancedReading().first()
            Result.success(updatedEntity?.toDomain() ?: reading)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteEnhancedReading(readingId: String): Result<Unit> {
        return try {
            val entity = enhancedMeterReadingDao.getAllEnhancedReadings().first()
                .find { it.id.toString() == readingId }
            entity?.let {
                enhancedMeterReadingDao.deleteEnhancedReading(it)
                Result.success(Unit)
            } ?: Result.failure(Exception("Reading not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getEnhancedReadingCount(): Result<Int> {
        return try {
            val count = enhancedMeterReadingDao.getEnhancedReadingCount()
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getReadingCountByStatus(): Result<Map<ReadingStatus, Int>> {
        return try {
            val counts = enhancedMeterReadingDao.getReadingCountByStatus()
            val result = counts.associate { 
                ReadingStatus.valueOf(it.status) to it.count 
            }
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getReadingCountByEmptyReason(): Result<Map<EmptyReadingReason, Int>> {
        return try {
            val counts = enhancedMeterReadingDao.getReadingCountByEmptyReason()
            val result = counts.associate { 
                EmptyReadingReason.valueOf(it.status) to it.count 
            }
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun enhancedReadingExists(timestamp: LocalDateTime): Result<Boolean> {
        return try {
            val exists = enhancedMeterReadingDao.enhancedReadingExists(timestamp.format(formatter))
            Result.success(exists)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getReadingStatsForDateRange(start: LocalDateTime, end: LocalDateTime): Result<EnhancedMeterReadingRepository.ReadingStats> {
        return try {
            val stats = enhancedMeterReadingDao.getReadingStatsForDateRange(
                start.format(formatter),
                end.format(formatter)
            )
            val completionRate = if (stats.totalReadings > 0) {
                (stats.completeReadings.toDouble() / stats.totalReadings) * 100
            } else 0.0
            
            Result.success(
                EnhancedMeterReadingRepository.ReadingStats(
                    totalReadings = stats.totalReadings,
                    completeReadings = stats.completeReadings,
                    emptyReadings = stats.emptyReadings,
                    estimatedReadings = stats.estimatedReadings,
                    pendingReadings = stats.pendingReadings,
                    completionRate = completionRate
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getLatestEmptyReading(): Result<EnhancedMeterReading?> {
        return try {
            val entity = enhancedMeterReadingDao.getLatestEmptyReading().first()
            Result.success(entity?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getEstimatedReadingsForConversion(): Result<List<EnhancedMeterReading>> {
        return try {
            val entities = enhancedMeterReadingDao.getEstimatedReadingsForConversion().first()
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun convertEstimatedToComplete(
        readingId: String,
        totalReading: Double,
        rate1Reading: Double,
        rate2Reading: Double,
        rate3Reading: Double,
        notes: String
    ): Result<EnhancedMeterReading> {
        return try {
            val entity = enhancedMeterReadingDao.getAllEnhancedReadings().first()
                .find { it.id.toString() == readingId }
            
            entity?.let {
                val updatedEntity = it.update(
                    status = "COMPLETED",
                    totalReading = totalReading,
                    rate1Reading = rate1Reading,
                    rate2Reading = rate2Reading,
                    rate3Reading = rate3Reading,
                    notes = notes,
                    emptyReason = null,
                    estimatedReading = false,
                    estimatedValue = null
                )
                enhancedMeterReadingDao.updateEnhancedReading(updatedEntity)
                val updatedReading = enhancedMeterReadingDao.getLatestEnhancedReading().first()
                Result.success(updatedReading?.toDomain() ?: throw Exception("Failed to retrieve updated reading"))
            } ?: Result.failure(Exception("Reading not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun markReadingAsEmpty(
        readingId: String,
        reason: EmptyReadingReason,
        notes: String
    ): Result<EnhancedMeterReading> {
        return try {
            val entity = enhancedMeterReadingDao.getAllEnhancedReadings().first()
                .find { it.id.toString() == readingId }
            
            entity?.let {
                val updatedEntity = it.update(
                    status = "EMPTY",
                    totalReading = null,
                    rate1Reading = null,
                    rate2Reading = null,
                    rate3Reading = null,
                    notes = notes,
                    emptyReason = reason.name,
                    estimatedReading = false,
                    estimatedValue = null
                )
                enhancedMeterReadingDao.updateEnhancedReading(updatedEntity)
                val updatedReading = enhancedMeterReadingDao.getLatestEnhancedReading().first()
                Result.success(updatedReading?.toDomain() ?: throw Exception("Failed to retrieve updated reading"))
            } ?: Result.failure(Exception("Reading not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun markReadingAsEstimated(
        readingId: String,
        estimatedValue: Double,
        notes: String
    ): Result<EnhancedMeterReading> {
        return try {
            val entity = enhancedMeterReadingDao.getAllEnhancedReadings().first()
                .find { it.id.toString() == readingId }
            
            entity?.let {
                val updatedEntity = it.update(
                    status = "ESTIMATED",
                    totalReading = null,
                    rate1Reading = null,
                    rate2Reading = null,
                    rate3Reading = null,
                    notes = notes,
                    emptyReason = null,
                    estimatedReading = true,
                    estimatedValue = estimatedValue
                )
                enhancedMeterReadingDao.updateEnhancedReading(updatedEntity)
                val updatedReading = enhancedMeterReadingDao.getLatestEnhancedReading().first()
                Result.success(updatedReading?.toDomain() ?: throw Exception("Failed to retrieve updated reading"))
            } ?: Result.failure(Exception("Reading not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getReadingsNeedingCalculation(): Result<List<EnhancedMeterReading>> {
        return try {
            val entities = enhancedMeterReadingDao.getReadingsWithoutCalculatedUsage().first()
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}