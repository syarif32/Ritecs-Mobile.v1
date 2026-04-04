package com.example.ritecsmobile.ui.screens.profile

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PusatBantuanScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val ritecsBlue = Color(0xFF0062CD)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pusat Bantuan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                // 💡 Header tetap Ritecs Blue agar identitas brand tidak hilang di Dark Mode
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ritecsBlue, titleContentColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                // 💡 Background Otomatis (Terang: Abu muda, Gelap: Navy Slate)
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- 1. HEADER ILUSTRASI ---
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(ritecsBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SupportAgent,
                    contentDescription = "Bantuan",
                    tint = ritecsBlue,
                    modifier = Modifier.size(56.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            // 💡 Warna Teks Otomatis
            Text(
                "Ada yang bisa kami bantu?",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Temukan jawaban cepat atau hubungi tim layanan pelanggan Ritecs untuk bantuan lebih lanjut.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(36.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Topik Populer",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column {
                        HelpTopicItem(icon = Icons.Default.LockReset, title = "Masalah Akun & Kata Sandi")
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(horizontal = 16.dp))
                        HelpTopicItem(icon = Icons.Default.Payments, title = "Kendala Pembayaran Membership")
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(horizontal = 16.dp))
                        HelpTopicItem(icon = Icons.Default.MenuBook, title = "Panduan Publikasi Jurnal/Buku")
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(horizontal = 16.dp))
                        HelpTopicItem(icon = Icons.Default.HelpOutline, title = "Pertanyaan Umum (FAQ)")
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // --- 3. KONTAK LANGSUNG (KARTU EMAIL INTERAKTIF) ---
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Hubungi Kami Langsung",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:ritecspublisher@gmail.com")
                                putExtra(Intent.EXTRA_SUBJECT, "Bantuan Layanan Ritecs Mobile")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Aplikasi Email tidak ditemukan", Toast.LENGTH_SHORT).show()
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .background(ritecsBlue.copy(alpha = 0.1f), shape = RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = ritecsBlue, modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Email Support Ritecs", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("ritecspublisher@gmail.com", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = ritecsBlue)
                        }
                        Icon(Icons.Default.OpenInNew, contentDescription = null, tint = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 💡 Footer
            Text("Kami akan membalas pesan Anda dalam 1x24 jam kerja.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// --- KOMPONEN BANTUAN ---
@Composable
fun HelpTopicItem(icon: ImageVector, title: String) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { Toast.makeText(context, "Membuka panduan: $title", Toast.LENGTH_SHORT).show() }
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outlineVariant)
    }
}