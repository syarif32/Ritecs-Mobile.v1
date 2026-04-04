package com.example.ritecsmobile.ui.theme.screens.book

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ritecsmobile.data.remote.RetrofitClient
import com.example.ritecsmobile.data.remote.dto.BookDto
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

// SESSION ANTI SPAM
val viewedBooksSession = mutableSetOf<Int>()
val downloadedBooksSession = mutableSetOf<Int>()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    book: BookDto,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var visitorCount by remember { mutableStateOf(book.visitor_count ?: 0) }
    var downloadCount by remember { mutableStateOf(book.download_count ?: 0) }

    // LOGIKA 1: TAMBAH VISITOR
    LaunchedEffect(book.book_id) {
        if (!viewedBooksSession.contains(book.book_id)) {
            viewedBooksSession.add(book.book_id)
            visitorCount++
            try {
                RetrofitClient.authApi.incrementBookVisit(book.book_id)
            } catch (e: Exception) {
                android.util.Log.e("VISIT_ERR", "Gagal tambah visitor: ${e.message}")
            }
        }
    }

    val imagePath = book.cover_path?.trimStart('/') ?: "assets/published/books/book_default.png"
    val imageUrl = BASE_URL_BE + imagePath
    val joinedAuthors = book.writers?.joinToString(", ") { it.name } ?: "Ritecs"

    Scaffold(
        topBar = {
            TopAppBar(
                // 💡 Judul Otomatis Hitam/Putih
                title = { Text("Detail Buku", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
                navigationIcon = {
                    // 💡 Ikon Back Otomatis
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.onSurface) }
                },
                // 💡 Background TopBar Otomatis
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            // LOGIKA 2: TOMBOL UNDUH
            Surface(
                shadowElevation = 8.dp,
                // 💡 Background BottomBar Otomatis
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        val pdfPath = book.ebook_path?.trimStart('/')
                        if (!pdfPath.isNullOrEmpty()) {
                            val ebookUrl = BASE_URL_BE + pdfPath
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ebookUrl))
                            context.startActivity(intent)

                            if (!downloadedBooksSession.contains(book.book_id)) {
                                downloadedBooksSession.add(book.book_id)
                                downloadCount++
                                scope.launch {
                                    try {
                                        RetrofitClient.authApi.incrementBookDownload(book.book_id)
                                    } catch (e: Exception) {
                                        android.util.Log.e("DL_ERR", "Gagal tambah download: ${e.message}")
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(context, "Mohon maaf, file E-Book belum tersedia.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.padding(16.dp).fillMaxWidth().height(50.dp),
                    // 💡 Warna tombol tetap primary agar mencolok, dan teksnya tetap putih
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = "Unduh", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Unduh PDF (Gratis)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                // 💡 Background keseluruhan otomatis
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    // 💡 Latar belakang area cover buku otomatis
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true).build(),
                    contentDescription = "Cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(160.dp)
                        .height(240.dp)
                        .shadow(8.dp, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    // 💡 Background area detail teks otomatis
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(24.dp)
            ) {
                Text(
                    text = book.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    // 💡 Judul Otomatis (Hitam/Putih)
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 30.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 💡 Ikon & Teks View otomatis (Abu-abu kalem)
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("$visitorCount x dilihat", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.width(16.dp))

                    // 💡 Ikon & Teks Download otomatis
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("$downloadCount x diunduh", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(modifier = Modifier.height(24.dp))

                DetailRow("Penulis", joinedAuthors)
                DetailRow("Penerbit", book.publisher ?: "Ritecs")
                DetailRow("Halaman", "${book.pages ?: "-"} halaman")

                // 💡 FIX: Konversi ke Int biar nol koma (.0) nya hilang kalau angkanya genap
                val widthFmt = book.width?.toInt()?.toString() ?: "0"
                val lengthFmt = book.length?.toInt()?.toString() ?: "0"
                DetailRow("Ukuran", "$widthFmt x $lengthFmt cm")

                DetailRow("Diterbitkan", formatTanggalDetail(book.publish_date))
                DetailRow("ISBN", book.isbn ?: "-")

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    // 💡 Label Harga Otomatis
                    Text("Harga", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.35f))
                    Column(modifier = Modifier.weight(0.65f)) {
                        // Harga Gratis tetap Hijau agar jadi "Semantic Color"
                        Text("Rp 0 (pdf)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF27AE60))
                        if (book.print_price != null && book.print_price > 0) {
                            // 💡 Nilai Cetak Otomatis
                            Text("Cetak: ${formatToRupiah(book.print_price)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                // 💡 Garis Pemisah Otomatis
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(24.dp))

                // 💡 Label Sinopsis Otomatis
                Text("Sinopsis :", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = book.synopsis ?: "Tidak ada sinopsis untuk buku ini.",
                    fontSize = 14.sp,
                    // 💡 Isi Sinopsis Otomatis (Abu-abu kalem)
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Justify
                )

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

// 💡 FIX TYPO: Sudah kembali ke jalan yang benar (verticalAlignment)
@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // 💡 Label Kiri (Abu-abu Otomatis)
        Text(text = label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.35f))
        // 💡 Value Kanan (Hitam/Putih Otomatis)
        Text(text = " :  $value", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(0.65f))
    }
}

fun formatTanggalDetail(tanggal: String?): String {
    if (tanggal.isNullOrEmpty()) return "-"
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID"))
        val date = parser.parse(tanggal)
        if (date != null) formatter.format(date) else tanggal
    } catch (e: Exception) {
        tanggal
    }
}