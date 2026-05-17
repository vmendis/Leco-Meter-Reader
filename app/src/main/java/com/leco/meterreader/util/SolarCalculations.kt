package com.leco.meterreader.util

import com.leco.meterreader.data.model.*

/**
 * Utility class for solar system calculations
 */
object SolarCalculations {
    
    /**
     * Calculate required number of solar panels
     */
    fun calculateRequiredPanels(
        dailyConsumption: Double,
        panelWattage: Double,
        sunHours: Double,
        systemEfficiency: Double
    ): Int {
        if (dailyConsumption <= 0 || panelWattage <= 0 || sunHours <= 0 || systemEfficiency <= 0) {
            return 0
        }
        
        val dailyProductionPerPanel = (panelWattage * sunHours * systemEfficiency) / 1000.0
        return ceil(dailyConsumption / dailyProductionPerPanel).toInt()
    }
    
    /**
     * Calculate total solar panel power
     */
    fun calculateTotalPanelPower(
        requiredPanels: Int,
        panelWattage: Double
    ): Double {
        return requiredPanels * panelWattage
    }
    
    /**
     * Calculate battery sizing requirements
     */
    fun calculateBatterySize(
        dailyConsumption: Double,
        daysOfAutonomy: Int = 1,
        dod: Double = 0.8,
        systemVoltage: Double = 48.0
    ): BatterySizingResult {
        if (dailyConsumption <= 0 || dod <= 0 || systemVoltage <= 0) {
            return BatterySizingResult(0.0, 0.0, 0.0, 0)
        }
        
        val totalCapacityWh = (dailyConsumption * daysOfAutonomy) / dod
        val capacityAh = totalCapacityWh / systemVoltage
        val recommendedCapacityAh = roundUpToStandardSize(capacityAh)
        val cycleLifeEstimate = estimateCycleLife(dod)
        
        return BatterySizingResult(
            totalCapacityWh = totalCapacityWh,
            capacityAh = capacityAh,
            recommendedCapacityAh = recommendedCapacityAh,
            cycleLifeEstimate = cycleLifeEstimate
        )
    }
    
    /**
     * Calculate inverter requirements
     */
    fun calculateInverterSize(
        peakConsumption: Double,
        totalPanelPower: Double,
        safetyFactor: Double = 1.25
    ): InverterSizingResult {
        if (peakConsumption <= 0 || totalPanelPower <= 0) {
            return InverterSizingResult(0.0, 0.0, 0.0, "Unknown")
        }
        
        val continuousPower = max(peakConsumption * 1000, totalPanelPower)
        val surgePower = continuousPower * 2.0 // Typical surge ratio
        val recommendedSize = continuousPower * safetyFactor
        
        return InverterSizingResult(
            continuousPower = continuousPower,
            surgePower = surgePower,
            recommendedSize = recommendedSize,
            inverterType = if (totalPanelPower > 10000) "String Inverter" else "Microinverter"
        )
    }
    
    /**
     * Calculate solar production
     */
    fun calculateDailyProduction(
        totalPanelPower: Double,
        sunHours: Double,
        systemEfficiency: Double
    ): Double {
        return (totalPanelPower * sunHours * systemEfficiency) / 1000.0
    }
    
    /**
     * Calculate grid dependency percentage
     */
    fun calculateGridDependency(
        dailyProduction: Double,
        dailyConsumption: Double
    ): Double {
        if (dailyConsumption <= 0) return 100.0
        
        return if (dailyProduction >= dailyConsumption) 0.0 else
            ((dailyConsumption - dailyProduction) / dailyConsumption) * 100
    }
    
    /**
     * Calculate economic analysis
     */
    fun calculateEconomicAnalysis(
        dailyConsumption: Double,
        gridDependency: Double,
        electricityRate: Double,
        systemCost: Double,
        panelCount: Int,
        batterySizeWh: Double,
        inverterSize: Double
    ): EconomicAnalysisResult {
        if (dailyConsumption <= 0 || electricityRate <= 0 || systemCost <= 0) {
            return EconomicAnalysisResult(0.0, 0.0, 0.0, 0.0)
        }
        
        val monthlyGridUsage = (dailyConsumption * gridDependency / 100) * 30
        val monthlySavings = monthlyGridUsage * electricityRate
        val yearlySavings = monthlySavings * 12
        
        // Simple payback period calculation
        val paybackPeriod = if (yearlySavings > 0) systemCost / yearlySavings else Double.POSITIVE_INFINITY
        val roi = if (yearlySavings > 0 && systemCost > 0) (yearlySavings / systemCost) * 100 else 0.0
        
        return EconomicAnalysisResult(
            monthlySavings = monthlySavings,
            yearlySavings = yearlySavings,
            paybackPeriod = paybackPeriod,
            roi = roi
        )
    }
    
    /**
     * Calculate CO2 reduction
     */
    fun calculateCO2Reduction(
        dailyProduction: Double,
        carbonFactor: Double = 0.5
    ): Double {
        return dailyProduction * 365 * carbonFactor // kg per year
    }
    
    /**
     * Calculate system cost estimation
     */
    fun estimateSystemCost(
        panelCount: Int,
        panelWattage: Double,
        batterySizeWh: Double,
        inverterSize: Double
    ): Double {
        val panelCost = panelCount * panelWattage * 2.50 // $2.50 per watt
        val batteryCost = batterySizeWh * 0.25 // $0.25 per Wh
        val inverterCost = inverterSize * 1.20 // $1.20 per watt
        
        return panelCost + batteryCost + inverterCost
    }
    
    /**
     * Calculate monthly sun hours average
     */
    fun calculateAverageMonthlySunHours(monthlyHours: Map<Int, Double>): Double {
        return monthlyHours.values.average()
    }
    
    /**
     * Get sun hours for specific month
     */
    fun getSunHoursForMonth(monthlyHours: Map<Int, Double>, month: Int): Double {
        return monthlyHours[month] ?: 5.5 // Default value
    }
    
    /**
     * Update monthly sun hours
     */
    fun updateMonthlySunHours(
        currentMonthlyHours: Map<Int, Double>,
        month: Int,
        newHours: Double
    ): Map<Int, Double> {
        return currentMonthlyHours.toMutableMap().apply {
            this[month] = newHours
        }
    }
    
    /**
     * Calculate panel efficiency degradation over time
     */
    fun calculateDegradationFactor(years: Int, annualDegradation: Double = 0.005): Double {
        return kotlin.math.pow(1 - annualDegradation, years)
    }
    
    /**
     * Calculate optimal tilt angle for location
     */
    fun calculateOptimalTilt(latitude: Double): Double {
        // Simplified calculation - optimal tilt is approximately latitude
        return abs(latitude)
    }
    
    /**
     * Calculate shading losses
     */
    fun calculateShadingLosses(shadingPercentage: Double): Double {
        return 1.0 - (shadingPercentage / 100.0)
    }
    
    /**
     * Calculate roof space requirements
     */
    fun calculateRoofSpaceRequirements(
        panelCount: Int,
        panelWidth: Double = 1.0, // meters
        panelHeight: Double = 1.7, // meters
        spacing: Double = 0.1 // meters between panels
    ): Pair<Double, Double> {
        val panelsPerRow = floor(sqrt(panelCount.toDouble())).toInt()
        val rows = ceil(panelCount.toDouble() / panelsPerRow).toInt()
        
        val rowWidth = panelWidth * panelsPerRow + spacing * (panelsPerRow - 1)
        val totalHeight = panelHeight * rows + spacing * (rows - 1)
        
        val totalArea = rowWidth * totalHeight
        val totalAreaSqFt = totalArea * 10.764 // Convert to square feet
        
        return Pair(totalArea, totalAreaSqFt)
    }
    
    /**
     * Calculate energy production for different months
     */
    fun calculateMonthlyProduction(
        totalPanelPower: Double,
        monthlySunHours: Map<Int, Double>,
        systemEfficiency: Double
    ): Map<Int, Double> {
        return monthlySunHours.mapValues { (month, hours) ->
            (totalPanelPower * hours * systemEfficiency) / 1000.0
        }
    }
    
    /**
     * Calculate consumption vs production comparison
     */
    fun calculateConsumptionVsProduction(
        dailyConsumption: Double,
        monthlyProduction: Map<Int, Double>
    ): Map<Int, Pair<Double, Double>> {
        return monthlyProduction.mapValues { (month, production) ->
            Pair(dailyConsumption * 30, production) // Monthly consumption vs production
        }
    }
    
    /**
     * Calculate battery state of charge over time
     */
    fun calculateBatterySOC(
        batteryCapacityWh: Double,
        dailyConsumption: Double,
        sunHours: Double,
        panelPower: Double,
        systemEfficiency: Double
    ): List<Double> {
        val dailyProduction = (panelPower * sunHours * systemEfficiency) / 1000.0
        val netEnergy = dailyProduction - dailyConsumption
        
        val socList = mutableListOf<Double>()
        var currentSOC = 0.0
        
        for (hour in 0..23) {
            // Simplified hourly calculation
            val hourlyConsumption = dailyConsumption / 24.0
            val hourlyProduction = dailyProduction / max(1.0, sunHours)
            
            if (hour < sunHours.toInt()) {
                // Production hours
                currentSOC += (hourlyProduction - hourlyConsumption) / batteryCapacityWh * 100
            } else {
                // Consumption only hours
                currentSOC -= hourlyConsumption / batteryCapacityWh * 100
            }
            
            currentSOC = max(0.0, min(100.0, currentSOC))
            socList.add(currentSOC)
        }
        
        return socList
    }
    
    /**
     * Calculate optimal battery size based on usage patterns
     */
    fun calculateOptimalBatterySize(
        consumptionPattern: List<Double>, // Hourly consumption
        productionPattern: List<Double>, // Hourly production
        maxDoD: Double = 0.8
    ): Double {
        val batteryCapacity = mutableListOf<Double>()
        var currentSOC = 0.0
        
        for (hour in consumptionPattern.indices) {
            val net = productionPattern[hour] - consumptionPattern[hour]
            currentSOC += net
            
            // Find maximum SOC swing
            batteryCapacity.add(abs(currentSOC))
        }
        
        val maxSocSwing = batteryCapacity.maxOrNull() ?: 0.0
        return maxSocSwing / maxDoD
    }
    
    /**
     * Calculate system reliability
     */
    fun calculateSystemReliability(
        panelCount: Int,
        batteryCapacity: Double,
        dailyConsumption: Double,
        sunHours: Double,
        daysWithoutSun: Int = 3
    ): Double {
        val productionPerPanel = (400 * sunHours * 0.85) / 1000.0 // Assuming 400W panels
        val totalProduction = panelCount * productionPerPanel
        totalProduction = max(totalProduction, dailyConsumption) // Ensure minimum production
        
        val batteryDays = batteryCapacity / (dailyConsumption * 24) // Assuming 48V system
        val totalAutonomy = daysWithoutSun + batteryDays
        
        return min(100.0, (totalAutonomy / 7.0) * 100) // Reliability vs 7-day week
    }
    
    // Helper functions
    private fun estimateCycleLife(dod: Double): Int {
        return when {
            dod <= 0.5 -> 2000
            dod <= 0.8 -> 3000
            dod <= 0.9 -> 1500
            else -> 1000
        }
    }
    
    private fun roundUpToStandardSize(capacityAh: Double): Double {
        val standardSizes = listOf(50.0, 100.0, 200.0, 300.0, 400.0, 500.0, 1000.0)
        return standardSizes.firstOrNull { it >= capacityAh } ?: standardSizes.last()
    }
}