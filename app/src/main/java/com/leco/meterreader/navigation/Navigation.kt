package com.leco.meterreader.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.leco.meterreader.ui.screens.dashboard.DashboardScreen
import com.leco.meterreader.ui.screens.reading.ReadingScreen
import com.leco.meterreader.ui.screens.history.HistoryScreen
import com.leco.meterreader.ui.screens.analytics.AnalyticsScreen
import com.leco.meterreader.ui.screens.solar.SolarScreen

/**
 * Main navigation graph for the LECO Solar Meter Analyzer app
 */
@Composable
fun LECOMeterReaderNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToReading = { navController.navigate(Screen.Reading.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) },
                onNavigateToSolar = { navController.navigate(Screen.Solar.route) }
            )
        }
        
        composable(Screen.Reading.route) {
            ReadingScreen(
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.History.route) {
            HistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Analytics.route) {
            AnalyticsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Solar.route) {
            SolarScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

/**
 * Navigation screens for the app
 */
sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Reading : Screen("reading")
    object History : Screen("history")
    object Analytics : Screen("analytics")
    object Solar : Screen("solar")
}