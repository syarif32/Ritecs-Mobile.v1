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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import com.example.ritecsmobile.data.local.AuthPreferences
import com.example.ritecsmobile.data.remote.RetrofitClient
import com.example.ritecsmobile.data.remote.dto.AdminUserManageDto
import com.example.ritecsmobile.data.remote.dto.MakeMemberRequest
import com.example.ritecsmobile.ui.screens.books.RitecsDarkBlue
import com.example.ritecsmobile.ui.screens.books.RitecsLightBlue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserManageScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authPreferences = remember { AuthPreferences(context) }
    val token by authPreferences.authToken.collectAsState(initial = "")

    var users by remember { mutableStateOf<List<AdminUserManageDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Tab State
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Pengguna Aktif", "Akun Nonaktif")

    // Dialog States
    var userToDeactivate by remember { mutableStateOf<AdminUserManageDto?>(null) }
    var userToRestore by remember { mutableStateOf<AdminUserManageDto?>(null) }
    var userToMakeMember by remember { mutableStateOf<AdminUserManageDto?>(null) }

    // Form Make Member State
    var memberNumber by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    fun loadData() {
        scope.launch {
            if (!token.isNullOrEmpty()) {
                isLoading = true
                try {
                    val response = if (selectedTabIndex == 0) {
                        RetrofitClient.authApi.getActiveUsers("Bearer $token")
                    } else {
                        RetrofitClient.authApi.getNonActiveUsers("Bearer $token")
                    }
                    if (response.isSuccessful) users = response.body()?.data ?: emptyList()
                } catch (e: CancellationException) { throw e }
                catch (e: Exception) { Toast.makeText(context, "Error mengambil data pengguna", Toast.LENGTH_SHORT).show() }
                finally { isLoading = false }
            }
        }
    }

    LaunchedEffect(token, selectedTabIndex) { loadData() }

    Scaffold(
        topBar = {
            Column {
                // 💡 HEADER DENGAN GRADASI PROFESIONAL
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(colors = listOf(RitecsDarkBlue, RitecsLightBlue)))
                ) {
                    TopAppBar(
                        title = { Text("Manajemen Pengguna", fontWeight = FontWeight.Bold, color = Color.White) },
                        navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                    )
                }
                // TAB ROW MODERN
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.White,
                    contentColor = RitecsBlue,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = RitecsBlue,
                            height = 3.dp
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    title,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTabIndex == index) RitecsBlue else Color.Gray
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = RitecsBlue) }
        } else if (users.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.GroupOff, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(Modifier.height(8.dp))
                    Text("Tidak ada pengguna di kategori ini", color = Color.Gray, fontWeight = FontWeight.Medium)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF5F6FA)).padding(16.dp)) {
                items(users) { user ->
                    UserManageCard(
                        user = user,
                        isActiveTab = selectedTabIndex == 0,
                        onDeactivate = { userToDeactivate = user },
                        onRestore = { userToRestore = user },
                        onMakeMember = {
                            userToMakeMember = user
                            // Auto generate member number Ritecs Format
                            memberNumber = "01.${SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())}.${(1000..9999).random()}"
                        }
                    )
                }
            }
        }

        // ==========================================
        // DIALOG NONAKTIFKAN (BLOKIR) USER
        // ==========================================
        userToDeactivate?.let { user ->
            AlertDialog(
                onDismissRequest = { if (!isSubmitting) userToDeactivate = null },
                shape = RoundedCornerShape(16.dp),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Block, contentDescription = null, tint = Color.Red)
                        Spacer(Modifier.width(8.dp))
                        Text("Blokir Pengguna?", fontWeight = FontWeight.Bold, color = Color.Red)
                    }
                },
                text = { Text("Akses akun untuk ${user.name} akan ditutup. Ia tidak akan bisa login lagi ke aplikasi Ritecs.", fontSize = 14.sp) },
                confirmButton = {
                    Button(
                        onClick = {
                            isSubmitting = true
                            scope.launch {
                                try {
                                    val res = RetrofitClient.authApi.deactivateUser("Bearer $token", user.user_id)
                                    if (res.isSuccessful) {
                                        Toast.makeText(context, "Akun berhasil diblokir", Toast.LENGTH_SHORT).show()
                                        userToDeactivate = null; loadData()
                                    }
                                } catch (e: CancellationException) { throw e } catch (e: Exception) {} finally { isSubmitting = false }
                            }
                        }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red), shape = RoundedCornerShape(8.dp)
                    ) { if (isSubmitting) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp)) else Text("Blokir Permanen", fontWeight = FontWeight.Bold) }
                },
                dismissButton = { TextButton(onClick = { userToDeactivate = null }, enabled = !isSubmitting) { Text("Batal", color = Color.Gray) } }
            )
        }

        // ==========================================
        // DIALOG RESTORE (BUKA BLOKIR) USER
        // ==========================================
        userToRestore?.let { user ->
            AlertDialog(
                onDismissRequest = { if (!isSubmitting) userToRestore = null },
                shape = RoundedCornerShape(16.dp),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SettingsBackupRestore, contentDescription = null, tint = Color(0xFF27AE60))
                        Spacer(Modifier.width(8.dp))
                        Text("Buka Akses Akun?", fontWeight = FontWeight.Bold, color = Color(0xFF27AE60))
                    }
                },
                text = { Text("Akses aplikasi untuk ${user.name} akan dibuka kembali.", fontSize = 14.sp) },
                confirmButton = {
                    Button(
                        onClick = {
                            isSubmitting = true
                            scope.launch {
                                try {
                                    val res = RetrofitClient.authApi.restoreUser("Bearer $token", user.user_id)
                                    if (res.isSuccessful) {
                                        Toast.makeText(context, "Akun berhasil diaktifkan", Toast.LENGTH_SHORT).show()
                                        userToRestore = null; loadData()
                                    }
                                } catch (e: CancellationException) { throw e } catch (e: Exception) {} finally { isSubmitting = false }
                            }
                        }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27AE60)), shape = RoundedCornerShape(8.dp)
                    ) { if (isSubmitting) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp)) else Text("Aktifkan Kembali", fontWeight = FontWeight.Bold) }
                },
                dismissButton = { TextButton(onClick = { userToRestore = null }, enabled = !isSubmitting) { Text("Batal", color = Color.Gray) } }
            )
        }

        // ==========================================
        // DIALOG ANGKAT JADI MEMBER (MANUAL)
        // ==========================================
        userToMakeMember?.let { user ->
            AlertDialog(
                onDismissRequest = { if (!isSubmitting) userToMakeMember = null },
                properties = DialogProperties(usePlatformDefaultWidth = false), // Form lebih lega
                modifier = Modifier.fillMaxWidth(0.9f).padding(vertical = 24.dp),
                shape = RoundedCornerShape(16.dp),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = Color(0xFFF1C40F).copy(alpha = 0.2f)) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color(0xFFF39C12), modifier = Modifier.padding(8.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Text("Jadikan Member", fontWeight = FontWeight.ExtraBold, color = RitecsDarkBlue)
                    }
                },
                text = {
                    Column {
                        Text("Anda akan mendaftarkan secara manual pengguna berikut menjadi Member Premium Ritecs:", fontSize = 13.sp, color = Color.Gray)
                        Spacer(Modifier.height(12.dp))

                        // KOTAK INFO SINGKAT
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)), elevation = CardDefaults.cardElevation(0.dp)) {
                            Row(Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PersonOutline, null, tint = RitecsBlue)
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(user.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(user.email, color = Color.Gray, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                        Text("Nomor Member Ritecs", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = RitecsBlue)
                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = memberNumber,
                            onValueChange = { memberNumber = it },
                            label = { Text("No. Member (Bisa diubah)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RitecsBlue),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (memberNumber.isBlank()) {
                                Toast.makeText(context, "Nomor member wajib diisi!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            isSubmitting = true
                            scope.launch {
                                try {
                                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    val startDate = sdf.format(Date())
                                    val cal = Calendar.getInstance()
                                    cal.add(Calendar.YEAR, 1)
                                    val endDate = sdf.format(cal.time)

                                    val req = MakeMemberRequest(memberNumber, startDate, endDate)
                                    val res = RetrofitClient.authApi.makeUserMember("Bearer $token", user.user_id, req)

                                    if (res.isSuccessful) {
                                        Toast.makeText(context, "Berhasil! ${user.name} sekarang adalah Member.", Toast.LENGTH_SHORT).show()
                                        userToMakeMember = null; loadData()
                                    } else {
                                        Toast.makeText(context, "Gagal: Nomor sudah dipakai/Duplikat", Toast.LENGTH_LONG).show()
                                    }
                                } catch (e: CancellationException) { throw e } catch (e: Exception) { Toast.makeText(context, "Koneksi Error", Toast.LENGTH_SHORT).show() } finally { isSubmitting = false }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RitecsBlue),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) { if (isSubmitting) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp)) else Text("Daftarkan Member Premium", fontWeight = FontWeight.Bold) }
                },
                dismissButton = { TextButton(onClick = { userToMakeMember = null }, modifier = Modifier.fillMaxWidth(), enabled = !isSubmitting) { Text("Batal", color = Color.Gray) } }
            )
        }
    }
}

// --- DESAIN KARTU USER ELEGAN ---
@Composable
fun UserManageCard(
    user: AdminUserManageDto,
    isActiveTab: Boolean,
    onDeactivate: () -> Unit,
    onRestore: () -> Unit,
    onMakeMember: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 💡 Penanganan Foto Profil Anti-Bug
                val finalImgUrl = if (!user.img_path.isNullOrEmpty()) {
                    if (user.img_path.startsWith("http")) user.img_path else "https://ritecs.org/${user.img_path}"
                } else null

                if (finalImgUrl != null) {
                    SubcomposeAsyncImage(
                        model = finalImgUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(52.dp).clip(CircleShape).background(Color(0xFFE2E8F0)),
                        loading = { CircularProgressIndicator(color = RitecsBlue, modifier = Modifier.padding(14.dp)) },
                        error = {
                            Box(modifier = Modifier.fillMaxSize().background(RitecsBlue), contentAlignment = Alignment.Center) {
                                Text(user.name.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                        }
                    )
                } else {
                    Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(Brush.linearGradient(listOf(RitecsDarkBlue, RitecsLightBlue))), contentAlignment = Alignment.Center) {
                        Text(user.name.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    }
                }

                Spacer(Modifier.width(16.dp))

                // Info Pengguna
                Column(modifier = Modifier.weight(1f)) {
                    Text(user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(user.email, color = Color.Gray, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (!user.phone.isNullOrEmpty()) {
                        Text("📞 ${user.phone}", color = Color.DarkGray, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(Modifier.height(12.dp))

            // Baris Aksi & Status
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {

                // Lencana Status
                if (user.is_member) {
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF1C40F).copy(alpha = 0.15f), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF39C12).copy(alpha = 0.3f))) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                            Icon(Icons.Default.WorkspacePremium, null, tint = Color(0xFFF39C12), modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("MEMBER: ${user.member_number}", color = Color(0xFFE67E22), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFE2E8F0)) {
                        Text("Reguler User", color = Color.DarkGray, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp))
                    }
                }

                // Tombol Aksi
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (isActiveTab) {
                        if (!user.is_member) {
                            OutlinedButton(
                                onClick = onMakeMember,
                                modifier = Modifier.height(34.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = RitecsBlue),
                                border = androidx.compose.foundation.BorderStroke(1.dp, RitecsBlue)
                            ) {
                                Text("Jadikan Member", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        IconButton(onClick = onDeactivate, modifier = Modifier.size(34.dp).background(Color(0xFFE74C3C).copy(alpha = 0.1f), RoundedCornerShape(8.dp))) {
                            Icon(Icons.Default.Block, contentDescription = "Blokir", tint = Color(0xFFE74C3C), modifier = Modifier.size(18.dp))
                        }
                    } else {
                        Button(
                            onClick = onRestore,
                            modifier = Modifier.height(34.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27AE60))
                        ) {
                            Icon(Icons.Default.Restore, null, Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Aktifkan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}