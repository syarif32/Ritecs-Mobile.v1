package com.example.ritecsmobile.ui.screens.books

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush // 💡 Import Brush untuk Gradasi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ritecsmobile.data.remote.RetrofitClient
import com.example.ritecsmobile.data.remote.dto.GuidelineDataDto

// 💡 Konstanta Warna Ritecs
val RitecsDarkBlue = Color(0xFF004191)
val RitecsLightBlue = Color(0xFF0091FF)
val RitecsBlue = Color(0xFF0062CD)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// 💡 Tambahkan parameter onNavigateBack untuk tombol kembali
fun PetunjukPenulisScreen(onNavigateBack: () -> Unit) {
    var guidelineData by remember { mutableStateOf<GuidelineDataDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.authApi.getGuidelines()
            guidelineData = response.data
        } catch (e: Exception) {
            android.util.Log.e("GUIDELINE_ERR", "Error: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            // 💡 BUNGKUS TOP APP BAR DENGAN BOX GRADASI
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(RitecsDarkBlue, RitecsLightBlue) // Gradasi Kiri ke Kanan
                        )
                    )
            ) {
                TopAppBar(
                    title = { Text("Petunjuk Penulis", fontWeight = FontWeight.Bold, color = Color.White) },
                    navigationIcon = {
                        // 💡 TOMBOL BACK DI KIRI ATAS
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent, // 💡 Transparan biar gradasi Box di belakangnya kelihatan
                        titleContentColor = Color.White
                    )
                )
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RitecsBlue) // Sesuaikan warna loading
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White) // 💡 Latar utama dibuat Putih Bersih
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {

                // BAGIAN 1: SKEMA PENERBITAN
                Text("Skema Penerbitan", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = RitecsDarkBlue)
                Spacer(modifier = Modifier.height(16.dp))

                guidelineData?.schemes?.forEach { scheme ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)), // Sedikit abu-abu agar kontras dengan latar putih
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Dibuat flat agar lebih modern
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(scheme.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(scheme.description ?: "-", fontSize = 14.sp, color = Color.DarkGray, lineHeight = 20.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0)) // Warna garis pemisah yang lebih halus
                Spacer(modifier = Modifier.height(24.dp))

                // BAGIAN 2: LANGKAH-LANGKAH
                Text("Langkah-Langkah", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = RitecsDarkBlue)
                Spacer(modifier = Modifier.height(16.dp))

                guidelineData?.steps?.forEachIndexed { index, step ->
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                        // Lingkaran Angka Step (Warna RitecsBlue)
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(RitecsBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${index + 1}", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Teks Langkah
                        Column(modifier = Modifier.weight(1f)) {
                            Text(step.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(step.description ?: "-", fontSize = 14.sp, color = Color.DarkGray, lineHeight = 20.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}