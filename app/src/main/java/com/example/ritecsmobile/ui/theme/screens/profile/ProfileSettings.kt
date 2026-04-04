package com.example.ritecsmobile.ui.screens.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ritecsmobile.data.local.AuthPreferences
import com.example.ritecsmobile.data.remote.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

// 💡 WARNA IDENTITAS BRAND (Dibiarkan tetap)
val RitecsBlue = Color(0xFF0062CD)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authPreferences = remember { AuthPreferences(context) }
    val token by authPreferences.authToken.collectAsState(initial = "")

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var nik by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var province by remember { mutableStateOf("") }
    var institution by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var ktpUri by remember { mutableStateOf<Uri?>(null) }
    var avatarUri by remember { mutableStateOf<Uri?>(null) }
    var existingAvatarUrl by remember { mutableStateOf<String?>(null) }
    var existingKtpUrl by remember { mutableStateOf<String?>(null) }

    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var isSavingAvatar by remember { mutableStateOf(false) }

    val avatarPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            avatarUri = uri
            coroutineScope.launch {
                isSavingAvatar = true
                try {
                    val file = uriToFile(context, uri)
                    if (file != null) {
                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        val avatarPart = MultipartBody.Part.createFormData("avatar", file.name, requestFile)
                        val response = RetrofitClient.authApi.updateAvatar("Bearer $token", avatarPart)
                        Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Gagal upload avatar", Toast.LENGTH_SHORT).show()
                } finally {
                    isSavingAvatar = false
                }
            }
        }
    }

    val ktpPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) ktpUri = uri
    }

    LaunchedEffect(token) {
        if (!token.isNullOrEmpty()) {
            try {
                val response = RetrofitClient.authApi.getDashboardProfile("Bearer $token")
                val data = response.data
                firstName = data.first_name ?: ""
                lastName = data.last_name ?: ""
                nik = data.nik ?: ""
                birthday = data.birthday ?: ""
                phone = data.phone ?: ""
                address = data.address ?: ""
                city = data.city ?: ""
                province = data.province ?: ""
                institution = data.institution ?: ""
                email = data.email ?: ""

                existingAvatarUrl = data.img_path?.let { "https://ritecs.org/sites/$it" }
                existingKtpUrl = data.ktp_path?.let { "https://ritecs.org/sites/$it" }
            } catch (e: Exception) {
                // error handle
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan Akun", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RitecsBlue,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RitecsBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    // 💡 Latar Belakang Layar Otomatis
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- 1. FOTO PROFIL BISA DIKLIK ---
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        // 💡 Warna placeholder avatar otomatis
                        .background(MaterialTheme.colorScheme.outlineVariant)
                        .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        .clickable { avatarPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSavingAvatar) {
                        CircularProgressIndicator(color = RitecsBlue)
                    } else if (avatarUri != null) {
                        AsyncImage(model = avatarUri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    } else if (existingAvatarUrl != null) {
                        AsyncImage(model = existingAvatarUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    } else {
                        // 💡 Warna icon otomatis (putih jika gelap, abu jika terang)
                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(60.dp))
                    }

                    // Icon Kamera Kecil di Kanan Bawah
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(RitecsBlue)
                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                // 💡 Teks Keterangan otomatis
                Text("Ketuk untuk ubah foto profil", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)

                Spacer(modifier = Modifier.height(32.dp))

                // --- 2. DATA DIRI ---
                FormSectionTitle(title = "Data Pribadi", icon = Icons.Default.Badge)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    // 💡 Warna Card otomatis
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CustomOutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = "Nama Depan*", modifier = Modifier.weight(1f))
                            CustomOutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = "Nama Belakang", modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        CustomOutlinedTextField(value = nik, onValueChange = { nik = it }, label = "Nomor Induk Kependudukan (NIK)", keyboardType = KeyboardType.Number)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CustomOutlinedTextField(value = birthday, onValueChange = { birthday = it }, label = "Tanggal Lahir (YYYY-MM-DD)", modifier = Modifier.weight(1f))
                            CustomOutlinedTextField(value = phone, onValueChange = { phone = it }, label = "Nomor Telepon", keyboardType = KeyboardType.Phone, modifier = Modifier.weight(1f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- 3. DOMISILI & INSTITUSI ---
                FormSectionTitle(title = "Domisili & Institusi", icon = Icons.Default.LocationCity)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    // 💡 Warna Card otomatis
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        CustomOutlinedTextField(value = address, onValueChange = { address = it }, label = "Alamat Lengkap", singleLine = false, modifier = Modifier.height(100.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CustomOutlinedTextField(value = city, onValueChange = { city = it }, label = "Kota", modifier = Modifier.weight(1f))
                            CustomOutlinedTextField(value = province, onValueChange = { province = it }, label = "Provinsi", modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        CustomOutlinedTextField(value = institution, onValueChange = { institution = it }, label = "Asal Institusi / Kampus")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- 4. DOKUMEN IDENTITAS (KTP) ---
                FormSectionTitle(title = "Verifikasi KTP", icon = Icons.Default.CreditCard)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    // 💡 Warna Card otomatis
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                // 💡 Warna placeholder KTP otomatis
                                .background(MaterialTheme.colorScheme.background)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                .clickable { ktpPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (ktpUri != null) {
                                AsyncImage(model = ktpUri, contentDescription = "KTP", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                            } else if (existingKtpUrl != null) {
                                AsyncImage(model = existingKtpUrl, contentDescription = "KTP Server", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = RitecsBlue, modifier = Modifier.size(48.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    // 💡 Warna Teks Keterangan otomatis
                                    Text("Ketuk untuk unggah foto KTP", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp, textAlign = TextAlign.Center)
                                    Text("Format: JPG, PNG", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), fontSize = 11.sp, textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- 5. KEAMANAN AKUN ---
                FormSectionTitle(title = "Keamanan", icon = Icons.Default.Security)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    // 💡 Warna Card otomatis
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        CustomOutlinedTextField(value = email, onValueChange = { }, label = "Email Akun", enabled = false)
                        Spacer(modifier = Modifier.height(12.dp))
                        CustomOutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Kata Sandi Baru (Opsional)",
                            visualTransformation = PasswordVisualTransformation()
                        )
                        // 💡 Warna Teks otomatis
                        Text("Biarkan kosong jika tidak ingin mengubah sandi.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp, start = 4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))

                // --- TOMBOL SIMPAN ---
                Button(
                    onClick = {
                        isSaving = true
                        coroutineScope.launch {
                            try {
                                fun String.toRb(): RequestBody = this.toRequestBody("text/plain".toMediaTypeOrNull())

                                var ktpPart: MultipartBody.Part? = null
                                if (ktpUri != null) {
                                    val file = uriToFile(context, ktpUri!!)
                                    if (file != null) {
                                        val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                                        ktpPart = MultipartBody.Part.createFormData("ktp_path", file.name, reqFile)
                                    }
                                }

                                val response = RetrofitClient.authApi.updateProfileWithKtp(
                                    token = "Bearer $token",
                                    firstName = firstName.toRb(), lastName = lastName.toRb(),
                                    nik = nik.toRb(), birthday = birthday.toRb(),
                                    phone = phone.toRb(), address = address.toRb(),
                                    city = city.toRb(), province = province.toRb(),
                                    institution = institution.toRb(), password = password.toRb(),
                                    ktp_path = ktpPart
                                )
                                Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                                onNavigateBack()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isSaving = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RitecsBlue),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simpan Perubahan", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

// --- KOMPONEN BANTUAN ---

@Composable
fun FormSectionTitle(title: String, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp, start = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = RitecsBlue, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        // 💡 Warna Judul otomatis (Hitam/Putih)
        Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    enabled: Boolean = true,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        enabled = enabled,
        singleLine = singleLine,
        modifier = modifier,
        // 💡 Warna Field otomatis menyesuaikan tema (Garis Pinggir & Teks dalam kotak)
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = RitecsBlue,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedLabelColor = RitecsBlue,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = RitecsBlue,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outlineVariant,
            disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp)
    )
}

fun uriToFile(context: android.content.Context, uri: android.net.Uri): java.io.File? {
    return try {
        val contentResolver = context.contentResolver
        val tempFile = java.io.File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
        contentResolver.openInputStream(uri)?.use { input ->
            tempFile.outputStream().use { output -> input.copyTo(output) }
        }
        tempFile
    } catch (e: Exception) {
        null
    }
}