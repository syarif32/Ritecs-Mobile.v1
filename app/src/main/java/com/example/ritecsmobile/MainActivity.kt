package com.example.ritecsmobile // Sesuai dengan package kamu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.ritecsmobile.data.local.AuthPreferences
import com.example.ritecsmobile.ui.screens.main.MainScreen
import com.example.ritecsmobile.ui.theme.RitecsMobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current

            val authPreferences = remember { AuthPreferences(context) }
            val isDarkMode by authPreferences.isDarkMode.collectAsState(initial = false)
            RitecsMobileTheme(
                darkTheme = isDarkMode,
                dynamicColor = false
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}