package com.example.ritecsmobile.ui.theme.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import com.example.ritecsmobile.data.local.AuthPreferences
import com.example.ritecsmobile.data.remote.RetrofitClient
import com.example.ritecsmobile.data.remote.dto.MembershipTransactionDto
import com.example.ritecsmobile.data.remote.dto.UpdateTransactionStatusRequest
import com.example.ritecsmobile.ui.screens.books.RitecsDarkBlue
import com.example.ritecsmobile.ui.screens.books.RitecsLightBlue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMembershipScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authPreferences = remember { AuthPreferences(context) }
    val token by authPreferences.authToken.collectAsState(initial = "")

    var transactions by remember { mutableStateOf<List<MembershipTransactionDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // State Tab (0 = Aktif/Pending, 1 = Trashed)
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Aktif / Pending", "Riwayat / Trashed")

    // State untuk Detail Dialog
    var selectedTransaction by remember { mutableStateOf<MembershipTransactionDto?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    // Fetch Data
    LaunchedEffect(token, selectedTabIndex) {
        if (!token.isNullOrEmpty()) {
            isLoading = true
            try {
                val response = if (selectedTabIndex == 0) {
                    RetrofitClient.authApi.getMembershipTransactions("Bearer $token")
                } else {
                    RetrofitClient.authApi.getTrashedMemberships("Bearer $token")
                }

                if (response.isSuccessful) {
                    transactions = response.body()?.data ?: emptyList()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(colors = listOf(RitecsDarkBlue, RitecsLightBlue)))
                ) {
                    TopAppBar(
                        title = { Text("Approval Membership", fontWeight = FontWeight.Bold, color = Color.White) },
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
        } else if (transactions.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(Modifier.height(8.dp))
                    Text("Tidak ada transaksi di tab ini", color = Color.Gray, fontWeight = FontWeight.Medium)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF5F6FA)).padding(16.dp)
            ) {
                items(transactions) { trx ->
                    TransactionCard(trx = trx, onClick = { selectedTransaction = trx })
                }
            }
        }

        // ==========================================
        // DIALOG DETAIL TRANSAKSI & BUKTI TRANSFER
        // ==========================================
        selectedTransaction?.let { trx ->
            AlertDialog(
                onDismissRequest = { if (!isSubmitting) selectedTransaction = null },
                properties = DialogProperties(usePlatformDefaultWidth = false), // Biar lebar full
                modifier = Modifier.fillMaxWidth(0.9f).padding(vertical = 24.dp),
                shape = RoundedCornerShape(16.dp),
                containerColor = Color(0xFFF8F9FA),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = RitecsBlue)
                        Spacer(Modifier.width(8.dp))
                        Text("Detail Pembayaran", fontWeight = FontWeight.ExtraBold, color = RitecsDarkBlue)
                    }
                },
                text = {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                        // --- KOTAK 1: INFO PENGGUNA (YANG MEMBAYAR) ---
                        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(1.dp), modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Informasi Akun", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(shape = CircleShape, color = RitecsBlue.copy(alpha = 0.1f), modifier = Modifier.size(40.dp)) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = RitecsBlue, modifier = Modifier.padding(8.dp))
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(trx.user_name ?: "Unknown", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                                        Text(trx.email ?: "-", color = Color.Gray, fontSize = 13.sp)
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // --- KOTAK 2: INFO TRANSFER (DARI MANA KE MANA) ---
                        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(1.dp), modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Rincian Transfer", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(12.dp))

                                // Pengirim
                                DetailRow("Pengirim A/N", trx.sender_name ?: "-")
                                HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(vertical = 8.dp))

                                // Tujuan Ritecs
                                DetailRow("Transfer Ke", trx.bank_name ?: "-")
                                DetailRow("No. Rekening", trx.bank_account_number ?: "-")
                                DetailRow("Penerima", trx.bank_account_name ?: "-")

                                HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(vertical = 8.dp))

                                // Total Bayar
                                val formatRp = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(trx.amount ?: 0)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Total Dibayar", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(formatRp, fontWeight = FontWeight.ExtraBold, color = RitecsBlue, fontSize = 18.sp)
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // --- KOTAK 3: FOTO BUKTI TRANSFER ---
                        Text("Bukti Transfer", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = RitecsDarkBlue, modifier = Modifier.padding(start = 4.dp))
                        Spacer(Modifier.height(8.dp))

                        // 💡 SOLUSI BUG GAMBAR: Parsing URL Super Aman
                        val finalProofUrl = if (trx.proof_url.isNullOrEmpty()) {
                            null
                        } else if (trx.proof_url.startsWith("http")) {
                            trx.proof_url
                        } else {
                            "https://ritecs.org/sites/" + trx.proof_url.removePrefix("/") // Pastikan selalu mengarah ke ritecs.org
                        }

                        if (!finalProofUrl.isNullOrEmpty()) {
                            Card(shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                                // 💡 Menggunakan SubcomposeAsyncImage agar kelihatan animasi loadingnya
                                SubcomposeAsyncImage(
                                    model = finalProofUrl,
                                    contentDescription = "Bukti Transfer",
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp),
                                    loading = {
                                        Box(Modifier.fillMaxWidth().height(200.dp).background(Color(0xFFE2E8F0)), contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(color = RitecsBlue)
                                        }
                                    },
                                    error = {
                                        Box(Modifier.fillMaxWidth().height(150.dp).background(Color(0xFFFFEBEE)), contentAlignment = Alignment.Center) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(Icons.Default.BrokenImage, contentDescription = null, tint = Color.Red, modifier = Modifier.size(40.dp))
                                                Text("Gambar rusak/tidak ditemukan", color = Color.Red, fontSize = 12.sp)
                                            }
                                        }
                                    }
                                )
                            }
                        } else {
                            Box(modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(12.dp)).background(Color.LightGray), contentAlignment = Alignment.Center) {
                                Text("Pengguna tidak mengunggah bukti", color = Color.DarkGray)
                            }
                        }
                    }
                },
                confirmButton = {
                    if (selectedTabIndex == 0 && trx.status == "pending") {
                        Button(
                            onClick = {
                                isSubmitting = true
                                scope.launch {
                                    try {
                                        val req = UpdateTransactionStatusRequest("paid")
                                        val res = RetrofitClient.authApi.updateMembershipTransaction("Bearer $token", trx.id, req)
                                        if (res.isSuccessful) {
                                            Toast.makeText(context, "Transaksi Disetujui!", Toast.LENGTH_SHORT).show()
                                            transactions = transactions.map { if (it.id == trx.id) it.copy(status = "paid") else it }
                                            selectedTransaction = null
                                        }
                                    } catch (e: Exception) {} finally { isSubmitting = false }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27AE60)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if (isSubmitting) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                            else {
                                Icon(Icons.Default.CheckCircle, null, Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Setujui Pembayaran", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else if (selectedTabIndex == 1) {
                        Button(
                            onClick = {
                                isSubmitting = true
                                scope.launch {
                                    try {
                                        val res = RetrofitClient.authApi.restoreMembershipTransaction("Bearer $token", trx.id)
                                        if (res.isSuccessful) {
                                            Toast.makeText(context, "Berhasil di-Restore!", Toast.LENGTH_SHORT).show()
                                            transactions = transactions.filter { it.id != trx.id }
                                            selectedTransaction = null
                                        }
                                    } catch (e: Exception) {} finally { isSubmitting = false }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RitecsBlue),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isSubmitting) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                            else Text("Restore Data")
                        }
                    }
                },
                dismissButton = {
                    if (selectedTabIndex == 0 && trx.status == "pending") {
                        OutlinedButton(
                            onClick = {
                                isSubmitting = true
                                scope.launch {
                                    try {
                                        val req = UpdateTransactionStatusRequest("rejected")
                                        val res = RetrofitClient.authApi.updateMembershipTransaction("Bearer $token", trx.id, req)
                                        if (res.isSuccessful) {
                                            Toast.makeText(context, "Ditolak!", Toast.LENGTH_SHORT).show()
                                            transactions = transactions.map { if (it.id == trx.id) it.copy(status = "rejected") else it }
                                            selectedTransaction = null
                                        }
                                    } catch (e: Exception) {} finally { isSubmitting = false }
                                }
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE74C3C)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isSubmitting
                        ) { Text("Tolak Pembayaran") }
                    } else {
                        TextButton(onClick = { selectedTransaction = null }, modifier = Modifier.fillMaxWidth()) { Text("Tutup Jendela", color = Color.Gray) }
                    }
                }
            )
        }
    }
}

// 💡 Komponen Bantuan untuk Baris Detail
@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 13.sp)
        Text(value, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color.Black)
    }
}

// --- DESAIN KARTU LIST ELEGAN ---
@Composable
fun TransactionCard(trx: MembershipTransactionDto, onClick: () -> Unit) {
    val formatRp = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(trx.amount ?: 0)

    val statusColor = when(trx.status) {
        "paid" -> Color(0xFF27AE60) // Hijau
        "pending" -> Color(0xFFF39C12) // Oren
        "rejected" -> Color(0xFFE74C3C) // Merah
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = Color(0xFFF5F6FA), modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.AccountBalanceWallet, null, tint = RitecsBlue, modifier = Modifier.padding(10.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(trx.user_name ?: "Unknown User", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                    Text(trx.created_at?.take(10) ?: "", color = Color.Gray, fontSize = 12.sp) // Cuma ambil tanggal
                }
                Surface(color = statusColor.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text(trx.status.uppercase(), color = statusColor, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Tipe Transaksi", fontSize = 11.sp, color = Color.Gray)
                    Text(if (trx.type == "extendedPayments") "Perpanjangan" else "Anggota Baru", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Nominal", fontSize = 11.sp, color = Color.Gray)
                    Text(formatRp, fontWeight = FontWeight.ExtraBold, color = RitecsBlue, fontSize = 15.sp)
                }
            }
        }
    }
}