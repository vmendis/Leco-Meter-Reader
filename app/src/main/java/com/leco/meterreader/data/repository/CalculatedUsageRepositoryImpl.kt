package com.leco.meterreader.data.repository

import com.leco.meterreader.data.local.CalculatedUsageDao
import com.leco.meterreader.data.local.CalculatedUsageEntity
import com.leco.meterreader.domain.model.CalculatedUsage
import com.leco.meterreader.domain.repository.CalculatedUsageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of CalculatedUsageRepository.
 * Handles data operations for calculated usage.
 */
class CalculatedUsageRepositoryImpl @Inject constructor(
    private val calculatedUsageDao: CalculatedUsageDao
) : CalculatedUsageRepository {

    override fun getAllCalculatedUsage(): Flow<List<CalculatedUsage>> {
        return calculatedUsageDao.getAllCalculatedUsage().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getCalculatedUsageById(id: Long): CalculatedUsage? {
        return calculatedUsageDao.getCalculatedUsageById(id)?.toDomain()
    }

    override suspend fun getCalculatedUsageByFromReading(fromReadingId: Long): CalculatedUsage? {
        return calculatedUsageDao.getCalculatedUsageByFromReading(fromReadingId)?.toDomain()
    }

    override suspend fun getCalculatedUsageByToReading(toReadingId: Long): CalculatedUsage? {
        return calculatedUsageDao.getCalculatedUsageByToReading(toReadingId)?.toDomain()
    }

    override suspend fun insertCalculatedUsage(usage: CalculatedUsage): Long {
        val entity = CalculatedUsageEntity.fromDomain(usage)
        return calculatedUsageDao.insertCalculatedUsage(entity)
    }

    override suspend fun updateCalculatedUsage(usage: CalculatedUsage) {
        val entity = CalculatedUsageEntity.fromDomain(usage)
        calculatedUsageDao.updateCalculatedUsage(entity)
    }

    override suspend fun deleteCalculatedUsage(usage: CalculatedUsage) {
        val entity = CalculatedUsageEntity.fromDomain(usage)
        calculatedUsageDao.deleteCalculatedUsage(entity)
    }

    override suspend fun deleteAllCalculatedUsage() {
        calculatedUsageDao.deleteAllCalculatedUsage()
    }
}