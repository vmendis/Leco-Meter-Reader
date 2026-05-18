package com.leco.meterreader.ui.screens.reading

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leco.meterreader.domain.model.MeterReading
import com.leco.meterreader.domain.repository.MeterReadingRepository
import com.leco.meterreader.domain.validation.ValidationEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * ViewModel for the Add Reading screen.
 * Manages form state and validation for meter reading entry.
 */
@HiltViewModel
class AddReadingViewModel @Inject constructor(
    private val repository: MeterReadingRepository,
    private val validationEngine: ValidationEngine
) : ViewModel() {

    // Form state
    private val _uiState = MutableStateFlow(AddReadingUiState())
    val uiState: StateFlow<AddReadingUiState> = _uiState

    // Current active field for keypad input
    private val _activeField = MutableStateFlow<ReadingField?>(null)
    val activeField: StateFlow<ReadingField?> = _activeField

    // Validation errors
    private val _validationErrors = MutableStateFlow<List<String>>(emptyList())
    val validationErrors: StateFlow<List<String>> = _validationErrors

    // Validation warnings
    private val _validationWarnings = MutableStateFlow<List<String>>(emptyList())
    val validationWarnings: StateFlow<List<String>> = _validationWarnings

    // Save result
    private val _saveResult = MutableStateFlow<SaveResult?>(null)
    val saveResult: StateFlow<SaveResult?> = _saveResult

    init {
        // Load the latest reading for validation
        viewModelScope.launch {
            repository.getLatestReading()?.let { latest ->
                _uiState.value = _uiState.value.copy(previousReading = latest)
            }
        }
    }

    /**
     * Set the active field for keypad input.
     */
    fun setActiveField(field: ReadingField?) {
        Log.d("AddReadingViewModel", "setActiveField called with: $field, current: ${_activeField.value}")
        _activeField.value = field
        Log.d("AddReadingViewModel", "activeField after update: ${_activeField.value}")
    }

    /**
     * Handle number input from the keypad.
     */
    fun onNumberInput(number: String) {
        val currentField = _activeField.value ?: return
        val currentValue = getCurrentFieldValue(currentField)
        val newValue = appendToValue(currentValue, number)
        updateFieldValue(currentField, newValue)
    }

    /**
     * Handle clear/backspace input.
     */
    fun onClearInput() {
        val currentField = _activeField.value ?: return
        val currentValue = getCurrentFieldValue(currentField)
        val newValue = removeLastChar(currentValue)
        updateFieldValue(currentField, newValue)
    }

    /**
     * Update a specific field value.
     */
    fun updateField(field: ReadingField, value: String) {
        updateFieldValue(field, value)
    }

    /**
     * Update timestamp.
     */
    fun updateTimestamp(timestamp: Date) {
        _uiState.value = _uiState.value.copy(timestamp = timestamp)
    }

    /**
     * Update notes.
     */
    fun updateNotes(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    /**
     * Validate the current form data.
     */
    fun validateForm(): Boolean {
        val state = _uiState.value
        val newReading = state.toMeterReading()

        if (newReading == null) {
            _validationErrors.value = listOf("Please enter all required readings")
            return false
        }

        val errors = validationEngine.getErrors(newReading, state.previousReading)
        val warnings = validationEngine.getWarnings(newReading, state.previousReading)

        _validationErrors.value = errors
        _validationWarnings.value = warnings

        return errors.isEmpty()
    }

    /**
     * Save the reading.
     */
    fun saveReading() {
        viewModelScope.launch {
            val state = _uiState.value
            val newReading = state.toMeterReading()

            if (newReading == null) {
                _saveResult.value = SaveResult.Error("Please enter all required readings")
                return@launch
            }

            val errors = validationEngine.getErrors(newReading, state.previousReading)
            if (errors.isNotEmpty()) {
                _validationErrors.value = errors
                _saveResult.value = SaveResult.Error("Validation failed: ${errors.joinToString(", ")}")
                return@launch
            }

            try {
                repository.insertReading(newReading)
                _saveResult.value = SaveResult.Success
            } catch (e: Exception) {
                _saveResult.value = SaveResult.Error("Failed to save: ${e.message}")
            }
        }
    }

    /**
     * Reset the save result.
     */
    fun resetSaveResult() {
        _saveResult.value = null
    }

    /**
     * Get the current value of a field.
     */
    private fun getCurrentFieldValue(field: ReadingField): String {
        return when (field) {
            ReadingField.TOTAL_READING -> _uiState.value.totalReading
            ReadingField.RATE_1_DAY -> _uiState.value.rate1Day
            ReadingField.RATE_2_OFF_PEAK -> _uiState.value.rate2OffPeak
            ReadingField.RATE_3_PEAK -> _uiState.value.rate3Peak
        }
    }

    /**
     * Update a field value.
     */
    private fun updateFieldValue(field: ReadingField, value: String) {
        _uiState.value = when (field) {
            ReadingField.TOTAL_READING -> _uiState.value.copy(totalReading = value)
            ReadingField.RATE_1_DAY -> _uiState.value.copy(rate1Day = value)
            ReadingField.RATE_2_OFF_PEAK -> _uiState.value.copy(rate2OffPeak = value)
            ReadingField.RATE_3_PEAK -> _uiState.value.copy(rate3Peak = value)
        }
    }

    /**
     * Append a character to the current value.
     */
    private fun appendToValue(current: String, newChar: String): String {
        // Handle decimal point - only allow one
        if (newChar == ".") {
            if (current.contains(".")) return current
            if (current.isEmpty()) return "0."
        }

        // Prevent leading zeros (except for "0." cases)
        if (current == "0" && newChar != ".") return newChar

        return current + newChar
    }

    /**
     * Remove the last character from the value.
     */
    private fun removeLastChar(value: String): String {
        if (value.isEmpty()) return ""
        if (value.length == 1) return ""
        return value.dropLast(1)
    }
}

/**
 * UI state for the Add Reading screen.
 */
data class AddReadingUiState(
    val timestamp: Date = Date(),
    val totalReading: String = "",
    val rate1Day: String = "",
    val rate2OffPeak: String = "",
    val rate3Peak: String = "",
    val notes: String = "",
    val previousReading: MeterReading? = null
) {
    /**
     * Convert the UI state to a MeterReading domain model.
     * Returns null if any required field is empty or invalid.
     */
    fun toMeterReading(): MeterReading? {
        return try {
            MeterReading(
                timestamp = timestamp,
                totalReading = totalReading.toDoubleOrNull() ?: return null,
                rate1Day = rate1Day.toDoubleOrNull() ?: return null,
                rate2OffPeak = rate2OffPeak.toDoubleOrNull() ?: return null,
                rate3Peak = rate3Peak.toDoubleOrNull() ?: return null,
                notes = notes.ifEmpty { null }
            )
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * Fields that can be edited with the keypad.
 */
enum class ReadingField {
    TOTAL_READING,
    RATE_1_DAY,
    RATE_2_OFF_PEAK,
    RATE_3_PEAK
}

/**
 * Result of a save operation.
 */
sealed class SaveResult {
    object Success : SaveResult()
    data class Error(val message: String) : SaveResult()
}