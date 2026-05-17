package com.leco.meterreader

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main application class for LECO Solar Meter Analyzer
 * Initializes Hilt dependency injection
 */
@HiltAndroidApp
class LECOMeterReaderApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Application initialization code will go here
    }
}