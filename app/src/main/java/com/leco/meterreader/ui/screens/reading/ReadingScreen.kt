package com.leco.meterreader.ui.screens.reading

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Main reading screen that delegates to the AddReadingScreen
 * This serves as a wrapper for navigation purposes
 */
@Composable
fun ReadingScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    AddReadingScreen(
        onBack = onBack,
        onReadingSaved = {
            // Handle successful reading save
            // Could navigate to history or show success message
        },
        modifier = modifier
    )
}