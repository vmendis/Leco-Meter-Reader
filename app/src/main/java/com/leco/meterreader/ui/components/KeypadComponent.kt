package com.leco.meterreader.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Large numeric keypad component for meter readings
 * 
 * @param onDigitPressed Callback when a digit is pressed
 * @param onBackspacePressed Callback when backspace is pressed
 * @param onClearPressed Callback when clear is pressed
 * @param modifier Modifier for the component
 */
@Composable
fun KeypadComponent(
    onDigitPressed: (Char) -> Unit,
    onBackspacePressed: () -> Unit,
    onClearPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Clear button
        KeypadButton(
            text = "CLEAR",
            onClick = onClearPressed,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        )
        
        // Number buttons (1-9)
        for (row in listOf(listOf('1', '2', '3'), listOf('4', '5', '6'), listOf('7', '8', '9'))) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                for (digit in row) {
                    KeypadButton(
                        text = digit.toString(),
                        onClick = { onDigitPressed(digit) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        
        // Bottom row with 0, decimal, and backspace
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            KeypadButton(
                text = "0",
                onClick = { onDigitPressed('0') },
                modifier = Modifier.weight(1f)
            )
            
            KeypadButton(
                text = ".",
                onClick = { onDigitPressed('.') },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
            
            KeypadButton(
                text = "⌫",
                onClick = onBackspacePressed,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            )
        }
    }
}

/**
 * Individual keypad button
 */
@Composable
private fun KeypadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors()
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp)),
        colors = colors,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp,
            disabledElevation = 0.dp
        )
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Reading display component that shows the current input
 */
@Composable
fun ReadingDisplay(
    value: String,
    label: String,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            shape = RoundedCornerShape(16.dp),
            color = if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant,
            shadowElevation = if (isError) 2.dp else 1.dp
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (value.isBlank()) "0.000" else value,
                    style = MaterialTheme.typography.headlineLarge,
                    color = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Field selector component for switching between input fields
 */
@Composable
fun FieldSelector(
    selectedField: String,
    onFieldSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val fields = listOf(
            "totalReading" to "Total",
            "rate1Reading" to "Rate 1",
            "rate2Reading" to "Rate 2", 
            "rate3Reading" to "Rate 3"
        )
        
        fields.forEach { (field, label) ->
            FilterChip(
                selected = selectedField == field,
                onClick = { onFieldSelected(field) },
                label = { 
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (selectedField == field) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unselectedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}