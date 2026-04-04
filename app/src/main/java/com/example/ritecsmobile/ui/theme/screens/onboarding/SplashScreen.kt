package com.example.ritecsmobile.ui.screens.onboarding

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme // 💡 Ditambahkan untuk tema
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ritecsmobile.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onNavigateToOnboarding: () -> Unit) {
    // State Animasi
    val bgScale = remember { Animatable(0f) }
    val rotation1 = remember { Animatable(0f) }
    val rotation2 = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        launch {
            bgScale.animateTo(
                targetValue = 40f,
                animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
            )
        }
        launch {
            rotation1.animateTo(
                targetValue = 360f,
                animationSpec = tween(durationMillis = 1800, easing = FastOutSlowInEasing)
            )
        }
        launch {
            rotation2.animateTo(
                targetValue = -360f,
                animationSpec = tween(durationMillis = 1800, easing = FastOutSlowInEasing)
            )
        }
        launch {
            delay(500)
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 1300,
                    easing = { OvershootInterpolator(2.5f).getInterpolation(it) }
                )
            )
        }

        delay(2200L)
        onNavigateToOnboarding()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            // 💡 CUMA INI YANG DIGANTI: Mencegah efek flashbang putih di malam hari!
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .scale(bgScale.value)
                .graphicsLayer { rotationZ = rotation1.value }
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 70.dp, bottomEnd = 30.dp, bottomStart = 80.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF0F2027), Color(0xFF0062CD))
                    )
                )
        )

        Box(
            modifier = Modifier
                .size(100.dp)
                .scale(bgScale.value * 0.9f)
                .graphicsLayer { rotationZ = rotation2.value }
                .clip(RoundedCornerShape(topStart = 80.dp, topEnd = 30.dp, bottomEnd = 70.dp, bottomStart = 40.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF0062CD), Color(0xFF0F2027))
                    )
                )
        )
        Image(
            painter = painterResource(id = R.drawable.ritecs_putih),
            contentDescription = "Logo Ritecs",
            modifier = Modifier
                .size(150.dp)
                .scale(logoScale.value)
        )
    }
}