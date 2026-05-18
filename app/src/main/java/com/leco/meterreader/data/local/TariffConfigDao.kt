package com.leco.meterreader.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for tariff configuration.
 */
@Dao
interface TariffConfigDao {
    @Query("SELECT * FROM tariff_config ORDER BY effectiveDate DESC")
    fun getAllTariffs(): Flow<List<TariffConfigEntity>>

    @Query("SELECT * FROM tariff_config WHERE id = :id")
    suspend fun getTariffById(id: Long): TariffConfigEntity?

    @Query("SELECT * FROM tariff_config ORDER BY effectiveDate DESC LIMIT 1")
    suspend fun getActiveTariff(): TariffConfigEntity?

    @Insert
    suspend fun insertTariff(tariff: TariffConfigEntity): Long

    @Update
    suspend fun updateTariff(tariff: TariffConfigEntity)

    @Delete
    suspend fun deleteTariff(tariff: TariffConfigEntity)
}