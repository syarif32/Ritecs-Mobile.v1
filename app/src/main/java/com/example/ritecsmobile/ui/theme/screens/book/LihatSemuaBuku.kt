package com.example.ritecsmobile.ui.theme.screens.book

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.ritecsmobile.data.remote.dto.BookDto
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LihatSemuaBukuScreen(
    sectionTitle: String,
    onNavigateBack: () -> Unit = {},
    onNavigateToDetail: (BookDto) -> Unit = {}
) {
    var allBooks by remember { mutableStateOf<List<BookDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var isGridLayout by remember { mutableStateOf(true) }
    var showSortSheet by remember { mutableStateOf(false) }
    var currentSort by remember { mutableStateOf("Paling Baru") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.authApi.getBooks()
            allBooks = response.data
        } catch (e: Exception) {
            android.util.Log.e("SEMUA_BUKU_ERR", "Error: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    val initialFilteredBooks = when (sectionTitle) {
        "Gratis" -> allBooks.filter { it.ebook_price == null || it.ebook_price == 0 }
        "Terbaru" -> allBooks.sortedByDescending { it.book_id }
        "Populer" -> allBooks.sortedByDescending { it.visitor_count ?: 0 }
        else -> allBooks
    }

    // 💡 FITUR DIKEMBALIKAN: Filter Paling Banyak Diunduh sudah aktif!
    val displayBooks = when (currentSort) {
        "Paling Lama" -> initialFilteredBooks.sortedBy { it.book_id }
        "Paling Baru" -> initialFilteredBooks.sortedByDescending { it.book_id }
        "Paling Banyak Dilihat" -> initialFilteredBooks.sortedByDescending { it.visitor_count ?: 0 }
        "Paling Banyak Diunduh" -> initialFilteredBooks.sortedByDescending { it.download_count ?: 0 }
        "Harga Tertinggi" -> initialFilteredBooks.sortedByDescending { it.print_price ?: 0 }
        "Harga Terendah" -> initialFilteredBooks.sortedBy { it.print_price ?: 0 }
        else -> initialFilteredBooks
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(sectionTitle.ifEmpty { "Semua Buku" }, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White) }
                },
                actions = {
                    IconButton(onClick = { isGridLayout = !isGridLayout }) {
                        Icon(imageVector = if (isGridLayout) Icons.Default.ViewList else Icons.Default.GridView, contentDescription = "Ganti Layout", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White)
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().background(Color(0xFFF5F6FA)).padding(paddingValues)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = { showSortSheet = true },
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color.LightGray)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Sort, contentDescription = "Sort", modifier = Modifier.size(18.dp), tint = Color.DarkGray)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Urutkan: $currentSort", fontSize = 13.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Down", modifier = Modifier.size(18.dp), tint = Color.DarkGray)
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "${displayBooks.size} Buku", fontSize = 12.sp, color = Color.Gray)
                }

                if (isGridLayout) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(displayBooks) { book -> BookGridCard(book = book, onClick = { onNavigateToDetail(book) }) }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(displayBooks) { book -> BookListCard(book = book, onClick = { onNavigateToDetail(book) }) }
                    }
                }
            }
        }
    }

    if (showSortSheet) {
        ModalBottomSheet(onDismissRequest = { showSortSheet = false }, sheetState = sheetState, containerColor = Color.White) {
            Column(modifier = Modifier.padding(bottom = 32.dp)) {
                Text(text = "Urutkan Berdasarkan", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))

                // 💡 Paling Banyak Diunduh sudah ditambah lagi ke opsi
                val sortOptions = listOf("Paling Baru", "Paling Lama", "Paling Banyak Dilihat", "Paling Banyak Diunduh", "Harga Tertinggi", "Harga Terendah")

                sortOptions.forEach { option ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable {
                            currentSort = option
                            scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) showSortSheet = false }
                        }.padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = option, fontSize = 15.sp, fontWeight = if (currentSort == option) FontWeight.Bold else FontWeight.Normal, color = if (currentSort == option) MaterialTheme.colorScheme.primary else Color.Black)
                        if (currentSort == option) Icon(Icons.Default.Check, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

// ================= PERUBAHAN: DESAIN GRID SEKARANG SAMA PERSIS DENGAN GRAMEDIABOOKCARD =================
@Composable
fun BookGridCard(book: BookDto, onClick: () -> Unit) {
    val joinedAuthors = book.writers?.joinToString(", ") { it.name } ?: "Ritecs"
    val firstCategoryName = book.categories?.firstOrNull()?.name ?: "BUKU"

    val imagePath = book.cover_path?.trimStart('/') ?: "assets/published/books/book_default.png"
    val imageUrl = BASE_URL_BE + imagePath

    Column(
        modifier = Modifier
            .fillMaxWidth() // Otomatis mengisi sel Grid (2 kolom)
            .clickable { onClick() }
    ) {
        // 1. COVER BUKU
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) // Lebih tinggi sedikit biar proporsional di grid besar
                .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(6.dp))
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFF8F9FA))
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Cover ${book.title}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2. BADGE KATEGORI
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(color = Color(0xFFE3F2FD), shape = RoundedCornerShape(4.dp)) {
                Text(
                    text = "🏷️ ${firstCategoryName.uppercase()}",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2),
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 3. PENULIS
        Text(
            text = joinedAuthors,
            fontSize = 11.sp,
            color = Color.Gray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(1.dp))

        // 4. JUDUL BUKU
        Text(
            text = book.title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 17.sp,
            modifier = Modifier.height(34.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // 5. HARGA
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "PDF: ", fontSize = 11.sp, color = Color.Gray)
                Text(
                    text = "GRATIS",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF27AE60)
                )
            }
            Spacer(modifier = Modifier.height(1.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Cetak: ", fontSize = 11.sp, color = Color.Gray)
                Text(
                    text = formatToRupiah(book.print_price),
                    fontSize = 11.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ================= LIST CARD (1 KOLOM) =================
@Composable
fun BookListCard(book: BookDto, onClick: () -> Unit) {
    val joinedAuthors = book.writers?.joinToString(", ") { it.name } ?: "Ritecs"
    val categoryName = book.categories?.firstOrNull()?.name ?: "BUKU"
    val imagePath = book.cover_path?.trimStart('/') ?: "assets/published/books/book_default.png"
    val imageUrl = BASE_URL_BE + imagePath

    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }, colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Box(modifier = Modifier.width(100.dp).height(140.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFF8F9FA))) {
                AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true).build(), contentDescription = "Cover", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Surface(color = Color(0xFFE3F2FD), shape = RoundedCornerShape(4.dp)) {
                    Text("🏷️ ${categoryName.uppercase()}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = book.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 20.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Oleh: $joinedAuthors", fontSize = 12.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(text = "Harga Cetak: ${formatToRupiah(book.print_price)}", fontSize = 10.sp, color = Color.Gray)
                        Text(text = if (book.ebook_price == null || book.ebook_price == 0) "GRATIS" else formatToRupiah(book.ebook_price), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = if (book.ebook_price == null || book.ebook_price == 0) Color(0xFF27AE60) else MaterialTheme.colorScheme.primary)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Tampilkan Download Count kalau di mode List
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${book.download_count ?: 0}", fontSize = 12.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${book.visitor_count ?: 0}", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}