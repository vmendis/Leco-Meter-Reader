package com.leco.meterreader.domain.calculation

import org.junit.Assert.*
import org.junit.Test

class TouCategoryTest {

    @Test
    fun `getTouCategoryForHour returns OFF_PEAK for 22:30`() {
        val category = getTouCategoryForHour(22, 30)
        assertEquals(TouCategory.OFF_PEAK, category)
    }

    @Test
    fun `getTouCategoryForHour returns OFF_PEAK for 23:00`() {
        val category = getTouCategoryForHour(23, 0)
        assertEquals(TouCategory.OFF_PEAK, category)
    }

    @Test
    fun `getTouCategoryForHour returns OFF_PEAK for 00:00`() {
        val category = getTouCategoryForHour(0, 0)
        assertEquals(TouCategory.OFF_PEAK, category)
    }

    @Test
    fun `getTouCategoryForHour returns OFF_PEAK for 04:59`() {
        val category = getTouCategoryForHour(4, 59)
        assertEquals(TouCategory.OFF_PEAK, category)
    }

    @Test
    fun `getTouCategoryForHour returns OFF_PEAK for 05:00`() {
        val category = getTouCategoryForHour(5, 0)
        assertEquals(TouCategory.OFF_PEAK, category)
    }

    @Test
    fun `getTouCategoryForHour returns OFF_PEAK for 05:29`() {
        val category = getTouCategoryForHour(5, 29)
        assertEquals(TouCategory.OFF_PEAK, category)
    }

    @Test
    fun `getTouCategoryForHour returns DAY for 05:30`() {
        val category = getTouCategoryForHour(5, 30)
        assertEquals(TouCategory.DAY, category)
    }

    @Test
    fun `getTouCategoryForHour returns DAY for 06:00`() {
        val category = getTouCategoryForHour(6, 0)
        assertEquals(TouCategory.DAY, category)
    }

    @Test
    fun `getTouCategoryForHour returns DAY for 12:00`() {
        val category = getTouCategoryForHour(12, 0)
        assertEquals(TouCategory.DAY, category)
    }

    @Test
    fun `getTouCategoryForHour returns DAY for 18:00`() {
        val category = getTouCategoryForHour(18, 0)
        assertEquals(TouCategory.DAY, category)
    }

    @Test
    fun `getTouCategoryForHour returns DAY for 18:29`() {
        val category = getTouCategoryForHour(18, 29)
        assertEquals(TouCategory.DAY, category)
    }

    @Test
    fun `getTouCategoryForHour returns PEAK for 18:30`() {
        val category = getTouCategoryForHour(18, 30)
        assertEquals(TouCategory.PEAK, category)
    }

    @Test
    fun `getTouCategoryForHour returns PEAK for 19:00`() {
        val category = getTouCategoryForHour(19, 0)
        assertEquals(TouCategory.PEAK, category)
    }

    @Test
    fun `getTouCategoryForHour returns PEAK for 20:00`() {
        val category = getTouCategoryForHour(20, 0)
        assertEquals(TouCategory.PEAK, category)
    }

    @Test
    fun `getTouCategoryForHour returns PEAK for 21:00`() {
        val category = getTouCategoryForHour(21, 0)
        assertEquals(TouCategory.PEAK, category)
    }

    @Test
    fun `getTouCategoryForHour returns PEAK for 22:00`() {
        val category = getTouCategoryForHour(22, 0)
        assertEquals(TouCategory.PEAK, category)
    }

    @Test
    fun `getTouCategoryForHour returns PEAK for 22:29`() {
        val category = getTouCategoryForHour(22, 29)
        assertEquals(TouCategory.PEAK, category)
    }

    @Test
    fun `getTouCategoryForHour with default minute returns DAY for 05:00`() {
        val category = getTouCategoryForHour(5)
        assertEquals(TouCategory.OFF_PEAK, category)
    }

    @Test
    fun `getTouCategoryForHour with default minute returns DAY for 06:00`() {
        val category = getTouCategoryForHour(6)
        assertEquals(TouCategory.DAY, category)
    }

    @Test
    fun `getTouCategoryForHour with default minute returns PEAK for 19:00`() {
        val category = getTouCategoryForHour(19)
        assertEquals(TouCategory.PEAK, category)
    }
}