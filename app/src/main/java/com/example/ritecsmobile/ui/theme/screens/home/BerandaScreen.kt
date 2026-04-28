package com.example.ritecsmobile.ui.theme.screens.home

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
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

val RitecsBlue = Color(0xFF0062CD)
val BackgroundSoft = Color(0xFFF8FAFC)

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BerandaScreen(
    onNavigate: (String) -> Unit,
    onNavigateToBookDetail: (BookDto) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    // STATE DATA DARI DATABASE
    var latestBooks by remember { mutableStateOf<List<BookDto>>(emptyList()) }
    var latestJournals by remember { mutableStateOf<List<JournalDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // =========================================================================
    // 💡 STATE KOORDINAT BARU (ANTI-BUG SAMSUNG)
    // =========================================================================
    var rootCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var searchCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var bukuCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var jurnalCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var profileCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }

    val sharedPreferences = context.getSharedPreferences("RitecsTourPrefs", android.content.Context.MODE_PRIVATE)
    var isTourActive by remember { mutableStateOf(sharedPreferences.getBoolean("has_seen_home_tour", false).not()) }
    var currentTourStep by remember { mutableStateOf(1) } // 1=Search, 2=Buku, 3=Jurnal, 4=Profile

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

    val filteredBooks = if (searchQuery.isEmpty()) {
        latestBooks
    } else {
        latestBooks.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }

    val filteredJournals = if (searchQuery.isEmpty()) {
        latestJournals
    } else {
        latestJournals.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }

    // Banner Promo
    val promoBanners = listOf(
        PromoBannerDrawable(R.drawable.banner1, "https://ritecs.org"),
        PromoBannerDrawable(R.drawable.banner2, "https://ritecs.org")
    )
    val pagerState = rememberPagerState(pageCount = { promoBanners.size })

    val topBarAlpha by remember {
        derivedStateOf {
            (scrollState.value / 300f).coerceIn(0f, 1f)
        }
    }

    // 💡 PASANG SENSOR INDUK DI BOX PALING LUAR
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .onGloballyPositioned { rootCoords = it } // Menangkap koordinat absolut layar ini
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomStart) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth()
                ) { page ->
                    val banner = promoBanners[page]
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = banner.imageResId),
                        contentDescription = "Promo Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clickable {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(banner.linkUrl)))
                            }
                    )
                }

                Row(
                    Modifier
                        .padding(start = 20.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    repeat(promoBanners.size) { iteration ->
                        val isSelected = pagerState.currentPage == iteration
                        val color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f)
                        val width = if (isSelected) 20.dp else 8.dp
                        Box(
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .clip(CircleShape)
                                .background(color)
                                .height(6.dp)
                                .width(width)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- MENU LAYANAN ---
            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 3.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

                        // 💡 TARGET 3: MENU JURNAL (Pasang Sensor)
                        Box(modifier = Modifier.onGloballyPositioned { jurnalCoords = it }) {
                            MenuIconItem(Icons.Default.Article, "Jurnal", Color(0xFF3498DB)) { onNavigate("jurnal_tab") }
                        }

                        // 💡 TARGET 2: MENU BUKU (Pasang Sensor)
                        Box(modifier = Modifier.onGloballyPositioned { bukuCoords = it }) {
                            MenuIconItem(Icons.Default.MenuBook, "Buku", Color(0xFFE67E22)) { onNavigate("buku_tab") }
                        }

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

            SectionHeader(if (searchQuery.isEmpty()) "Buku Terbaru" else "Hasil Pencarian Buku", onSeeAll = { onNavigate("buku_tab") })

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(24.dp), color = RitecsBlue)
            } else if (filteredBooks.isEmpty()) {
                Text("Buku tidak ditemukan.", modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(filteredBooks) { book ->
                        val imagePath = book.cover_path?.trimStart('/') ?: "assets/published/books/book_default.png"
                        val imageUrl = BASE_URL_BE + imagePath

                        val priceText = if (book.print_price != null && book.print_price > 0) formatToRupiah(book.print_price) else "GRATIS"
                        val priceColor = if (priceText == "GRATIS") Color(0xFF27AE60) else MaterialTheme.colorScheme.onSurface
                        val author = book.writers?.joinToString(", ") { it.name } ?: "Ritecs"

                        HomeVerticalCard(
                            title = book.title, subtitle = author, label = "🏷️ BUKU", tagColor = Color(0xFF1976D2),
                            imageUrl = imageUrl, priceText = priceText, priceColor = priceColor,
                            onClick = { onNavigateToBookDetail(book) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            SectionHeader(if (searchQuery.isEmpty()) "Jurnal Rilis Terbaru" else "Hasil Pencarian Jurnal", onSeeAll = { onNavigate("jurnal_tab") })

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(24.dp), color = RitecsBlue)
            } else if (filteredJournals.isEmpty()) {
                Text("Jurnal tidak ditemukan.", modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(filteredJournals) { journal ->
                        val imagePath = journal.cover_path?.trimStart('/') ?: ""
                        val imageUrl = BASE_URL_BE + imagePath

                        val keywords = journal.keywords?.joinToString(", ") { it.name } ?: "Jurnal Ilmiah"

                        HomeVerticalCard(
                            title = journal.title, subtitle = keywords, label = "🏷️ JURNAL", tagColor = Color(0xFF27AE60),
                            imageUrl = imageUrl, priceText = "Buka Portal", priceColor = RitecsBlue,
                            onClick = {
                                if (!journal.url_path.isNullOrEmpty()) {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(journal.url_path)))
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF004191).copy(alpha = topBarAlpha),
                            Color(0xFF0091FF).copy(alpha = topBarAlpha)
                        )
                    )
                )
                .padding(top = statusBarHeight, bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // 💡 TARGET 1: SEARCH BAR (Pasang Sensor)
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .onGloballyPositioned { searchCoords = it },
                    shadowElevation = 0.dp
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp).fillMaxSize()
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))

                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                            if (searchQuery.isEmpty()) {
                                Text("Cari buku, jurnal, dll...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                            }
                            androidx.compose.foundation.text.BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // 💡 TARGET 4: PROFILE ICON (Pasang Sensor)
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .size(50.dp)
                        .onGloballyPositioned { profileCoords = it }
                        .clickable { onNavigate("profile_tab") },
                    shadowElevation = 0.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = "Akun", tint = RitecsBlue, modifier = Modifier.size(24.dp))
                    }
                }
            }
        }

        // =========================================================================
        // 💡 KALKULASI RECTANGLE SUPER PRESISI (DIJAMIN GAK MELESET DI SAMSUNG)
        // =========================================================================
        if (isTourActive) {
            val targetRect = remember(rootCoords, searchCoords, bukuCoords, jurnalCoords, profileCoords, currentTourStep) {
                if (rootCoords?.isAttached != true) return@remember null

                val coords = when (currentTourStep) {
                    1 -> searchCoords
                    2 -> bukuCoords
                    3 -> jurnalCoords
                    4 -> profileCoords
                    else -> null
                }

                if (coords?.isAttached == true) {
                    try {
                        // 💡 RUMUS AJAIB: Menghitung posisi anak absolut terhadap induk layarnya
                        rootCoords!!.localBoundingBoxOf(coords)
                    } catch (e: Exception) { null }
                } else null
            }

            val title = when (currentTourStep) {
                1 -> "Cari Lebih Cepat"
                2 -> "Koleksi Buku"
                3 -> "Jurnal Ilmiah"
                4 -> "Profil & Pengaturan"
                else -> ""
            }
            val desc = when (currentTourStep) {
                1 -> "Ketik kata kunci di sini untuk mencari koleksi buku atau jurnal dengan cepat."
                2 -> "Jelajahi berbagai koleksi buku ilmiah dan fiksi terbitan Ritecs."
                3 -> "Akses portal jurnal Open Journal System (OJS) Ritecs dengan mudah."
                4 -> "Kelola akun, riwayat transaksi, dan kartu member Anda di sini."
                else -> ""
            }

            if (targetRect != null && targetRect.width > 0) {
                SpotlightGuidedTour(
                    targetRect = targetRect,
                    title = title,
                    description = desc,
                    stepIndex = currentTourStep,
                    totalSteps = 4,
                    onNext = {
                        if (currentTourStep < 4) {
                            currentTourStep++
                        } else {
                            isTourActive = false
                            sharedPreferences.edit().putBoolean("has_seen_home_tour", true).apply()
                        }
                    },
                    onSkip = {
                        isTourActive = false
                        sharedPreferences.edit().putBoolean("has_seen_home_tour", true).apply()
                    }
                )
            }
        }
    }
}

// =========================================================================
// 💡 KODINGAN CUSTOM VIEW UNTUK SPOTLIGHT / COACH MARK REUSABLE
// =========================================================================
@Composable
fun SpotlightGuidedTour(
    targetRect: Rect,
    title: String,
    description: String,
    stepIndex: Int,
    totalSteps: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    key(stepIndex) {
        var isVisible by remember { mutableStateOf(false) }
        var tooltipHeight by remember { mutableStateOf(0f) }
        var overlayHeight by remember { mutableStateOf(1000f) } // Ambil tinggi canvas

        val density = LocalDensity.current

        LaunchedEffect(Unit) { isVisible = true }

        val alpha by animateFloatAsState(targetValue = if (isVisible) 1f else 0f, animationSpec = tween(250), label = "alpha")
        val scale by animateFloatAsState(targetValue = if (isVisible) 1f else 0.95f, animationSpec = tween(300, easing = FastOutSlowInEasing), label = "scale")

        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { overlayHeight = it.size.height.toFloat() } // Sensor tinggi layar overlay
                .graphicsLayer { this.alpha = alpha }
                .pointerInput(Unit) { /* Menahan semua sentuhan ke UI di belakangnya */ }
        ) {
            Canvas(modifier = Modifier.fillMaxSize().graphicsLayer { this.alpha = 0.99f }) {
                drawRect(Color.Black.copy(alpha = 0.75f))

                val padding = 8.dp.toPx()
                val targetWidth = targetRect.width + (padding * 2)
                val targetHeight = targetRect.height + (padding * 2)

                val scaledWidth = targetWidth * scale
                val scaledHeight = targetHeight * scale

                val offsetX = targetRect.left - padding + (targetWidth - scaledWidth) / 2
                val offsetY = targetRect.top - padding + (targetHeight - scaledHeight) / 2

                drawRoundRect(
                    color = Color.Black,
                    topLeft = Offset(offsetX, offsetY),
                    size = Size(scaledWidth, scaledHeight),
                    cornerRadius = CornerRadius(100f, 100f),
                    blendMode = BlendMode.Clear
                )
            }

            // Posisikan tooltip cerdas, pakai overlayHeight (bukan ScreenHeight) biar aman dari bug Samsung!
            val showBelow = targetRect.center.y < (overlayHeight / 2)

            val yOffsetDp = with(density) {
                if (showBelow) {
                    targetRect.bottom.toDp() + 16.dp
                } else {
                    (targetRect.top - tooltipHeight).toDp() - 16.dp
                }
            }

            val tooltipAlpha = if (tooltipHeight > 0f) 1f else 0f

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = yOffsetDp)
                    .padding(horizontal = 24.dp)
                    .graphicsLayer { this.alpha = tooltipAlpha }
                    .onGloballyPositioned { tooltipHeight = it.size.height.toFloat() }
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onSkip) {
                        Text("Lewati Tour", color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = onNext,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0062CD)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (stepIndex == totalSteps) "Selesai" else "Selanjutnya", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// --- HELPER COMPOSABLES ---
data class PromoBannerDrawable(val imageResId: Int, val linkUrl: String)

@Composable
fun MenuIconItem(icon: ImageVector, title: String, tint: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(72.dp).clickable { onClick() }) {
        Box(modifier = Modifier.size(50.dp).clip(RoundedCornerShape(14.dp)).background(tint.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = title, tint = tint, modifier = Modifier.size(26.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, fontSize = 11.sp, textAlign = TextAlign.Center, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun SectionHeader(title: String, onSeeAll: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface)
        Text("Lihat Semua", color = RitecsBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onSeeAll() })
    }
}

@Composable
fun HomeVerticalCard(title: String, subtitle: String, label: String, tagColor: Color, imageUrl: String, priceText: String, priceColor: Color, onClick: () -> Unit) {
    Column(modifier = Modifier.width(140.dp).clickable { onClick() }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true).build(), contentDescription = title, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        }
        Spacer(modifier = Modifier.height(8.dp))
        Surface(color = tagColor.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
            Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = tagColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(modifier = Modifier.height(2.dp))
        Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 17.sp, modifier = Modifier.height(34.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(priceText, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = priceColor)
    }
}