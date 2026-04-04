package com.example.ritecsmobile.ui.screens.profile

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ritecsmobile.data.local.AuthPreferences
import com.example.ritecsmobile.data.remote.RetrofitClient
import com.example.ritecsmobile.data.remote.dto.UserTransactionDto
import kotlinx.coroutines.CancellationException
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val authPreferences = remember { AuthPreferences(context) }
    val token by authPreferences.authToken.collectAsState(initial = "")

    var transactions by remember { mutableStateOf<List<UserTransactionDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Warna Tema Ritecs (Tetap di-hardcode untuk elemen brand)
    val ritecsBlue = Color(0xFF0062CD)

    LaunchedEffect(token) {
        if (!token.isNullOrEmpty()) {
            isLoading = true
            try {
                val response = RetrofitClient.authApi.getUserTransactions("Bearer $token")

                if (response.isSuccessful) {
                    transactions = response.body()?.data ?: emptyList()
                } else {
                    Toast.makeText(context, "Gagal memuat riwayat", Toast.LENGTH_SHORT).show()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Toast.makeText(context, "Koneksi Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            // Header Elegan Gradasi Biru (Tetap dipertahankan di Dark Mode)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(colors = listOf(Color(0xFF004191), ritecsBlue)))
            ) {
                TopAppBar(
                    title = { Text("Riwayat Transaksi", fontWeight = FontWeight.ExtraBold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                // 💡 Background Layar Utama Otomatis
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ritecsBlue)
                }
            } else if (transactions.isEmpty()) {
                // Tampilan Jika Belum Ada Transaksi
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 💡 Ikon & Teks Empty State Otomatis menyesuaikan tema
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), modifier = Modifier.size(80.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Belum Ada Transaksi", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text("Anda belum melakukan transaksi apapun.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                }
            } else {
                // List Transaksi
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(transactions) { trx ->
                        UserTransactionCard(trx = trx)
                    }
                }
            }
        }
    }
}

@Composable
fun UserTransactionCard(trx: UserTransactionDto) {
    val formatRp = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(trx.amount ?: 0)

    // Logika Warna dan Ikon Berdasarkan Status (Tetap di-hardcode karena merupakan warna semantik pasti)
    val (statusColor, statusIcon, statusText) = when (trx.status.lowercase()) {
        "paid" -> Triple(Color(0xFF27AE60), Icons.Default.CheckCircle, "BERHASIL")
        "pending" -> Triple(Color(0xFFF39C12), Icons.Default.Schedule, "MENUNGGU")
        "rejected" -> Triple(Color(0xFFE74C3C), Icons.Default.Cancel, "DITOLAK")
        else -> Triple(Color.Gray, Icons.Default.Help, "UNKNOWN")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        // 💡 Warna Latar Kartu Otomatis
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // --- HEADER CARD (Tanggal & Status) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 💡 Warna Tanggal Otomatis (Abu Kalem)
                Text(trx.created_at?.take(10) ?: "-", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)

                // Badge Status
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(statusIcon, contentDescription = null, tint = statusColor, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(statusText, color = statusColor, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            // 💡 Warna Garis Pemisah Otomatis
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            // --- ISI KONTEN (Tipe & Nominal) ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Ikon Transaksi (Tetap Biru Ritecs)
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFF0062CD).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CardMembership, contentDescription = null, tint = Color(0xFF0062CD), modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (trx.type == "extendedPayments") "Perpanjangan Membership" else "Membership Baru",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text("Trf via: ${trx.bank_name ?: "-"}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(formatRp, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color(0xFF0062CD))
            }
        }
    }
}