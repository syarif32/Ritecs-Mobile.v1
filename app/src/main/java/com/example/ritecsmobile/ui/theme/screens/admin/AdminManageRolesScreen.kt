package com.example.ritecsmobile.ui.theme.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.ritecsmobile.data.local.AuthPreferences
import com.example.ritecsmobile.data.remote.RetrofitClient
import com.example.ritecsmobile.data.remote.dto.PasswordConfirmRequest
import com.example.ritecsmobile.data.remote.dto.RoleManageUserDto
import com.example.ritecsmobile.ui.screens.books.RitecsDarkBlue
import com.example.ritecsmobile.ui.screens.books.RitecsLightBlue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManageRolesScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authPreferences = remember { AuthPreferences(context) }
    val token by authPreferences.authToken.collectAsState(initial = "")

    var users by remember { mutableStateOf<List<RoleManageUserDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // State untuk Dialog
    var selectedUserForAction by remember { mutableStateOf<Pair<RoleManageUserDto, String>?>(null) } // "PROMOTE" or "DEMOTE"
    var adminPasswordInput by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    LaunchedEffect(token) {
        if (!token.isNullOrEmpty()) {
            try {
                val response = RetrofitClient.authApi.getRoleManagementUsers("Bearer $token")
                if (response.isSuccessful) {
                    users = response.body()?.data ?: emptyList()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Toast.makeText(context, "Error loading users", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            // 💡 HEADER DENGAN GRADASI PROFESIONAL
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(colors = listOf(RitecsDarkBlue, RitecsLightBlue)))
            ) {
                TopAppBar(
                    title = { Text("Manajemen Akses", fontWeight = FontWeight.Bold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RitecsBlue)
            }
        } else if (users.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(Modifier.height(8.dp))
                    Text("Tidak ada data pengguna", color = Color.Gray, fontWeight = FontWeight.Medium)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF5F6FA)).padding(16.dp)) {
                items(users) { user ->
                    RoleUserCard(
                        user = user,
                        onPromote = {
                            selectedUserForAction = Pair(user, "PROMOTE")
                            adminPasswordInput = ""
                        },
                        onDemote = {
                            selectedUserForAction = Pair(user, "DEMOTE")
                            adminPasswordInput = ""
                        }
                    )
                }
            }
        }

        // ==========================================
        // DIALOG KONFIRMASI PASSWORD ADMIN
        // ==========================================
        selectedUserForAction?.let { (targetUser, actionType) ->
            val isPromote = actionType == "PROMOTE"
            val actionTitle = if (isPromote) "Jadikan Admin" else "Cabut Akses Admin"
            val actionColor = if (isPromote) RitecsBlue else Color(0xFFE74C3C)
            val actionIcon = if (isPromote) Icons.Default.AdminPanelSettings else Icons.Default.PersonRemove

            AlertDialog(
                onDismissRequest = { if (!isSubmitting) selectedUserForAction = null },
                properties = DialogProperties(usePlatformDefaultWidth = false),
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(16.dp),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(actionIcon, contentDescription = null, tint = actionColor)
                        Spacer(Modifier.width(8.dp))
                        Text(actionTitle, fontWeight = FontWeight.ExtraBold, color = RitecsDarkBlue)
                    }
                },
                text = {
                    Column {
                        Text("Anda akan mengubah hak akses untuk pengguna:", fontSize = 13.sp, color = Color.Gray)
                        Spacer(Modifier.height(4.dp))
                        Text(targetUser.email, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)

                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFFF0F0F0))
                        Spacer(Modifier.height(16.dp))

                        Text("Masukkan password Anda untuk konfirmasi tindakan ini.", fontSize = 13.sp, color = Color.DarkGray)
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = adminPasswordInput,
                            onValueChange = { adminPasswordInput = it },
                            label = { Text("Password Admin Anda") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray) },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = actionColor),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (adminPasswordInput.isBlank()) {
                                Toast.makeText(context, "Password wajib diisi!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            isSubmitting = true
                            scope.launch {
                                try {
                                    val req = PasswordConfirmRequest(adminPasswordInput)
                                    val response = if (isPromote) {
                                        RetrofitClient.authApi.promoteUser("Bearer $token", targetUser.user_id, req)
                                    } else {
                                        RetrofitClient.authApi.demoteUser("Bearer $token", targetUser.user_id, req)
                                    }

                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "Hak akses berhasil diubah!", Toast.LENGTH_SHORT).show()
                                        // Update UI State langsung tanpa harus reload API
                                        users = users.map {
                                            if (it.user_id == targetUser.user_id) it.copy(role = if (isPromote) "Admin" else "User")
                                            else it
                                        }
                                        selectedUserForAction = null
                                    } else {
                                        Toast.makeText(context, "Gagal: Password salah / Akses ditolak", Toast.LENGTH_LONG).show()
                                    }
                                } catch (e: CancellationException) {
                                    throw e
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isSubmitting = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = actionColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isSubmitting) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                        else Text("Konfirmasi Tindakan", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedUserForAction = null }, modifier = Modifier.fillMaxWidth()) {
                        Text("Batal", color = Color.Gray)
                    }
                }
            )
        }
    }
}

// ==========================================
// CARD LIST PENGGUNA (ELEGAN)
// ==========================================
@Composable
fun RoleUserCard(user: RoleManageUserDto, onPromote: () -> Unit, onDemote: () -> Unit) {
    val isAdmin = user.role.equals("Admin", ignoreCase = true)

    // Ambil inisial nama untuk avatar
    val initial = if (user.name.isNotBlank()) user.name.take(1).uppercase() else "?"

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            // Bagian Atas: Info User
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar Inisial
                Surface(
                    shape = CircleShape,
                    color = if (isAdmin) RitecsBlue.copy(alpha = 0.1f) else Color(0xFFF0F0F0),
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(initial, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = if (isAdmin) RitecsBlue else Color.Gray)
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(user.email, color = Color.Gray, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                // Label Status Role
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isAdmin) RitecsBlue.copy(alpha = 0.1f) else Color(0xFFF5F5F5)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isAdmin) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                            contentDescription = null,
                            tint = if (isAdmin) RitecsBlue else Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = user.role.uppercase(),
                            color = if (isAdmin) RitecsBlue else Color.DarkGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(Modifier.height(12.dp))

            // Bagian Bawah: Tombol Aksi
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                if (isAdmin) {
                    if (user.email != "admin@ritecs.com") { // Lindungi Super Admin
                        OutlinedButton(
                            onClick = onDemote,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE74C3C)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(36.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text("Cabut Akses Admin", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text("Super Administrator", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(end = 8.dp))
                    }
                } else {
                    Button(
                        onClick = onPromote,
                        colors = ButtonDefaults.buttonColors(containerColor = RitecsBlue),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text("Jadikan Admin", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}