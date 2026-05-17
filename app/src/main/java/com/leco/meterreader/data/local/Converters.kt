package com.leco.meterreader.data.local

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Type converters for Room database
 * Handles conversion between Kotlin types and database storage types
 */
class Converters {
    
    /**
     * DateTime formatter for LocalDateTime
     */
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    /**
     * Convert LocalDateTime to String for database storage
     */
    @TypeConverter
    fun fromLocalDateTime(date: LocalDateTime): String {
        return date.format(formatter)
    }
    
    /**
     * Convert String from database to LocalDateTime
     */
    @TypeConverter
    fun toLocalDateTime(dateString: String): LocalDateTime {
        return LocalDateTime.parse(dateString, formatter)
    }
}