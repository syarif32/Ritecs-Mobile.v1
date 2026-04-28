package com.example.ritecsmobile.ui.screens.main

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
import com.example.ritecsmobile.data.remote.RetrofitClient
import com.example.ritecsmobile.data.remote.dto.BookDto
import com.example.ritecsmobile.ui.screens.auth.LoginScreen
import com.example.ritecsmobile.ui.screens.auth.RegisterScreen
import com.example.ritecsmobile.ui.screens.auth.VerifyOtpScreen
import com.example.ritecsmobile.ui.screens.books.PetunjukPenulisScreen
import com.example.ritecsmobile.ui.screens.journal.JurnalScreen
import com.example.ritecsmobile.ui.screens.onboarding.OnboardingScreen
import com.example.ritecsmobile.ui.screens.onboarding.SplashScreen
import com.example.ritecsmobile.ui.screens.profile.ChangePasswordScreen
import com.example.ritecsmobile.ui.theme.screens.home.BerandaScreen
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
    val bottomNavController = rememberNavController()
    val context = LocalContext.current
    val authPreferences = remember { AuthPreferences(context) }
    val scope = rememberCoroutineScope()
    val hasSeenOnboarding by authPreferences.hasSeenOnboarding.collectAsState(initial = false)

    // --- KONSTANTA WARNA ---
    val RitecsBlue = Color(0xFF0062CD)
    val BackgroundSoft = Color(0xFFF5F6FA)

    // --- STATE MANAGEMENT ---
    val token by authPreferences.authToken.collectAsState(initial = "")
    var selectedBook by remember { mutableStateOf<BookDto?>(null) }
    var selectedTraining by remember { mutableStateOf<com.example.ritecsmobile.data.remote.dto.TrainingDto?>(null) }
    var selectedHaki by remember { mutableStateOf<com.example.ritecsmobile.data.remote.dto.HakiPackageDto?>(null) }
    var registeredEmail by remember { mutableStateOf("") }

    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val mainTabs = listOf("home_tab", "buku_tab", "jurnal_tab", "profile_tab")

    Scaffold(
        bottomBar = {
            if (currentRoute in mainTabs) {
                BottomNavigationBar(bottomNavController)
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = bottomNavController,
                startDestination = "splash_route"
            ) {

                // ==========================================
                // 1. RUTE ONBOARDING & SPLASH
                // ==========================================
                composable("splash_route") {
                    com.example.ritecsmobile.ui.screens.onboarding.SplashScreen(
                        onNavigateToOnboarding = {
                            if (hasSeenOnboarding) {
                                bottomNavController.navigate("home_tab") {
                                    popUpTo("splash_route") { inclusive = true }
                                }
                            } else {
                                bottomNavController.navigate("onboarding_route") {
                                    popUpTo("splash_route") { inclusive = true }
                                }
                            }
                        }
                    )
                }

                composable("onboarding_route") {
                    com.example.ritecsmobile.ui.screens.onboarding.OnboardingScreen(
                        onNavigateToLogin = {
                            scope.launch {
                                authPreferences.setOnboardingCompleted()
                            }
                            bottomNavController.navigate("login_route") {
                                popUpTo("onboarding_route") { inclusive = true }
                            }
                        },
                        onNavigateToHome = {
                            scope.launch {
                                authPreferences.setOnboardingCompleted()
                            }
                            bottomNavController.navigate("home_tab") {
                                popUpTo("onboarding_route") { inclusive = true }
                            }
                        }
                    )
                }
                composable("login_route") {
                    LoginScreen(
                        onLoginSuccess = { role ->
                            if (role.equals("Admin", ignoreCase = true)) {
                                bottomNavController.navigate("admin_dashboard") {
                                    popUpTo("login_route") { inclusive = true }
                                }
                            } else {
                                bottomNavController.navigate("home_tab") {
                                    popUpTo("login_route") { inclusive = true }
                                }
                            }
                        },
                        onNavigateToRegister = { bottomNavController.navigate("register_route") },
                        onNavigateToOtp = { email ->
                            bottomNavController.navigate("otp_route/$email")
                        }
                    )
                }

                composable("register_route") {
                    RegisterScreen(
                        onRegisterSuccess = { email ->
                            bottomNavController.navigate("otp_route/$email")
                        },
                        onNavigateBack = { bottomNavController.popBackStack() }
                    )
                }

                composable("otp_route/{email}") { backStackEntry ->
                    val emailDariRoute = backStackEntry.arguments?.getString("email") ?: ""

                    VerifyOtpScreen(
                        email = emailDariRoute,
                        onVerifySuccess = {
                            bottomNavController.navigate("home_tab") {
                                popUpTo("login_route") { inclusive = true }
                            }
                        },
                        onNavigateBack = { bottomNavController.popBackStack() }
                    )
                }

                composable("home_tab") {
                    BerandaScreen(
                        onNavigate = { route ->
                            if (route in mainTabs) {
                                bottomNavController.navigate(route) {
                                    popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            } else {
                                bottomNavController.navigate(route)
                            }
                        },
                        onNavigateToBookDetail = { book ->
                            selectedBook = book
                            bottomNavController.navigate("detail_buku")
                        }
                    )
                }

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

                composable("jurnal_tab") {
                    JurnalScreen()
                }

                composable("profile_tab") {
                    if (token.isNullOrEmpty()) {
                        LoginScreen(
                            onLoginSuccess = { bottomNavController.navigate("home_tab") },
                            onNavigateToRegister = { bottomNavController.navigate("register_route") },
                            onNavigateToOtp = { email ->
                                bottomNavController.navigate("otp_route/$email")
                            }
                        )
                    } else {
                        com.example.ritecsmobile.ui.screens.profile.ProfileScreen(
                            onNavigate = { route -> bottomNavController.navigate(route) },
                            onLogout = {
                                scope.launch {
                                    authPreferences.clearAuthToken()
                                    bottomNavController.navigate("login_route") {
                                        popUpTo(0)
                                    }
                                }
                            }
                        )
                    }
                }

                // ==========================================
                // 4. RUTE DETAIL & LAYANAN (USER)
                // ==========================================
                composable("detail_buku") {
                    selectedBook?.let { book ->
                        com.example.ritecsmobile.ui.theme.screens.book.BookDetailScreen(
                            book = book,
                            onNavigateBack = { bottomNavController.popBackStack() }
                        )
                    }
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

                composable("profile_membership") {
                    com.example.ritecsmobile.ui.screens.profile.MembershipCardScreen(onNavigateBack = { bottomNavController.popBackStack() })
                }
                composable("profile_settings") {
                    com.example.ritecsmobile.ui.screens.profile.ProfileSettingsScreen(onNavigateBack = { bottomNavController.popBackStack() })
                }
                composable("riwayat_transaksi") {
                    com.example.ritecsmobile.ui.screens.profile.TransactionHistoryScreen(
                        onNavigateBack = { bottomNavController.popBackStack() }
                    )
                }
                composable("layanan_jurnal") {
                    com.example.ritecsmobile.ui.screens.home.LayananJurnalScreen()
                }
                composable("benefit_member") {
                    com.example.ritecsmobile.ui.screens.members.MemberRegistrationScreen(
                        onNavigateBack = { bottomNavController.popBackStack() },
                        onSuccess = { bottomNavController.navigate("profile_membership") }
                    )
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
                    com.example.ritecsmobile.ui.screens.books.PetunjukPenulisScreen(
                        onNavigateBack = { bottomNavController.popBackStack() }
                    )
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
                composable("ubah_password") {
                    // 💡 INI YANG DIGANTI (Dari navController jadi bottomNavController)
                    ChangePasswordScreen(onNavigateBack = { bottomNavController.popBackStack() })
                }


                // 5. RUTE ADMIN (DASHBOARD & MANAGEMENT)
                composable("admin_dashboard") {
                    // State untuk menyimpan data statistik
                    var dashboardData by remember { mutableStateOf<com.example.ritecsmobile.data.remote.dto.AdminDashboardData?>(null) }
                    LaunchedEffect(token) {
                        if (!token.isNullOrEmpty()) {
                            try {
                                val response = RetrofitClient.authApi.getAdminDashboardStats("Bearer $token")

                                if (response.isSuccessful) {
                                    dashboardData = response.body()?.data
                                } else {
                                    Log.e("AdminDashboard", "Error: ${response.errorBody()?.string()}")
                                }
                            } catch (e: Exception) {
                                Log.e("AdminDashboard", "Exception: ${e.message}")
                            }
                        }
                    }

                    com.example.ritecsmobile.ui.theme.screens.admin.AdminDashboardScreen(

                        onNavigate = { route -> bottomNavController.navigate(route) },
                        onLogout = {
                            scope.launch {
                                authPreferences.clearAuthToken()
                                bottomNavController.navigate("login_route") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    )
                }

                composable("admin_activation_requests") {
                    com.example.ritecsmobile.ui.theme.screens.admin.ActivationRequestScreen(onNavigateBack = { bottomNavController.popBackStack() })
                }

                composable("admin_manage_users") {
                    // Screen List User Admin
                    // com.example.ritecsmobile.ui.theme.screens.admin.AdminUserListScreen(onNavigateBack = { bottomNavController.popBackStack() })
                }
                composable("admin_manage_roles"){
                    com.example.ritecsmobile.ui.theme.screens.admin.AdminManageRolesScreen(onNavigateBack = { bottomNavController.popBackStack() })
                }


                composable("admin_membership_transactions") {
                    com.example.ritecsmobile.ui.theme.screens.admin.AdminMembershipScreen(onNavigateBack = { bottomNavController.popBackStack() })
                }
                composable("admin_manage_banks") {
                    com.example.ritecsmobile.ui.theme.screens.admin.AdminBankScreen (onNavigateBack = { bottomNavController.popBackStack() })
                }
                composable("admin_manage_users") {
                    com.example.ritecsmobile.ui.theme.screens.admin.AdminUserManageScreen(onNavigateBack = { bottomNavController.popBackStack() })
                }
                composable("admin_manage_books") {
                    com.example.ritecsmobile.ui.theme.screens.admin.AdminManageBooksScreen(onNavigateBack = { bottomNavController.popBackStack() })
                }
                composable("admin_manage_journals") {
                    com.example.ritecsmobile.ui.theme.screens.admin.AdminManageJournalsScreen(onNavigateBack = { bottomNavController.popBackStack() })
                }
                composable("admin_manage_awardings") {

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
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        modifier = Modifier.border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF004191), ritecsBlue)
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 20.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = item.icon, contentDescription = item.title, tint = Color.White)
                        }
                    } else {
                        Icon(imageVector = item.icon, contentDescription = item.title, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) ritecsBlue else MaterialTheme.colorScheme.onSurfaceVariant
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
                    indicatorColor = Color.Transparent,
                    selectedTextColor = ritecsBlue,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

data class BottomNavItem(val title: String, val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)