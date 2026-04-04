package com.example.ritecsmobile.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ritecsmobile.R
import com.example.ritecsmobile.data.remote.RetrofitClient
import com.example.ritecsmobile.data.remote.dto.TentangDataDto

// 💡 FUNGSI SAKTI PEMBERSIH HTML
fun String.stripHtml(): String {
    return this.replace(Regex("<[^>]*>"), "").trim()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TentangScreen(onNavigateBack: () -> Unit) {
    var tentangData by remember { mutableStateOf<TentangDataDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // 💡 WARNA BRAND TETAP DI-HARDCODE
    val ritecsBlue = Color(0xFF0062CD)

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.authApi.getTentangData()
            tentangData = response.data
        } catch (e: Exception) {
            android.util.Log.e("TENTANG_ERR", "Gagal load data: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        // 💡 Background Layar Utama Otomatis
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Tentang Kami", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                // 💡 Header Otomatis menyesuaikan tema
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ritecsBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // 1. LOGO RITECS ASLI
                Image(
                    painter = painterResource(id = R.drawable.ritecs_logo),
                    contentDescription = "Logo Ritecs",
                    modifier = Modifier.size(100.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = tentangData?.pre_title?.stripHtml() ?: "Ritecs",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = ritecsBlue,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 2. LEGALITAS CARD (HIJAU ELEGAN OTOMATIS AMAN DI DARK MODE)
                Surface(
                    // 💡 Gunakan warna hijau dengan alpha agar aman di Dark Mode
                    color = Color(0xFF27AE60).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Gavel, contentDescription = null, tint = Color(0xFF27AE60), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = tentangData?.subtitle?.stripHtml() ?: "Terdaftar Resmi",
                            fontSize = 13.sp,
                            color = Color(0xFF27AE60),
                            fontWeight = FontWeight.Bold,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. VISI & MISI CARD
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    // 💡 Warna Kartu Otomatis
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        // VISI (DENGAN PEMBERSIH HTML)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Visibility, contentDescription = null, tint = ritecsBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Visi", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = ritecsBlue)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        // 💡 Teks Visi Otomatis (Abu-abu Kalem)
                        Text(
                            text = tentangData?.vision?.stripHtml() ?: "Visi belum diatur.",
                            fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 22.sp, textAlign = TextAlign.Justify
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                        // 💡 Garis Pemisah Otomatis
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(20.dp))

                        // MISI (DENGAN PEMBERSIH HTML)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Flag, contentDescription = null, tint = ritecsBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Misi", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = ritecsBlue)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        // 💡 Teks Misi Otomatis
                        Text(
                            text = tentangData?.mision?.stripHtml() ?: "Misi belum diatur.",
                            fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 22.sp, textAlign = TextAlign.Justify
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 4. INFO FOOTER CARD
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    // 💡 Warna Kartu Otomatis
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        InfoRow(Icons.Default.LocationOn, "Semarang, Jawa Tengah")
                        // 💡 Garis Pemisah Otomatis
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        InfoRow(Icons.Default.Language, "www.ritecs.org")
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // 5. DEVELOPER INFO (KREDIT UNTUK BOS SYARIF)
                Text(
                    text = "Dikembangkan oleh",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Muhammad Najwa Syarif",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "© 2026 Ritecs Team",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // 💡 Icon & Text otomatis
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
    }
}