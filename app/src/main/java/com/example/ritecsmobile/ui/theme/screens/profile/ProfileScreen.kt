package com.example.ritecsmobile.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ritecsmobile.data.local.AuthPreferences
import com.example.ritecsmobile.data.remote.RetrofitClient
import com.example.ritecsmobile.data.remote.dto.UserProfileDataDto
import com.example.ritecsmobile.ui.theme.screens.book.BASE_URL_BE // 💡 Import URL Dinamis

@Composable
fun ProfileScreen(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val authPreferences = remember { AuthPreferences(context) }

    // 💡 AMBIL DATA TOKEN & ROLE DARI PREFERENCES
    val token by authPreferences.authToken.collectAsState(initial = "")

    // Asumsi: Di AuthPreferences kamu sudah ada variabel Flow untuk userRole
    // Jika namanya beda, silakan disesuaikan (misal: role)
    val userRole by authPreferences.userRole.collectAsState(initial = "user")

    val isLoggedIn = !token.isNullOrEmpty()
    val isAdmin = userRole.equals("Admin", ignoreCase = true) // 💡 PENGECEKAN ADMIN

    var userData by remember { mutableStateOf<UserProfileDataDto?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(token) {
        if (!token.isNullOrEmpty()) {
            isLoading = true
            try {
                val response = RetrofitClient.authApi.getDashboardProfile("Bearer $token")
                userData = response.data
            } catch (e: Exception) {
                android.util.Log.e("PROFILE_ERR", "Gagal tarik data profil: ${e.message}")
            } finally {
                isLoading = false
            }
        } else {
            userData = null
        }
    }

    val firstName = userData?.first_name ?: "Guest"
    val lastName = userData?.last_name ?: ""
    val initial = if (firstName != "Guest") {
        "${firstName.take(1)}${if (lastName.isNotEmpty()) lastName.take(1) else ""}".uppercase()
    } else {
        "G"
    }

    // Siapkan URL Foto Profil (Avatar)
    val avatarUrl = userData?.img_path?.let { path ->
        BASE_URL_BE + path.trimStart('/')
    }

    // 💡 Warna Tema Ritecs
    val ritecsBlue = Color(0xFF0062CD)
    val ritecsLightBlue = Color(0xFF2E86EB)
    val backgroundSoft = Color(0xFFF5F6FA)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundSoft)
            .verticalScroll(rememberScrollState())
    ) {
        // 1. HEADER MELENGKUNG GRADASI BIRU RITECS
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    brush = Brush.verticalGradient(colors = listOf(ritecsBlue, ritecsLightBlue)),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
        )

        // 2. KARTU IDENTITAS MELAYANG
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .offset(y = (-60).dp), // Ditarik ke atas agar melayang
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ritecsBlue)
                    }
                } else if (isLoggedIn && userData != null) {
                    // TAMPILAN JIKA SUDAH LOGIN
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        // FOTO PROFIL (AVATAR) ATAU INISIAL
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(ritecsBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            if (avatarUrl != null) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context).data(avatarUrl).crossfade(true).build(),
                                    contentDescription = "Avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Text(initial, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            // 💡 Tambahkan label Admin di samping nama jika dia Admin
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("$firstName $lastName", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.Black)
                                if (isAdmin) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(color = Color(0xFFE74C3C), shape = RoundedCornerShape(4.dp)) {
                                        Text("ADMIN", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            if (userData?.is_member == true) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color(0xFFF1C40F), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Premium Member | ${userData?.member_number ?: ""}", fontSize = 11.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Cancel, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Membership NonAktif", fontSize = 11.sp, color = Color.Gray)
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))
                            Text(userData?.email ?: "", fontSize = 12.sp, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Tombol Aksi Profil
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = { onNavigate("profile_settings") },
                            modifier = Modifier.weight(1f).height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ritecsBlue)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Profil", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { if(userData?.is_member == true) onNavigate("profile_membership") },
                            modifier = Modifier.weight(1f).height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ritecsBlue),
                            enabled = userData?.is_member == true
                        ) {
                            Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Kartu", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // TAMPILAN GUEST
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(Color.LightGray), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Guest / Pengunjung", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.Black)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Masuk untuk akses penuh", fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { onNavigate("login_route") },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ritecsBlue)
                    ) {
                        Text("Masuk / Daftar Akun", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 3. DAFTAR MENU (GROUPED MODERN)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .offset(y = (-40).dp)
        ) {

            // 💡 --- GRUP KHUSUS ADMIN (HANYA MUNCUL JIKA ROLE == ADMIN) ---
            if (isLoggedIn && isAdmin) {
                Text("Administrator", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE74C3C), modifier = Modifier.padding(start = 8.dp, bottom = 8.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        MenuListItem(
                            icon = Icons.Default.Dashboard,
                            iconTint = Color(0xFFE74C3C),
                            title = "Masuk Dashboard Admin",
                            onClick = { onNavigate("admin_dashboard") }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- GRUP 1: AKUN & TRANSAKSI ---
            if (isLoggedIn) {
                Text("Akun & Transaksi", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(start = 8.dp, bottom = 8.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        MenuListItem(icon = Icons.Default.Lock, iconTint = ritecsBlue, title = "Keamanan Akun", onClick = { onNavigate("profile_settings") })
                        HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(horizontal = 16.dp))
                        MenuListItem(icon = Icons.Default.ReceiptLong, iconTint = ritecsBlue, title = "Riwayat Transaksi", onClick = { /* TODO */ })
                        HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(horizontal = 16.dp))
                        MenuListItem(icon = Icons.Default.CardGiftcard, iconTint = ritecsBlue, title = "Member Benefit", onClick = { onNavigate("benefit_member") })
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- GRUP 2: BANTUAN & INFO ---
            Text("Bantuan & Informasi", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(start = 8.dp, bottom = 8.dp))
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    MenuListItem(icon = Icons.Default.Info, iconTint = Color(0xFFE67E22), title = "Tentang Ritecs", onClick = { onNavigate("tentang_ritecs") })
                    HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(horizontal = 16.dp))
                    MenuListItem(icon = Icons.Default.Phone, iconTint = Color(0xFF27AE60), title = "Hubungi Kami", onClick = { onNavigate("kontak") })
                    HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(horizontal = 16.dp))
                    MenuListItem(icon = Icons.Default.HelpOutline, iconTint = Color(0xFFE74C3C), title = "Pusat Bantuan", onClick = { onNavigate("pusat_bantuan") })
                }
            }

            // --- TOMBOL KELUAR ---
            if (isLoggedIn) {
                Spacer(modifier = Modifier.height(36.dp))
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFFE74C3C)),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Keluar Akun", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(60.dp)) // Jarak napas untuk Bottom Navbar
        }
    }
}
@Composable
fun MenuListItem(icon: ImageVector, iconTint: Color, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(iconTint.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.Black, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
    }
}