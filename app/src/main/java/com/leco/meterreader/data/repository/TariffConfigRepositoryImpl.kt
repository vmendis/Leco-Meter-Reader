package com.leco.meterreader.data.repository

import com.leco.meterreader.data.local.TariffConfigDao
import com.leco.meterreader.data.local.TariffConfigEntity
import com.leco.meterreader.domain.model.TariffConfig
import com.leco.meterreader.domain.repository.TariffConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of TariffConfigRepository.
 * Handles data operations for tariff configuration.
 */
class TariffConfigRepositoryImpl @Inject constructor(
    private val tariffConfigDao: TariffConfigDao
) : TariffConfigRepository {

    override fun getAllTariffs(): Flow<List<TariffConfig>> {
        return tariffConfigDao.getAllTariffs().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getActiveTariff(): TariffConfig? {
        return tariffConfigDao.getActiveTariff()?.toDomain()
    }

    override suspend fun getTariffById(id: Long): TariffConfig? {
        return tariffConfigDao.getTariffById(id)?.toDomain()
    }

    override suspend fun insertTariff(tariff: TariffConfig): Long {
        val entity = TariffConfigEntity.fromDomain(tariff)
        return tariffConfigDao.insertTariff(entity)
    }

    override suspend fun updateTariff(tariff: TariffConfig) {
        val entity = TariffConfigEntity.fromDomain(tariff)
        tariffConfigDao.updateTariff(entity)
    }

    override suspend fun deleteTariff(tariff: TariffConfig) {
        val entity = TariffConfigEntity.fromDomain(tariff)
        tariffConfigDao.deleteTariff(entity)
    }
}