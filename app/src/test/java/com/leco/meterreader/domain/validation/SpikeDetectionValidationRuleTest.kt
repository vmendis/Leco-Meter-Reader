package com.leco.meterreader.domain.validation

import com.leco.meterreader.domain.model.MeterReading
import org.junit.Assert.*
import org.junit.Test
import java.util.Date

class SpikeDetectionValidationRuleTest {

    private val rule = SpikeDetectionValidationRule()
    private val baseTime = Date()

    @Test
    fun `validate returns valid when previous reading is null`() {
        val newReading = createReading(100.0, 50.0, 30.0, 20.0)
        
        val result = rule.validate(newReading, null)
        
        assertTrue(result.isValid)
    }

    @Test
    fun `validate returns valid when usage is below threshold`() {
        val previousReading = createReading(100.0, 50.0, 30.0, 20.0)
        val newReading = createReading(150.0, 70.0, 40.0, 40.0) // 50 kWh delta
        
        val result = rule.validate(newReading, previousReading)
        
        assertTrue(result.isValid)
        assertNull(result.warningMessage)
    }

    @Test
    fun `validate returns warning when usage exceeds threshold`() {
        val previousReading = createReading(100.0, 50.0, 30.0, 20.0)
        val newReading = createReading(250.0, 100.0, 80.0, 70.0) // 150 kWh delta
        
        val result = rule.validate(newReading, previousReading)
        
        assertTrue(result.isValid) // Still valid, just a warning
        assertNotNull(result.warningMessage)
        assertTrue(result.warningMessage!!.contains("Unusually high daily usage"))
    }

    @Test
    fun `validate returns warning with correct usage value`() {
        val previousReading = createReading(100.0, 50.0, 30.0, 20.0)
        val newReading = createReading(220.0, 80.0, 60.0, 40.0) // 120 kWh delta
        
        val result = rule.validate(newReading, previousReading)
        
        assertTrue(result.warningMessage!!.contains("120.0 kWh"))
    }

    @Test
    fun `validate with custom threshold`() {
        val ruleWithCustomThreshold = SpikeDetectionValidationRule(dailyUsageThreshold = 50.0)
        val previousReading = createReading(100.0, 50.0, 30.0, 20.0)
        val newReading = createReading(160.0, 70.0, 40.0, 50.0) // 60 kWh delta
        
        val result = ruleWithCustomThreshold.validate(newReading, previousReading)
        
        assertNotNull(result.warningMessage)
    }

    @Test
    fun `validate returns valid when usage equals threshold`() {
        val ruleWithCustomThreshold = SpikeDetectionValidationRule(dailyUsageThreshold = 100.0)
        val previousReading = createReading(100.0, 50.0, 30.0, 20.0)
        val newReading = createReading(200.0, 80.0, 60.0, 60.0) // 100 kWh delta
        
        val result = ruleWithCustomThreshold.validate(newReading, previousReading)
        
        assertNull(result.warningMessage)
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