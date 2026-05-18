package com.leco.meterreader.di

import com.leco.meterreader.data.local.CalculatedUsageDao
import com.leco.meterreader.data.local.MeterReadingDao
import com.leco.meterreader.data.local.TariffConfigDao
import com.leco.meterreader.data.repository.CalculatedUsageRepositoryImpl
import com.leco.meterreader.data.repository.MeterReadingRepositoryImpl
import com.leco.meterreader.data.repository.TariffConfigRepositoryImpl
import com.leco.meterreader.domain.repository.CalculatedUsageRepository
import com.leco.meterreader.domain.repository.MeterReadingRepository
import com.leco.meterreader.domain.repository.TariffConfigRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for repository bindings.
 * Binds repository implementations to their interfaces.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMeterReadingRepository(
        impl: MeterReadingRepositoryImpl
    ): MeterReadingRepository

    @Binds
    @Singleton
    abstract fun bindCalculatedUsageRepository(
        impl: CalculatedUsageRepositoryImpl
    ): CalculatedUsageRepository

    @Binds
    @Singleton
    abstract fun bindTariffConfigRepository(
        impl: TariffConfigRepositoryImpl
    ): TariffConfigRepository
}