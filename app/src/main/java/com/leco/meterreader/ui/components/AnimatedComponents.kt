package com.leco.meterreader.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Animated reading display with smooth transitions
 */
@Composable
fun AnimatedReadingDisplay(
    value: String,
    label: String,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    var lastValue by remember { value }
    
    // Trigger animation when value changes
    LaunchedEffect(value) {
        if (value != lastValue) {
            isVisible = false
            delay(50) // Brief pause before showing new value
            isVisible = true
            lastValue = value
        }
    }
    
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
        
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(
                animationSpec = tween(300, easing = LinearEasing)
            ) + expandVertically(
                animationSpec = tween(300, easing = FastOutSlowInEasing),
                initialHeight = 0
            ),
            exit = fadeOut(
                animationSpec = tween(200)
            ) + shrinkVertically(
                animationSpec = tween(200),
                targetHeight = 0
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .graphicsLayer {
                        rotationX = if (isError) 2f else 0f
                    },
                shape = RoundedCornerShape(16.dp),
                color = if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = if (isError) 4.dp else 2.dp
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
}

/**
 * Animated keypad button with ripple effect
 */
@Composable
fun AnimatedKeypadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors()
) {
    var isPressed by remember { mutableStateOf(false) }
    
    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .scale(if (isPressed) 0.95f else 1f),
        colors = colors,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp,
            disabledElevation = 0.dp
        ),
        interactionSource = remember { MutableInteractionSource() }
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
 * Pulsating save button animation
 */
@Composable
fun PulsatingSaveButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    var pulse by remember { mutableStateOf(false) }
    
    LaunchedEffect(enabled) {
        if (enabled) {
            pulse = true
        }
    }
    
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .scale(if (pulse && enabled) 1.1f else 1f)
            .animateFloatAsState(
                targetValue = if (pulse && enabled) 1.1f else 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                finishedListener = { _ -> pulse = false }
            ).value,
        enabled = enabled,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Save reading"
        )
    }
}

/**
 * Animated error message with slide and fade effects
 */
@Composable
fun AnimatedErrorMessage(
    message: String,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { -50 }
        ) + fadeIn(
            animationSpec = tween(300)
        ),
        exit = slideOutVertically(
            targetOffsetY = { -50 }
        ) + fadeOut(
            animationSpec = tween(200)
        ),
        modifier = modifier
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

/**
 * Shimmer loading animation
 */
@Composable
fun ShimmerLoading(
    modifier: Modifier = Modifier
) {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    )
    
    val transition = rememberInfiniteTransition()
    val shimmerOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = shimmerColors,
                            startX = shimmerOffset + index * 200f,
                            endX = shimmerOffset + index * 200f + 200f
                        )
                    )
            )
        }
    }
}

/**
 * Animated field selector with smooth transitions
 */
@Composable
fun AnimatedFieldSelector(
    selectedField: String,
    onFieldSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val fields = listOf(
        "totalReading" to "Total",
        "rate1Reading" to "Rate 1",
        "rate2Reading" to "Rate 2", 
        "rate3Reading" to "Rate 3"
    )
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
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
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .animateContentSize()
                    .graphicsLayer {
                        scaleX = if (selectedField == field) 1.05f else 1f
                        scaleY = if (selectedField == field) 1.05f else 1f
                    }
            )
        }
    }
}

/**
 * Animated notes field with expand/collapse animation
 */
@Composable
fun AnimatedNotesField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxLength: Int = 500
) {
    var isExpanded by remember { mutableStateOf(value.isNotBlank()) }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header with expand/collapse button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Notes (Optional)",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            IconButton(
                onClick = { isExpanded = !isExpanded }
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse notes" else "Expand notes",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Animated notes input field
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(
                animationSpec = tween(300, easing = FastOutSlowInEasing),
                initialHeight = 0
            ) + fadeIn(
                animationSpec = tween(300)
            ),
            exit = shrinkVertically(
                animationSpec = tween(300, easing = FastOutSlowInEasing),
                targetHeight = 0
            ) + fadeOut(
                animationSpec = tween(200)
            )
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = 1.dp
            ) {
                TextField(
                    value = value,
                    onValueChange = { 
                        if (it.length <= maxLength) {
                            onValueChange(it)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(12.dp),
                    placeholder = {
                        Text(
                            text = "Add any relevant notes about this reading...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        placeholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp
                    ),
                    maxLines = 4
                )
            }
        }
    }
}