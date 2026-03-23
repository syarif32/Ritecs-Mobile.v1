package com.example.ritecsmobile.ui.screens.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ritecsmobile.data.remote.RetrofitClient
import com.example.ritecsmobile.data.remote.dto.ContactInfoData
import com.example.ritecsmobile.data.remote.dto.ContactSendRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KontakScreen(onNavigateBack: () -> Unit = {}) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var infoData by remember { mutableStateOf<ContactInfoData?>(null) }
    var isLoadingInfo by remember { mutableStateOf(true) }
    var isSubmitting by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.authApi.getContactInfo()
            infoData = response.data
        } catch (e: Exception) {} finally { isLoadingInfo = false }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hubungi Kami", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White)
            )
        }
    ) { paddingValues ->
        if (isLoadingInfo) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().background(Color(0xFFF5F6FA)).padding(paddingValues).verticalScroll(rememberScrollState()).padding(24.dp)
            ) {
                Text("Mari Terhubung", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Punya pertanyaan atau butuh bantuan? Tim Ritecs siap melayani Anda.", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(24.dp))

                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ContactItem(Icons.Default.LocationOn, "Alamat", infoData?.address ?: "-")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))
                        ContactItem(Icons.Default.Email, "Email", infoData?.email ?: "-")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))
                        ContactItem(Icons.Default.Phone, "Telepon / WA", infoData?.phone ?: "-")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))
                        ContactItem(Icons.Default.Language, "Situs Web", infoData?.site ?: "-")
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text("Tinggalkan Pesan", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))

                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Lengkap*") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email*") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), singleLine = true)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("No. Handphone*") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), singleLine = true)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Asal Instansi / Alamat*") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subjek*") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(value = message, onValueChange = { message = it }, label = { Text("Pesan Anda*") }, modifier = Modifier.fillMaxWidth(), minLines = 4)

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (name.isBlank() || email.isBlank() || message.isBlank()) {
                                    Toast.makeText(context, "Harap lengkapi field bertanda bintang (*)", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                isSubmitting = true
                                coroutineScope.launch {
                                    try {
                                        val req = ContactSendRequest(name, email, phone, address, subject, message)
                                        val res = RetrofitClient.authApi.sendContactMessage(req)
                                        Toast.makeText(context, res.message, Toast.LENGTH_LONG).show()
                                        name = ""; email = ""; phone = ""; address = ""; subject = ""; message = ""
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Gagal mengirim pesan", Toast.LENGTH_SHORT).show()
                                    } finally { isSubmitting = false }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isSubmitting
                        ) {
                            if (isSubmitting) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            else {
                                Icon(Icons.Default.Send, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Kirim Pesan", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ContactItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
            Text(value, fontSize = 14.sp, color = Color.DarkGray)
        }
    }
}