package com.leco.meterreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.leco.meterreader.navigation.LECOMeterReaderNavHost
import com.leco.meterreader.ui.theme.LECOMeterReaderTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for LECO Solar Meter Analyzer
 * Serves as the entry point for the application
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LECOMeterReaderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        LECOMeterReaderNavHost(
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    LECOMeterReaderTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = androidx.compose.material3.MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                LECOMeterReaderNavHost(
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}