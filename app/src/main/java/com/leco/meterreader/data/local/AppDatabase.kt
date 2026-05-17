package com.leco.meterreader.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.leco.meterreader.data.local.dao.MeterReadingDao
import com.leco.meterreader.data.local.dao.CalculatedUsageDao
import com.leco.meterreader.data.local.dao.TariffConfigurationDao
import com.leco.meterreader.data.local.dao.EnhancedMeterReadingDao
import com.leco.meterreader.data.local.entity.MeterReadingEntity
import com.leco.meterreader.data.local.entity.CalculatedUsageEntity
import com.leco.meterreader.data.local.entity.TariffConfigurationEntity
import com.leco.meterreader.data.local.entity.EnhancedMeterReadingEntity

/**
 * Room database for the LECO Solar Meter Analyzer app
 * Contains meter readings, calculated usage, tariff configuration, and enhanced meter reading tables
 */
@Database(
    entities = [
        MeterReadingEntity::class,
        CalculatedUsageEntity::class,
        TariffConfigurationEntity::class,
        EnhancedMeterReadingEntity::class
    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Get the MeterReadingDao for accessing meter reading data
     */
    abstract fun meterReadingDao(): MeterReadingDao
    
    /**
     * Get the CalculatedUsageDao for accessing calculated usage data
     */
    abstract fun calculatedUsageDao(): CalculatedUsageDao
    
    /**
     * Get the TariffConfigurationDao for accessing tariff configuration data
     */
    abstract fun tariffConfigurationDao(): TariffConfigurationDao
    
    /**
     * Get the EnhancedMeterReadingDao for accessing enhanced meter reading data
     */
    abstract fun enhancedMeterReadingDao(): EnhancedMeterReadingDao
    
    companion object {
        /**
         * Database name constant
         */
        const val DATABASE_NAME = "leco_meter_reader_db"
    }
}