package com.leco.meterreader.data.repository

import com.leco.meterreader.data.local.MeterReadingDao
import com.leco.meterreader.data.local.MeterReadingEntity
import com.leco.meterreader.domain.model.MeterReading
import com.leco.meterreader.domain.repository.MeterReadingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of MeterReadingRepository.
 * Handles data operations for meter readings.
 */
class MeterReadingRepositoryImpl @Inject constructor(
    private val meterReadingDao: MeterReadingDao
) : MeterReadingRepository {

    override fun getAllReadings(): Flow<List<MeterReading>> {
        return meterReadingDao.getAllReadings().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getReadingById(id: Long): MeterReading? {
        return meterReadingDao.getReadingById(id)?.toDomain()
    }

    override suspend fun getLatestReading(): MeterReading? {
        return meterReadingDao.getLatestReading()?.toDomain()
    }

    override suspend fun insertReading(reading: MeterReading): Long {
        val entity = MeterReadingEntity.fromDomain(reading)
        return meterReadingDao.insertReading(entity)
    }

    override suspend fun updateReading(reading: MeterReading) {
        val entity = MeterReadingEntity.fromDomain(reading)
        meterReadingDao.updateReading(entity)
    }

    override suspend fun deleteReading(reading: MeterReading) {
        val entity = MeterReadingEntity.fromDomain(reading)
        meterReadingDao.deleteReading(entity)
    }

    override suspend fun deleteAllReadings() {
        meterReadingDao.deleteAllReadings()
    }
}