package com.leco.meterreader.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leco.meterreader.data.model.MeterReading
import com.leco.meterreader.util.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ReadingViewModel : ViewModel() {
    
    // UI State
    var uiState by mutableStateOf(ReadingUiState())
        private set
    
    // Private state for business logic
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _lastSavedReading = MutableStateFlow<MeterReading?>(null)
    val lastSavedReading: StateFlow<MeterReading?> = _lastSavedReading
    
    // Repository would be injected here in a real app
    private val readingRepository = FakeReadingRepository()
    
    /**
     * Updates the total reading value
     */
    fun onTotalReadingInput(value: String) {
        uiState = uiState.copy(totalReading = value)
        validateInput("totalReading")
    }
    
    /**
     * Updates a rate reading value
     */
    fun onRateReadingInput(rateType: String, value: String) {
        val updatedRates = uiState.rateReadings.toMutableMap()
        updatedRates[rateType] = value
        uiState = uiState.copy(rateReadings = updatedRates)
        validateInput(rateType)
    }
    
    /**
     * Updates the timestamp
     */
    fun onTimestampChange(timestamp: LocalDateTime) {
        uiState = uiState.copy(timestamp = timestamp)
        validateInput("timestamp")
    }
    
    /**
     * Updates the notes
     */
    fun onNotesChange(notes: String) {
        uiState = uiState.copy(notes = notes)
    }
    
    /**
     * Adds a digit to the current input field
     */
    fun addDigit(digit: Char) {
        when (uiState.currentInputField) {
            "totalReading" -> {
                val newValue = uiState.totalReading + digit
                uiState = uiState.copy(totalReading = newValue)
                validateInput("totalReading")
            }
            "rate1Reading" -> {
                val updatedRates = uiState.rateReadings.toMutableMap()
                updatedRates["rate1Reading"] = updatedRates["rate1Reading"] + digit
                uiState = uiState.copy(rateReadings = updatedRates)
                validateInput("rate1Reading")
            }
            "rate2Reading" -> {
                val updatedRates = uiState.rateReadings.toMutableMap()
                updatedRates["rate2Reading"] = updatedRates["rate2Reading"] + digit
                uiState = uiState.copy(rateReadings = updatedRates)
                validateInput("rate2Reading")
            }
            "rate3Reading" -> {
                val updatedRates = uiState.rateReadings.toMutableMap()
                updatedRates["rate3Reading"] = updatedRates["rate3Reading"] + digit
                uiState = uiState.copy(rateReadings = updatedRates)
                validateInput("rate3Reading")
            }
        }
    }
    
    /**
     * Removes the last digit from the current input field
     */
    fun removeLastDigit() {
        when (uiState.currentInputField) {
            "totalReading" -> {
                val newValue = if (uiState.totalReading.length > 1) {
                    uiState.totalReading.dropLast(1)
                } else ""
                uiState = uiState.copy(totalReading = newValue)
                validateInput("totalReading")
            }
            "rate1Reading" -> {
                val updatedRates = uiState.rateReadings.toMutableMap()
                val currentValue = updatedRates["rate1Reading"] ?: ""
                updatedRates["rate1Reading"] = if (currentValue.length > 1) {
                    currentValue.dropLast(1)
                } else ""
                uiState = uiState.copy(rateReadings = updatedRates)
                validateInput("rate1Reading")
            }
            "rate2Reading" -> {
                val updatedRates = uiState.rateReadings.toMutableMap()
                val currentValue = updatedRates["rate2Reading"] ?: ""
                updatedRates["rate2Reading"] = if (currentValue.length > 1) {
                    currentValue.dropLast(1)
                } else ""
                uiState = uiState.copy(rateReadings = updatedRates)
                validateInput("rate2Reading")
            }
            "rate3Reading" -> {
                val updatedRates = uiState.rateReadings.toMutableMap()
                val currentValue = updatedRates["rate3Reading"] ?: ""
                updatedRates["rate3Reading"] = if (currentValue.length > 1) {
                    currentValue.dropLast(1)
                } else ""
                uiState = uiState.copy(rateReadings = updatedRates)
                validateInput("rate3Reading")
            }
        }
    }
    
    /**
     * Clears the current input field
     */
    fun clearCurrentField() {
        when (uiState.currentInputField) {
            "totalReading" -> {
                uiState = uiState.copy(totalReading = "")
                validateInput("totalReading")
            }
            "rate1Reading" -> {
                val updatedRates = uiState.rateReadings.toMutableMap()
                updatedRates["rate1Reading"] = ""
                uiState = uiState.copy(rateReadings = updatedRates)
                validateInput("rate1Reading")
            }
            "rate2Reading" -> {
                val updatedRates = uiState.rateReadings.toMutableMap()
                updatedRates["rate2Reading"] = ""
                uiState = uiState.copy(rateReadings = updatedRates)
                validateInput("rate2Reading")
            }
            "rate3Reading" -> {
                val updatedRates = uiState.rateReadings.toMutableMap()
                updatedRates["rate3Reading"] = ""
                uiState = uiState.copy(rateReadings = updatedRates)
                validateInput("rate3Reading")
            }
        }
    }
    
    /**
     * Sets the current input field for keypad input
     */
    fun setCurrentInputField(field: String) {
        uiState = uiState.copy(currentInputField = field)
    }
    
    /**
     * Validates a specific input field
     */
    private fun validateInput(field: String) {
        val errors = mutableMapOf<String, String>()
        
        when (field) {
            "totalReading" -> {
                if (uiState.totalReading.isBlank()) {
                    errors["totalReading"] = "Reading is required"
                } else if (!ValidationUtils.isValidNumber(uiState.totalReading)) {
                    errors["totalReading"] = "Invalid number format"
                } else {
                    val value = uiState.totalReading.toDouble()
                    if (!ValidationUtils.isValidReadingValue(value)) {
                        errors["totalReading"] = if (value < 0) "Reading cannot be negative" else "Reading value too large"
                    } else if (_lastSavedReading.value != null) {
                        val increaseError = ValidationUtils.validateReadingIncrease(
                            value, 
                            _lastSavedReading.value!!.totalReading
                        )
                        if (increaseError != null) {
                            errors["totalReading"] = increaseError
                        }
                    }
                }
            }
            "rate1Reading", "rate2Reading", "rate3Reading" -> {
                val value = uiState.rateReadings[field] ?: ""
                if (value.isNotBlank()) {
                    if (!ValidationUtils.isValidNumber(value)) {
                        errors[field] = "Invalid number format"
                    } else {
                        val numValue = value.toDouble()
                        if (!ValidationUtils.isValidReadingValue(numValue)) {
                            errors[field] = if (numValue < 0) "Reading cannot be negative" else "Reading value too large"
                        }
                    }
                }
            }
            "timestamp" -> {
                val timestampError = ValidationUtils.validateTimestamp(uiState.timestamp)
                if (timestampError != null) {
                    errors["timestamp"] = timestampError
                }
            }
        }
        
        uiState = uiState.copy(errors = errors)
    }
    
    /**
     * Validates all inputs
     */
    fun validateAllInputs(): Boolean {
        val totalReading = uiState.totalReading
        val rate1Reading = uiState.rateReadings["rate1Reading"] ?: ""
        val rate2Reading = uiState.rateReadings["rate2Reading"] ?: ""
        val rate3Reading = uiState.rateReadings["rate3Reading"] ?: ""
        
        val errors = ValidationUtils.validateMeterReading(
            totalReading,
            rate1Reading,
            rate2Reading,
            rate3Reading,
            _lastSavedReading.value
        )
        
        uiState = uiState.copy(errors = errors)
        return ValidationUtils.hasErrors(errors)
    }
    
    /**
     * Saves the meter reading
     */
    fun saveReading(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (!validateAllInputs()) {
            onError("Please fix validation errors before saving")
            return
        }
        
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val reading = MeterReading(
                    timestamp = uiState.timestamp,
                    totalReading = uiState.totalReading.toDouble(),
                    rate1Reading = uiState.rateReadings["rate1Reading"]?.toDouble() ?: 0.0,
                    rate2Reading = uiState.rateReadings["rate2Reading"]?.toDouble() ?: 0.0,
                    rate3Reading = uiState.rateReadings["rate3Reading"]?.toDouble() ?: 0.0,
                    notes = uiState.notes
                )
                
                // Save to repository (fake for now)
                readingRepository.saveReading(reading)
                
                _lastSavedReading.value = reading
                _isLoading.value = false
                onSuccess()
                
                // Reset form after successful save
                resetForm()
                
            } catch (e: Exception) {
                _isLoading.value = false
                onError("Failed to save reading: ${e.message}")
            }
        }
    }
    
    /**
     * Cancels the current reading entry
     */
    fun cancelReading() {
        resetForm()
    }
    
    /**
     * Resets the form to initial state
     */
    private fun resetForm() {
        uiState = ReadingUiState(
            timestamp = LocalDateTime.now(),
            currentInputField = "totalReading"
        )
    }
    
    /**
     * Loads the latest reading for comparison
     */
    fun loadLatestReading() {
        viewModelScope.launch {
            try {
                _lastSavedReading.value = readingRepository.getLatestReading()
            } catch (e: Exception) {
                // Ignore error, just means no previous reading exists
            }
        }
    }
    
    /**
     * Formats timestamp for display
     */
    fun formatTimestampForDisplay(): String {
        return ValidationUtils.formatTimestamp(uiState.timestamp)
    }
}

/**
 * UI State for the reading screen
 */
data class ReadingUiState(
    val totalReading: String = "",
    val rateReadings: Map<String, String> = mapOf(
        "rate1Reading" to "",
        "rate2Reading" to "",
        "rate3Reading" to ""
    ),
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val notes: String = "",
    val errors: Map<String, String> = emptyMap(),
    val currentInputField: String = "totalReading" // Which field is currently being edited
)

/**
 * Fake repository for demonstration
 */
private class FakeReadingRepository {
    private val readings = mutableListOf<MeterReading>()
    
    suspend fun saveReading(reading: MeterReading): MeterReading {
        readings.add(reading)
        return reading
    }
    
    suspend fun getLatestReading(): MeterReading? {
        return readings.maxByOrNull { it.timestamp }
    }
}