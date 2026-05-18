package com.leco.meterreader.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room database for the LECO Meter Reader app.
 * Contains tables for meter readings, calculated usage, and tariff configuration.
 */
@Database(
    entities = [
        MeterReadingEntity::class,
        CalculatedUsageEntity::class,
        TariffConfigEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun meterReadingDao(): MeterReadingDao
    abstract fun calculatedUsageDao(): CalculatedUsageDao
    abstract fun tariffConfigDao(): TariffConfigDao
}