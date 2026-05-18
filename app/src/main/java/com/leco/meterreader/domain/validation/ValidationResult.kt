package com.leco.meterreader.domain.validation

/**
 * Result of a validation check.
 */
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null,
    val warningMessage: String? = null
)