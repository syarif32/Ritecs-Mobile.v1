package com.example.ritecsmobile.ui.screens.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.Chat
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

    // 💡 Warna Premium Ritecs
    val ritecsBlue = Color(0xFF004191)
    val ritecsLightBlue = Color(0xFF0091FF)
    val goldPremium = Color(0xFFF59E0B) // Warna Gold untuk Paket
    val tagGreen = Color(0xFF10B981)

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
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = { Text("Layanan HAKI", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
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
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = ritecsLightBlue)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // ==========================================
                    // 1. HEADER PREMIUM
                    // ==========================================
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.verticalGradient(colors = listOf(ritecsBlue, ritecsLightBlue)),
                                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                                )
                                .padding(horizontal = 24.dp, vertical = 32.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Box(
                                    modifier = Modifier.size(64.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = goldPremium, modifier = Modifier.size(36.dp))
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = hakiData?.intro_title ?: "Pusat Perlindungan HAKI",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = hakiData?.intro_description ?: "Lindungi karya, merek, dan inovasi Anda secara hukum bersama konsultan ahli dari Ritecs.",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // ==========================================
                    // 2. JENIS PERLINDUNGAN (HORIZONTAL SCROLL)
                    // ==========================================
                    val types = hakiData?.types ?: emptyList()
                    if (types.isNotEmpty()) {
                        item {
                            Text(
                                text = "Kategori Layanan",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(types) { type ->
                                    Card(
                                        modifier = Modifier.width(140.dp).height(120.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        elevation = CardDefaults.cardElevation(2.dp),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp).fillMaxSize(),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(Icons.Default.Gavel, contentDescription = null, tint = ritecsLightBlue, modifier = Modifier.size(32.dp))
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                text = type.name ?: "Kategori",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                textAlign = TextAlign.Center,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }

                    // ==========================================
                    // 3. PAKET LAYANAN (PRICING CARD STYLE)
                    // ==========================================
                    val packages = hakiData?.packages ?: emptyList()
                    if (packages.isNotEmpty()) {
                        item {
                            Text(
                                text = "Pilihan Paket",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        items(packages) { pkg ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 8.dp)
                                    .clickable { onNavigateToDetail(pkg) },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(4.dp),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    // Header Paket
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(ritecsLightBlue.copy(alpha = 0.1f))
                                            .padding(horizontal = 20.dp, vertical = 16.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.LocalOffer, contentDescription = null, tint = ritecsLightBlue, modifier = Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = pkg.title ?: "Nama Paket",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }

                                    // Body Paket
                                    Column(modifier = Modifier.padding(20.dp)) {
                                        if (!pkg.old_price.isNullOrEmpty()) {
                                            Text(
                                                text = pkg.old_price,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textDecoration = TextDecoration.LineThrough
                                            )
                                        }
                                        Text(
                                            text = pkg.new_price ?: "Hubungi Admin",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = ritecsLightBlue
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                        Spacer(modifier = Modifier.height(16.dp))

                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(
                                                text = "Lihat Detail Layanan",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = ritecsBlue
                                            )
                                            Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = ritecsBlue, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(32.dp)) }
                    }
                }
            }
        }
    }
}

// =========================================================================
// HALAMAN 2: DETAIL PAKET HAKI (REDESIGN)
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HakiDetailScreen(
    hakiPackage: HakiPackageDto,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val ritecsBlue = Color(0xFF004191)
    val ritecsLightBlue = Color(0xFF0091FF)
    val tagGreen = Color(0xFF10B981)

    Scaffold(
        topBar = {
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = { Text("Detail Paket", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.onSurface) }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            }
        },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 24.dp, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Total Investasi", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(hakiPackage.new_price ?: "Hubungi Admin", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = tagGreen)
                    }
                    Button(
                        onClick = {
                            val waNumber = hakiPackage.whatsapp_number ?: "6285225969825"
                            val message = hakiPackage.whatsapp_message ?: "Halo Admin Ritecs, saya tertarik untuk konsultasi mengenai Paket HAKI: ${hakiPackage.title}."
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$waNumber?text=${Uri.encode(message)}"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ritecsLightBlue),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
                    ) {
                        Icon(Icons.Outlined.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Konsultasi", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Detail
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ritecsBlue.copy(alpha = 0.05f))
                    .padding(24.dp)
            ) {
                Column {
                    Surface(color = ritecsLightBlue.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp)) {
                        Text(
                            text = "🏷️ PAKET LAYANAN HAKI",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ritecsLightBlue,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = hakiPackage.title ?: "Nama Paket",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 34.sp
                    )
                }
            }

            // Body Detail
            Column(modifier = Modifier.padding(24.dp)) {
                // Keunggulan (Checklist)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = tagGreen, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Proses Cepat & Transparan", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = tagGreen, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Didampingi Konsultan Ahli", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Deskripsi Layanan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = hakiPackage.description ?: "Belum ada detail lebih lanjut mengenai paket ini.",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Justify
                )

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}