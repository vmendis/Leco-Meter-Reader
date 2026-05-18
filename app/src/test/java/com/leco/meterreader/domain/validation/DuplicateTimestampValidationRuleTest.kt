package com.leco.meterreader.domain.validation

import com.leco.meterreader.domain.model.MeterReading
import org.junit.Assert.*
import org.junit.Test
import java.util.Date

class DuplicateTimestampValidationRuleTest {

    private val rule = DuplicateTimestampValidationRule()
    private val baseTime = Date()

    @Test
    fun `validate returns valid when previous reading is null`() {
        val newReading = createReading(100.0, 50.0, 30.0, 20.0)
        
        val result = rule.validate(newReading, null)
        
        assertTrue(result.isValid)
    }

    @Test
    fun `validate returns valid when timestamps are different`() {
        val previousReading = createReading(100.0, 50.0, 30.0, 20.0, baseTime)
        val newReading = createReading(150.0, 70.0, 40.0, 40.0, Date(baseTime.time + 3600000)) // 1 hour later
        
        val result = rule.validate(newReading, previousReading)
        
        assertTrue(result.isValid)
    }

    @Test
    fun `validate returns invalid when timestamps are the same`() {
        val previousReading = createReading(100.0, 50.0, 30.0, 20.0, baseTime)
        val newReading = createReading(150.0, 70.0, 40.0, 40.0, baseTime)
        
        val result = rule.validate(newReading, previousReading)
        
        assertFalse(result.isValid)
        assertEquals("A reading already exists for this timestamp", result.errorMessage)
    }

    @Test
    fun `validate returns invalid when timestamps are equal but different Date objects`() {
        val previousReading = createReading(100.0, 50.0, 30.0, 20.0, baseTime)
        val newReading = createReading(150.0, 70.0, 40.0, 40.0, Date(baseTime.time))
        
        val result = rule.validate(newReading, previousReading)
        
        assertFalse(result.isValid)
    }

    private fun createReading(
        total: Double,
        rate1: Double,
        rate2: Double,
        rate3: Double,
        timestamp: Date = baseTime
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
}