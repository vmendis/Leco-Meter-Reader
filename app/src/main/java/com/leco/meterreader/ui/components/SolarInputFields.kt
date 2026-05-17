package com.leco.meterreader.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leco.meterreader.R
import com.leco.meterreader.viewmodel.SolarParameter
import com.leco.meterreader.viewmodel.SolarPlanningViewModel
import java.text.DecimalFormat

/**
 * Reusable solar input field component
 */
@Composable
fun SolarInputField(
    label: String,
    value: Double,
    onValueChange: (Double) -> Unit,
    unit: String,
    parameter: SolarParameter,
    viewModel: SolarPlanningViewModel,
    minValue: Double = 0.0,
    maxValue: Double = Double.MAX_VALUE,
    step: Double = 1.0,
    decimalPlaces: Int = 1,
    helperText: String? = null,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    val decimalFormat = DecimalFormat("#.${"0".repeat(decimalPlaces)}")
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = unit,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = decimalFormat.format(value),
                onValueChange = { input ->
                    try {
                        val numericValue = input.toDouble()
                        if (numericValue >= minValue && numericValue <= maxValue) {
                            onValueChange(numericValue)
                        }
                    } catch (e: NumberFormatException) {
                        // Ignore invalid input
                    }
                },
                modifier = Modifier.weight(1f),
                singleLine = true,
                isError = isError,
                supportingText = {
                    if (isError) {
                        Text(
                            text = "Value must be between $minValue and $maxValue",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else if (helperText != null) {
                        Text(
                            text = helperText,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                )
            )
            
            // Increment button
            IconButton(
                onClick = {
                    val newValue = minOf(value + step, maxValue)
                    onValueChange(newValue)
                },
                enabled = value < maxValue
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Increment",
                    tint = if (value < maxValue) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Decrement button
            IconButton(
                onClick = {
                    val newValue = maxOf(value - step, minValue)
                    onValueChange(newValue)
                },
                enabled = value > minValue
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Decrement",
                    tint = if (value > minValue) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Solar consumption input section
 */
@Composable
fun SolarConsumptionInputs(
    viewModel: SolarPlanningViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val parameters = uiState.parameters
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.solar_consumption_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            SolarInputField(
                label = stringResource(R.string.solar_daily_consumption),
                value = parameters.dailyConsumption,
                onValueChange = { viewModel.updateParameter(SolarParameter.DAILY_CONSUMPTION, it) },
                unit = stringResource(R.string.unit_kwh),
                parameter = SolarParameter.DAILY_CONSUMPTION,
                viewModel = viewModel,
                minValue = 0.1,
                maxValue = 100.0,
                step = 0.1,
                decimalPlaces = 1,
                helperText = stringResource(R.string.solar_daily_consumption_helper)
            )
            
            SolarInputField(
                label = stringResource(R.string.solar_peak_consumption),
                value = parameters.peakConsumption,
                onValueChange = { viewModel.updateParameter(SolarParameter.PEAK_CONSUMPTION, it) },
                unit = stringResource(R.string.unit_kwh),
                parameter = SolarParameter.PEAK_CONSUMPTION,
                viewModel = viewModel,
                minValue = 0.0,
                maxValue = 50.0,
                step = 0.1,
                decimalPlaces = 1,
                helperText = stringResource(R.string.solar_peak_consumption_helper)
            )
            
            SolarInputField(
                label = stringResource(R.string.solar_off_peak_consumption),
                value = parameters.offPeakConsumption,
                onValueChange = { viewModel.updateParameter(SolarParameter.OFF_PEAK_CONSUMPTION, it) },
                unit = stringResource(R.string.unit_kwh),
                parameter = SolarParameter.OFF_PEAK_CONSUMPTION,
                viewModel = viewModel,
                minValue = 0.0,
                maxValue = 50.0,
                step = 0.1,
                decimalPlaces = 1,
                helperText = stringResource(R.string.solar_off_peak_consumption_helper)
            )
        }
    }
}

/**
 * Solar panel configuration section
 */
@Composable
fun SolarPanelInputs(
    viewModel: SolarPlanningViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val parameters = uiState.parameters
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.solar_panel_configuration),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            SolarInputField(
                label = stringResource(R.string.solar_panel_wattage),
                value = parameters.solarPanelWattage,
                onValueChange = { viewModel.updateParameter(SolarParameter.PANEL_WATTAGE, it) },
                unit = "W",
                parameter = SolarParameter.PANEL_WATTAGE,
                viewModel = viewModel,
                minValue = 100.0,
                maxValue = 1000.0,
                step = 50.0,
                decimalPlaces = 0,
                helperText = stringResource(R.string.solar_panel_wattage_helper)
            )
            
            SolarInputField(
                label = stringResource(R.string.solar_sun_hours),
                value = parameters.sunHours,
                onValueChange = { viewModel.updateParameter(SolarParameter.SUN_HOURS, it) },
                unit = "h",
                parameter = SolarParameter.SUN_HOURS,
                viewModel = viewModel,
                minValue = 1.0,
                maxValue = 12.0,
                step = 0.5,
                decimalPlaces = 1,
                helperText = stringResource(R.string.solar_sun_hours_helper)
            )
        }
    }
}

/**
 * Battery configuration section
 */
@Composable
fun SolarBatteryInputs(
    viewModel: SolarPlanningViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val parameters = uiState.parameters
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.solar_battery_configuration),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            SolarInputField(
                label = stringResource(R.string.solar_battery_capacity),
                value = parameters.batteryCapacity,
                onValueChange = { viewModel.updateParameter(SolarParameter.BATTERY_CAPACITY, it) },
                unit = stringResource(R.stringunit_wh),
                parameter = SolarParameter.BATTERY_CAPACITY,
                viewModel = viewModel,
                minValue = 0.0,
                maxValue = 50000.0,
                step = 100.0,
                decimalPlaces = 0,
                helperText = stringResource(R.string.solar_battery_capacity_helper)
            )
            
            SolarInputField(
                label = stringResource(R.string.solar_battery_dod),
                value = parameters.batteryDoD,
                onValueChange = { viewModel.updateParameter(SolarParameter.BATTERY_DOD, it) },
                unit = "%",
                parameter = SolarParameter.BATTERY_DOD,
                viewModel = viewModel,
                minValue = 0.1,
                maxValue = 1.0,
                step = 0.05,
                decimalPlaces = 2,
                helperText = stringResource(R.string.solar_battery_dod_helper)
            )
            
            SolarInputField(
                label = stringResource(R.string.solar_days_of_autonomy),
                value = parameters.daysOfAutonomy.toDouble(),
                onValueChange = { 
                    viewModel.updateParameter(SolarParameter.DAYS_OF_AUTONOMY, it)
                },
                unit = "days",
                parameter = SolarParameter.DAYS_OF_AUTONOMY,
                viewModel = viewModel,
                minValue = 1.0,
                maxValue = 14.0,
                step = 1.0,
                decimalPlaces = 0,
                helperText = stringResource(R.string.solar_days_of_autonomy_helper)
            )
        }
    }
}

/**
 * System efficiency section
 */
@Composable
fun SolarEfficiencyInputs(
    viewModel: SolarPlanningViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val parameters = uiState.parameters
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.solar_system_efficiency),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            SolarInputField(
                label = stringResource(R.string.solar_inverter_efficiency),
                value = parameters.inverterEfficiency,
                onValueChange = { viewModel.updateParameter(SolarParameter.INVERTER_EFFICIENCY, it) },
                unit = "%",
                parameter = SolarParameter.INVERTER_EFFICIENCY,
                viewModel = viewModel,
                minValue = 0.8,
                maxValue = 1.0,
                step = 0.01,
                decimalPlaces = 2,
                helperText = stringResource(R.string.solar_inverter_efficiency_helper)
            )
            
            SolarInputField(
                label = stringResource(R.string.solar_wiring_efficiency),
                value = parameters.wiringEfficiency,
                onValueChange = { viewModel.updateParameter(SolarParameter.WIRING_EFFICIENCY, it) },
                unit = "%",
                parameter = SolarParameter.WIRING_EFFICIENCY,
                viewModel = viewModel,
                minValue = 0.9,
                maxValue = 1.0,
                step = 0.01,
                decimalPlaces = 2,
                helperText = stringResource(R.string.solar_wiring_efficiency_helper)
            )
            
            SolarInputField(
                label = stringResource(R.string.solar_temperature_coefficient),
                value = parameters.temperatureCoefficient,
                onValueChange = { viewModel.updateParameter(SolarParameter.TEMPERATURE_COEFFICIENT, it) },
                unit = "%",
                parameter = SolarParameter.TEMPERATURE_COEFFICIENT,
                viewModel = viewModel,
                minValue = 0.8,
                maxValue = 1.0,
                step = 0.01,
                decimalPlaces = 2,
                helperText = stringResource(R.string.solar_temperature_coefficient_helper)
            )
        }
    }
}

/**
 * Economic analysis section
 */
@Composable
fun SolarEconomicInputs(
    viewModel: SolarPlanningViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val parameters = uiState.parameters
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.solar_economic_analysis),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            SolarInputField(
                label = stringResource(R.string.solar_electricity_rate),
                value = parameters.electricityRate,
                onValueChange = { viewModel.updateParameter(SolarParameter.ELECTRICITY_RATE, it) },
                unit = stringResource(R.stringunit_usd_per_kwh),
                parameter = SolarParameter.ELECTRICITY_RATE,
                viewModel = viewModel,
                minValue = 0.01,
                maxValue = 1.0,
                step = 0.01,
                decimalPlaces = 2,
                helperText = stringResource(R.string.solar_electricity_rate_helper)
            )
            
            SolarInputField(
                label = stringResource(R.string.solar_system_voltage),
                value = parameters.systemVoltage,
                onValueChange = { viewModel.updateParameter(SolarParameter.SYSTEM_VOLTAGE, it) },
                unit = "V",
                parameter = SolarParameter.SYSTEM_VOLTAGE,
                viewModel = viewModel,
                minValue = 12.0,
                maxValue = 48.0,
                step = 12.0,
                decimalPlaces = 0,
                helperText = stringResource(R.string.solar_system_voltage_helper)
            )
        }
    }
}

/**
 * Monthly sun hours configuration component
 */
@Composable
fun SolarMonthlySunHoursInputs(
    viewModel: SolarPlanningViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val parameters = uiState.parameters
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.solar_sun_hours_monthly),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(
                    onClick = {
                        // Reset to default monthly sun hours
                        viewModel.resetMonthlySunHours()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Reset monthly sun hours",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Location input
            OutlinedTextField(
                value = parameters.location,
                onValueChange = { viewModel.updateLocation(it) },
                label = { Text(stringResource(R.string.solar_sun_hours_location)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Monthly sun hours grid
            val months = listOf(
                1 to R.string.solar_sun_hours_january,
                2 to R.string.solar_sun_hours_february,
                3 to R.string.solar_sun_hours_march,
                4 to R.string.solar_sun_hours_april,
                5 to R.string.solar_sun_hours_may,
                6 to R.string.solar_sun_hours_june,
                7 to R.string.solar_sun_hours_july,
                8 to R.string.solar_sun_hours_august,
                9 to R.string.solar_sun_hours_september,
                10 to R.string.solar_sun_hours_october,
                11 to R.string.solar_sun_hours_november,
                12 to R.string.solar_sun_hours_december
            )
            
            // Grid layout for monthly inputs (3 columns for better space usage)
            val decimalFormat = DecimalFormat("#.0")
            
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                months.chunked(3).forEach { monthChunk ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        monthChunk.forEach { (month, stringRes) ->
                            val monthHours = parameters.monthlySunHours[month] ?: 5.0
                            
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = stringResource(stringRes),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                                
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = {
                                            val newValue = maxOf(0.0, monthHours - 0.5)
                                            viewModel.updateMonthlySunHours(month, newValue)
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Remove,
                                            contentDescription = "Decrease",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    
                                    Text(
                                        text = decimalFormat.format(monthHours),
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.width(40.dp),
                                        textAlign = TextAlign.Center
                                    )
                                    
                                    IconButton(
                                        onClick = {
                                            val newValue = minOf(12.0, monthHours + 0.5)
                                            viewModel.updateMonthlySunHours(month, newValue)
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Increase",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Average sun hours display
            val averageHours = parameters.monthlySunHours.values.average()
            Text(
                text = stringResource(
                    R.string.solar_average_sun_hours,
                    decimalFormat.format(averageHours)
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Complete solar planning input form
 */
@Composable
fun SolarPlanningInputs(
    viewModel: SolarPlanningViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.solar_planning_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Reset button
            TextButton(
                onClick = { viewModel.resetToDefaults() }
            ) {
                Text(
                    text = stringResource(R.string.solar_reset_defaults),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Input sections
        SolarConsumptionInputs(viewModel = viewModel)
        SolarPanelInputs(viewModel = viewModel)
        SolarMonthlySunHoursInputs(viewModel = viewModel)
        SolarBatteryInputs(viewModel = viewModel)
        SolarEfficiencyInputs(viewModel = viewModel)
        SolarEconomicInputs(viewModel = viewModel)
    }
}