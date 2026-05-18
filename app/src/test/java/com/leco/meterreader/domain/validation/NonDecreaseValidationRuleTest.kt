package com.leco.meterreader.domain.validation

import com.leco.meterreader.domain.model.MeterReading
import org.junit.Assert.*
import org.junit.Test
import java.util.Date

class NonDecreaseValidationRuleTest {

    private val rule = NonDecreaseValidationRule()
    private val baseTime = Date()

    @Test
    fun `validate returns valid when previous reading is null`() {
        val newReading = createReading(100.0, 50.0, 30.0, 20.0)
        
        val result = rule.validate(newReading, null)
        
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `validate returns valid when all readings increase`() {
        val previousReading = createReading(100.0, 50.0, 30.0, 20.0)
        val newReading = createReading(150.0, 70.0, 40.0, 40.0)
        
        val result = rule.validate(newReading, previousReading)
        
        assertTrue(result.isValid)
    }

    @Test
    fun `validate returns valid when all readings stay the same`() {
        val previousReading = createReading(100.0, 50.0, 30.0, 20.0)
        val newReading = createReading(100.0, 50.0, 30.0, 20.0)
        
        val result = rule.validate(newReading, previousReading)
        
        assertTrue(result.isValid)
    }

    @Test
    fun `validate returns invalid when total reading decreases`() {
        val previousReading = createReading(100.0, 50.0, 30.0, 20.0)
        val newReading = createReading(90.0, 50.0, 30.0, 20.0)
        
        val result = rule.validate(newReading, previousReading)
        
        assertFalse(result.isValid)
        assertEquals("Total reading cannot decrease", result.errorMessage)
    }

    @Test
    fun `validate returns invalid when rate1 day decreases`() {
        val previousReading = createReading(100.0, 50.0, 30.0, 20.0)
        val newReading = createReading(150.0, 40.0, 40.0, 40.0)
        
        val result = rule.validate(newReading, previousReading)
        
        assertFalse(result.isValid)
        assertEquals("Rate 1 (Day) reading cannot decrease", result.errorMessage)
    }

    @Test
    fun `validate returns invalid when rate2 offPeak decreases`() {
        val previousReading = createReading(100.0, 50.0, 30.0, 20.0)
        val newReading = createReading(150.0, 60.0, 20.0, 40.0)
        
        val result = rule.validate(newReading, previousReading)
        
        assertFalse(result.isValid)
        assertEquals("Rate 2 (Off-Peak) reading cannot decrease", result.errorMessage)
    }

    @Test
    fun `validate returns invalid when rate3 peak decreases`() {
        val previousReading = createReading(100.0, 50.0, 30.0, 20.0)
        val newReading = createReading(150.0, 60.0, 40.0, 10.0)
        
        val result = rule.validate(newReading, previousReading)
        
        assertFalse(result.isValid)
        assertEquals("Rate 3 (Peak) reading cannot decrease", result.errorMessage)
    }

    private fun createReading(
        total: Double,
        rate1: Double,
        rate2: Double,
        rate3: Double
    ): MeterReading {
        return MeterReading(
            id = 1,
            timestamp = baseTime,
            totalReading = total,
            rate1Day = rate1,
            rate2OffPeak = rate2,
            rate3Peak = rate3
        )
    }
}