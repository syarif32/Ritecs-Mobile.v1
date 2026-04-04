package com.example.ritecsmobile.ui.screens.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ritecsmobile.R
import com.example.ritecsmobile.data.local.AuthPreferences // 💡 Import memori lokal
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

    // 💡 1. Panggil AuthPreferences
    val authPreferences = remember { AuthPreferences(context) }

    // 💡 2. Pantau status tema dari memori secara Real-Time
    val isDarkThemeLocal by authPreferences.isDarkMode.collectAsState(initial = false)

    // 💡 Latar Belakang Layar Otomatis
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween, // 💡 Kiri Kanan terpisah
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 💡 TOGGLE SWITCH TEMA (KIRI) - HANYA MUNCUL DI SLIDE 1
                if (pagerState.currentPage == 0) {
                    ThemeToggleSwitch(
                        isDark = isDarkThemeLocal,
                        onToggle = {
                            // 💡 3. EKSEKUSI! Simpan pilihan user ke memori.
                            coroutineScope.launch {
                                // PERHATIAN: Jika nama fungsi di AuthPreferences milikmu beda, ubah kata "setDarkMode" ini ya!
                                authPreferences.setDarkMode(!isDarkThemeLocal)
                            }
                        }
                    )
                } else {
                    Spacer(modifier = Modifier.size(60.dp)) // 💡 Penjaga Jarak agar Lewati tetap di Kanan
                }

                // 💡 TOMBOL LEWATI (KANAN)
                TextButton(onClick = { onNavigateToHome() }) {
                    // 💡 Warna Teks Otomatis
                    Text("Lewati", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                }
            }

            // AREA KONTEN YANG BISA DI-SWIPE
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
                        // 💡 Warna Indikator Otomatis
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
                        // 💡 Warna Teks Lewati Otomatis
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
            // 💡 Warna Judul Otomatis (Hitam/Putih)
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = page.description,
            fontSize = 15.sp,
            // 💡 Warna Deskripsi Otomatis (Abu-abu Kalem)
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

// 💡 KOMPONEN CUSTOM TOGGLE SWITCH SUPER ELEGAN
@Composable
fun ThemeToggleSwitch(
    isDark: Boolean,
    onToggle: () -> Unit
) {
    val switchWidth = 64.dp
    val switchHeight = 32.dp
    val thumbSize = 24.dp
    val padding = (switchHeight - thumbSize) / 2

    // Animasi pergerakan tombol bundar (kiri ke kanan)
    val thumbOffset by animateDpAsState(
        targetValue = if (isDark) (switchWidth - thumbSize - padding) else padding,
        animationSpec = tween(durationMillis = 300), label = "thumbOffset"
    )

    Box(
        modifier = Modifier
            .width(switchWidth)
            .height(switchHeight)
            .clip(RoundedCornerShape(50))
            .background(if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)) // Biru gelap vs Abu muda
            .clickable { onToggle() },
        contentAlignment = Alignment.CenterStart
    ) {
        // Tombol Bundar (Thumb)
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
                tint = if (isDark) Color(0xFF0F172A) else Color(0xFFF59E0B), // Bulan gelap, Matahari oranye
                modifier = Modifier.size(16.dp)
            )
        }
    }
}