package com.example.ritecsmobile.ui.screens.onboarding

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ritecsmobile.R
import com.example.ritecsmobile.data.local.AuthPreferences
import kotlinx.coroutines.launch

data class OnboardingPage(val title: String, val description: String, val imageRes: Int)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onNavigateToLogin: () -> Unit, onNavigateToHome: () -> Unit) {
    val context = LocalContext.current
    val pages = listOf(
        OnboardingPage(
            title = "Selamat Datang di Ritecs",
            description = "Platform inovasi dan riset terbaik untuk menemani perjalanan akademis dan profesionalmu.",
            imageRes = R.drawable.ritecs_logo
        ),
        OnboardingPage(
            title = "Akses Jurnal & Buku",
            description = "Temukan jurnal ilmiah dan buku digital dengan mudah dalam genggaman tanganmu.",
            imageRes = R.drawable.ritecs_logo
        ),
        OnboardingPage(
            title = "Mulai Perjalananmu",
            description = "Bergabunglah menjadi member premium atau telusuri koleksi kami sebagai tamu.",
            imageRes = R.drawable.ritecs_logo
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()
    val ritecsBlue = Color(0xFF0062CD)

    val authPreferences = remember { AuthPreferences(context) }
    val isDarkThemeLocal by authPreferences.isDarkMode.collectAsState(initial = false)

    // 💡 STATE BARU UNTUK SPOTLIGHT / COACH MARK
    var toggleCoordinates by remember { mutableStateOf<Rect?>(null) }

    // 💡 MENGGUNAKAN MEMORI LOKAL AGAR CUMA MUNCUL SEKALI SEUMUR HIDUP
    val sharedPreferences = context.getSharedPreferences("RitecsTourPrefs", android.content.Context.MODE_PRIVATE)
    var hasDismissedSpotlight by remember { mutableStateOf(sharedPreferences.getBoolean("has_seen_onboarding_spotlight", false)) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        // Tumpukan UI menggunakan Box agar Overlay bisa di atas segalanya
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, start = 20.dp, end = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (pagerState.currentPage == 0) {
                        // 💡 PASANG "KAMERA PENGINTAI" KOORDINAT DI SINI
                        Box(modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                            toggleCoordinates = layoutCoordinates.boundsInRoot()
                        }) {
                            ThemeToggleSwitch(
                                isDark = isDarkThemeLocal,
                                onToggle = {
                                    coroutineScope.launch {
                                        authPreferences.setDarkMode(!isDarkThemeLocal)
                                    }
                                }
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(60.dp))
                    }

                    TextButton(onClick = { onNavigateToHome() }) {
                        Text("Lewati", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { position ->
                    OnboardingPageUI(page = pages[position])
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 32.dp)) {
                        repeat(pages.size) { iteration ->
                            val color = if (pagerState.currentPage == iteration) ritecsBlue else MaterialTheme.colorScheme.outlineVariant
                            val width = if (pagerState.currentPage == iteration) 24.dp else 10.dp
                            Box(
                                modifier = Modifier
                                    .height(10.dp)
                                    .width(width)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                        }
                    }

                    if (pagerState.currentPage == pages.size - 1) {
                        Button(
                            onClick = { onNavigateToLogin() },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ritecsBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Masuk Akun", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(
                            onClick = { onNavigateToHome() },
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text("Lanjut tanpa akun", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    } else {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ritecsBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Lanjut", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                        }
                    }
                }
            }

            // =========================================================
            // 💡 PEMANGGILAN OVERLAY SPOTLIGHT (COACH MARK)
            // =========================================================
            if (pagerState.currentPage == 0 && !hasDismissedSpotlight && toggleCoordinates != null && toggleCoordinates!!.width > 0) {
                SpotlightOverlay(
                    targetRect = toggleCoordinates!!,
                    onDismiss = {
                        // 💡 SIMPAN KE MEMORI SAAT DIKLIK "MENGERTI"
                        hasDismissedSpotlight = true
                        sharedPreferences.edit().putBoolean("has_seen_onboarding_spotlight", true).apply()
                    }
                )
            }
        }
    }
}

@Composable
fun OnboardingPageUI(page: OnboardingPage) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = null,
            modifier = Modifier.size(250.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = page.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = page.description,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

@Composable
fun ThemeToggleSwitch(
    isDark: Boolean,
    onToggle: () -> Unit
) {
    val switchWidth = 64.dp
    val switchHeight = 32.dp
    val thumbSize = 24.dp
    val padding = (switchHeight - thumbSize) / 2

    val thumbOffset by animateDpAsState(
        targetValue = if (isDark) (switchWidth - thumbSize - padding) else padding,
        animationSpec = tween(durationMillis = 300), label = "thumbOffset"
    )

    Box(
        modifier = Modifier
            .width(switchWidth)
            .height(switchHeight)
            .clip(RoundedCornerShape(50))
            .background(if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0))
            .clickable { onToggle() },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(thumbSize)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                contentDescription = null,
                tint = if (isDark) Color(0xFF0F172A) else Color(0xFFF59E0B),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// =========================================================
// 💡 KODINGAN CUSTOM VIEW UNTUK SPOTLIGHT / COACH MARK
// =========================================================
@Composable
fun SpotlightOverlay(
    targetRect: Rect,
    onDismiss: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    var tooltipHeight by remember { mutableStateOf(0f) }
    var overlayHeight by remember { mutableStateOf(1000f) }

    // Animasi Zoom-In saat layar dibuka
    LaunchedEffect(Unit) { isVisible = true }

    val animatedScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.9f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "spotlight_scale"
    )

    val density = LocalDensity.current

    // pointerInput menahan semua klik ke elemen di bawahnya kecuali tombol "Mengerti"
    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { overlayHeight = it.size.height.toFloat() }
            .pointerInput(Unit) {}
    ) {

        // Canvas ini menggambar background hitam dan melubangi area target
        Canvas(modifier = Modifier.fillMaxSize().graphicsLayer { alpha = 0.99f }) {
            drawRect(Color.Black.copy(alpha = 0.75f))

            val padding = 12.dp.toPx()
            val targetWidth = targetRect.width + (padding * 2)
            val targetHeight = targetRect.height + (padding * 2)

            val scaledWidth = targetWidth * animatedScale
            val scaledHeight = targetHeight * animatedScale

            // Hitung koordinat offset yang presisi
            val offsetX = targetRect.left - padding + (targetWidth - scaledWidth) / 2
            val offsetY = targetRect.top - padding + (targetHeight - scaledHeight) / 2

            drawRoundRect(
                color = Color.Black,
                topLeft = Offset(offsetX, offsetY),
                size = Size(scaledWidth, scaledHeight),
                cornerRadius = CornerRadius(100f, 100f), // Agar bentuknya membulat rapi
                blendMode = BlendMode.Clear // Melubangi (Punch Hole)
            )
        }

        // 💡 FIXED: Kalkulasi pintar & aman dari error toPx()
        val showBelow = targetRect.center.y < (overlayHeight / 2)
        val yOffsetDp = with(density) {
            if (showBelow) {
                targetRect.bottom.toDp() + 20.dp
            } else {
                (targetRect.top - tooltipHeight).toDp() - 20.dp
            }
        }

        val tooltipAlpha = if (tooltipHeight > 0f) 1f else 0f

        // Area Tooltip dan Tombol
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = yOffsetDp)
                .padding(horizontal = 24.dp)
                .graphicsLayer { this.alpha = tooltipAlpha }
                .onGloballyPositioned { tooltipHeight = it.size.height.toFloat() }
        ) {
            Text(
                text = "Fitur Baru: Mode Gelap \uD83C\uDF19",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sesuaikan tampilan aplikasi dengan kenyamanan matamu. Klik di sini untuk mencobanya!",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0062CD)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Mengerti", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}