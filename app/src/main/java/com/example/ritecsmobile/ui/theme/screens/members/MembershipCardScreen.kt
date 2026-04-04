package com.example.ritecsmobile.ui.screens.profile

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ritecsmobile.data.local.AuthPreferences
import com.example.ritecsmobile.data.remote.RetrofitClient
import com.example.ritecsmobile.data.remote.dto.UserProfileDataDto
import com.example.ritecsmobile.ui.theme.screens.book.BASE_URL_BE
import kotlinx.coroutines.*



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembershipCardScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authPreferences = remember { AuthPreferences(context) }
    val token by authPreferences.authToken.collectAsState(initial = "")

    var userData by remember { mutableStateOf<UserProfileDataDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // FETCH DATA
    LaunchedEffect(token) {
        if (!token.isNullOrEmpty()) {
            isLoading = true
            try {
                val response = RetrofitClient.authApi.getDashboardProfile("Bearer $token")
                userData = response.data
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                android.util.Log.e("KARTU_ERROR", "Error API: ${e.message}", e)
                Toast.makeText(context, "Gagal memuat kartu: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kartu Membership", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White) }
                },
                // 💡 TopBar tetap RitecsBlue agar konsisten identitasnya
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RitecsBlue, titleContentColor = Color.White)
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RitecsBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    // 💡 Latar Belakang Layar Otomatis (Terang/Gelap menyesuaikan HP)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 💡 Warna Teks Judul Otomatis (Hitam/Putih)
                Text(
                    text = "Kartu Digital Eksklusif",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                // 💡 Warna Teks Deskripsi Otomatis (Abu-abu)
                Text(
                    text = "Tunjukkan kartu ini untuk mendapatkan benefit layanan Ritecs.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ==========================================
                // === KARTU DEPAN (FRONT) ===
                // ==========================================
                Card(
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        val frontUrl = userData?.card_image_path?.let {
                            if (it.startsWith("http")) it else BASE_URL_BE + it.trimStart('/')
                        }

                        // Background Gambar Kartu
                        if (frontUrl != null) {
                            AsyncImage(model = frontUrl, contentDescription = "Front Card", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        } else {
                            Box(modifier = Modifier.fillMaxSize().background(RitecsBlue))
                        }

                        // 💡 GRADASI GELAP DI BAWAH (Tetap dipertahankan agar teks putih selalu terbaca)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .align(Alignment.BottomCenter)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                    )
                                )
                        )

                        // Konten Teks & QR Code (TETAP PUTIH KARENA ADA DI ATAS GAMBAR KARTU)
                        Box(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                            // Nama dan ID (Kiri Bawah)
                            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                                Text(
                                    text = "${userData?.first_name ?: ""} ${userData?.last_name ?: ""}".uppercase(),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White // 💡 Tetap Putih
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "ID: ${userData?.member_number ?: "-"}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.9f) // 💡 Tetap Putih
                                )
                            }

                            // QR Code (Kanan Bawah)
                            val qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=${userData?.member_number ?: "RITECS"}"
                            AsyncImage(
                                model = qrUrl,
                                contentDescription = "QR Code",
                                modifier = Modifier
                                    .size(65.dp)
                                    .align(Alignment.BottomEnd)
                                    .background(Color.White, RoundedCornerShape(8.dp)) // Frame QR Code tetap putih agar scan gampang
                                    .padding(6.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ==========================================
                // === KARTU BELAKANG (BACK) ===
                // ==========================================
                Card(
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    // 💡 Latar Belakang fallback kalau gambar gagal dimuat, ikutin warna kartu Surface
                    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
                        val backUrl = userData?.card_back_image_path?.let {
                            if (it.startsWith("http")) it else BASE_URL_BE + it.trimStart('/')
                        }
                        if (backUrl != null) {
                            AsyncImage(model = backUrl, contentDescription = "Back Card", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // ==========================================
                // === TOMBOL UNDUH ===
                // ==========================================
                Button(
                    onClick = {
                        val front = userData?.card_image_path?.let { if (it.startsWith("http")) it else BASE_URL_BE + it.trimStart('/') } ?: ""
                        val back = userData?.card_back_image_path?.let { if (it.startsWith("http")) it else BASE_URL_BE + it.trimStart('/') } ?: ""

                        if (front.isNotEmpty() && back.isNotEmpty()) {
                            saveCardToGallery(context, scope, front, back)
                        } else {
                            Toast.makeText(context, "Gambar kartu belum siap diunduh", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    // 💡 Tombol tetap RitecsBlue agar mencolok dan identitas brand kuat
                    colors = ButtonDefaults.buttonColors(containerColor = RitecsBlue)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Unduh Kartu Digital", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ==========================================================
// FUNGSI STANDALONE UNTUK SAVE KE GALERI (TIDAK ADA YANG DIUBAH)
// ==========================================================
fun saveCardToGallery(context: Context, scope: CoroutineScope, frontUrl: String, backUrl: String) {
    Toast.makeText(context, "Mempersiapkan unduhan...", Toast.LENGTH_SHORT).show()

    scope.launch(Dispatchers.IO) {
        try {
            val loader = ImageLoader(context)

            val frontReq = ImageRequest.Builder(context).data(frontUrl).allowHardware(false).build()
            val backReq = ImageRequest.Builder(context).data(backUrl).allowHardware(false).build()

            val frontResult = (loader.execute(frontReq).drawable as? BitmapDrawable)?.bitmap
            val backResult = (loader.execute(backReq).drawable as? BitmapDrawable)?.bitmap

            if (frontResult != null && backResult != null) {
                // Gabungkan Atas (Front) dan Bawah (Back)
                val width = frontResult.width
                val height = frontResult.height + backResult.height + 40
                val combined = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

                val canvas = Canvas(combined)
                canvas.drawColor(android.graphics.Color.WHITE)
                canvas.drawBitmap(frontResult, 0f, 0f, null)
                canvas.drawBitmap(backResult, 0f, (frontResult.height + 40).toFloat(), null)

                // Simpan ke HP
                val filename = "Ritecs_Card_${System.currentTimeMillis()}.jpg"
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Ritecs")
                    }
                }

                val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                uri?.let {
                    context.contentResolver.openOutputStream(it).use { out ->
                        combined.compress(Bitmap.CompressFormat.JPEG, 95, out!!)
                    }
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Berhasil! Cek folder Pictures/Ritecs di Galeri", Toast.LENGTH_LONG).show()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Gagal memuat gambar dari server.", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Gagal mengunduh: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}