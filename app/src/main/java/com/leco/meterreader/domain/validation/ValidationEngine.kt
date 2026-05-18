package com.leco.meterreader.domain.validation

import com.leco.meterreader.domain.model.MeterReading
import javax.inject.Inject

/**
 * Validation engine that runs all validation rules on meter readings.
 */
class ValidationEngine @Inject constructor() {
    private val rules: List<ValidationRule> = listOf(
        NonDecreaseValidationRule(),
        SumCheckValidationRule(),
        DuplicateTimestampValidationRule(),
        SpikeDetectionValidationRule()
    )

    /**
     * Validate a new reading against the previous reading.
     * @param newReading The new reading to validate
     * @param previousReading The previous reading (if any)
     * @return List of validation results
     */
    fun validate(newReading: MeterReading, previousReading: MeterReading?): List<ValidationResult> {
        return rules.map { rule -> rule.validate(newReading, previousReading) }
    }

    /**
     * Check if a reading is valid (all rules pass).
     * @param newReading The new reading to validate
     * @param previousReading The previous reading (if any)
     * @return true if all validation rules pass
     */
    fun isValid(newReading: MeterReading, previousReading: MeterReading?): Boolean {
        return validate(newReading, previousReading).all { it.isValid }
    }

    /**
     * Get all error messages from validation.
     */
    fun getErrors(newReading: MeterReading, previousReading: MeterReading?): List<String> {
        return validate(newReading, previousReading)
            .filter { !it.isValid && it.errorMessage != null }
            .map { it.errorMessage!! }
    }

    /**
     * Get all warning messages from validation.
     */
    fun getWarnings(newReading: MeterReading, previousReading: MeterReading?): List<String> {
        return validate(newReading, previousReading)
            .filter { it.warningMessage != null }
            .map { it.warningMessage!! }
    }
}