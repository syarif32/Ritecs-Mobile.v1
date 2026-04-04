package com.example.ritecsmobile.ui.screens.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CastForEducation
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ritecsmobile.data.remote.RetrofitClient
import com.example.ritecsmobile.data.remote.dto.TrainingDataDto
import com.example.ritecsmobile.data.remote.dto.TrainingDto
import com.example.ritecsmobile.ui.theme.screens.book.BASE_URL_BE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PelatihanScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToDetail: (TrainingDto) -> Unit
) {
    var trainingData by remember { mutableStateOf<TrainingDataDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Warna lokal agar tidak bentrok dengan BerandaScreen
    val ritecsBlue = Color(0xFF0062CD)

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.authApi.getTrainingCenter()
            trainingData = response.data
        } catch (e: Exception) {
            android.util.Log.e("TRAINING_ERR", "Error: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = { Text("Pusat Pelatihan", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface) },
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
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = ritecsBlue)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    val trainings = trainingData?.trainings ?: emptyList()

                    if (trainings.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("Belum ada program pelatihan.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    } else {
                        items(trainings) { training ->
                            TrainingListCard(training = training, ritecsBlue = ritecsBlue, onClick = { onNavigateToDetail(training) })
                        }
                    }

                    // --- SECTION HAKI (Opsional di Bawah List) ---
                    val hakiServices = trainingData?.haki_services
                    if (!hakiServices.isNullOrEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(trainingData?.haki_title ?: "Layanan Pendukung", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        items(hakiServices) { service ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = ritecsBlue.copy(alpha = 0.05f)),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(0.dp)
                            ) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                                    Icon(Icons.Default.CastForEducation, contentDescription = null, tint = ritecsBlue, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(service.title ?: "Layanan", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = ritecsBlue)
                                        if (!service.description.isNullOrEmpty()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(service.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrainingListCard(training: TrainingDto, ritecsBlue: Color, onClick: () -> Unit) {
    val tagGreen = Color(0xFF27AE60)
    val imageUrl = training.image_path?.let { path ->
        if (path.startsWith("http")) {
            path
        } else {
            val cleanPath = path.trimStart('/')
            if (cleanPath.startsWith("storage/") || cleanPath.startsWith("assets/")) {
                BASE_URL_BE + cleanPath
            } else {
                BASE_URL_BE + "storage/" + cleanPath
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // 1. GAMBAR COVER (Kiri)
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
            ) {
                if (imageUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true).build(),
                        contentDescription = "Cover", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.align(Alignment.Center).size(32.dp))
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 2. DATA PELATIHAN (Kanan)
            Column(modifier = Modifier.weight(1f)) {
                Surface(color = ritecsBlue.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                    Text("🎓 PELATIHAN", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ritecsBlue, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(training.title ?: "Nama Program", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 20.sp)

                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(training.schedule ?: "Jadwal Menyusul", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(training.price ?: "Hubungi Admin", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = tagGreen)
            }
        }
    }
}
// detail latih
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PelatihanDetailScreen(
    training: TrainingDto,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val ritecsBlue = Color(0xFF0062CD)
    val tagGreen = Color(0xFF27AE60)

    val imageUrl = training.image_path?.let { path ->
        if (path.startsWith("http")) {
            path
        } else {
            val cleanPath = path.trimStart('/')
            if (cleanPath.startsWith("storage/") || cleanPath.startsWith("assets/")) {
                BASE_URL_BE + cleanPath
            } else {
                BASE_URL_BE + "storage/" + cleanPath
            }
        }
    }

    Scaffold(
        topBar = {
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = { Text("Detail Pelatihan", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.onSurface) }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            }
        },
        bottomBar = {
            // 💡 TOMBOL DAFTAR MELAYANG DI BAWAH (STICKY BOTTOM BAR)
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 16.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Biaya Investasi", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(training.price ?: "Hubungi Admin", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = tagGreen)
                    }
                    Button(
                        onClick = {
                            if (!training.button_url.isNullOrEmpty()) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(training.button_url))
                                context.startActivity(intent)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ritecsBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(50.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(training.button_text ?: "Daftar Sekarang", fontWeight = FontWeight.Bold)
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
            // 1. GAMBAR BESAR
            if (imageUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true).build(),
                    contentDescription = "Cover", contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(220.dp).background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }

            Column(modifier = Modifier.padding(20.dp)) {
                // 2. JUDUL
                Surface(color = ritecsBlue.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                    Text("🎓 PROGRAM PELATIHAN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ritecsBlue, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(training.title ?: "Nama Program", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface, lineHeight = 28.sp)

                Spacer(modifier = Modifier.height(24.dp))

                // 3. KOTAK INFO SINGKAT (Jadwal & Kontak)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = ritecsBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Jadwal Pelaksanaan", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(training.schedule ?: "-", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = ritecsBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Contact Person", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(training.contact_person ?: "Admin Ritecs", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 4. DESKRIPSI LENGKAP
                Text("Deskripsi Pelatihan", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = training.description ?: "Belum ada deskripsi untuk pelatihan ini.",
                    fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 22.sp,
                    textAlign = TextAlign.Justify
                )

                if (!training.price_note.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    // Warna kuning alert ini dipertahankan karena ini "Semantic Color" (Peringatan/Catatan)
                    Surface(color = Color(0xFFFFF3CD), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Text("Catatan: ${training.price_note}", modifier = Modifier.padding(12.dp), fontSize = 12.sp, color = Color(0xFF856404))
                    }
                }

                Spacer(modifier = Modifier.height(40.dp)) // Ruang nafas untuk bottom bar
            }
        }
    }
}