# Solar Planning Screen Implementation Plan

## Overview
This plan outlines the implementation of a comprehensive solar planning screen for the LECO Solar Meter Analyzer Android app. The screen will provide users with tools to calculate solar panel requirements, battery sizing, inverter specifications, and grid dependency analysis based on their electricity consumption patterns.

## Architecture Overview

### Data Layer
- **SolarPlanningData**: Data class for storing solar parameters and calculation results
- **SolarPlanningViewModel**: State management and business logic for solar calculations
- **SolarPlanningRepository**: Data persistence and retrieval (if needed)

### UI Layer
- **SolarScreen**: Main screen with tabbed sections for different solar planning aspects
- **SolarInputFields**: Form components for parameter input
- **SolarResultsDisplay**: Cards showing calculation results
- **SolarCharts**: Visualizations for solar production vs consumption
- **SolarScenarioComparison**: Tools for comparing different configurations

### Business Logic Layer
- **SolarCalculations**: Core algorithms for solar system sizing
- **BatterySizing**: Battery capacity calculations with DoD considerations
- **PanelSizing**: Solar panel quantity and wattage calculations
- **InverterSizing**: Inverter power requirements
- **GridAnalysis**: Grid independence calculations
- **CostAnalysis**: ROI and cost savings estimates

## Detailed Implementation Steps

### Phase 1: Foundation and Data Models

#### 1.1 SolarPlanningData Class
```kotlin
data class SolarPlanningData(
    // Input Parameters
    val dailyConsumption: Double = 0.0, // kWh
    val peakConsumption: Double = 0.0, // kWh
    val offPeakConsumption: Double = 0.0, // kWh
    val solarPanelWattage: Double = 400.0, // W
    val batteryCapacity: Double = 0.0, // Wh
    val batteryDoD: Double = 0.8, // 80%
    val inverterEfficiency: Double = 0.95, // 95%
    val wiringEfficiency: Double = 0.98, // 98%
    val temperatureCoefficient: Double = 0.85, // 85%
    val sunHours: Double = 5.5, // hours per day
    val electricityRate: Double = 0.25, // $/kWh
    
    // Calculated Results
    val requiredPanelCount: Int = 0,
    val totalPanelPower: Double = 0.0, // W
    val batterySizeAh: Double = 0.0, // Ah
    val inverterSize: Double = 0.0, // W
    val dailyProduction: Double = 0.0, // kWh
    val gridDependency: Double = 0.0, // percentage
    val monthlySavings: Double = 0.0, // $
    val yearlySavings: Double = 0.0, // $
    val paybackPeriod: Double = 0.0, // years
    co2Reduction: Double = 0.0 // kg/year
)
```

#### 1.2 SolarPlanningViewModel
```kotlin
@HiltViewModel
class SolarPlanningViewModel @Inject constructor(
    private val solarRepository: SolarPlanningRepository
) : ViewModel() {
    private val _uiState = mutableStateOf(SolarPlanningUiState())
    val uiState: State<SolarPlanningUiState> = _uiState
    
    fun updateParameter(parameter: SolarParameter, value: Double) {
        // Update parameter and recalculate
    }
    
    fun calculateSolarSystem() {
        // Perform all calculations
    }
    
    fun saveScenario(name: String) {
        // Save current configuration
    }
    
    fun loadScenario(id: String) {
        // Load saved configuration
    }
    
    fun compareScenarios(scenarioIds: List<String>) {
        // Compare multiple scenarios
    }
}
```

### Phase 2: String Resources and Localization

#### 2.1 Update strings.xml with comprehensive solar planning strings
```xml
<!-- Solar Planning Screen -->
<string name="solar_planning_title">Solar Planning Calculator</string>
<string name="solar_planning_subtitle">Design your perfect solar system</string>

<!-- Input Parameters -->
<string name="solar_daily_consumption">Daily Consumption (kWh)</string>
<string name="solar_peak_consumption">Peak Usage (kWh)</string>
<string name="solar_off_peak_consumption">Off-Peak Usage (kWh)</string>
<string name="solar_electricity_rate">Electricity Rate ($/kWh)</string>

<!-- Solar Panel Configuration -->
<string name="solar_panel_wattage">Panel Wattage</string>
<string name="solar_panel_options">300W, 400W, 500W, 600W</string>
<string name="solar_panel_count">Required Panels</string>

<!-- Battery Configuration -->
<string name="solar_battery_capacity">Battery Capacity (Wh)</string>
<string name="solar_battery_dod">Depth of Discharge (%)</string>
<string name="solar_battery_options">50%, 80%, 90%, 100%</string>

<!-- System Efficiency -->
<string name="solar_inverter_efficiency">Inverter Efficiency (%)</string>
<string name="solar_wiring_efficiency">Wiring Efficiency (%)</string>
<string name="solar_temperature_coefficient">Temperature Coefficient (%)</string>

<!-- Environmental Factors -->
<string name="solar_sun_hours">Daily Sun Hours</string>
<string name="solar_sun_hours_location">Location</string>
<string name="solar_sun_hours_monthly">Monthly Sun Hours</string>

<!-- Results -->
<string name="solar_total_power">Total System Power</string>
<string name="solar_daily_production">Daily Production</string>
<string name="solar_grid_dependency">Grid Dependency</string>
<string name="solar_monthly_savings">Monthly Savings</string>
<string name="solar_yearly_savings">Yearly Savings</string>
<string name="solar_payback_period">Payback Period</string>
<string name="solar_co2_reduction">CO₂ Reduction</string>

<!-- Scenarios -->
<string name="solar_scenarios">Scenarios</string>
<string name="solar_add_scenario">Add Scenario</string>
<string name="solar_compare_scenarios">Compare Scenarios</string>
<string name="solar_scenario_name">Scenario Name</string>

<!-- Charts -->
<string name="solar_production_chart">Solar Production vs Consumption</string>
<string name="solar_monthly_chart">Monthly Production Analysis</string>
<string name="solar_savings_chart">Savings Over Time</string>
```

### Phase 3: Core Calculation Algorithms

#### 3.1 Solar Calculations
```kotlin
object SolarCalculations {
    fun calculateRequiredPanels(
        dailyConsumption: Double,
        panelWattage: Double,
        sunHours: Double,
        systemEfficiency: Double
    ): Int {
        val dailyProductionPerPanel = (panelWattage * sunHours * systemEfficiency) / 1000
        return ceil(dailyConsumption / dailyProductionPerPanel).toInt()
    }
    
    fun calculateBatterySize(
        dailyConsumption: Double,
        daysOfAutonomy: Double = 1.0,
        dod: Double = 0.8
    ): Double {
        return (dailyConsumption * daysOfAutonomy) / dod
    }
    
    fun calculateInverterSize(
        peakConsumption: Double,
        totalPanelPower: Double,
        safetyFactor: Double = 1.25
    ): Double {
        return max(peakConsumption * 1000, totalPanelPower) * safetyFactor
    }
    
    fun calculateGridDependency(
        dailyProduction: Double,
        dailyConsumption: Double
    ): Double {
        return if (dailyProduction >= dailyConsumption) 0.0 else
            ((dailyConsumption - dailyProduction) / dailyConsumption) * 100
    }
    
    fun calculateSavings(
        dailyConsumption: Double,
        gridDependency: Double,
        electricityRate: Double
    ): Pair<Double, Double> {
        val monthlyGridUsage = (dailyConsumption * gridDependency / 100) * 30
        val monthlySavings = monthlyGridUsage * electricityRate
        val yearlySavings = monthlySavings * 12
        return Pair(monthlySavings, yearlySavings)
    }
}
```

#### 3.2 Battery Sizing with DoD
```kotlin
object BatterySizing {
    fun calculateBatteryRequirements(
        dailyConsumption: Double,
        desiredDaysOfAutonomy: Int = 1,
        dod: Double = 0.8,
        systemVoltage: Double = 48.0
    ): BatterySizingResult {
        val totalCapacityWh = dailyConsumption * desiredDaysOfAutonomy / dod
        val capacityAh = totalCapacityWh / systemVoltage
        
        return BatterySizingResult(
            totalCapacityWh = totalCapacityWh,
            capacityAh = capacityAh,
            recommendedCapacityAh = roundUpToStandardSize(capacityAh),
            cycleLifeEstimate = estimateCycleLife(dod)
        )
    }
    
    private fun estimateCycleLife(dod: Double): Int {
        return when (dod) {
            0.5 -> 2000
            0.8 -> 3000
            0.9 -> 1500
            1.0 -> 1000
            else -> 2000
        }
    }
}
```

### Phase 4: UI Components Implementation

#### 4.1 Solar Input Form Component
```kotlin
@Composable
fun SolarInputForm(
    parameters: SolarParameters,
    onParameterChange: (SolarParameter, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Consumption Inputs
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Consumption Patterns",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                NumberInputField(
                    value = parameters.dailyConsumption,
                    onValueChange = { onParameterChange(DAILY_CONSUMPTION, it) },
                    label = "Daily Consumption (kWh)",
                    min = 0.0,
                    max = 100.0,
                    step = 0.1
                )
                
                // Peak/Off-Peak inputs
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    NumberInputField(
                        value = parameters.peakConsumption,
                        onValueChange = { onParameterChange(PEAK_CONSUMPTION, it) },
                        label = "Peak Usage (kWh)",
                        min = 0.0,
                        max = 50.0,
                        step = 0.1,
                        modifier = Modifier.weight(1f)
                    )
                    
                    NumberInputField(
                        value = parameters.offPeakConsumption,
                        onValueChange = { onParameterChange(OFF_PEAK_CONSUMPTION, it) },
                        label = "Off-Peak Usage (kWh)",
                        min = 0.0,
                        max = 50.0,
                        step = 0.1,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        
        // Solar Configuration
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Solar Configuration",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                // Panel wattage selector
                SelectorField(
                    selectedValue = parameters.panelWattage.toString(),
                    options = listOf("300W", "400W", "500W", "600W"),
                    onOptionSelected = { 
                        val wattage = it.removeSuffix("W").toDouble()
                        onParameterChange(PANEL_WATTAGE, wattage)
                    },
                    label = "Panel Wattage"
                )
                
                NumberInputField(
                    value = parameters.sunHours,
                    onValueChange = { onParameterChange(SUN_HOURS, it) },
                    label = "Daily Sun Hours",
                    min = 1.0,
                    max = 12.0,
                    step = 0.1
                )
            }
        }
    }
}
```

#### 4.2 Solar Results Display
```kotlin
@Composable
fun SolarResultsDisplay(
    results: SolarResults,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // System Overview Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "System Overview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ResultValue(
                        value = "${results.requiredPanels}",
                        label = "Solar Panels",
                        unit = "panels"
                    )
                    
                    ResultValue(
                        value = "${results.totalPowerWatts}",
                        label = "Total Power",
                        unit = "W"
                    )
                    
                    ResultValue(
                        value = "${results.inverterSizeWatts}",
                        label = "Inverter",
                        unit = "W"
                    )
                }
            }
        }
        
        // Performance Metrics
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Performance Metrics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ResultValue(
                        value = "${String.format("%.1f", results.gridDependency)}%",
                        label = "Grid Dependency",
                        unit = "",
                        color = if (results.gridDependency < 20) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.error
                    )
                    
                    ResultValue(
                        value = "${String.format("%.1f", results.dailyProduction)}",
                        label = "Daily Production",
                        unit = "kWh"
                    )
                }
            }
        }
        
        // Economic Analysis
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Economic Analysis",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ResultValue(
                        value = "$${String.format("%.0f", results.monthlySavings)}",
                        label = "Monthly Savings",
                        unit = ""
                    )
                    
                    ResultValue(
                        value = "$${String.format("%.0f", results.yearlySavings)}",
                        label = "Yearly Savings",
                        unit = ""
                    )
                    
                    ResultValue(
                        value = "${String.format("%.1f", results.paybackPeriod)}",
                        label = "Payback",
                        unit = "years"
                    )
                }
            }
        }
    }
}
```

### Phase 5: Charts and Visualizations

#### 5.1 Solar Production vs Consumption Chart
```kotlin
@Composable
fun SolarProductionChart(
    productionData: List<Double>,
    consumptionData: List<Double>,
    timeLabels: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Solar Production vs Consumption",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ChartContainer(
                data = listOf(
                    ChartData(
                        name = "Production",
                        data = productionData,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    ChartData(
                        name = "Consumption",
                        data = consumptionData,
                        color = MaterialTheme.colorScheme.secondary
                    )
                ),
                labels = timeLabels,
                chartType = ChartType.LINE,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
```

#### 5.2 Monthly Sun Hours Configuration
```kotlin
@Composable
fun MonthlySunHoursConfig(
    monthlyHours: Map<Int, Double>,
    onHoursChange: (Int, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Monthly Sun Hours",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(monthlyHours.keys.toList()) { month ->
                    val hours = monthlyHours[month] ?: 0.0
                    MonthSunHourInput(
                        month = month,
                        hours = hours,
                        onHoursChange = { newHours ->
                            onHoursChange(month, newHours)
                        }
                    )
                }
            }
        }
    }
}
```

### Phase 6: Scenario Management

#### 6.1 Scenario Comparison Tool
```kotlin
@Composable
fun ScenarioComparison(
    scenarios: List<SolarScenario>,
    onScenarioSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Scenario selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Compare Scenarios",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(onClick = { /* Add scenario */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Scenario")
                    }
                }
                
                // Scenario chips
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    scenarios.forEach { scenario ->
                        FilterChip(
                            selected = scenario.isSelected,
                            onClick = { onScenarioSelect(scenario.id) },
                            label = { Text(scenario.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }
        }
        
        // Comparison results
        if (scenarios.filter { it.isSelected }.size >= 2) {
            ScenarioComparisonTable(
                scenarios = scenarios.filter { it.isSelected }
            )
        }
    }
}
```

### Phase 7: Responsive Layout Integration

#### 7.1 Main Solar Screen Layout
```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolarScreen(
    viewModel: SolarPlanningViewModel = hiltViewModel(),
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isTablet = isTablet()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solar Planning") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        if (isTablet) {
            // Tablet layout - side by side
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left column - Input forms
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SolarInputForm(
                        parameters = uiState.parameters,
                        onParameterChange = viewModel::updateParameter
                    )
                    
                    MonthlySunHoursConfig(
                        monthlyHours = uiState.monthlySunHours,
                        onHoursChange = viewModel::updateMonthlySunHours
                    )
                }
                
                // Right column - Results and charts
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SolarResultsDisplay(results = uiState.results)
                    
                    SolarProductionChart(
                        productionData = uiState.productionData,
                        consumptionData = uiState.consumptionData,
                        timeLabels = uiState.timeLabels
                    )
                    
                    ScenarioComparison(
                        scenarios = uiState.scenarios,
                        onScenarioSelect = viewModel::selectScenario
                    )
                }
            }
        } else {
            // Phone layout - single column
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    SolarInputForm(
                        parameters = uiState.parameters,
                        onParameterChange = viewModel::updateParameter
                    )
                }
                
                item {
                    MonthlySunHoursConfig(
                        monthlyHours = uiState.monthlySunHours,
                        onHoursChange = viewModel::updateMonthlySunHours
                    )
                }
                
                item {
                    SolarResultsDisplay(results = uiState.results)
                }
                
                item {
                    SolarProductionChart(
                        productionData = uiState.productionData,
                        consumptionData = uiState.consumptionData,
                        timeLabels = uiState.timeLabels
                    )
                }
                
                item {
                    ScenarioComparison(
                        scenarios = uiState.scenarios,
                        onScenarioSelect = viewModel::selectScenario
                    )
                }
            }
        }
    }
}
```

## Implementation Timeline

### Week 1: Foundation and Data Models
- [ ] Create SolarPlanningData class
- [ ] Implement SolarPlanningViewModel
- [ ] Add comprehensive string resources
- [ ] Create calculation algorithms

### Week 2: Core UI Components
- [ ] Implement SolarInputForm component
- [ ] Create SolarResultsDisplay component
- [ ] Add validation and error handling
- [ ] Implement real-time calculation updates

### Week 3: Charts and Visualizations
- [ ] Create SolarProductionChart
- [ ] Implement MonthlySunHoursConfig
- [ ] Add scenario comparison charts
- [ ] Create savings over time visualization

### Week 4: Advanced Features
- [ ] Implement scenario management
- [ ] Add responsive layout for tablets
- [ ] Create export functionality for results
- [ ] Add location-based sun hours data

### Week 5: Testing and Optimization
- [ ] Unit tests for calculation algorithms
- [ ] UI testing for all components
- [ ] Performance optimization
- [ ] User experience refinements

## Technical Considerations

### Performance
- Use Compose state hoisting for efficient re-renders
- Implement lazy loading for large datasets
- Use memoization for expensive calculations

### Accessibility
- Ensure all interactive elements have proper content descriptions
- Support screen readers with semantic UI elements
- Provide sufficient color contrast for all UI elements

### Data Persistence
- Save user scenarios locally using Room database
- Implement cloud sync for scenarios (optional)
- Provide import/export functionality for configurations

### Error Handling
- Validate user inputs with appropriate ranges
- Handle calculation edge cases gracefully
- Provide clear error messages for invalid inputs

## Success Metrics

1. **User Engagement**: Users complete solar planning calculations
2. **Accuracy**: Calculations match industry standards
3. **User Satisfaction**: High ratings for ease of use and usefulness
4. **Performance**: Fast calculation response times
5. **Adoption**: Regular use of scenario comparison features

## Dependencies

- Jetpack Compose for UI
- Material 3 for design system
- Hilt for dependency injection
- Room for data persistence (optional)
- Charts library for visualizations

This comprehensive plan provides a clear roadmap for implementing a professional solar planning screen that will help users make informed decisions about their solar energy investments.