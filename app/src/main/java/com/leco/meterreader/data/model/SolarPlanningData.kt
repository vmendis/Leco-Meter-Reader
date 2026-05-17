package com.leco.meterreader.data.model

import androidx.compose.runtime.Immutable
import java.time.LocalDate

/**
 * Data class for solar planning parameters and calculation results
 */
@Immutable
data class SolarPlanningData(
    // Input Parameters
    val dailyConsumption: Double = 0.0, // kWh per day
    val peakConsumption: Double = 0.0, // kWh during peak hours
    val offPeakConsumption: Double = 0.0, // kWh during off-peak hours
    val solarPanelWattage: Double = 400.0, // W per panel
    val batteryCapacity: Double = 0.0, // Wh (optional, calculated if 0)
    val batteryDoD: Double = 0.8, // Depth of discharge (0-1)
    val inverterEfficiency: Double = 0.95, // Inverter efficiency (0-1)
    val wiringEfficiency: Double = 0.98, // Wiring efficiency (0-1)
    val temperatureCoefficient: Double = 0.85, // Temperature effects (0-1)
    val sunHours: Double = 5.5, // Average daily sun hours
    val electricityRate: Double = 0.25, // $ per kWh
    val systemVoltage: Double = 48.0, // System voltage (V)
    val daysOfAutonomy: Int = 1, // Days of battery autonomy
    
    // Monthly sun hours (key = month, value = hours)
    val monthlySunHours: Map<Int, Double> = mapOf(
        1 to 5.2, 2 to 5.5, 3 to 6.0, 4 to 6.5,
        5 to 7.0, 6 to 7.2, 7 to 7.0, 8 to 6.8,
        9 to 6.2, 10 to 5.8, 11 to 5.3, 12 to 5.0
    ),
    
    // Calculated Results
    val requiredPanelCount: Int = 0,
    val totalPanelPower: Double = 0.0, // W
    val roofSpaceSqM: Double = 0.0, // Square meters
    val roofSpaceSqFt: Double = 0.0, // Square feet
    val batterySizeWh: Double = 0.0, // Wh
    val batterySizeAh: Double = 0.0, // Ah
    val inverterSize: Double = 0.0, // W
    val dailyProduction: Double = 0.0, // kWh
    val gridDependency: Double = 0.0, // percentage (0-100)
    val monthlySavings: Double = 0.0, // $
    val yearlySavings: Double = 0.0, // $
    val paybackPeriod: Double = 0.0, // years
    val co2Reduction: Double = 0.0, // kg/year
    val systemCost: Double = 0.0, // Initial system cost
    
    // Scenario metadata
    val scenarioName: String = "Default",
    val createdAt: LocalDate = LocalDate.now(),
    val isModified: Boolean = false
) {
    // Helper properties for validation
    val isValid: Boolean
        get() = dailyConsumption > 0 && 
                solarPanelWattage > 0 && 
                sunHours > 0 && 
                electricityRate > 0 &&
                batteryDoD > 0 && batteryDoD <= 1 &&
                inverterEfficiency > 0 && inverterEfficiency <= 1 &&
                wiringEfficiency > 0 && wiringEfficiency <= 1 &&
                temperatureCoefficient > 0 && temperatureCoefficient <= 1
    
    // System efficiency calculation
    val systemEfficiency: Double
        get() = inverterEfficiency * wiringEfficiency * temperatureCoefficient
    
    // Total consumption (peak + off-peak)
    val totalConsumption: Double
        get() = peakConsumption + offPeakConsumption
    
    // Battery capacity in kWh
    val batterySizeKwh: Double
        get() = batterySizeWh / 1000.0
    
    // Solar production potential
    val maxDailyProduction: Double
        get() = (totalPanelPower * sunHours * systemEfficiency) / 1000.0
    
    // Grid independence percentage (100% = fully independent)
    val gridIndependence: Double
        get() = if (dailyProduction >= dailyConsumption) 100.0 else
            ((dailyProduction / dailyConsumption) * 100)
    
    // Energy offset percentage
    val energyOffset: Double
        get() = if (dailyConsumption > 0) (dailyProduction / dailyConsumption) * 100 else 0.0
    
    // Cost per watt for system estimation
    val costPerWatt: Double = 2.50 // Industry average
    
    // Estimated system cost
    val estimatedSystemCost: Double
        get() = totalPanelPower * costPerWatt + (batterySizeWh * 0.25) + (inverterSize * 1.20)
    
    // ROI calculation
    val roi: Double
        get() = if (yearlySavings > 0 && estimatedSystemCost > 0) 
            (yearlySavings / estimatedSystemCost) * 100 else 0.0
    
    // Carbon factor (kg CO2 per kWh)
    val carbonFactor: Double = 0.5 // Average grid carbon intensity
    
    constructor(
        // Copy constructor for easy updates
        other: SolarPlanningData,
        dailyConsumption: Double = other.dailyConsumption,
        peakConsumption: Double = other.peakConsumption,
        offPeakConsumption: Double = other.offPeakConsumption,
        solarPanelWattage: Double = other.solarPanelWattage,
        batteryCapacity: Double = other.batteryCapacity,
        batteryDoD: Double = other.batteryDoD,
        inverterEfficiency: Double = other.inverterEfficiency,
        wiringEfficiency: Double = other.wiringEfficiency,
        temperatureCoefficient: Double = other.temperatureCoefficient,
        sunHours: Double = other.sunHours,
        electricityRate: Double = other.electricityRate,
        systemVoltage: Double = other.systemVoltage,
        daysOfAutonomy: Int = other.daysOfAutonomy,
        monthlySunHours: Map<Int, Double> = other.monthlySunHours
    ) : this(
        dailyConsumption = dailyConsumption,
        peakConsumption = peakConsumption,
        offPeakConsumption = offPeakConsumption,
        solarPanelWattage = solarPanelWattage,
        batteryCapacity = batteryCapacity,
        batteryDoD = batteryDoD,
        inverterEfficiency = inverterEfficiency,
        wiringEfficiency = wiringEfficiency,
        temperatureCoefficient = temperatureCoefficient,
        sunHours = sunHours,
        electricityRate = electricityRate,
        systemVoltage = systemVoltage,
        daysOfAutonomy = daysOfAutonomy,
        monthlySunHours = monthlySunHours,
        // Keep calculated results as they will be recalculated
        requiredPanelCount = other.requiredPanelCount,
        totalPanelPower = other.totalPanelPower,
        roofSpaceSqM = other.roofSpaceSqM,
        roofSpaceSqFt = other.roofSpaceSqFt,
        batterySizeWh = other.batterySizeWh,
        batterySizeAh = other.batterySizeAh,
        inverterSize = other.inverterSize,
        dailyProduction = other.dailyProduction,
        gridDependency = other.gridDependency,
        monthlySavings = other.monthlySavings,
        yearlySavings = other.yearlySavings,
        paybackPeriod = other.paybackPeriod,
        co2Reduction = other.co2Reduction,
        systemCost = other.systemCost,
        scenarioName = other.scenarioName,
        createdAt = other.createdAt,
        isModified = true
    )
}

/**
 * UI state for solar planning screen
 */
data class SolarPlanningUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val parameters: SolarPlanningData = SolarPlanningData(),
    val results: SolarPlanningData = SolarPlanningData(),
    val scenarios: List<SolarScenario> = emptyList(),
    val selectedScenarios: Set<String> = emptySet(),
    val calculationHistory: List<SolarCalculationLog> = emptyList()
)

/**
 * Solar scenario for comparison
 */
data class SolarScenario(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val data: SolarPlanningData,
    val createdAt: LocalDate = LocalDate.now(),
    val isFavorite: Boolean = false
)

/**
 * Log entry for calculation history
 */
data class SolarCalculationLog(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: LocalDate = LocalDate.now(),
    val parameters: SolarPlanningData,
    val results: SolarPlanningData,
    val calculationTimeMs: Long = 0
)

/**
 * Solar calculation parameters for specific calculations
 */
data class SolarCalculationParams(
    val consumption: Double,
    val panelWattage: Double,
    sunHours: Double,
    val efficiency: Double,
    val batteryDoD: Double,
    val systemVoltage: Double,
    val daysOfAutonomy: Int
)

/**
 * Battery sizing result
 */
data class BatterySizingResult(
    val totalCapacityWh: Double,
    val capacityAh: Double,
    val recommendedCapacityAh: Double,
    val cycleLifeEstimate: Int,
    val recommendedBatteryType: String = "LiFePO4"
)

/**
 * Panel sizing result
 */
data class PanelSizingResult(
    val requiredPanels: Int,
    val totalPowerWatts: Double,
    val dailyProductionKwh: Double,
    val estimatedRoofSpaceSqM: Double = 0.0,
    val estimatedRoofSpaceSqFt: Double = 0.0
)

/**
 * Inverter sizing result
 */
data class InverterSizingResult(
    val continuousPower: Double,
    val surgePower: Double,
    val recommendedSize: Double,
    val inverterType: String = "String Inverter"
)

/**
 * Economic analysis result
 */
data class EconomicAnalysisResult(
    val monthlySavings: Double,
    val yearlySavings: Double,
    val paybackPeriod: Double,
    val roi: Double,
    val netPresentValue: Double = 0.0,
    val levelizedCost: Double = 0.0
)