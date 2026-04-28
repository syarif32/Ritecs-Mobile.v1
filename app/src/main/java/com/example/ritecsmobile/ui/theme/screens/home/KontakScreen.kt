package com.example.ritecsmobile.ui.screens.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

    // Form States
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    // 💡 Warna Brand Tetap Statis
    val ritecsBlue = Color(0xFF0062CD)

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.authApi.getContactInfo()
            infoData = response.data
        } catch (e: Exception) {
            android.util.Log.e("CONTACT_ERR", "Error: ${e.message}")
        } finally { isLoadingInfo = false }
    }

    Scaffold(
        // 💡 Latar belakang otomatis
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Hubungi Kami",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface // 💡 Hitam/Putih Otomatis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.onSurface) // 💡 Otomatis
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface // 💡 Putih/Dark Slate Otomatis
                ),
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { paddingValues ->
        if (isLoadingInfo) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ritecsBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // HEADER SECTION
                Text(
                    "Mari Terhubung",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = ritecsBlue,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Punya pertanyaan atau butuh bantuan? Tim Ritecs siap melayani Anda sepenuh hati.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, // 💡 Abu-abu Otomatis
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                // INFO KONTAK CARD
                Text("Informasi Kontak", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(bottom = 12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // 💡 Otomatis
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        ContactItem(Icons.Default.LocationOn, "Alamat Kantor", infoData?.address ?: "-", ritecsBlue)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant) // 💡 Otomatis
                        ContactItem(Icons.Default.Email, "Email Support", infoData?.email ?: "-", ritecsBlue)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant) // 💡 Otomatis
                        ContactItem(Icons.Default.Phone, "WhatsApp / Telp", infoData?.phone ?: "-", ritecsBlue)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // FORM SECTION
                // FORM SECTION
                // FORM SECTION
                Text("Kirim Pesan Langsung", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(bottom = 12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {

                        CustomOutlinedTextField(value = name, onValueChange = { name = it }, label = "Nama Lengkap*", icon = Icons.Default.Person)
                        Spacer(modifier = Modifier.height(16.dp))

                        CustomOutlinedTextField(value = email, onValueChange = { email = it }, label = "Alamat Email*", icon = Icons.Default.AlternateEmail, keyboardType = KeyboardType.Email)
                        Spacer(modifier = Modifier.height(16.dp))

                        CustomOutlinedTextField(value = phone, onValueChange = { phone = it }, label = "No. Handphone*", icon = Icons.Default.Smartphone, keyboardType = KeyboardType.Phone)
                        Spacer(modifier = Modifier.height(16.dp))

                        // 💡 INI KOLOM ALAMAT YANG KETINGGALAN TADI!
                        CustomOutlinedTextField(value = address, onValueChange = { address = it }, label = "Alamat Domisili*", icon = Icons.Default.LocationOn)
                        Spacer(modifier = Modifier.height(16.dp))

                        CustomOutlinedTextField(value = subject, onValueChange = { subject = it }, label = "Subjek Pesan*", icon = Icons.Default.Topic)
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = message,
                            onValueChange = { message = it },
                            label = { Text("Pesan Anda*") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 4,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ritecsBlue,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                // 💡 VALIDASI LOKAL DIPERKETAT (Semua wajib diisi)
                                if (name.isBlank() || email.isBlank() || phone.isBlank() || address.isBlank() || subject.isBlank() || message.isBlank()) {
                                    Toast.makeText(context, "Lengkapi semua field wajib (*)", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                isSubmitting = true
                                coroutineScope.launch {
                                    try {
                                        val req = ContactSendRequest(name, email, phone, address, subject, message)
                                        val res = RetrofitClient.authApi.sendContactMessage(req)

                                        if (res.isSuccessful) {
                                            val successMessage = res.body()?.message ?: "Pesan berhasil dikirim!"
                                            Toast.makeText(context, successMessage, Toast.LENGTH_LONG).show()

                                            // Reset form kalau sukses
                                            name = ""; email = ""; phone = ""; address = ""; subject = ""; message = ""
                                        } else {
                                            Toast.makeText(context, "Gagal mengirim pesan. Coba lagi.", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isSubmitting = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ritecsBlue),
                            enabled = !isSubmitting
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Kirim Pesan Sekarang", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }, // 💡 Otomatis
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF0062CD),
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant, // 💡 Otomatis
            focusedTextColor = MaterialTheme.colorScheme.onSurface, // 💡 Otomatis
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface // 💡 Otomatis
        )
    )
}

@Composable
fun ContactItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(color.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp) // 💡 Otomatis
            Text(value, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium) // 💡 Otomatis
        }
    }
}