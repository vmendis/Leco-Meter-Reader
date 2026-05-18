package com.leco.meterreader.domain.validation

import com.leco.meterreader.domain.model.MeterReading

/**
 * Interface for validation rules.
 */
interface ValidationRule {
    /**
     * Validate the new reading against the previous reading.
     * @param newReading The new reading to validate
     * @param previousReading The previous reading (if any)
     * @return ValidationResult indicating if the reading is valid
     */
    fun validate(newReading: MeterReading, previousReading: MeterReading?): ValidationResult
}