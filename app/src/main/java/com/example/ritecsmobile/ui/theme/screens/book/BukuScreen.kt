package com.example.ritecsmobile.ui.theme.screens.book

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import java.text.NumberFormat
import java.util.Locale

const val BASE_URL_BE = "http://192.168.1.3:8000"
fun formatToRupiah(amount: Int?): String {
    if (amount == null || amount == 0) return "Rp0"
    val localeID = Locale("in", "ID")
    val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
    return formatRupiah.format(amount).replace(",00", "")
}

@Composable
fun BukuScreen(
    onNavigateToDetail: (BookDto) -> Unit = {},
    onNavigateToLihatSemua: (String) -> Unit = {}
) {
    var allBooks by remember { mutableStateOf<List<BookDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.authApi.getBooks()
            allBooks = response.data
        } catch (e: Exception) {
            android.util.Log.e("BUKU_ERR", "Error: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    val filteredBooks = if (searchQuery.isBlank()) {
        allBooks
    } else {
        allBooks.filter { book ->
            val matchTitle = book.title.contains(searchQuery, ignoreCase = true)
            val matchAuthor = book.writers?.any { it.name.contains(searchQuery, ignoreCase = true) } ?: false
            matchTitle || matchAuthor
        }
    }

    val categories = filteredBooks.flatMap { it.categories ?: emptyList() }.map { it.name }.distinct()
    val freeBooks = filteredBooks.filter { it.ebook_price == null || it.ebook_price == 0 }
    val latestBooks = filteredBooks.sortedByDescending { it.book_id }
    val popularBooks = filteredBooks.sortedByDescending { it.visitor_count ?: 0 }

    Scaffold(
        topBar = {
            Surface(color = Color.White, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari Judul Buku atau Penulis Ritecs", color = Color.Gray, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp).height(50.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        unfocusedContainerColor = Color(0xFFF8F9FA),
                        focusedContainerColor = Color.White
                    ),
                    singleLine = true
                )
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (filteredBooks.isEmpty() && searchQuery.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.SearchOff, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Buku tidak ditemukan", color = Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                if (categories.isNotEmpty()) {
                    SectionHeader(title = "Kategori Buku Ritecs", onSeeAllClick = { onNavigateToLihatSemua("Kategori") })
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { categoryName -> CategoryChip(name = categoryName) }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                if (freeBooks.isNotEmpty()) {
                    SectionHeader(title = "Baca & Unduh Gratis", onSeeAllClick = { onNavigateToLihatSemua("Gratis") })
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(freeBooks) { book -> GramediaBookCard(book = book, onClick = { onNavigateToDetail(book) }) }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                if (latestBooks.isNotEmpty()) {
                    SectionHeader(title = "Buku Terbaru", onSeeAllClick = { onNavigateToLihatSemua("Terbaru") })
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(latestBooks) { book -> GramediaBookCard(book = book, onClick = { onNavigateToDetail(book) }) }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                if (popularBooks.isNotEmpty()) {
                    SectionHeader(title = "Paling Banyak Dilihat", onSeeAllClick = { onNavigateToLihatSemua("Populer") })
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(popularBooks) { book -> GramediaBookCard(book = book, onClick = { onNavigateToDetail(book) }) }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, onSeeAllClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
        Text(text = "Lihat Semua", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.clickable { onSeeAllClick() })
    }
}

@Composable
fun CategoryChip(name: String) {
    Box(
        modifier = Modifier
            .border(1.dp, Color.LightGray, RoundedCornerShape(20.dp))
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 7.dp)
    ) {
        Text(text = name, fontSize = 12.sp, color = Color.DarkGray)
    }
}

@Composable
fun GramediaBookCard(book: BookDto, onClick: () -> Unit) {
    val joinedAuthors = book.writers?.joinToString(", ") { it.name } ?: "Ritecs"
    val firstCategoryName = book.categories?.firstOrNull()?.name ?: "BUKU"
    val imagePath = book.cover_path?.trimStart('/') ?: "assets/published/books/book_default.png"
    val imageUrl = BASE_URL_BE + imagePath

    Column(modifier = Modifier.width(135.dp).clickable { onClick() }) {
        Box(
            modifier = Modifier.fillMaxWidth().height(190.dp).border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(6.dp))
                .clip(RoundedCornerShape(6.dp)).background(Color(0xFFF8F9FA))
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true).build(),
                contentDescription = "Cover ${book.title}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(color = Color(0xFFE3F2FD), shape = RoundedCornerShape(4.dp)) {
                Text("🏷️ ${firstCategoryName.uppercase()}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2), modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(text = joinedAuthors, fontSize = 11.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(modifier = Modifier.height(1.dp))
        Text(text = book.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 17.sp, modifier = Modifier.height(34.dp))
        Spacer(modifier = Modifier.height(4.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "PDF: ", fontSize = 11.sp, color = Color.Gray)
                Text(text = "GRATIS", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF27AE60))
            }
            Spacer(modifier = Modifier.height(1.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Cetak: ", fontSize = 11.sp, color = Color.Gray)
                Text(text = formatToRupiah(book.print_price), fontSize = 11.sp, color = Color.DarkGray, fontWeight = FontWeight.Bold)
            }
        }
    }
}