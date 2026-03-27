package com.example.ritecsmobile.ui.theme.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ritecsmobile.data.local.AuthPreferences
import com.example.ritecsmobile.data.remote.RetrofitClient
import com.example.ritecsmobile.data.remote.dto.AdminDashboardData
import com.example.ritecsmobile.ui.screens.books.RitecsDarkBlue
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

// 💡 Warna Tema Admin
val AdminDark = Color(0xFF0F2027)
val RitecsBlue = Color(0xFF0062CD)
val RitecsLightBlue = Color(0xFF2E86EB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authPreferences = remember { AuthPreferences(context) }
    val token by authPreferences.authToken.collectAsState(initial = "")

    // 💡 State untuk Sidebar (Drawer)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // 💡 State untuk Data Analitik
    var dashboardData by remember { mutableStateOf<AdminDashboardData?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch Data Analitik saat layar dibuka
    LaunchedEffect(token) {
        if (!token.isNullOrEmpty()) {
            isLoading = true
            try {
                val response = RetrofitClient.authApi.getAdminDashboardStats("Bearer $token")
                if (response.isSuccessful) {
                    dashboardData = response.body()?.data
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal memuat analitik", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    // 💡 DAFTAR MENU SIDEBAR (Tanpa Awarding)
    val menuItems = listOf(
        DrawerMenuItem("Beranda Admin", Icons.Default.Dashboard, "admin_dashboard"),
        DrawerMenuItem("Daftar Pengguna", Icons.Default.People, "admin_manage_users"),
        DrawerMenuItem("Manajemen Role", Icons.Default.Security, "admin_manage_roles"),
        DrawerMenuItem("Aktivasi Manual", Icons.Default.VerifiedUser, "admin_activation_requests"),
        DrawerMenuItem("Approval Member", Icons.Default.CardMembership, "admin_membership_transactions"),
        DrawerMenuItem("Kelola Buku", Icons.Default.MenuBook, "admin_manage_books"),
        DrawerMenuItem("Kelola Jurnal", Icons.Default.Article, "admin_manage_journals"),
//        DrawerMenuItem("Guidelines", Icons.Default.Description, "admin_manage_guidelines"),
//        DrawerMenuItem("Layanan HAKI", Icons.Default.Gavel, "admin_manage_haki")
    )

    // ==========================================
    // SIDEBAR (DRAWER) WRAPPER
    // ==========================================
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true, // Bisa di-swipe dari kiri
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                modifier = Modifier.width(280.dp)
            ) {
                // Header Sidebar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Brush.verticalGradient(colors = listOf(AdminDark, RitecsBlue))),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Ritecs Admin Panel", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text("v2.0 Production", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // List Menu Sidebar
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    menuItems.forEach { item ->
                        NavigationDrawerItem(
                            icon = { Icon(item.icon, contentDescription = null, tint = if (item.route == "admin_dashboard") RitecsBlue else Color.Gray) },
                            label = { Text(item.title, fontWeight = if (item.route == "admin_dashboard") FontWeight.Bold else FontWeight.Medium) },
                            selected = item.route == "admin_dashboard",
                            onClick = {
                                scope.launch { drawerState.close() }
                                if (item.route != "admin_dashboard") onNavigate(item.route)
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = RitecsBlue.copy(alpha = 0.1f),
                                unselectedContainerColor = Color.Transparent,
                                selectedTextColor = RitecsBlue,
                                unselectedTextColor = Color.DarkGray
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFFE74C3C)) },
                        label = { Text("Keluar Admin", color = Color(0xFFE74C3C), fontWeight = FontWeight.Bold) },
                        selected = false,
                        onClick = onLogout,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    ) {
        // ==========================================
        // KONTEN UTAMA (DASHBOARD)
        // ==========================================
        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(colors = listOf(AdminDark, RitecsBlue)))
                ) {
                    TopAppBar(
                        title = { Text("Dashboard", fontWeight = FontWeight.ExtraBold, color = Color.White) },
                        navigationIcon = {
                            // 💡 TOMBOL HAMBURGER UNTUK BUKA SIDEBAR
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF4F6F7))
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // --- HEADER WELCOME & SWITCH MODE (Tetap Dipertahankan) ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(colors = listOf(RitecsBlue, Color(0xFFF4F6F7))),
                            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(56.dp)) {
                                Icon(Icons.Default.QueryStats, contentDescription = null, tint = Color.White, modifier = Modifier.padding(12.dp))
                            }
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text("Halo, Administrator!", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                                Text("Ringkasan analitik Ritecs hari ini.", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 💡 TOMBOL PINDAH KE MODE USER
                        Button(
                            onClick = { onNavigate("home_tab") },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = AdminDark),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Icon(Icons.Default.Smartphone, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Pindah ke Tampilan User", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- DATA ANALITIK DARI API ---
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = RitecsBlue)
                    }
                } else if (dashboardData != null) {
                    val data = dashboardData!!
                    val formatRp = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(data.totalRevenue)

                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                        // 1. KARTU PENDAPATAN
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = RitecsDarkBlue),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth().background(Brush.horizontalGradient(colors = listOf(AdminDark, RitecsBlue)))) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AccountBalanceWallet, null, tint = Color.White, modifier = Modifier.size(20.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text("Total Pendapatan", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Text(formatRp, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                                    Spacer(Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.TrendingUp, null, tint = Color(0xFF2ECC71), modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("+${data.revenueGrowth}% dari bulan lalu", color = Color(0xFF2ECC71), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // 2. GRID STATISTIK KONTEN & USER
                        Text("Ringkasan Sistem", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.DarkGray, modifier = Modifier.padding(bottom = 12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatBox(modifier = Modifier.weight(1f), title = "Total Pengguna", value = data.totalUsers.toString(), icon = Icons.Default.Group, iconColor = Color(0xFF3498DB))
                            StatBox(modifier = Modifier.weight(1f), title = "Member Aktif", value = data.activeMemberships.toString(), icon = Icons.Default.WorkspacePremium, iconColor = Color(0xFFF1C40F))
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatBox(modifier = Modifier.weight(1f), title = "Buku Rilis", value = data.totalBooks.toString(), icon = Icons.Default.LibraryBooks, iconColor = Color(0xFF2ECC71))
                            StatBox(modifier = Modifier.weight(1f), title = "Jurnal Rilis", value = data.totalJournals.toString(), icon = Icons.Default.Article, iconColor = Color(0xFFE67E22))
                        }

                        Spacer(Modifier.height(32.dp))
                    }
                } else {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("Gagal memuat data statistik", color = Color.Gray)
                    }
                }
            }
        }
    }
}

// 💡 KOMPONEN KOTAK STATISTIK KECIL
@Composable
fun StatBox(modifier: Modifier = Modifier, title: String, value: String, icon: ImageVector, iconColor: Color) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Surface(shape = CircleShape, color = iconColor.copy(alpha = 0.1f), modifier = Modifier.size(32.dp)) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.padding(6.dp))
                }
                Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = AdminDark)
            }
            Text(title, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// Data Class untuk Menu Sidebar
data class DrawerMenuItem(val title: String, val icon: ImageVector, val route: String)