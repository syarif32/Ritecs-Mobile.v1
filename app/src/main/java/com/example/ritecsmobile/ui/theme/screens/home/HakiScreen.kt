package com.example.ritecsmobile.ui.screens.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Loyalty
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ritecsmobile.data.remote.RetrofitClient
import com.example.ritecsmobile.data.remote.dto.HakiDataDto
import com.example.ritecsmobile.data.remote.dto.HakiPackageDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HakiScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToDetail: (HakiPackageDto) -> Unit
) {
    var hakiData by remember { mutableStateOf<HakiDataDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // 💡 Warna Identitas Brand Tetap Statis
    val ritecsBlue = Color(0xFF0062CD)
    val ritecsLightBlue = Color(0xFF2E86EB)
    val tagGreen = Color(0xFF27AE60)

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.authApi.getHakiServices()
            hakiData = response.data
        } catch (e: Exception) {
            android.util.Log.e("HAKI_ERR", "Error: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = { Text("Layanan HAKI", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.onSurface) }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                // 💡 Background Layar Dinamis
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = ritecsBlue)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 1. HEADER / INTRO
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                // 💡 Gradasi Header Biru tetap dipertahankan
                                .background(brush = Brush.verticalGradient(colors = listOf(ritecsBlue, ritecsLightBlue)), shape = RoundedCornerShape(16.dp))
                                .padding(20.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Shield, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(hakiData?.intro_title ?: "Pusat Perlindungan HAKI", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(hakiData?.intro_description ?: "Lindungi karya dan inovasi Anda secara hukum bersama Ritecs.", fontSize = 13.sp, color = Color.White.copy(alpha = 0.9f), lineHeight = 20.sp)
                            }
                        }
                    }

                    // 2. JENIS PERLINDUNGAN
                    val types = hakiData?.types ?: emptyList()
                    if (types.isNotEmpty()) {
                        item {
                            Text("Jenis Perlindungan", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        items(types) { type ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                // 💡 Warna Card Dinamis
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(1.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Gavel, contentDescription = null, tint = ritecsBlue, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        // 💡 Teks Hitam/Putih Dinamis
                                        Text(type.name ?: "Kategori", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                                        if (!type.description.isNullOrEmpty()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            // 💡 Teks Abu-abu Dinamis
                                            Text(type.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 3. PAKET LAYANAN
                    val packages = hakiData?.packages ?: emptyList()
                    if (packages.isNotEmpty()) {
                        item {
                            Text("Pilihan Paket HAKI", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        items(packages) { pkg ->
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { onNavigateToDetail(pkg) },
                                // 💡 Warna Card Dinamis
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(2.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)).background(ritecsBlue.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Loyalty, contentDescription = null, tint = ritecsBlue, modifier = Modifier.size(24.dp))
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Surface(color = tagGreen.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                                            Text("🏷️ PAKET", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = tagGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))

                                        // 💡 Teks Dinamis
                                        Text(pkg.title ?: "Nama Paket", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        Spacer(modifier = Modifier.height(4.dp))

                                        // 💡 TAMPILKAN HARGA CORET JIKA ADA old_price
                                        if (!pkg.old_price.isNullOrEmpty()) {
                                            Text(
                                                text = pkg.old_price,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant, // 💡 Abu-abu
                                                textDecoration = TextDecoration.LineThrough
                                            )
                                        }
                                        // 💡 Harga Biru Tetap Mencolok
                                        Text(pkg.new_price ?: "Harga Menyesuaikan", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = ritecsBlue)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// HALAMAN 2: DETAIL PAKET HAKI
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HakiDetailScreen(
    hakiPackage: HakiPackageDto,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val ritecsBlue = Color(0xFF0062CD)
    val tagGreen = Color(0xFF27AE60)

    Scaffold(
        topBar = {
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = { Text("Detail Paket", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.onSurface) }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            }
        },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 16.dp) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Biaya Layanan", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        // 💡 Harga Baru (Hijau)
                        Text(hakiPackage.new_price ?: "Hubungi Admin", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = tagGreen)
                    }
                    Button(
                        onClick = {
                            // 💡 AMBIL NOMOR DAN PESAN DARI DATABASE SECARA DINAMIS!
                            val waNumber = hakiPackage.whatsapp_number ?: "6285225969825"
                            val message = hakiPackage.whatsapp_message ?: "Halo Admin Ritecs, saya tertarik untuk konsultasi mengenai Paket HAKI: ${hakiPackage.title}."

                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$waNumber?text=${Uri.encode(message)}"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ritecsBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(50.dp)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Konsultasi", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            // 💡 Background Layar Utama Dinamis
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(paddingValues).verticalScroll(rememberScrollState()).padding(20.dp)
        ) {
            Surface(color = tagGreen.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                Text("🏷️ PAKET LAYANAN HAKI", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = tagGreen, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            // 💡 Judul Utama Dinamis
            Text(hakiPackage.title ?: "Nama Paket", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface, lineHeight = 30.sp)

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                // 💡 Warna Kartu Dinamis
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ritecsBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Proses Cepat & Transparan", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    // 💡 Garis Pemisah Dinamis
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = ritecsBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Didampingi Konsultan Ahli", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 💡 Teks Deskripsi Dinamis
            Text("Detail Layanan", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = hakiPackage.description ?: "Belum ada detail lebih lanjut mengenai paket ini.",
                fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 22.sp, textAlign = TextAlign.Justify
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}