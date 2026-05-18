package com.leco.meterreader.di

import android.content.Context
import androidx.room.Room
import com.leco.meterreader.data.local.AppDatabase
import com.leco.meterreader.data.local.CalculatedUsageDao
import com.leco.meterreader.data.local.MeterReadingDao
import com.leco.meterreader.data.local.TariffConfigDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "meter_reader_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideMeterReadingDao(database: AppDatabase): MeterReadingDao {
        return database.meterReadingDao()
    }

    @Provides
    fun provideCalculatedUsageDao(database: AppDatabase): CalculatedUsageDao {
        return database.calculatedUsageDao()
    }

    @Provides
    fun provideTariffConfigDao(database: AppDatabase): TariffConfigDao {
        return database.tariffConfigDao()
    }
}