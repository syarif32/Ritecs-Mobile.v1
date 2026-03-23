package com.example.ritecsmobile.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ritecsmobile.data.local.AuthPreferences
import com.example.ritecsmobile.data.remote.dto.BookDto
import com.example.ritecsmobile.ui.screens.auth.LoginScreen
import com.example.ritecsmobile.ui.screens.home.BerandaScreen
import com.example.ritecsmobile.ui.screens.journal.JurnalScreen

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
    Scaffold(
        bottomBar = { BottomNavigationBar(bottomNavController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = bottomNavController,
                startDestination = "home_tab"
            ) {
                // 1. Tab Beranda
                composable("home_tab") {
                    BerandaScreen(onNavigate = { route -> bottomNavController.navigate(route) })
                }

                // 2. Tab BUKU
                composable("buku_tab") {
                    com.example.ritecsmobile.ui.theme.screens.book.BukuScreen(
                        onNavigateToDetail = { book ->
                            // 💡 SIMPAN BUKU YANG DIKLIK & BUKA HALAMAN DETAIL
                            selectedBook = book
                            bottomNavController.navigate("detail_buku")
                        },
                        onNavigateToLihatSemua = { section ->
                            bottomNavController.navigate("lihat_semua_buku/$section")
                        }
                    )
                }

                // 💡 PINTU MASUK LIHAT SEMUA BUKU
                composable("lihat_semua_buku/{section}") { backStackEntry ->
                    val section = backStackEntry.arguments?.getString("section") ?: "Semua Buku"
                    com.example.ritecsmobile.ui.theme.screens.book.LihatSemuaBukuScreen(
                        sectionTitle = section,
                        onNavigateBack = { bottomNavController.popBackStack() },
                        onNavigateToDetail = { book ->
                            // 💡 SIMPAN BUKU YANG DIKLIK & BUKA HALAMAN DETAIL
                            selectedBook = book
                            bottomNavController.navigate("detail_buku")
                        }
                    )
                }

                // 💡 PINTU MASUK DETAIL BUKU (BARU DITAMBAHKAN!)
                composable("detail_buku") {
                    selectedBook?.let { book ->
                        com.example.ritecsmobile.ui.theme.screens.book.BookDetailScreen(
                            book = book,
                            onNavigateBack = { bottomNavController.popBackStack() }
                        )
                    }
                }

                // 3. Tab Member
//                composable("member_tab") {
//                    com.example.ritecsmobile.ui.screens.members.MemberScreen()
//                }
                // 3. Tab JURNAL (Menggantikan Member)
                composable("jurnal_tab") {
                    JurnalScreen()
                }

                // 4. Tab PROFIL
                composable("profile_tab") {
                    if (token.isNullOrEmpty()) {
                        LoginScreen(onLoginSuccess = { })
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
                    LoginScreen(onLoginSuccess = {
                        bottomNavController.navigate("profile_tab") { popUpTo("home_tab") }
                    })
                }

                // ================== SUB-MENU ==================
                composable("profile_membership") {
                    com.example.ritecsmobile.ui.screens.profile.MembershipCardScreen(
                        onNavigateBack = { bottomNavController.popBackStack() }
                    )
                }
                composable("profile_settings") {
                    com.example.ritecsmobile.ui.screens.profile.ProfileSettingsScreen(
                        onNavigateBack = { bottomNavController.popBackStack() }
                    )
                }
                composable("layanan_jurnal") {
                    com.example.ritecsmobile.ui.screens.home.LayananJurnalScreen()
                }
                composable("benefit_member") {
                    com.example.ritecsmobile.ui.screens.members.MemberRegistrationScreen()
                }
                // Rute List HAKI
                composable("haki") {
                    com.example.ritecsmobile.ui.screens.home.HakiScreen(
                        onNavigateBack = { bottomNavController.popBackStack() },
                        onNavigateToDetail = { pkg ->
                            selectedHaki = pkg
                            bottomNavController.navigate("detail_haki") // Pindah halaman
                        }
                    )
                }

                // Rute Detail HAKI
                composable("detail_haki") {
                    selectedHaki?.let { pkg ->
                        com.example.ritecsmobile.ui.screens.home.HakiDetailScreen(
                            hakiPackage = pkg,
                            onNavigateBack = { bottomNavController.popBackStack() }
                        )
                    }
                }
                // Rute List Pelatihan
                composable("pelatihan") {
                    com.example.ritecsmobile.ui.screens.home.PelatihanScreen(
                        onNavigateBack = { bottomNavController.popBackStack() },
                        onNavigateToDetail = { training ->
                            selectedTraining = training
                            bottomNavController.navigate("detail_pelatihan")
                        }
                    )
                }

                // Rute Detail Pelatihan
                composable("detail_pelatihan") {
                    selectedTraining?.let { training ->
                        com.example.ritecsmobile.ui.screens.home.PelatihanDetailScreen(
                            training = training,
                            onNavigateBack = { bottomNavController.popBackStack() }
                        )
                    }
                }
                composable("petunjuk_penulis") {
                    com.example.ritecsmobile.ui.screens.books.PetunjukPenulisScreen()
                }
                composable("tentang_ritecs") {
                    com.example.ritecsmobile.ui.screens.profile.TentangScreen(
                        onNavigateBack = { bottomNavController.popBackStack() }
                    )
                }
                composable("pusat_bantuan") {
                    com.example.ritecsmobile.ui.screens.profile.PusatBantuanScreen(
                        onNavigateBack = { bottomNavController.popBackStack() }
                    )
                }
                composable("kontak") {
                    com.example.ritecsmobile.ui.screens.home.KontakScreen(
                        onNavigateBack = { bottomNavController.popBackStack() }
                    )
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
//        BottomNavItem("Member", "member_tab", Icons.Default.AccountBox),
        BottomNavItem("Jurnal", "jurnal_tab", Icons.Default.Article),
        BottomNavItem("Profil", "profile_tab", Icons.Default.Person)
    )

    NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

data class BottomNavItem(val title: String, val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)