package com.leco.meterreader.domain.validation

import com.leco.meterreader.domain.model.MeterReading
import kotlin.math.abs

/**
 * Validation rule that checks if the total delta equals the sum of Rate1 + Rate2 + Rate3 deltas.
 * Allows for small rounding differences (within 0.01 kWh).
 */
class SumCheckValidationRule(
    private val tolerance: Double = 0.01
) : ValidationRule {
    override fun validate(newReading: MeterReading, previousReading: MeterReading?): ValidationResult {
        if (previousReading == null) {
            return ValidationResult(isValid = true)
        }

        val totalDelta = newReading.totalReading - previousReading.totalReading
        val sumDelta = (newReading.rate1Day - previousReading.rate1Day) +
                (newReading.rate2OffPeak - previousReading.rate2OffPeak) +
                (newReading.rate3Peak - previousReading.rate3Peak)

        return if (abs(totalDelta - sumDelta) > tolerance) {
            ValidationResult(
                isValid = false,
                errorMessage = "Total reading delta (${"%.2f".format(totalDelta)} kWh) does not match sum of rate deltas (${"%.2f".format(sumDelta)} kWh)"
            )
        } else {
            ValidationResult(isValid = true)
        }
    }
}