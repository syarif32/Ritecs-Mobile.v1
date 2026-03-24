package com.example.ritecsmobile.ui.screens.main

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ritecsmobile.data.local.AuthPreferences
import com.example.ritecsmobile.data.remote.dto.BookDto
import com.example.ritecsmobile.ui.screens.auth.LoginScreen
import com.example.ritecsmobile.ui.screens.journal.JurnalScreen
import com.example.ritecsmobile.ui.theme.screens.home.BerandaScreen
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
    val bottomNavController = rememberNavController()
    val context = LocalContext.current
    val authPreferences = remember { AuthPreferences(context) }

    val token by authPreferences.authToken.collectAsState(initial = "")
    var selectedBook by remember { mutableStateOf<BookDto?>(null) }
    var selectedTraining by remember { mutableStateOf<com.example.ritecsmobile.data.remote.dto.TrainingDto?>(null) }
    var selectedHaki by remember { mutableStateOf<com.example.ritecsmobile.data.remote.dto.HakiPackageDto?>(null) }

    // 💡 [BARU] MATA-MATA ROUTE: Cek user lagi di halaman mana
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    // 💡 [BARU] Daftar halaman yang BOLEH nampilin Bottom Nav
    val mainTabs = listOf("home_tab", "buku_tab", "jurnal_tab", "profile_tab")

    Scaffold(
        bottomBar = {
            // 💡 [BARU] Logika Pinter: Munculin Navbar CUMA kalau lagi di Menu Utama
            if (currentRoute in mainTabs) {
                BottomNavigationBar(bottomNavController)
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = bottomNavController,
                startDestination = "home_tab"
            ) {
                // 1. Tab Beranda
                composable("home_tab") {
                    BerandaScreen(onNavigate = { route ->
                        if (route in mainTabs) {
                            bottomNavController.navigate(route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        } else {
                            bottomNavController.navigate(route)
                        }
                    })
                }

                // 2. Tab BUKU
                composable("buku_tab") {
                    com.example.ritecsmobile.ui.theme.screens.book.BukuScreen(
                        onNavigateToDetail = { book ->
                            selectedBook = book
                            bottomNavController.navigate("detail_buku")
                        },
                        onNavigateToLihatSemua = { section ->
                            bottomNavController.navigate("lihat_semua_buku/$section")
                        }
                    )
                }

                composable("lihat_semua_buku/{section}") { backStackEntry ->
                    val section = backStackEntry.arguments?.getString("section") ?: "Semua Buku"
                    com.example.ritecsmobile.ui.theme.screens.book.LihatSemuaBukuScreen(
                        sectionTitle = section,
                        onNavigateBack = { bottomNavController.popBackStack() },
                        onNavigateToDetail = { book ->
                            selectedBook = book
                            bottomNavController.navigate("detail_buku")
                        }
                    )
                }

                composable("detail_buku") {
                    selectedBook?.let { book ->
                        com.example.ritecsmobile.ui.theme.screens.book.BookDetailScreen(
                            book = book,
                            onNavigateBack = { bottomNavController.popBackStack() }
                        )
                    }
                }

                // 3. Tab JURNAL
                composable("jurnal_tab") {
                    JurnalScreen()
                }

                // 4. Tab PROFIL
                composable("profile_tab") {
                    if (token.isNullOrEmpty()) {
                        LoginScreen(
                            onLoginSuccess = { },
                            onNavigateToRegister = { bottomNavController.navigate("register_route") },
                            onNavigateToOtp = { bottomNavController.navigate("otp_route") }
                        )
                    } else {
                        com.example.ritecsmobile.ui.screens.profile.ProfileScreen(
                            onNavigate = { route -> bottomNavController.navigate(route) },
                            onLogout = {
                                kotlinx.coroutines.GlobalScope.launch {
                                    authPreferences.clearAuthToken()
                                }
                            }
                        )
                    }
                }

                composable("login_route") {
                    LoginScreen(
                        onLoginSuccess = {
                            bottomNavController.navigate("profile_tab") { popUpTo("home_tab") }
                        },
                        onNavigateToRegister = { bottomNavController.navigate("register_route") },
                        onNavigateToOtp = { bottomNavController.navigate("otp_route") }
                    )
                }

                composable("profile_membership") {
                    com.example.ritecsmobile.ui.screens.profile.MembershipCardScreen(onNavigateBack = { bottomNavController.popBackStack() })
                }
                composable("profile_settings") {
                    com.example.ritecsmobile.ui.screens.profile.ProfileSettingsScreen(onNavigateBack = { bottomNavController.popBackStack() })
                }
                composable("layanan_jurnal") {
                    com.example.ritecsmobile.ui.screens.home.LayananJurnalScreen()
                }
                composable("benefit_member") {
                    com.example.ritecsmobile.ui.screens.members.MemberRegistrationScreen()
                }
                composable("haki") {
                    com.example.ritecsmobile.ui.screens.home.HakiScreen(
                        onNavigateBack = { bottomNavController.popBackStack() },
                        onNavigateToDetail = { pkg ->
                            selectedHaki = pkg
                            bottomNavController.navigate("detail_haki")
                        }
                    )
                }
                composable("detail_haki") {
                    selectedHaki?.let { pkg ->
                        com.example.ritecsmobile.ui.screens.home.HakiDetailScreen(hakiPackage = pkg, onNavigateBack = { bottomNavController.popBackStack() })
                    }
                }
                composable("pelatihan") {
                    com.example.ritecsmobile.ui.screens.home.PelatihanScreen(
                        onNavigateBack = { bottomNavController.popBackStack() },
                        onNavigateToDetail = { training ->
                            selectedTraining = training
                            bottomNavController.navigate("detail_pelatihan")
                        }
                    )
                }
                composable("detail_pelatihan") {
                    selectedTraining?.let { training ->
                        com.example.ritecsmobile.ui.screens.home.PelatihanDetailScreen(training = training, onNavigateBack = { bottomNavController.popBackStack() })
                    }
                }
                composable("petunjuk_penulis") {
                    com.example.ritecsmobile.ui.screens.books.PetunjukPenulisScreen()
                }
                composable("tentang_ritecs") {
                    com.example.ritecsmobile.ui.screens.profile.TentangScreen(onNavigateBack = { bottomNavController.popBackStack() })
                }
                composable("pusat_bantuan") {
                    com.example.ritecsmobile.ui.screens.profile.PusatBantuanScreen(onNavigateBack = { bottomNavController.popBackStack() })
                }
                composable("kontak") {
                    com.example.ritecsmobile.ui.screens.home.KontakScreen(onNavigateBack = { bottomNavController.popBackStack() })
                }
                composable("register_route") {
                    Text("Halaman Register (Belum dibuat)")
                }
                composable("otp_route") {
                    Text("Halaman OTP (Belum dibuat)")
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("Beranda", "home_tab", Icons.Default.Home),
        BottomNavItem("Buku", "buku_tab", Icons.Default.Book),
        BottomNavItem("Jurnal", "jurnal_tab", Icons.Default.Article),
        BottomNavItem("Profil", "profile_tab", Icons.Default.Person)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val ritecsBlue = Color(0xFF0062CD)

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier.border(0.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) ritecsBlue else Color(0xFF94A3B8)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) ritecsBlue else Color(0xFF94A3B8)
                    )
                },
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = ritecsBlue.copy(alpha = 0.1f),
                    selectedIconColor = ritecsBlue,
                    unselectedIconColor = Color(0xFF94A3B8),
                    selectedTextColor = ritecsBlue,
                    unselectedTextColor = Color(0xFF94A3B8)
                )
            )
        }
    }
}

data class BottomNavItem(val title: String, val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)