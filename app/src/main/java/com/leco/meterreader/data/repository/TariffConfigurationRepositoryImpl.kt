package com.leco.meterreader.data.repository

import com.leco.meterreader.data.local.AppDatabase
import com.leco.meterreader.data.local.entity.TariffConfigurationEntity
import com.leco.meterreader.data.model.TariffConfiguration
import com.leco.meterreader.domain.repository.TariffConfigurationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Implementation of TariffConfigurationRepository
 * Provides concrete implementation of repository interface using Room database
 */
class TariffConfigurationRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : TariffConfigurationRepository {
    
    override suspend fun saveTariffConfiguration(tariff: TariffConfiguration): Result<TariffConfiguration> {
        return try {
            val entity = TariffConfigurationEntity.fromDomain(tariff)
            val id = database.tariffConfigurationDao().insertTariffConfiguration(entity)
            Result.success(tariff.copy(id = id.toString()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getLatestTariffConfiguration(): Result<TariffConfiguration?> {
        return try {
            val entity = database.tariffConfigurationDao().getLatestTariffConfiguration()
            Result.success(entity?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getAllTariffConfigurations(): Result<List<TariffConfiguration>> {
        return try {
            val entities = database.tariffConfigurationDao().getAllTariffConfigurations()
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTariffConfigurationById(id: String): Result<TariffConfiguration?> {
        return try {
            val entity = database.tariffConfigurationDao().getTariffConfigurationById(id.toLong())
            Result.success(entity?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getActiveTariffConfiguration(): Result<TariffConfiguration?> {
        return try {
            val entity = database.tariffConfigurationDao().getActiveTariffConfiguration()
            Result.success(entity?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTariffConfigurationsAtDate(date: LocalDateTime): Result<List<TariffConfiguration>> {
        return try {
            val entities = database.tariffConfigurationDao().getTariffConfigurationsAtDate(date.toString())
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTariffConfigurationsByDateRange(start: LocalDateTime, end: LocalDateTime): Result<List<TariffConfiguration>> {
        return try {
            val entities = database.tariffConfigurationDao().getTariffConfigurationsByDateRange(
                start = start.toString(),
                end = end.toString()
            )
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTariffConfigurationsByYear(year: Int): Result<List<TariffConfiguration>> {
        return try {
            val entities = database.tariffConfigurationDao().getTariffConfigurationsByYear(year.toString())
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteTariffConfiguration(id: String): Result<Unit> {
        return try {
            val entity = database.tariffConfigurationDao().getTariffConfigurationById(id.toLong())
            entity?.let {
                database.tariffConfigurationDao().deleteTariffConfiguration(it)
                Result.success(Unit)
            } ?: Result.failure(Exception("Tariff configuration not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTariffConfigurationCount(): Result<Int> {
        return try {
            val count = database.tariffConfigurationDao().getTariffConfigurationCount()
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getActiveTariffConfigurationCount(): Result<Int> {
        return try {
            val count = database.tariffConfigurationDao().getActiveTariffConfigurationCount()
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun tariffConfigurationExists(effectiveDate: LocalDateTime): Result<Boolean> {
        return try {
            val exists = database.tariffConfigurationDao().tariffConfigurationExists(effectiveDate.toString())
            Result.success(exists)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun getTariffConfigurationFlow(): Flow<List<TariffConfiguration>> {
        return database.tariffConfigurationDao().getAllTariffConfigurations().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getActiveTariffConfigurationFlow(): Flow<List<TariffConfiguration>> {
        return database.tariffConfigurationDao().getActiveTariffConfigurations().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getTariffConfigurationsByYearFlow(year: Int): Flow<List<TariffConfiguration>> {
        return database.tariffConfigurationDao().getTariffConfigurationsByYear(year.toString()).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getTariffConfigurationsByDescriptionFlow(description: String): Flow<List<TariffConfiguration>> {
        return database.tariffConfigurationDao().getTariffConfigurationsByDescription(description).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getTariffConfigurationsByMinPeakRateFlow(minPeakRate: Double): Flow<List<TariffConfiguration>> {
        return database.tariffConfigurationDao().getTariffConfigurationsByMinPeakRate(minPeakRate).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getTariffConfigurationsByDayRateFlow(): Flow<List<TariffConfiguration>> {
        return database.tariffConfigurationDao().getTariffConfigurationsByDayRate().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}