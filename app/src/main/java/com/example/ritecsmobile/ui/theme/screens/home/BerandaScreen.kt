package com.example.ritecsmobile.ui.theme.screens.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ritecsmobile.R
import com.example.ritecsmobile.data.remote.RetrofitClient
import com.example.ritecsmobile.data.remote.dto.BookDto
import com.example.ritecsmobile.data.remote.dto.JournalDto
import com.example.ritecsmobile.ui.theme.screens.book.BASE_URL_BE
import com.example.ritecsmobile.ui.theme.screens.book.formatToRupiah
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
// 💡 Konstanta Warna Ritecs
val RitecsBlue = Color(0xFF0062CD)
val RitecsLightBlue = Color(0xFF2E86EB)
val BackgroundSoft = Color(0xFFF8FAFC)

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BerandaScreen(onNavigate: (String) -> Unit) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    // STATE DATA DARI DATABASE
    var latestBooks by remember { mutableStateOf<List<BookDto>>(emptyList()) }
    var latestJournals by remember { mutableStateOf<List<JournalDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // FETCH API (Buku & Jurnal)
    LaunchedEffect(Unit) {
        try {
            val bookResponse = RetrofitClient.authApi.getBooks()
            latestBooks = bookResponse.data.sortedByDescending { it.book_id }.take(5)

            val journalResponse = RetrofitClient.authApi.getJournals()
            latestJournals = journalResponse.data.take(5)
        } catch (e: Exception) {
            android.util.Log.e("BERANDA_ERR", "Error: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    // Banner Promo
    val promoBanners = listOf(
        PromoBanner("https://img.freepik.com/free-vector/gradient-modern-book-sale-banner-template_23-2149867049.jpg", "https://ritecs.org"),
        PromoBanner("https://img.freepik.com/free-vector/flat-world-book-day-horizontal-sale-banner-template_23-2149312151.jpg", "https://ritecs.org"),
        PromoBanner("https://img.freepik.com/free-vector/gradient-world-book-day-horizontal-sale-banner-template_23-2149313278.jpg", "https://ritecs.org")
    )
    val pagerState = rememberPagerState(pageCount = { promoBanners.size })

    // 💡 LOGIKA ALPHA UNTUK STICKY TOP BAR (Smooth Transition)
    val topBarAlpha by remember {
        derivedStateOf {
            (scrollState.value / 350f).coerceIn(0f, 1f)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundSoft)) {

        // --- 1. CONTENT LAYER (Scrollable) ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // HEADER AREA (Gradient Background + Banner)
            Box(modifier = Modifier.fillMaxWidth()) {
                // Background Biru Gradasi
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(RitecsBlue, RitecsLightBlue.copy(alpha = 0.8f))
                            ),
                            shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                        )
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(115.dp)) // Ruang untuk Sticky Top Bar

                    // BANNER PROMO
                    HorizontalPager(
                        state = pagerState,
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        pageSpacing = 16.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) { page ->
                        val banner = promoBanners[page]
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clickable {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(banner.linkUrl)))
                                },
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context).data(banner.imageUrl).crossfade(true).build(),
                                contentDescription = "Promo Banner",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // Dots Indikator
                    Row(
                        Modifier.height(35.dp).fillMaxWidth().padding(top = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(promoBanners.size) { iteration ->
                            val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.4f)
                            val width = if (pagerState.currentPage == iteration) 22.dp else 8.dp
                            Box(modifier = Modifier.padding(horizontal = 3.dp).clip(CircleShape).background(color).height(7.dp).width(width))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- MENU LAYANAN ---
            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 3.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        MenuIconItem(Icons.Default.Article, "Jurnal", Color(0xFF3498DB)) { onNavigate("jurnal_tab") }
                        MenuIconItem(Icons.Default.MenuBook, "Buku", Color(0xFFE67E22)) { onNavigate("buku_tab") }
                        MenuIconItem(Icons.Default.WorkspacePremium, "Membership", Color(0xFFF1C40F)) { onNavigate("benefit_member") }
                        MenuIconItem(Icons.Default.Gavel, "HAKI", Color(0xFF2ECC71)) { onNavigate("haki") }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        MenuIconItem(Icons.Default.School, "Pelatihan", Color(0xFF9B59B6)) { onNavigate("pelatihan") }
                        MenuIconItem(Icons.Default.DocumentScanner, "Panduan", Color(0xFFE74C3C)) { onNavigate("petunjuk_penulis") }
                        MenuIconItem(Icons.Default.HeadsetMic, "Bantuan", Color(0xFF1ABC9C)) { onNavigate("pusat_bantuan") }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // --- SECTION BUKU TERBARU ---
            SectionHeader("Buku Terbaru", onSeeAll = { onNavigate("buku_tab") })
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(24.dp), color = RitecsBlue)
            } else {
                LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(latestBooks) { book ->
                        val imageUrl = "$BASE_URL_BE/${book.cover_path?.trimStart('/') ?: ""}"
                        val priceText = if (book.print_price != null && book.print_price > 0) formatToRupiah(book.print_price) else "GRATIS"
                        val priceColor = if (priceText == "GRATIS") Color(0xFF27AE60) else Color.DarkGray
                        val author = book.writers?.joinToString(", ") { it.name } ?: "Ritecs"

                        HomeVerticalCard(
                            title = book.title, subtitle = author, label = "🏷️ BUKU", tagColor = Color(0xFF1976D2),
                            imageUrl = imageUrl, priceText = priceText, priceColor = priceColor, onClick = { onNavigate("buku_tab") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // --- SECTION JURNAL TERBARU ---
            SectionHeader("Jurnal Rilis Terbaru", onSeeAll = { onNavigate("jurnal_tab") })
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(24.dp), color = RitecsBlue)
            } else {
                LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(latestJournals) { journal ->
                        val imageUrl = "$BASE_URL_BE/${journal.cover_path?.trimStart('/') ?: ""}"
                        val keywords = journal.keywords?.joinToString(", ") { it.name } ?: "Jurnal Ilmiah"

                        HomeVerticalCard(
                            title = journal.title, subtitle = keywords, label = "🏷️ JURNAL", tagColor = Color(0xFF27AE60),
                            imageUrl = imageUrl, priceText = "Buka Portal", priceColor = RitecsBlue, onClick = {
                                if (!journal.url_path.isNullOrEmpty()) {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(journal.url_path)))
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp)) // Ruang ekstra di bawah
        }

        // --- 2. STICKY TOP BAR LAYER (Fixed at top) ---
        Surface(
            modifier = Modifier.fillMaxWidth().height(110.dp),
            color = RitecsBlue.copy(alpha = topBarAlpha), // Transisi warna di sini
            shadowElevation = (topBarAlpha * 4).dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Search Bar White
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White,
                        modifier = Modifier.weight(1f).height(46.dp),
                        shadowElevation = 2.dp
                    ) {
                        OutlinedTextField(
                            value = searchQuery, onValueChange = { searchQuery = it },
                            placeholder = { Text("Cari buku, jurnal, dll...", color = Color.Gray, fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = RitecsBlue, modifier = Modifier.size(20.dp)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                cursorColor = RitecsBlue
                            ),
                            singleLine = true, modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Profile Icon White
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        modifier = Modifier.size(46.dp).clickable { onNavigate("profile_tab") },
                        shadowElevation = 2.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, contentDescription = "Akun", tint = RitecsBlue, modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }
    }
}

// --- HELPER COMPOSABLES ---

data class PromoBanner(val imageUrl: String, val linkUrl: String)

@Composable
fun MenuIconItem(icon: ImageVector, title: String, tint: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(72.dp).clickable { onClick() }) {
        Box(modifier = Modifier.size(50.dp).clip(RoundedCornerShape(14.dp)).background(tint.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = title, tint = tint, modifier = Modifier.size(26.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, fontSize = 11.sp, textAlign = TextAlign.Center, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
    }
}

@Composable
fun SectionHeader(title: String, onSeeAll: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = Color.Black)
        Text("Lihat Semua", color = RitecsBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onSeeAll() })
    }
}

@Composable
fun HomeVerticalCard(title: String, subtitle: String, label: String, tagColor: Color, imageUrl: String, priceText: String, priceColor: Color, onClick: () -> Unit) {
    Column(modifier = Modifier.width(140.dp).clickable { onClick() }) {
        Box(modifier = Modifier.fillMaxWidth().height(200.dp).border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(10.dp)).clip(RoundedCornerShape(10.dp)).background(Color(0xFFF8F9FA))) {
            AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true).build(), contentDescription = title, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        }
        Spacer(modifier = Modifier.height(8.dp))
        Surface(color = tagColor.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
            Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = tagColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(subtitle, fontSize = 10.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(modifier = Modifier.height(2.dp))
        Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 17.sp, modifier = Modifier.height(34.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(priceText, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = priceColor)
    }
}