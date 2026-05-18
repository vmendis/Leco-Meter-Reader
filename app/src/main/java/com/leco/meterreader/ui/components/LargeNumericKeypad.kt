package com.leco.meterreader.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A large numeric keypad designed for easy meter reading entry.
 * Features large buttons with clear digits and a clear button.
 *
 * @param onNumberClick Called when a number button is pressed
 * @param onClearClick Called when the clear button is pressed
 * @param modifier Modifier for styling
 */
@Composable
fun LargeNumericKeypad(
    onNumberClick: (String) -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonSize = 64.dp
    val cornerRadius = 12.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Row 1: 1, 2, 3
        KeypadRow(
            buttons = listOf("1", "2", "3"),
            onNumberClick = onNumberClick,
            buttonSize = buttonSize,
            cornerRadius = cornerRadius
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Row 2: 4, 5, 6
        KeypadRow(
            buttons = listOf("4", "5", "6"),
            onNumberClick = onNumberClick,
            buttonSize = buttonSize,
            cornerRadius = cornerRadius
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Row 3: 7, 8, 9
        KeypadRow(
            buttons = listOf("7", "8", "9"),
            onNumberClick = onNumberClick,
            buttonSize = buttonSize,
            cornerRadius = cornerRadius
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Row 4: ., 0, C (clear)
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Decimal point button
            KeypadButton(
                text = ".",
                onClick = { onNumberClick(".") },
                modifier = Modifier.size(buttonSize),
                cornerRadius = cornerRadius
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Zero button
            KeypadButton(
                text = "0",
                onClick = { onNumberClick("0") },
                modifier = Modifier.size(buttonSize),
                cornerRadius = cornerRadius
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Clear button
            Button(
                onClick = onClearClick,
                modifier = Modifier.size(buttonSize),
                shape = RoundedCornerShape(cornerRadius),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text(
                    text = "C",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * A row of keypad buttons.
 */
@Composable
private fun KeypadRow(
    buttons: List<String>,
    onNumberClick: (String) -> Unit,
    buttonSize: androidx.compose.ui.unit.Dp,
    cornerRadius: androidx.compose.ui.unit.Dp
) {
    Row {
        buttons.forEachIndexed { index, text ->
            if (index > 0) {
                Spacer(modifier = Modifier.width(8.dp))
            }
            KeypadButton(
                text = text,
                onClick = { onNumberClick(text) },
                modifier = Modifier.size(buttonSize),
                cornerRadius = cornerRadius
            )
        }
    }
}

/**
 * A single keypad button with large text.
 */
@Composable
private fun KeypadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: androidx.compose.ui.unit.Dp
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}