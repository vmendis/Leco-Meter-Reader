package com.leco.meterreader.domain.validation

import com.leco.meterreader.domain.model.MeterReading
import org.junit.Assert.*
import org.junit.Test
import java.util.Date

class SumCheckValidationRuleTest {

    private val rule = SumCheckValidationRule()
    private val baseTime = Date()

    @Test
    fun `validate returns valid when previous reading is null`() {
        val newReading = createReading(100.0, 50.0, 30.0, 20.0)
        
        val result = rule.validate(newReading, null)
        
        assertTrue(result.isValid)
    }

    @Test
    fun `validate returns valid when total equals sum of rates`() {
        val previousReading = createReading(100.0, 50.0, 30.0, 20.0)
        val newReading = createReading(150.0, 70.0, 40.0, 40.0)
        
        val result = rule.validate(newReading, previousReading)
        
        assertTrue(result.isValid)
    }

    @Test
    fun `validate returns valid when within tolerance`() {
        val previousReading = createReading(100.0, 50.0, 30.0, 20.0)
        val newReading = createReading(150.0, 70.0, 40.0, 39.995) // 0.005 difference
        
        val result = rule.validate(newReading, previousReading)
        
        assertTrue(result.isValid)
    }

    @Test
    fun `validate returns invalid when total does not match sum`() {
        val previousReading = createReading(100.0, 50.0, 30.0, 20.0)
        val newReading = createReading(150.0, 70.0, 40.0, 30.0) // Sum is 20, total is 50
        
        val result = rule.validate(newReading, previousReading)
        
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
        assertTrue(result.errorMessage!!.contains("does not match sum"))
    }

    @Test
    fun `validate returns invalid when difference exceeds tolerance`() {
        val previousReading = createReading(100.0, 50.0, 30.0, 20.0)
        val newReading = createReading(150.0, 70.0, 40.0, 30.0) // 30 kWh difference
        
        val result = rule.validate(newReading, previousReading)
        
        assertFalse(result.isValid)
    }

    @Test
    fun `validate with custom tolerance`() {
        val ruleWithCustomTolerance = SumCheckValidationRule(tolerance = 0.001)
        val previousReading = createReading(100.0, 50.0, 30.0, 20.0)
        val newReading = createReading(150.0, 70.0, 40.0, 39.995) // 0.005 difference
        
        val result = ruleWithCustomTolerance.validate(newReading, previousReading)
        
        assertFalse(result.isValid)
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