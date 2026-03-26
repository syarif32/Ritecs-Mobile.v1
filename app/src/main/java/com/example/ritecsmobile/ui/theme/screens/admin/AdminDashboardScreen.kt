package com.example.ritecsmobile.ui.theme.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
val AdminDark = Color(0xFF1C2833)
val RitecsBlue = Color(0xFF0062CD)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(onNavigate: (String) -> Unit, onLogout: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ritecs Admin Panel", fontWeight = FontWeight.ExtraBold, color = Color.White) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RitecsBlue)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F6F7))
                .padding(paddingValues)
        ) {
            // Header Welcome
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RitecsBlue)
                    .padding(20.dp)
            ) {
                Column {
                    Text("Dashboard Admin", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Kelola semua data Ritecs dalam satu genggaman.", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                }
            }

            // Menu Grid
            val adminMenus = listOf(
                AdminMenu("Users", Icons.Default.People, "admin_manage_users"),
                AdminMenu("Verify OTP", Icons.Default.VerifiedUser, "admin_activation_requests"),
                AdminMenu("Membership", Icons.Default.CardMembership, "admin_membership_transactions"),
                AdminMenu("Books", Icons.Default.MenuBook, "admin_manage_books"),
                AdminMenu("Journals", Icons.Default.Article, "admin_manage_journals"),
                AdminMenu("Awards", Icons.Default.EmojiEvents, "admin_manage_awards"),
                AdminMenu("Guidelines", Icons.Default.Description, "admin_manage_guidelines"),
                AdminMenu("HAKI", Icons.Default.Gavel, "admin_manage_haki")
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(adminMenus) { menu ->
                    AdminMenuItem(menu) { onNavigate(menu.route) }
                }
            }
        }
    }
}

@Composable
fun AdminMenuItem(menu: AdminMenu, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(menu.icon, contentDescription = null, tint = RitecsBlue, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(menu.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AdminDark)
        }
    }
}

data class AdminMenu(val title: String, val icon: ImageVector, val route: String)