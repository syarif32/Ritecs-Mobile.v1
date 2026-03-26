package com.example.ritecsmobile.ui.theme.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ritecsmobile.data.local.AuthPreferences
import com.example.ritecsmobile.data.remote.RetrofitClient
import com.example.ritecsmobile.data.remote.dto.ActivationRequestDto
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivationRequestScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authPreferences = AuthPreferences(context)
    val token by authPreferences.authToken.collectAsState(initial = "")

    var requests by remember { mutableStateOf<List<ActivationRequestDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // FETCH DATA
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.authApi.getActivationRequests("Bearer $token")
            if (response.isSuccessful) {
                requests = response.body()?.data ?: emptyList()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Permintaan Aktivasi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (requests.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Tidak ada permintaan tertunda") }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
                items(requests) { item ->
                    ActivationCard(
                        item = item,
                        onApprove = {
                            scope.launch {
                                val res = RetrofitClient.authApi.approveActivation("Bearer $token", item.id)
                                if (res.isSuccessful) {
                                    Toast.makeText(context, "User Berhasil Diaktivasi!", Toast.LENGTH_SHORT).show()
                                    requests = requests.filter { it.id != item.id } // Hapus dari list
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ActivationCard(item: ActivationRequestDto, onApprove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(item.email, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Alasan: ${item.reason}", color = Color.Gray, fontSize = 14.sp)
            if (!item.other_reason_detail.isNullOrEmpty()) {
                Text("Detail: ${item.other_reason_detail}", fontSize = 12.sp, color = Color.DarkGray)
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Setujui")
                }
            }
        }
    }
}