package com.example.ritecsmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ritecsmobile.ui.screens.main.MainScreen
import com.example.ritecsmobile.ui.theme.RitecsMobileTheme
import com.example.ritecsmobile.ui.theme.screens.home.BerandaScreen
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RitecsMobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Langsung jalankan aplikasi dengan sistem Bottom Navbar!
                    MainScreen()
                }
            }
        }
    }
}