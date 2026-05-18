package com.leco.meterreader.domain.validation

import com.leco.meterreader.domain.model.MeterReading
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar
import java.util.Date

class ValidationEngineTest {

    private val engine = ValidationEngine()
    private val baseTime = Date()

    @Test
    fun `validate returns all results for valid reading`() {
        val previousReading = createReading(100.0, 50.0, 30.0, 20.0, baseTime)
        val newReading = createReading(150.0, 70.0, 40.0, 40.0, createDate(2024, 1, 2, 12, 0))
        
        val results = engine.validate(newReading, previousReading)
        
        assertEquals(4, results.size)
        assertTrue(results.all { it.isValid })
    }

    @Test
    fun `validate returns error for invalid reading`() {
        val previousReading = createReading(100.0, 50.0, 30.0, 20.0, baseTime)
        // All values decrease proportionally to pass sum check
        val newReading = createReading(90.0, 45.0, 27.0, 18.0, createDate(2024, 1, 2, 12, 0))
        
        val results = engine.validate(newReading, previousReading)
        
        val errorCount = results.count { !it.isValid }
        assertEquals(1, errorCount) // NonDecrease should fail first
    }

    @Test
    fun `isValid returns true for valid reading`() {
        val previousReading = createReading(100.0, 50.0, 30.0, 20.0, baseTime)
        val newReading = createReading(150.0, 70.0, 40.0, 40.0, createDate(2024, 1, 2, 12, 0))
        
        val isValid = engine.isValid(newReading, previousReading)
        
        assertTrue(isValid)
    }

    @Test
    fun `isValid returns false for invalid reading`() {
        val previousReading = createReading(100.0, 50.0, 30.0, 20.0, baseTime)
        val newReading = createReading(90.0, 45.0, 27.0, 18.0, createDate(2024, 1, 2, 12, 0)) // Total decreased
        
        val isValid = engine.isValid(newReading, previousReading)
        
        assertFalse(isValid)
    }

    @Test
    fun `isValid returns true when previous reading is null`() {
        val newReading = createReading(100.0, 50.0, 30.0, 20.0, baseTime)
        
        val isValid = engine.isValid(newReading, null)
        
        assertTrue(isValid)
    }

    @Test
    fun `getErrors returns empty list for valid reading`() {
        val previousReading = createReading(100.0, 50.0, 30.0, 20.0, baseTime)
        val newReading = createReading(150.0, 70.0, 40.0, 40.0, createDate(2024, 1, 2, 12, 0))
        
        val errors = engine.getErrors(newReading, previousReading)
        
        assertTrue(errors.isEmpty())
    }

    @Test
    fun `getErrors returns error messages for invalid reading`() {
        val previousReading = createReading(100.0, 50.0, 30.0, 20.0, baseTime)
        val newReading = createReading(90.0, 45.0, 27.0, 18.0, createDate(2024, 1, 2, 12, 0)) // Total decreased
        
        val errors = engine.getErrors(newReading, previousReading)
        
        assertEquals(1, errors.size)
        assertEquals("Total reading cannot decrease", errors[0])
    }

    @Test
    fun `getWarnings returns empty list for normal usage`() {
        val previousReading = createReading(100.0, 50.0, 30.0, 20.0, baseTime)
        val newReading = createReading(150.0, 70.0, 40.0, 40.0, createDate(2024, 1, 2, 12, 0)) // 50 kWh delta
        
        val warnings = engine.getWarnings(newReading, previousReading)
        
        assertTrue(warnings.isEmpty())
    }

    @Test
    fun `getWarnings returns warning for high usage`() {
        val previousReading = createReading(100.0, 50.0, 30.0, 20.0, baseTime)
        val newReading = createReading(250.0, 100.0, 80.0, 70.0, createDate(2024, 1, 2, 12, 0)) // 150 kWh delta
        
        val warnings = engine.getWarnings(newReading, previousReading)
        
        assertEquals(1, warnings.size)
        assertTrue(warnings[0].contains("Unusually high daily usage"))
    }

    private fun createReading(
        total: Double,
        rate1: Double,
        rate2: Double,
        rate3: Double,
        timestamp: Date
    ): MeterReading {
        return MeterReading(
            id = 1,
            timestamp = timestamp,
            totalReading = total,
            rate1Day = rate1,
            rate2OffPeak = rate2,
            rate3Peak = rate3
        )
    }

    private fun createDate(year: Int, month: Int, day: Int, hour: Int, minute: Int): Date {
        val calendar = Calendar.getInstance().apply {
            set(year, month - 1, day, hour, minute)
        }
        return calendar.time
    }
}