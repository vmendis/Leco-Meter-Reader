package com.leco.meterreader.domain.validation

import com.leco.meterreader.domain.model.MeterReading

/**
 * Validation rule that detects abnormal usage spikes.
 * Warns if daily usage exceeds a threshold (default 100 kWh).
 */
class SpikeDetectionValidationRule(
    private val dailyUsageThreshold: Double = 100.0
) : ValidationRule {
    override fun validate(newReading: MeterReading, previousReading: MeterReading?): ValidationResult {
        if (previousReading == null) {
            return ValidationResult(isValid = true)
        }

        val totalDelta = newReading.totalReading - previousReading.totalReading
        
        return if (totalDelta > dailyUsageThreshold) {
            ValidationResult(
                isValid = true,
                warningMessage = "Unusually high daily usage detected (${"%.1f".format(totalDelta)} kWh). Please verify the reading."
            )
        } else {
            ValidationResult(isValid = true)
        }
    }
}