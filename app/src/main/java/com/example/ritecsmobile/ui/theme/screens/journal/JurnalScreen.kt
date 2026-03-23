package com.example.ritecsmobile.ui.screens.journal

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ritecsmobile.data.remote.RetrofitClient
import com.example.ritecsmobile.data.remote.dto.JournalDto
import com.example.ritecsmobile.ui.theme.screens.book.BASE_URL_BE

// 💡 Warna Tema Baru Ritecs
val RitecsBlue = Color(0xFF0062CD)
val BackgroundSoft = Color(0xFFF5F6FA)
val TagGreen = Color(0xFF27AE60) // Senada dengan label Jurnal di Beranda

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JurnalScreen() {
    var allJournals by remember { mutableStateOf<List<JournalDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch API
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.authApi.getJournals()
            allJournals = response.data
        } catch (e: Exception) {
            android.util.Log.e("JURNAL_ERR", "Error: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            // Top Bar Putih Bersih ala E-Commerce
            Surface(color = Color.White, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = { Text("Jurnal Publikasi", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundSoft)
                .padding(paddingValues)
        ) {
            if (isLoading) {
                // 💡 Loading indicator pakai warna RitecsBlue
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = RitecsBlue)
            } else if (allJournals.isEmpty()) {
                Text("Belum ada jurnal yang diterbitkan.", color = Color.Gray, modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(allJournals) { journal ->
                        JournalListCard(journal = journal)
                    }
                }
            }
        }
    }
}

@Composable
fun JournalListCard(journal: JournalDto) {
    val context = LocalContext.current

    val imagePath = journal.cover_path?.trimStart('/') ?: "assets/published/journals/journal_default.png"
    val imageUrl = BASE_URL_BE + imagePath

    val joinedKeywords = if (!journal.keywords.isNullOrEmpty()) {
        journal.keywords.joinToString(", ") { it.name }
    } else {
        "Tidak ada kata kunci"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (!journal.url_path.isNullOrEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(journal.url_path))
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Link portal belum tersedia", Toast.LENGTH_SHORT).show()
                }
            },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // COVER JURNAL (KIRI)
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(140.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF8F9FA))
                    .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Cover ${journal.title}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // KONTEN DETAIL (KANAN)
            Column(modifier = Modifier.weight(1f)) {

                // 💡 Tag Jurnal (Warna Hijau Senada dengan Beranda)
                Surface(color = TagGreen.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                    Text(
                        text = "🏷️ JURNAL",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TagGreen,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Judul
                Text(
                    text = journal.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 20.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Kata Kunci (Keywords)
                Text(
                    text = "Keywords: $joinedKeywords",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                // TOMBOL KE PORTAL JURNAL
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            if (!journal.url_path.isNullOrEmpty()) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(journal.url_path))
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context, "Link portal belum tersedia", Toast.LENGTH_SHORT).show()
                            }
                        },
                        // 💡 Tombol warna RitecsBlue
                        colors = ButtonDefaults.buttonColors(containerColor = RitecsBlue),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Language, contentDescription = "Web", modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Buka Portal", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}