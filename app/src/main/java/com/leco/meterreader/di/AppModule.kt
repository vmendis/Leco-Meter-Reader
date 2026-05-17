package com.leco.meterreader.di

import android.content.Context
import androidx.room.Room
import com.leco.meterreader.data.local.AppDatabase
import com.leco.meterreader.data.repository.MeterReadingRepositoryImpl
import com.leco.meterreader.data.repository.CalculatedUsageRepositoryImpl
import com.leco.meterreader.data.repository.TariffConfigurationRepositoryImpl
import com.leco.meterreader.data.repository.EnhancedMeterReadingRepositoryImpl
import com.leco.meterreader.domain.repository.MeterReadingRepository
import com.leco.meterreader.domain.repository.CalculatedUsageRepository
import com.leco.meterreader.domain.repository.TariffConfigurationRepository
import com.leco.meterreader.domain.repository.EnhancedMeterReadingRepository
import com.leco.meterreader.domain.repository.SolarPlanningRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing application-level dependencies
 * Provides database, repositories, and other singletons
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    /**
     * Provides the Room database instance
     */
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }
    
    /**
     * Provides the MeterReadingRepository implementation
     */
    @Provides
    @Singleton
    fun provideMeterReadingRepository(
        database: AppDatabase
    ): MeterReadingRepository {
        return MeterReadingRepositoryImpl(database)
    }
    
    /**
     * Provides the SolarPlanningRepository implementation
     * TODO: Implement actual repository when solar planning data models are ready
     */
    @Provides
    @Singleton
    fun provideSolarPlanningRepository(): SolarPlanningRepository {
        // Placeholder implementation - will be replaced with actual implementation
        return object : SolarPlanningRepository {
            override suspend fun saveSolarPlanningData(data: com.leco.meterreader.data.model.SolarPlanningData): Result<com.leco.meterreader.data.model.SolarPlanningData> {
                return Result.success(data)
            }
            
            override suspend fun getLatestSolarPlanningData(): Result<com.leco.meterreader.data.model.SolarPlanningData?> {
                return Result.success(null)
            }
            
            override suspend fun getAllSolarPlanningData(): Result<List<com.leco.meterreader.data.model.SolarPlanningData>> {
                return Result.success(emptyList())
            }
            
            override suspend fun getSolarPlanningDataById(id: String): Result<com.leco.meterreader.data.model.SolarPlanningData?> {
                return Result.success(null)
            }
            
            override suspend fun deleteSolarPlanningData(id: String): Result<Unit> {
                return Result.success(Unit)
            }
            
            override suspend fun getSolarPlanningDataCount(): Result<Int> {
                return Result.success(0)
            }
            
            override suspend fun solarPlanningDataExists(dailyConsumption: Double): Result<Boolean> {
                return Result.success(false)
            }
            
            /**
             * Provides the CalculatedUsageRepository implementation
             */
            @Provides
            @Singleton
            fun provideCalculatedUsageRepository(
                database: AppDatabase
            ): CalculatedUsageRepository {
                return CalculatedUsageRepositoryImpl(database)
            }
            
            /**
             * Provides the TariffConfigurationRepository implementation
             */
            @Provides
            @Singleton
            fun provideTariffConfigurationRepository(
                database: AppDatabase
            ): TariffConfigurationRepository {
                return TariffConfigurationRepositoryImpl(database)
            }
            
            /**
             * Provides the EnhancedMeterReadingRepository implementation
             */
            @Provides
            @Singleton
            fun provideEnhancedMeterReadingRepository(
                database: AppDatabase
            ): EnhancedMeterReadingRepository {
                return EnhancedMeterReadingRepositoryImpl(database.enhancedMeterReadingDao())
            }
        }
    }
}