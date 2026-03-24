package com.example.ritecsmobile.ui.screens.journal

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ritecsmobile.R
import com.example.ritecsmobile.data.remote.RetrofitClient
import com.example.ritecsmobile.data.remote.dto.JournalDto
import com.example.ritecsmobile.ui.theme.screens.book.BASE_URL_BE

// 💡 Warna Tema Baru Ritecs
val RitecsBlue = Color(0xFF0062CD)
val BackgroundSoft = Color(0xFFF8FAFC)
val TagGreen = Color(0xFF27AE60)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JurnalScreen() {
    var allJournals by remember { mutableStateOf<List<JournalDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var showInfoDialog by remember { mutableStateOf(false) }

    val uriHandler = LocalUriHandler.current

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
    val filteredJournals = if (searchQuery.isBlank()) {
        allJournals
    } else {
        allJournals.filter { journal ->
            val matchTitle = journal.title.contains(searchQuery, ignoreCase = true)
            val matchKeyword = journal.keywords?.any { it.name.contains(searchQuery, ignoreCase = true) } ?: false
            matchTitle || matchKeyword
        }
    }

    Scaffold(
        topBar = {
            // Top Bar Modern dengan Search Bar & Logo Mini
            Surface(color = Color.White, shadowElevation = 3.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text("Jurnal Publikasi", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // SEARCH BAR
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Cari Judul atau Keyword...", color = Color.Gray, fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray, modifier = Modifier.size(20.dp))
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.LightGray,
                                focusedBorderColor = RitecsBlue,
                                unfocusedContainerColor = Color(0xFFF1F5F9),
                                focusedContainerColor = Color.White
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // LOGO MINI (TOMBOL POP-UP)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier
                                .size(50.dp)
                                .clickable { showInfoDialog = true } // 💡 Memicu Pop-up
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(id = R.drawable.ritecs_logo),
                                    contentDescription = "Info Jurnal",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
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
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = RitecsBlue)
            } else if (filteredJournals.isEmpty()) {
                // TAMPILAN KOSONG JIKA PENCARIAN TIDAK DITEMUKAN
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.SearchOff, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (searchQuery.isNotEmpty()) "Jurnal tidak ditemukan" else "Belum ada jurnal yang diterbitkan.",
                        color = Color.Gray, fontSize = 15.sp, fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredJournals) { journal ->
                        JournalListCard(journal = journal)
                    }
                }
            }
        }

        // ==========================================
        // 💡 POP-UP INFO JURNAL (DIALOG BANTUAN)
        // ==========================================
        if (showInfoDialog) {
            AlertDialog(
                onDismissRequest = { showInfoDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(painter = painterResource(id = R.drawable.ritecs_logo), contentDescription = null, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Portal Jurnal Ritecs", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                },
                text = {
                    Column {
                        Text(
                            text = "Selamat datang di repositori jurnal publikasi ilmiah Ritecs. Kami mendedikasikan platform ini untuk menyebarluaskan hasil riset inovatif dan teknologi terkini.",
                            fontSize = 13.sp, color = Color.DarkGray, lineHeight = 20.sp, textAlign = TextAlign.Justify
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Kunjungi portal resmi kami di:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                        Spacer(modifier = Modifier.height(4.dp))

                        // 💡 LINK BISA DIKLIK LANGSUNG KE BROWSER
                        Text(
                            text = "https://ritecs.org/journal/",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = RitecsBlue,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable {
                                uriHandler.openUri("https://ritecs.org/journal/")
                            }.padding(vertical = 4.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showInfoDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = RitecsBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Tutup Mengerti", fontWeight = FontWeight.Bold)
                    }
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = Color.White
            )
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
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // COVER JURNAL (KIRI)
            Box(
                modifier = Modifier
                    .width(90.dp)
                    .height(130.dp)
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

                Text(
                    text = "Keywords: $joinedKeywords",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

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