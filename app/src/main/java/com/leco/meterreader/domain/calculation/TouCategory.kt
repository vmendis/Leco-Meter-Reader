package com.leco.meterreader.domain.calculation

/**
 * Time Of Use categories for LECO tariff.
 */
enum class TouCategory {
    OFF_PEAK,  // 22:30 – 05:30
    DAY,       // 05:30 – 18:30
    PEAK       // 18:30 – 22:30
}

/**
 * Get TOU category for a given hour.
 * LECO TOU windows:
 * - Off-Peak: 22:30 – 05:30
 * - Day: 05:30 – 18:30
 * - Peak: 18:30 – 22:30
 */
fun getTouCategoryForHour(hour: Int, minute: Int = 0): TouCategory {
    return when {
        // Off-Peak: 22:30 – 05:30
        (hour == 22 && minute >= 30) || (hour in 23..23) || (hour in 0..4) || (hour == 5 && minute < 30) -> 
            TouCategory.OFF_PEAK
        // Peak: 18:30 – 22:30
        (hour == 18 && minute >= 30) || (hour in 19..21) || (hour == 22 && minute < 30) -> 
            TouCategory.PEAK
        // Day: 05:30 – 18:30
        else -> TouCategory.DAY
    }
}