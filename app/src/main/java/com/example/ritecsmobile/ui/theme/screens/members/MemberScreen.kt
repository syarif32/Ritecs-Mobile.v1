package com.example.ritecsmobile.ui.screens.members

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ritecsmobile.data.remote.RetrofitClient
import com.example.ritecsmobile.data.remote.dto.MemberDto
import com.example.ritecsmobile.ui.theme.screens.book.BASE_URL_BE // 💡 Import URL Dinamis

// 💡 Warna Tema Baru Ritecs
val RitecsBlue = Color(0xFF0062CD)
val BackgroundSoft = Color(0xFFF5F6FA)
val StatusActiveColor = Color(0xFF27AE60) // Hijau terang
val StatusExpiredColor = Color(0xFFE74C3C) // Merah lembut

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberScreen(
    onNavigateBack: () -> Unit = {} // Berjaga-jaga kalau dipanggil dari sub-menu
) {
    var members by remember { mutableStateOf<List<MemberDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.authApi.getMembers()
            members = response.data
        } catch (e: Exception) {
            android.util.Log.e("MEMBER_ERR", "Error: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            // 💡 Top Bar Putih Bersih
            Surface(color = Color.White, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = { Text("Direktori Anggota", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black) },
                    navigationIcon = {
                        // Hilangkan blok if ini kalau halaman ini jadi menu utama lagi
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.Black)
                        }
                    },
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
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = RitecsBlue)
            } else if (members.isEmpty()) {
                Text(
                    text = "Belum ada anggota yang terdaftar.",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(members) { member ->
                        MemberCard(member)
                    }
                }
            }
        }
    }
}

@Composable
fun MemberCard(member: MemberDto) {
    val firstName = member.first_name ?: member.guest_first_name ?: "Anggota"
    val lastName = member.last_name ?: member.guest_last_name ?: ""
    val fullName = "$firstName $lastName".trim()

    // 💡 Ambil URL gambar dinamis
    val avatarUrl = member.img_path?.let { BASE_URL_BE + it.trimStart('/') }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Foto Profil Bulat
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0F0F0)), // Abu-abu sangat muda buat background icon
                contentAlignment = Alignment.Center
            ) {
                if (avatarUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(avatarUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(32.dp))
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Data Teks
            Column(modifier = Modifier.weight(1f)) {
                Text(text = fullName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = member.member_number ?: "No. ID: -", fontSize = 13.sp, color = Color.Gray)
            }

            // Badge Status Aktif / Expired Modern
            val isAktif = member.status == 1
            val statusColor = if (isAktif) StatusActiveColor else StatusExpiredColor
            val statusText = if (isAktif) "Aktif" else "Expired"

            Surface(
                color = statusColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}