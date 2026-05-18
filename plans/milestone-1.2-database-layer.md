# Milestone 1.2: Database Layer Implementation Plan

## Overview

This plan details the implementation of the complete Room database layer for the LECO Meter Reader app.

## Current State

### Already Implemented
- `MeterReadingEntity` - with all 4 cumulative readings
- `TariffConfigEntity` - with configurable rates and effective dates
- `MeterReadingDao` - with Flow-based queries
- `TariffConfigDao` - with Flow-based queries
- `AppDatabase` - basic setup (version 1)
- `MeterReadingRepositoryImpl` - data access abstraction
- `TariffConfigRepositoryImpl` - tariff management
- `DatabaseModule` - DI setup
- `RepositoryModule` - repository bindings

## Components to Create

### 1. CalculatedUsage Domain Model
**File:** `app/src/main/java/com/leco/meterreader/domain/model/CalculatedUsage.kt`

Fields based on roadmap:
- `id: Long` - Primary key
- `fromReadingId: Long` - Reference to previous reading
- `toReadingId: Long` - Reference to current reading
- `totalUsed: Double` - Total kWh used
- `dayUsed: Double` - Day usage (rate1)
- `offPeakUsed: Double` - Off-peak usage (rate2)
- `peakUsed: Double` - Peak usage (rate3)
- `estimatedCost: Double` - Calculated cost
- `calculationTimestamp: Date` - When calculation was performed

### 2. CalculatedUsageEntity
**File:** `app/src/main/java/com/leco/meterreader/data/local/CalculatedUsageEntity.kt`

Room entity with:
- `@Entity(tableName = "calculated_usage")`
- Foreign key relationships to meter_readings
- `fromDomain()` and `toDomain()` mapping functions

### 3. Converters
**File:** `app/src/main/java/com/leco/meterreader/data/local/Converters.kt`

Type converters for:
- `Long` ↔ `Date` conversion
- Annotated with `@TypeConverter`

### 4. CalculatedUsageDao
**File:** `app/src/main/java/com/leco/meterreader/data/local/CalculatedUsageDao.kt`

Queries:
- `getAllCalculatedUsage(): Flow<List<CalculatedUsageEntity>>`
- `getCalculatedUsageByReadingId(readingId: Long): List<CalculatedUsageEntity>`
- `getLatestCalculatedUsage(): CalculatedUsageEntity?`
- `insertCalculatedUsage(usage: CalculatedUsageEntity): Long`
- `deleteCalculatedUsage(usage: CalculatedUsageEntity)`

### 5. AppDatabase Update
**File:** `app/src/main/java/com/leco/meterreader/data/local/AppDatabase.kt`

Changes:
- Add `CalculatedUsageEntity` to entities list
- Increment version to 2
- Add `calculatedUsageDao()` abstract method
- Add `TypeConverters` annotation

### 6. Migration
**File:** `app/src/main/java/com/leco/meterreader/data/local/Migration1To2.kt`

Migration from version 1 to 2:
- Create `calculated_usage` table
- Add foreign key constraints

### 7. CalculatedUsageRepository Interface
**File:** `app/src/main/java/com/leco/meterreader/domain/repository/CalculatedUsageRepository.kt`

Methods:
- `getAllCalculatedUsage(): Flow<List<CalculatedUsage>>`
- `getCalculatedUsageByReadingId(readingId: Long): List<CalculatedUsage>`
- `getLatestCalculatedUsage(): CalculatedUsage?`
- `insertCalculatedUsage(usage: CalculatedUsage): Long`
- `deleteCalculatedUsage(usage: CalculatedUsage)`

### 8. CalculatedUsageRepositoryImpl
**File:** `app/src/main/java/com/leco/meterreader/data/repository/CalculatedUsageRepositoryImpl.kt`

Implementation that:
- Maps between entity and domain models
- Uses DAO for data operations

### 9. DatabaseModule Update
**File:** `app/src/main/java/com/leco/meterreader/di/DatabaseModule.kt`

Changes:
- Add `calculatedUsageDao()` provider
- Add `TypeConverters` to database builder
- Replace `fallbackToDestructiveMigration()` with proper migrations

### 10. RepositoryModule Update
**File:** `app/src/main/java/com/leco/meterreader/di/RepositoryModule.kt`

Changes:
- Add `bindCalculatedUsageRepository()` binding

## File Structure

```
app/src/main/java/com/leco/meterreader/
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt (updated)
│   │   ├── Converters.kt (new)
│   │   ├── MeterReadingDao.kt
│   │   ├── MeterReadingEntity.kt
│   │   ├── TariffConfigDao.kt
│   │   ├── TariffConfigEntity.kt
│   │   ├── CalculatedUsageDao.kt (new)
│   │   └── CalculatedUsageEntity.kt (new)
│   └── repository/
│       ├── MeterReadingRepositoryImpl.kt
│       ├── TariffConfigRepositoryImpl.kt
│       └── CalculatedUsageRepositoryImpl.kt (new)
├── domain/
│   ├── model/
│   │   ├── MeterReading.kt
│   │   ├── TariffConfig.kt
│   │   └── CalculatedUsage.kt (new)
│   └── repository/
│       ├── MeterReadingRepository.kt
│       ├── TariffConfigRepository.kt
│       └── CalculatedUsageRepository.kt (new)
└── di/
    ├── DatabaseModule.kt (updated)
    └── RepositoryModule.kt (updated)
```

## Implementation Order

1. Create `CalculatedUsage` domain model
2. Create `Converters` for type conversion
3. Create `CalculatedUsageEntity`
4. Create `CalculatedUsageDao`
5. Update `AppDatabase` with migrations
6. Create `CalculatedUsageRepository` interface
7. Create `CalculatedUsageRepositoryImpl`
8. Update `DatabaseModule`
9. Update `RepositoryModule`

## Notes

- Using Long (milliseconds) for DateTime storage - consistent with existing entities
- Clean architecture: data layer entities are mapped to domain models in repositories
- All list queries return `Flow<List<T>>` for reactive UI updates
- Foreign key constraints ensure data integrity