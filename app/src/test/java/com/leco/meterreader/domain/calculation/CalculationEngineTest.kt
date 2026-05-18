package com.leco.meterreader.domain.calculation

import com.leco.meterreader.domain.model.CalculatedUsage
import com.leco.meterreader.domain.model.MeterReading
import com.leco.meterreader.domain.model.TariffConfig
import org.junit.Assert.*
import org.junit.Test
import java.util.Date

class CalculationEngineTest {

    private val engine = CalculationEngine()
    private val baseTime = Date()

    @Test
    fun `calculateUsageDeltas returns correct values`() {
        val previousReading = createReading(1, 100.0, 50.0, 30.0, 20.0)
        val newReading = createReading(2, 150.0, 70.0, 40.0, 40.0)
        
        val result = engine.calculateUsageDeltas(previousReading, newReading)
        
        assertEquals(1L, result.fromReadingId)
        assertEquals(2L, result.toReadingId)
        assertEquals(50.0, result.totalUsed, 0.01)
        assertEquals(20.0, result.dayUsed, 0.01)
        assertEquals(10.0, result.offPeakUsed, 0.01)
        assertEquals(20.0, result.peakUsed, 0.01)
        assertNotNull(result.calculationTimestamp)
    }

    @Test
    fun `calculateUsageDeltas handles zero deltas`() {
        val previousReading = createReading(1, 100.0, 50.0, 30.0, 20.0)
        val newReading = createReading(2, 100.0, 50.0, 30.0, 20.0)
        
        val result = engine.calculateUsageDeltas(previousReading, newReading)
        
        assertEquals(0.0, result.totalUsed, 0.01)
        assertEquals(0.0, result.dayUsed, 0.01)
        assertEquals(0.0, result.offPeakUsed, 0.01)
        assertEquals(0.0, result.peakUsed, 0.01)
    }

    @Test
    fun `calculateCost returns correct value`() {
        val calculatedUsage = CalculatedUsage(
            fromReadingId = 1,
            toReadingId = 2,
            totalUsed = 50.0,
            dayUsed = 20.0,
            offPeakUsed = 10.0,
            peakUsed = 20.0
        )
        val tariff = TariffConfig(
            dayRate = 15.0,
            offPeakRate = 10.0,
            peakRate = 20.0
        )
        
        val cost = engine.calculateCost(calculatedUsage, tariff)
        
        // 20 * 15 + 10 * 10 + 20 * 20 = 300 + 100 + 400 = 800
        assertEquals(800.0, cost, 0.01)
    }

    @Test
    fun `calculateCost handles zero usage`() {
        val calculatedUsage = CalculatedUsage(
            fromReadingId = 1,
            toReadingId = 2,
            totalUsed = 0.0,
            dayUsed = 0.0,
            offPeakUsed = 0.0,
            peakUsed = 0.0
        )
        val tariff = TariffConfig(
            dayRate = 15.0,
            offPeakRate = 10.0,
            peakRate = 20.0
        )
        
        val cost = engine.calculateCost(calculatedUsage, tariff)
        
        assertEquals(0.0, cost, 0.01)
    }

    @Test
    fun `calculateDailyCost returns correct value`() {
        val tariff = TariffConfig(
            dayRate = 15.0,
            offPeakRate = 10.0,
            peakRate = 20.0
        )
        
        val cost = engine.calculateDailyCost(
            totalUsed = 50.0,
            dayUsed = 20.0,
            offPeakUsed = 10.0,
            peakUsed = 20.0,
            tariff = tariff
        )
        
        // 20 * 15 + 10 * 10 + 20 * 20 = 300 + 100 + 400 = 800
        assertEquals(800.0, cost, 0.01)
    }

    @Test
    fun `getTouCategory returns OFF_PEAK for 22:30`() {
        val timestamp = createDate(2024, 1, 1, 22, 30)
        
        val category = engine.getTouCategory(timestamp)
        
        assertEquals(TouCategory.OFF_PEAK, category)
    }

    @Test
    fun `getTouCategory returns OFF_PEAK for 05:00`() {
        val timestamp = createDate(2024, 1, 1, 5, 0)
        
        val category = engine.getTouCategory(timestamp)
        
        assertEquals(TouCategory.OFF_PEAK, category)
    }

    @Test
    fun `getTouCategory returns DAY for 05:30`() {
        val timestamp = createDate(2024, 1, 1, 5, 30)
        
        val category = engine.getTouCategory(timestamp)
        
        assertEquals(TouCategory.DAY, category)
    }

    @Test
    fun `getTouCategory returns DAY for 12:00`() {
        val timestamp = createDate(2024, 1, 1, 12, 0)
        
        val category = engine.getTouCategory(timestamp)
        
        assertEquals(TouCategory.DAY, category)
    }

    @Test
    fun `getTouCategory returns DAY for 18:00`() {
        val timestamp = createDate(2024, 1, 1, 18, 0)
        
        val category = engine.getTouCategory(timestamp)
        
        assertEquals(TouCategory.DAY, category)
    }

    @Test
    fun `getTouCategory returns PEAK for 18:30`() {
        val timestamp = createDate(2024, 1, 1, 18, 30)
        
        val category = engine.getTouCategory(timestamp)
        
        assertEquals(TouCategory.PEAK, category)
    }

    @Test
    fun `getTouCategory returns PEAK for 21:00`() {
        val timestamp = createDate(2024, 1, 1, 21, 0)
        
        val category = engine.getTouCategory(timestamp)
        
        assertEquals(TouCategory.PEAK, category)
    }

    @Test
    fun `getTouCategory returns PEAK for 22:00`() {
        val timestamp = createDate(2024, 1, 1, 22, 0)
        
        val category = engine.getTouCategory(timestamp)
        
        assertEquals(TouCategory.PEAK, category)
    }

    @Test
    fun `getTouCategory returns OFF_PEAK for 22:29`() {
        val timestamp = createDate(2024, 1, 1, 22, 29)
        
        val category = engine.getTouCategory(timestamp)
        
        assertEquals(TouCategory.PEAK, category)
    }

    @Test
    fun `getTouCategory returns OFF_PEAK for 05:29`() {
        val timestamp = createDate(2024, 1, 1, 5, 29)
        
        val category = engine.getTouCategory(timestamp)
        
        assertEquals(TouCategory.OFF_PEAK, category)
    }

    private fun createReading(
        id: Long,
        total: Double,
        rate1: Double,
        rate2: Double,
        rate3: Double
    ): MeterReading {
        return MeterReading(
            id = id,
            timestamp = baseTime,
            totalReading = total,
            rate1Day = rate1,
            rate2OffPeak = rate2,
            rate3Peak = rate3
        )
    }

    private fun createDate(year: Int, month: Int, day: Int, hour: Int, minute: Int): Date {
        val calendar = java.util.Calendar.getInstance().apply {
            set(year, month - 1, day, hour, minute)
        }
        return calendar.time
    }
}