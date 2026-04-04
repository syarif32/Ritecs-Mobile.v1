package com.example.ritecsmobile.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ritecsmobile.data.local.AuthPreferences
import com.example.ritecsmobile.data.remote.RetrofitClient
import com.example.ritecsmobile.data.remote.dto.UserDto
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(onLogout: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authPreferences = remember { AuthPreferences(context) }

    val token by authPreferences.authToken.collectAsState(initial = "")

    var userProfile by remember { mutableStateOf<UserDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(token) {
        if (!token.isNullOrEmpty()) {
            try {

                val response = RetrofitClient.authApi.getUserProfile("Bearer $token")
                userProfile = response
            } catch (e: Exception) {
                android.util.Log.e("HOME_ERR", "Gagal load data: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (isLoading) {
            CircularProgressIndicator() // Tampilkan loading saat nembak API
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Text("Dashboard Ritecs", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)

                Spacer(modifier = Modifier.height(24.dp))

                // Kartu Profil Keren
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Informasi Pengguna", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Menampilkan Nama Asli dari Database!
                        Text("Halo, ${userProfile?.first_name ?: "Pengguna"}!", fontSize = 22.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(4.dp))

                        // Menampilkan Email Asli
                        Text(userProfile?.email ?: "-", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // ... (Kode sebelumnya) ...

                Spacer(modifier = Modifier.height(24.dp))

                // Logika Pengecekan Membership
                if (userProfile?.membership != null && userProfile?.membership?.status == "active") {

                    Text("Premium Membership", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Berlaku hingga: ${userProfile?.membership?.end_date}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(16.dp))

                    // KARTU MEMBERSHIP DIGITAL (Gambar dari Server)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp), // Tinggi kartu proporsional
                        shape = MaterialTheme.shapes.extraLarge,
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // 1. Layer Paling Bawah: Gambar Kartu PNG dari Server
                            AsyncImage(
                                // Ganti URL ini dengan URL aslimu jika sudah live
                                model = "https://ritecs.org/sites/${userProfile?.membership?.card_image_path}",
                                contentDescription = "Membership Card Background",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop // Biar gambarnya nge-pas di kotak
                            )

                            // 2. Layer Atas: Teks Data User yang Numpang di Atas Gambar
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Teks dibuat warna putih/gelap tergantung warna asli PNG kartumu
                                Text(
                                    text = userProfile?.first_name ?: "",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = androidx.compose.ui.graphics.Color.White // Sesuaikan warnanya
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Exp: ${userProfile?.membership?.end_date ?: "-"}",
                                    fontSize = 14.sp,
                                    color = androidx.compose.ui.graphics.Color.White
                                )
                            }
                        }
                    }
                } else {
                    // Tampilan jika user BELUM punya membership
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Anda belum memiliki Membership Aktif", color = MaterialTheme.colorScheme.onErrorContainer, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { /* TODO: Navigasi ke halaman beli membership */ }) {
                                Text("Beli Sekarang")
                            }
                        }
                    }
                }

                // ... (Tombol Logout dan kode setelahnya) ...
                // Tombol Logout Merah
                Button(
                    onClick = {
                        coroutineScope.launch {
                            authPreferences.clearAuthToken()
                            onLogout()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Keluar (Logout)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}