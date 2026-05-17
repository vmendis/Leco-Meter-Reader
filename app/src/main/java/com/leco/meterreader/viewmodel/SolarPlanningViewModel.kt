package com.leco.meterreader.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leco.meterreader.data.model.*
import com.leco.meterreader.util.SolarCalculations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

@HiltViewModel
class SolarPlanningViewModel @Inject constructor() : ViewModel() {
    
    // Private mutable state
    private val _uiState = MutableStateFlow(SolarPlanningUiState())
    val uiState: StateFlow<SolarPlanningUiState> = _uiState.asStateFlow()
    
    // Public state access
    val isLoading: Boolean get() = _uiState.value.isLoading
    val error: String? get() = _uiState.value.error
    val parameters: SolarPlanningData get() = _uiState.value.parameters
    val results: SolarPlanningData get() = _uiState.value.results
    val scenarios: List<SolarScenario> get() = _uiState.value.scenarios
    val selectedScenarios: Set<String> get() = _uiState.value.selectedScenarios
    
    // Debounce timer for real-time calculations
    private var calculationJob: kotlinx.coroutines.Job? = null
    
    /**
     * Update a parameter and trigger recalculation
     */
    fun updateParameter(parameter: SolarParameter, value: Double) {
        val currentParams = _uiState.value.parameters
        
        val newParams = when (parameter) {
            SolarParameter.DAILY_CONSUMPTION -> currentParams.copy(
                dailyConsumption = value,
                isModified = true
            )
            SolarParameter.PEAK_CONSUMPTION -> currentParams.copy(
                peakConsumption = value,
                isModified = true
            )
            SolarParameter.OFF_PEAK_CONSUMPTION -> currentParams.copy(
                offPeakConsumption = value,
                isModified = true
            )
            SolarParameter.PANEL_WATTAGE -> currentParams.copy(
                solarPanelWattage = value,
                isModified = true
            )
            SolarParameter.BATTERY_CAPACITY -> currentParams.copy(
                batteryCapacity = value,
                isModified = true
            )
            SolarParameter.BATTERY_DOD -> currentParams.copy(
                batteryDoD = value,
                isModified = true
            )
            SolarParameter.INVERTER_EFFICIENCY -> currentParams.copy(
                inverterEfficiency = value,
                isModified = true
            )
            SolarParameter.WIRING_EFFICIENCY -> currentParams.copy(
                wiringEfficiency = value,
                isModified = true
            )
            SolarParameter.TEMPERATURE_COEFFICIENT -> currentParams.copy(
                temperatureCoefficient = value,
                isModified = true
            )
            SolarParameter.SUN_HOURS -> currentParams.copy(
                sunHours = value,
                isModified = true
            )
            SolarParameter.ELECTRICITY_RATE -> currentParams.copy(
                electricityRate = value,
                isModified = true
            )
            SolarParameter.SYSTEM_VOLTAGE -> currentParams.copy(
                systemVoltage = value,
                isModified = true
            )
            SolarParameter.DAYS_OF_AUTONOMY -> currentParams.copy(
                daysOfAutonomy = value.toInt(),
                isModified = true
            )
        }
        
        _uiState.value = _uiState.value.copy(
            parameters = newParams,
            error = null
        )
        
        // Debounce calculation to avoid excessive recalculations
        calculationJob?.cancel()
        calculationJob = viewModelScope.launch {
            kotlinx.coroutines.delay(500) // 500ms debounce
            performCalculations(newParams)
        }
    }
    
    /**
     * Update monthly sun hours for a specific month
     */
    fun updateMonthlySunHours(month: Int, hours: Double) {
        val currentParams = _uiState.value.parameters
        val newMonthlyHours = SolarCalculations.updateMonthlySunHours(
            currentParams.monthlySunHours,
            month,
            hours
        )
        
        val newParams = currentParams.copy(
            monthlySunHours = newMonthlyHours,
            sunHours = SolarCalculations.calculateAverageMonthlySunHours(newMonthlyHours),
            isModified = true
        )
        
        _uiState.value = _uiState.value.copy(
            parameters = newParams,
            error = null
        )
        
        // Trigger recalculation
        calculationJob?.cancel()
        calculationJob = viewModelScope.launch {
            kotlinx.coroutines.delay(300)
            performCalculations(newParams)
        }
    }
    
    /**
     * Update location string
     */
    fun updateLocation(location: String) {
        val currentParams = _uiState.value.parameters
        
        val newParams = currentParams.copy(
            location = location,
            isModified = true
        )
        
        _uiState.value = _uiState.value.copy(
            parameters = newParams,
            isLoading = true,
            error = null
        )
        
        // Trigger debounced calculation
        calculationJob?.cancel()
        calculationJob = viewModelScope.launch {
            kotlinx.coroutines.delay(500) // 500ms debounce
            performCalculations(newParams)
        }
    }
    
    /**
     * Reset monthly sun hours to defaults
     */
    fun resetMonthlySunHours() {
        val currentParams = _uiState.value.parameters
        
        // Reset to default monthly sun hours (5.0 hours for all months)
        val defaultMonthlyHours = (1..12).associateWith { 5.0 }
        
        val newParams = currentParams.copy(
            monthlySunHours = defaultMonthlyHours,
            sunHours = SolarCalculations.calculateAverageMonthlySunHours(defaultMonthlyHours),
            isModified = true
        )
        
        _uiState.value = _uiState.value.copy(
            parameters = newParams,
            isLoading = true,
            error = null
        )
        
        // Trigger debounced calculation
        calculationJob?.cancel()
        calculationJob = viewModelScope.launch {
            kotlinx.coroutines.delay(500) // 500ms debounce
            performCalculations(newParams)
        }
    }
    
    /**
     * Perform all solar calculations
     */
    private fun performCalculations(params: SolarPlanningData) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val startTime = System.currentTimeMillis()
                
                // Calculate system efficiency
                val systemEfficiency = params.systemEfficiency
                
                // Calculate required panels
                val requiredPanels = SolarCalculations.calculateRequiredPanels(
                    params.dailyConsumption,
                    params.solarPanelWattage,
                    params.sunHours,
                    systemEfficiency
                )
                
                // Calculate total panel power
                val totalPanelPower = SolarCalculations.calculateTotalPanelPower(
                    requiredPanels,
                    params.solarPanelWattage
                )
                
                // Calculate roof space requirements
                val roofSpace = SolarCalculations.calculateRoofSpaceRequirements(
                    requiredPanels
                )
                
                // Calculate battery size if not provided
                val batterySizing = if (params.batteryCapacity > 0) {
                    BatterySizingResult(
                        totalCapacityWh = params.batteryCapacity,
                        capacityAh = params.batteryCapacity / params.systemVoltage,
                        recommendedCapacityAh = params.batteryCapacity / params.systemVoltage,
                        cycleLifeEstimate = SolarCalculations.estimateCycleLife(params.batteryDoD)
                    )
                } else {
                    SolarCalculations.calculateBatterySize(
                        params.dailyConsumption,
                        params.daysOfAutonomy,
                        params.batteryDoD,
                        params.systemVoltage
                    )
                }
                
                // Calculate inverter size
                val inverterSizing = SolarCalculations.calculateInverterSize(
                    params.peakConsumption,
                    totalPanelPower
                )
                
                // Calculate daily production
                val dailyProduction = SolarCalculations.calculateDailyProduction(
                    totalPanelPower,
                    params.sunHours,
                    systemEfficiency
                )
                
                // Calculate grid dependency
                val gridDependency = SolarCalculations.calculateGridDependency(
                    dailyProduction,
                    params.dailyConsumption
                )
                
                // Calculate economic analysis
                val systemCost = SolarCalculations.estimateSystemCost(
                    requiredPanels,
                    params.solarPanelWattage,
                    batterySizing.totalCapacityWh,
                    inverterSizing.recommendedSize
                )
                
                val economicAnalysis = SolarCalculations.calculateEconomicAnalysis(
                    params.dailyConsumption,
                    gridDependency,
                    params.electricityRate,
                    systemCost,
                    requiredPanels,
                    batterySizing.totalCapacityWh,
                    inverterSizing.recommendedSize
                )
                
                // Calculate CO2 reduction
                val co2Reduction = SolarCalculations.calculateCO2Reduction(dailyProduction)
                
                // Calculate monthly production data
                val monthlyProduction = SolarCalculations.calculateMonthlyProduction(
                    totalPanelPower,
                    params.monthlySunHours,
                    systemEfficiency
                )
                
                // Create results data
                val results = params.copy(
                    requiredPanelCount = requiredPanels,
                    totalPanelPower = totalPanelPower,
                    roofSpaceSqM = roofSpace.first,
                    roofSpaceSqFt = roofSpace.second,
                    batterySizeWh = batterySizing.totalCapacityWh,
                    batterySizeAh = batterySizing.capacityAh,
                    inverterSize = inverterSizing.recommendedSize,
                    dailyProduction = dailyProduction,
                    gridDependency = gridDependency,
                    monthlySavings = economicAnalysis.monthlySavings,
                    yearlySavings = economicAnalysis.yearlySavings,
                    paybackPeriod = economicAnalysis.paybackPeriod,
                    co2Reduction = co2Reduction,
                    systemCost = systemCost
                )
                
                val calculationTime = System.currentTimeMillis() - startTime
                
                // Log calculation
                val log = SolarCalculationLog(
                    parameters = params,
                    results = results,
                    calculationTimeMs = calculationTime
                )
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    results = results,
                    calculationHistory = listOf(log) + _uiState.value.calculationHistory.take(9) // Keep last 10
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Calculation error: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Save current configuration as a scenario
     */
    fun saveScenario(name: String) {
        val currentParams = _uiState.value.parameters
        val currentResults = _uiState.value.results
        
        val scenario = SolarScenario(
            name = name,
            data = currentParams.copy(
                scenarioName = name,
                createdAt = LocalDate.now(),
                isModified = false
            ),
            createdAt = LocalDate.now()
        )
        
        _uiState.value = _uiState.value.copy(
            scenarios = listOf(scenario) + _uiState.value.scenarios
        )
    }
    
    /**
     * Load a scenario
     */
    fun loadScenario(scenarioId: String) {
        val scenario = _uiState.value.scenarios.find { it.id == scenarioId }
        if (scenario != null) {
            _uiState.value = _uiState.value.copy(
                parameters = scenario.data,
                error = null
            )
            
            // Trigger recalculation with loaded parameters
            viewModelScope.launch {
                kotlinx.coroutines.delay(300)
                performCalculations(scenario.data)
            }
        }
    }
    
    /**
     * Delete a scenario
     */
    fun deleteScenario(scenarioId: String) {
        _uiState.value = _uiState.value.copy(
            scenarios = _uiState.value.scenarios.filter { it.id != scenarioId }
        )
    }
    
    /**
     * Toggle scenario selection for comparison
     */
    fun toggleScenarioSelection(scenarioId: String) {
        val currentSelection = _uiState.value.selectedScenarios
        val newSelection = if (scenarioId in currentSelection) {
            currentSelection - scenarioId
        } else {
            currentSelection + scenarioId
        }
        
        _uiState.value = _uiState.value.copy(
            selectedScenarios = newSelection
        )
    }
    
    /**
     * Clear all selected scenarios
     */
    fun clearScenarioSelection() {
        _uiState.value = _uiState.value.copy(
            selectedScenarios = emptySet()
        )
    }
    
    /**
     * Export current results
     */
    fun exportResults(): String {
        val results = _uiState.value.results
        return """
            Solar Planning Results - ${results.scenarioName}
            Generated: ${LocalDate.now()}
            
            System Configuration:
            - Daily Consumption: ${results.dailyConsumption} kWh
            - Solar Panel Wattage: ${results.solarPanelWattage}W
            - Required Panels: ${results.requiredPanelCount}
            - Total Panel Power: ${results.totalPanelPower}W
            - Battery Capacity: ${results.batterySizeWh}Wh (${results.batterySizeAh}Ah)
            - Inverter Size: ${results.inverterSize}W
            
            Performance:
            - Daily Production: ${results.dailyProduction} kWh
            - Grid Dependency: ${results.gridDependency}%
            - Grid Independence: ${results.gridIndependence}%
            
            Economic Analysis:
            - Monthly Savings: $${results.monthlySavings}
            - Yearly Savings: $${results.yearlySavings}
            - Payback Period: ${results.paybackPeriod} years
            - ROI: ${results.roi}%
            
            Environmental Impact:
            - CO2 Reduction: ${results.co2Reduction} kg/year
            - System Cost: $${results.systemCost}
        """.trimIndent()
    }
    
    /**
     * Reset to default values
     */
    fun resetToDefaults() {
        val defaultParams = SolarPlanningData()
        _uiState.value = SolarPlanningUiState(
            parameters = defaultParams,
            results = defaultParams,
            scenarios = _uiState.value.scenarios,
            selectedScenarios = emptySet(),
            calculationHistory = emptyList()
        )
        
        // Trigger initial calculation
        viewModelScope.launch {
            kotlinx.coroutines.delay(500)
            performCalculations(defaultParams)
        }
    }
    
    /**
     * Get comparison data for selected scenarios
     */
    fun getComparisonData(): List<SolarScenario> {
        return _uiState.value.scenarios.filter { it.id in _uiState.value.selectedScenarios }
    }
    
    /**
     * Validate current parameters
     */
    fun validateParameters(): Boolean {
        return _uiState.value.parameters.isValid
    }
    
    /**
     * Get validation errors
     */
    fun getValidationErrors(): List<String> {
        val errors = mutableListOf<String>()
        val params = _uiState.value.parameters
        
        if (params.dailyConsumption <= 0) {
            errors.add("Daily consumption must be greater than 0")
        }
        if (params.solarPanelWattage <= 0) {
            errors.add("Panel wattage must be greater than 0")
        }
        if (params.sunHours <= 0) {
            errors.add("Sun hours must be greater than 0")
        }
        if (params.electricityRate <= 0) {
            errors.add("Electricity rate must be greater than 0")
        }
        if (params.batteryDoD <= 0 || params.batteryDoD > 1) {
            errors.add("Battery depth of discharge must be between 0 and 1")
        }
        if (params.inverterEfficiency <= 0 || params.inverterEfficiency > 1) {
            errors.add("Inverter efficiency must be between 0 and 1")
        }
        if (params.wiringEfficiency <= 0 || params.wiringEfficiency > 1) {
            errors.add("Wiring efficiency must be between 0 and 1")
        }
        if (params.temperatureCoefficient <= 0 || params.temperatureCoefficient > 1) {
            errors.add("Temperature coefficient must be between 0 and 1")
        }
        
        return errors
    }
}

/**
 * Enum for solar parameters to avoid magic strings
 */
enum class SolarParameter {
    DAILY_CONSUMPTION,
    PEAK_CONSUMPTION,
    OFF_PEAK_CONSUMPTION,
    PANEL_WATTAGE,
    BATTERY_CAPACITY,
    BATTERY_DOD,
    INVERTER_EFFICIENCY,
    WIRING_EFFICIENCY,
    TEMPERATURE_COEFFICIENT,
    SUN_HOURS,
    ELECTRICITY_RATE,
    SYSTEM_VOLTAGE,
    DAYS_OF_AUTONOMY
}