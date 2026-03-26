package com.example.ritecsmobile.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ritecsmobile.R
import kotlinx.coroutines.launch

data class OnboardingPage(val title: String, val description: String, val imageRes: Int)

@OptIn(ExperimentalFoundationApi::class)
@Composable

fun OnboardingScreen(onNavigateToLogin: () -> Unit, onNavigateToHome: () -> Unit) {
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

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 40.dp, end = 20.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { onNavigateToHome() }) {
                    Text("Lewati", color = Color.Gray, fontWeight = FontWeight.Bold)
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
                        val color = if (pagerState.currentPage == iteration) ritecsBlue else Color.LightGray
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
                        Text("Lanjut tanpa akun", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 15.sp)
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
            color = Color(0xFF1E293B),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = page.description,
            fontSize = 15.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}