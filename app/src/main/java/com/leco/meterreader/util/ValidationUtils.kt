package com.leco.meterreader.util

import com.leco.meterreader.data.model.MeterReading
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object ValidationUtils {
    
    // Error messages
    object Errors {
        const val INVALID_NUMBER_FORMAT = "Invalid number format"
        const val READING_DECREASED = "Reading cannot be less than previous reading"
        const val READING_REQUIRED = "Reading is required"
        const val READING_TOO_LARGE = "Reading value too large"
        const val READING_NEGATIVE = "Reading cannot be negative"
        const val INVALID_TIMESTAMP = "Invalid timestamp"
        const val TIMESTAMP_IN_FUTURE = "Timestamp cannot be in the future"
    }
    
    /**
     * Validates that a string can be parsed as a valid number
     */
    fun isValidNumber(input: String): Boolean {
        return try {
            input.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }
    
    /**
     * Validates that a number is within reasonable bounds for meter readings
     */
    fun isValidReadingValue(value: Double): Boolean {
        return value >= 0 && value <= 1_000_000 // Max 1 million kWh
    }
    
    /**
     * Validates that a reading doesn't decrease from the previous reading
     */
    fun validateReadingIncrease(currentReading: Double, previousReading: Double?): String? {
        if (previousReading == null) return null // No previous reading to compare against
        
        if (currentReading < previousReading) {
            return Errors.READING_DECREASED
        }
        return null
    }
    
    /**
     * Validates a complete meter reading
     */
    fun validateMeterReading(
        totalReading: String,
        rate1Reading: String,
        rate2Reading: String,
        rate3Reading: String,
        previousReading: MeterReading? = null
    ): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        
        // Validate total reading
        if (totalReading.isBlank()) {
            errors["totalReading"] = Errors.READING_REQUIRED
        } else if (!isValidNumber(totalReading)) {
            errors["totalReading"] = Errors.INVALID_NUMBER_FORMAT
        } else {
            val totalValue = totalReading.toDouble()
            if (!isValidReadingValue(totalValue)) {
                errors["totalReading"] = if (totalValue < 0) Errors.READING_NEGATIVE else Errors.READING_TOO_LARGE
            } else if (previousReading != null) {
                val increaseError = validateReadingIncrease(totalValue, previousReading.totalReading)
                if (increaseError != null) {
                    errors["totalReading"] = increaseError
                }
            }
        }
        
        // Validate rate readings (optional but must be valid if provided)
        listOf("rate1Reading" to rate1Reading, "rate2Reading" to rate2Reading, "rate3Reading" to rate3Reading)
            .forEach { (field, value) ->
                if (value.isNotBlank()) {
                    if (!isValidNumber(value)) {
                        errors[field] = Errors.INVALID_NUMBER_FORMAT
                    } else {
                        val rateValue = value.toDouble()
                        if (!isValidReadingValue(rateValue)) {
                            errors[field] = if (rateValue < 0) Errors.READING_NEGATIVE else Errors.READING_TOO_LARGE
                        }
                    }
                }
            }
        
        return errors
    }
    
    /**
     * Validates timestamp format and logic
     */
    fun validateTimestamp(timestamp: LocalDateTime, currentTime: LocalDateTime = LocalDateTime.now()): String? {
        // Check if timestamp is in the future
        if (timestamp.isAfter(currentTime)) {
            return Errors.TIMESTAMP_IN_FUTURE
        }
        return null
    }
    
    /**
     * Formats a number for display with proper decimal places
     */
    fun formatReadingValue(value: Double): String {
        return String.format("%.3f", value) // Show up to 3 decimal places
    }
    
    /**
     * Parses a timestamp string to LocalDateTime
     */
    fun parseTimestamp(timestampString: String): LocalDateTime? {
        return try {
            LocalDateTime.parse(timestampString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        } catch (e: DateTimeParseException) {
            null
        }
    }
    
    /**
     * Formats LocalDateTime to a readable string
     */
    fun formatTimestamp(timestamp: LocalDateTime): String {
        return timestamp.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
    }
    
    /**
     * Checks if there are any validation errors
     */
    fun hasErrors(errors: Map<String, String>): Boolean {
        return errors.isNotEmpty()
    }
    
    /**
     * Gets the first error message for display
     */
    fun getFirstError(errors: Map<String, String>): String? {
        return errors.values.firstOrNull()
    }
}