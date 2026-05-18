package com.leco.meterreader.domain.validation

import com.leco.meterreader.domain.model.MeterReading

/**
 * Validation rule that ensures readings never decrease.
 * New readings must be >= previous readings for all values.
 */
class NonDecreaseValidationRule : ValidationRule {
    override fun validate(newReading: MeterReading, previousReading: MeterReading?): ValidationResult {
        if (previousReading == null) {
            return ValidationResult(isValid = true)
        }

        return when {
            newReading.totalReading < previousReading.totalReading -> 
                ValidationResult(isValid = false, errorMessage = "Total reading cannot decrease")
            newReading.rate1Day < previousReading.rate1Day -> 
                ValidationResult(isValid = false, errorMessage = "Rate 1 (Day) reading cannot decrease")
            newReading.rate2OffPeak < previousReading.rate2OffPeak -> 
                ValidationResult(isValid = false, errorMessage = "Rate 2 (Off-Peak) reading cannot decrease")
            newReading.rate3Peak < previousReading.rate3Peak -> 
                ValidationResult(isValid = false, errorMessage = "Rate 3 (Peak) reading cannot decrease")
            else -> ValidationResult(isValid = true)
        }
    }
}