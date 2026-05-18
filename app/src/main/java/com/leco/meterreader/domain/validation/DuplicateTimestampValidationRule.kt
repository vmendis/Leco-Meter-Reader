package com.leco.meterreader.domain.validation

import com.leco.meterreader.domain.model.MeterReading

/**
 * Validation rule that detects duplicate timestamps.
 */
class DuplicateTimestampValidationRule : ValidationRule {
    override fun validate(newReading: MeterReading, previousReading: MeterReading?): ValidationResult {
        if (previousReading == null) {
            return ValidationResult(isValid = true)
        }

        return if (newReading.timestamp == previousReading.timestamp) {
            ValidationResult(
                isValid = false,
                errorMessage = "A reading already exists for this timestamp"
            )
        } else {
            ValidationResult(isValid = true)
        }
    }
}