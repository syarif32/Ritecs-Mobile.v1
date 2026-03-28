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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ritecsmobile.data.local.AuthPreferences
import com.example.ritecsmobile.data.remote.RetrofitClient
import com.example.ritecsmobile.data.remote.dto.UserProfileDataDto
import com.example.ritecsmobile.ui.theme.screens.book.BASE_URL_BE
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope() // 💡 Tambahan scope untuk menyimpan setting Dark Mode
    val authPreferences = remember { AuthPreferences(context) }

    // 💡 AMBIL DATA TOKEN, ROLE, & TEMA DARI PREFERENCES
    val token by authPreferences.authToken.collectAsState(initial = "")
    val userRole by authPreferences.userRole.collectAsState(initial = "user")
    val isDarkMode by authPreferences.isDarkMode.collectAsState(initial = false) // 💡 State Tema Gelap

    val isLoggedIn = !token.isNullOrEmpty()
    val isAdmin = userRole.equals("Admin", ignoreCase = true)

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

    val avatarUrl = userData?.img_path?.let { path ->
        BASE_URL_BE + path.trimStart('/')
    }

    // 💡 WARNA TEMA BARU (GAYA ADMIN PREMIUM)
    val AdminDark = Color(0xFF1A303A)
    val ritecsBlue = Color(0xFF0062CD)
    val backgroundSoft = Color(0xFFF4F6F7)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundSoft)
            .verticalScroll(rememberScrollState())
    ) {
        // ==========================================
        // 1. HEADER MELENGKUNG (GRADASI ADMIN DARK)
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    brush = Brush.verticalGradient(colors = listOf(AdminDark, ritecsBlue)),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
        )

        // ==========================================
        // 2. KARTU IDENTITAS (TIDAK DIUBAH SAMA SEKALI)
        // ==========================================
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .offset(y = (-60).dp),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
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

        // ==========================================
        // 3. DAFTAR MENU (GROUPED MODERN DENGAN TEMA ADMIN)
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .offset(y = (-40).dp)
        ) {

            // --- GRUP KHUSUS ADMIN ---
            if (isLoggedIn && isAdmin) {
                Text("Administrator", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFE74C3C), modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                Text("Akun & Transaksi", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = AdminDark, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        MenuListItem(icon = Icons.Default.Lock, iconTint = AdminDark, title = "Keamanan Akun", onClick = { onNavigate("profile_settings") })
                        HorizontalDivider(color = Color(0xFF2196F3), modifier = Modifier.padding(horizontal = 16.dp))
                        MenuListItem(icon = Icons.Default.ReceiptLong, iconTint = AdminDark, title = "Riwayat Transaksi", onClick = { onNavigate("riwayat_transaksi") })
                        HorizontalDivider(color = Color(0xFF03A9F4), modifier = Modifier.padding(horizontal = 16.dp))

                        // 💡 TOMBOL SWITCH MODE GELAP (Disisipkan tepat di bawah Riwayat Transaksi)
                        MenuSwitchItem(
                            icon = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            iconTint = if (isDarkMode) Color(0xFFF39C12) else ritecsBlue,
                            title = "Mode Gelap",
                            isChecked = isDarkMode,
                            onCheckedChange = { newValue ->
                                scope.launch {
                                    authPreferences.setDarkMode(newValue)
                                }
                            }
                        )

                        HorizontalDivider(color = Color(0xFF03A9F4), modifier = Modifier.padding(horizontal = 16.dp))
                        MenuListItem(icon = Icons.Default.CardGiftcard, iconTint = AdminDark, title = "Member Benefit", onClick = { onNavigate("benefit_member") })
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- GRUP 2: BANTUAN & INFO ---
            Text("Bantuan & Informasi", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = AdminDark, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    MenuListItem(icon = Icons.Default.Info, iconTint = Color(0xFFE67E22), title = "Tentang Ritecs", onClick = { onNavigate("tentang_ritecs") })
                    HorizontalDivider(color = Color(0xFFF4F6F7), modifier = Modifier.padding(horizontal = 16.dp))
                    MenuListItem(icon = Icons.Default.Phone, iconTint = Color(0xFF27AE60), title = "Hubungi Kami", onClick = { onNavigate("kontak") })
                    HorizontalDivider(color = Color(0xFFF4F6F7), modifier = Modifier.padding(horizontal = 16.dp))
                    MenuListItem(icon = Icons.Default.HelpOutline, iconTint = ritecsBlue, title = "Pusat Bantuan", onClick = { onNavigate("pusat_bantuan") })
                }
            }

            // --- TOMBOL KELUAR ---
            if (isLoggedIn) {
                Spacer(modifier = Modifier.height(36.dp))
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color(0xFFD32F2F)),
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Keluar Akun", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(60.dp)) // Jarak napas untuk Bottom Navbar
        }
    }
}

// ==========================================
// KOMPONEN LIST MENU REGULER
// ==========================================
@Composable
fun MenuListItem(icon: ImageVector, iconTint: Color, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(iconTint.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2C3E50), modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFCBD5E1))
    }
}

// ==========================================
// 💡 KOMPONEN KHUSUS UNTUK MENU SWITCH (MODE GELAP)
// ==========================================
@Composable
fun MenuSwitchItem(icon: ImageVector, iconTint: Color, title: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(horizontal = 16.dp, vertical = 8.dp), // Vertical padding disesuaikan agar tinggi switch tidak merusak formasi
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(iconTint.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2C3E50), modifier = Modifier.weight(1f))

        // Switch Interaktif
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.scale(0.85f), // Skala diperkecil sedikit biar menyatu elegan dengan baris menu
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF0F2027), // AdminDark
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.LightGray
            )
        )
    }
}