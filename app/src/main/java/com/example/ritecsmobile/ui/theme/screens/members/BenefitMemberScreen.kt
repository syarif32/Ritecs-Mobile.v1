package com.example.ritecsmobile.ui.screens.members

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ritecsmobile.data.local.AuthPreferences
import com.example.ritecsmobile.data.remote.RetrofitClient
import com.example.ritecsmobile.data.remote.dto.BankDto
import com.example.ritecsmobile.data.remote.dto.BenefitDataDto
import com.example.ritecsmobile.data.remote.dto.UserProfileDataDto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import kotlinx.coroutines.CancellationException

// 💡 Warna Tema Ritecs


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberRegistrationScreen(
    onNavigateBack: () -> Unit = {},
    onSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val authPreferences = remember { AuthPreferences(context) }
    val token by authPreferences.authToken.collectAsState(initial = "")

    var userData by remember { mutableStateOf<UserProfileDataDto?>(null) }
    var benefitData by remember { mutableStateOf<BenefitDataDto?>(null) }
    var banks by remember { mutableStateOf<List<BankDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var nik by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var province by remember { mutableStateOf("") }
    var institution by remember { mutableStateOf("") }
    var senderName by remember { mutableStateOf("") }
    var senderBank by remember { mutableStateOf("") }

    var expandedBankMenu by remember { mutableStateOf(false) }
    var selectedBank by remember { mutableStateOf<BankDto?>(null) }

    var ktpImageUri by remember { mutableStateOf<Uri?>(null) }
    var proofImageUri by remember { mutableStateOf<Uri?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val ktpLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { ktpImageUri = it }
    val proofLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { proofImageUri = it }

    LaunchedEffect(token) {
        if (!token.isNullOrEmpty()) {
            isLoading = true
            try {
                val profileResponse = RetrofitClient.authApi.getDashboardProfile("Bearer $token")
                userData = profileResponse.data

                if (userData?.is_member != true && userData?.has_pending_transaction != true) {
                    val benefitResponse = RetrofitClient.authApi.getMembershipBenefits()
                    benefitData = benefitResponse.data

                    val bankResponse = RetrofitClient.authApi.getBanks("Bearer $token")
                    banks = bankResponse.data

                    firstName = userData?.first_name ?: ""
                    lastName = userData?.last_name ?: ""
                    email = userData?.email ?: ""
                }
            } catch (e: Exception) {
                if (e !is CancellationException) android.util.Log.e("REG_ERR", "Error: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(context, { _, y, m, d ->
        birthday = "$y-${String.format("%02d", m + 1)}-${String.format("%02d", d)}"
    }, year, month, day)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val titleText = when {
                        userData?.is_member == true -> "Keanggotaan Anda"
                        userData?.has_pending_transaction == true -> "Status Pendaftaran"
                        else -> "Gabung Membership"
                    }
                    Text(titleText, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RitecsBlue, titleContentColor = Color.White)
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
                    .background(BackgroundSoft)
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(20.dp)
            ) {
                // ==========================================================
                // KONDISI 1: SUDAH MEMBER (DETAIL)
                // ==========================================================
                if (userData?.is_member == true) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF27AE60), modifier = Modifier.size(72.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Keanggotaan Aktif", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF27AE60))
                            Spacer(modifier = Modifier.height(24.dp))

                            HorizontalDivider(color = Color(0xFFEEEEEE))
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Nama Lengkap", color = Color.Gray, fontSize = 14.sp)
                                Text("${userData?.first_name} ${userData?.last_name ?: ""}", fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Nomor ID Member", color = Color.Gray, fontSize = 14.sp)
                                Text(userData?.member_number ?: "-", fontWeight = FontWeight.ExtraBold, color = RitecsBlue)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onSuccess,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RitecsBlue)
                    ) {
                        Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Lihat Kartu Digital", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    }
                }

                // ==========================================================
                // KONDISI 2: ADA TRANSAKSI PENDING
                // ==========================================================
                else if (userData?.has_pending_transaction == true) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.HourglassTop, contentDescription = null, tint = Color(0xFFF39C12), modifier = Modifier.size(72.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Transaksi Diproses", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFF39C12))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Pendaftaran membership Anda sedang diverifikasi oleh Admin. Harap tunggu beberapa saat.", textAlign = TextAlign.Center, color = Color.Gray, fontSize = 14.sp)

                            Spacer(modifier = Modifier.height(24.dp))
                            HorizontalDivider(color = Color(0xFFEEEEEE))
                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    val waNumber = "6285225969825"
                                    val intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse("https://wa.me/$waNumber?text=${Uri.encode("Halo Admin, saya ingin konfirmasi transaksi pembayaran membership saya.")}") }
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Konfirmasi via WhatsApp", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }

                // ==========================================================
                // KONDISI 3: BELUM MEMBER & GAK ADA PENDING (FORM PENDAFTARAN)
                // ==========================================================
                else {
                    // Harga Membership
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = RitecsBlue.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Biaya Keanggotaan", fontSize = 14.sp, color = RitecsBlue)
                            Text(benefitData?.price ?: "Rp 150.000", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = RitecsBlue)
                            Text(benefitData?.price_description ?: "Berlaku untuk 1 Tahun", fontSize = 13.sp, color = Color.DarkGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Formulir Pendaftaran", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                    Spacer(modifier = Modifier.height(12.dp))

                    // --- FORM IDENTITAS ---
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            FormGroupTitle("1. Data Identitas", Icons.Default.Badge)
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                RitecsOutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = "Nama Depan*", modifier = Modifier.weight(1f))
                                RitecsOutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = "Belakang", modifier = Modifier.weight(1f))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            RitecsOutlinedTextField(value = email, onValueChange = { email = it }, label = "Email Aktif*", readOnly = true)
                            Spacer(modifier = Modifier.height(12.dp))
                            RitecsOutlinedTextField(value = nik, onValueChange = { nik = it }, label = "Nomor NIK KTP", keyboardType = KeyboardType.Number)
                            Spacer(modifier = Modifier.height(12.dp))
                            RitecsOutlinedTextField(
                                value = birthday, onValueChange = {}, label = "Tanggal Lahir",
                                modifier = Modifier.clickable { datePickerDialog.show() }, readOnly = true,
                                trailingIcon = { IconButton(onClick = { datePickerDialog.show() }) { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = RitecsBlue) } }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            UploadButton(text = "Upload Foto KTP Asli*", isUploaded = ktpImageUri != null, onClick = { ktpLauncher.launch("image/*") })
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- FORM KONTAK ---
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            FormGroupTitle("2. Kontak & Domisili", Icons.Default.LocationCity)
                            Spacer(modifier = Modifier.height(12.dp))
                            RitecsOutlinedTextField(value = phone, onValueChange = { phone = it }, label = "No. Telepon / WA", keyboardType = KeyboardType.Phone)
                            Spacer(modifier = Modifier.height(12.dp))
                            RitecsOutlinedTextField(value = address, onValueChange = { address = it }, label = "Alamat Lengkap")
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                RitecsOutlinedTextField(value = city, onValueChange = { city = it }, label = "Kota", modifier = Modifier.weight(1f))
                                RitecsOutlinedTextField(value = province, onValueChange = { province = it }, label = "Provinsi", modifier = Modifier.weight(1f))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            RitecsOutlinedTextField(value = institution, onValueChange = { institution = it }, label = "Asal Institusi / Kampus")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- FORM PEMBAYARAN ---
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            FormGroupTitle("3. Verifikasi Pembayaran", Icons.Default.AccountBalanceWallet)
                            Spacer(modifier = Modifier.height(12.dp))

                            ExposedDropdownMenuBox(expanded = expandedBankMenu, onExpandedChange = { expandedBankMenu = it }) {
                                OutlinedTextField(
                                    value = selectedBank?.let { "${it.bank_name} - ${it.account_number}" } ?: "",
                                    onValueChange = {}, readOnly = true, label = { Text("Pilih Bank Tujuan Transfer*") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBankMenu) },
                                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RitecsBlue, focusedLabelColor = RitecsBlue, cursorColor = RitecsBlue)
                                )
                                ExposedDropdownMenu(expanded = expandedBankMenu, onDismissRequest = { expandedBankMenu = false }) {
                                    banks.forEach { bank ->
                                        DropdownMenuItem(
                                            text = { Text("${bank.bank_name} - ${bank.account_number} (${bank.account_name})", fontWeight = FontWeight.SemiBold) },
                                            onClick = { selectedBank = bank; expandedBankMenu = false }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            RitecsOutlinedTextField(value = senderName, onValueChange = { senderName = it }, label = "Nama Pengirim (Sesuai Rekening)*")
                            Spacer(modifier = Modifier.height(12.dp))
                            RitecsOutlinedTextField(value = senderBank, onValueChange = { senderBank = it }, label = "Nomor Rekening Anda*", keyboardType = KeyboardType.Number)
                            Spacer(modifier = Modifier.height(16.dp))
                            UploadButton(text = "Upload Bukti Transfer*", isUploaded = proofImageUri != null, onClick = { proofLauncher.launch("image/*") })
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- SUBMIT BUTTON ---
                    Button(
                        onClick = {
                            if (firstName.isEmpty() || email.isEmpty() || senderName.isEmpty() || senderBank.isEmpty() || selectedBank == null || ktpImageUri == null || proofImageUri == null) {
                                Toast.makeText(context, "Harap lengkapi form bertanda *, pilih Bank, dan unggah foto!", Toast.LENGTH_LONG).show()
                                return@Button
                            }
                            isSubmitting = true

                            scope.launch {
                                try {
                                    val tokenStr = authPreferences.authToken.first() ?: ""
                                    fun String.toRb() = this.toRequestBody("text/plain".toMediaTypeOrNull())

                                    val ktpFile = getFileFromUri(context, ktpImageUri!!, "ktp_temp.jpg")
                                    val proofFile = getFileFromUri(context, proofImageUri!!, "proof_temp.jpg")

                                    if (ktpFile != null && proofFile != null) {
                                        val mediaType = "image/jpeg".toMediaTypeOrNull()
                                        val response = RetrofitClient.authApi.registerMembership(
                                            "Bearer $tokenStr",
                                            firstName.toRb(), lastName.toRb(), email.toRb(), nik.toRb(), birthday.toRb(), phone.toRb(), address.toRb(), city.toRb(), province.toRb(), institution.toRb(),
                                            senderName.toRb(), senderBank.toRb(), selectedBank!!.bank_id.toString().toRb(), "150000".toRb(), "firstPayments".toRb(),
                                            MultipartBody.Part.createFormData("ktp_path", ktpFile.name, ktpFile.asRequestBody(mediaType)),
                                            MultipartBody.Part.createFormData("proof", proofFile.name, proofFile.asRequestBody(mediaType))
                                        )
                                        Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                                        userData = userData?.copy(has_pending_transaction = true)
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Gagal: ${e.message}", Toast.LENGTH_LONG).show()
                                } finally {
                                    isSubmitting = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RitecsBlue),
                        enabled = !isSubmitting
                    ) {
                        if (isSubmitting) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        else Text("Kirim Pendaftaran", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

// --- BANTUAN UI ---
@Composable
fun FormGroupTitle(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = RitecsBlue, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = RitecsBlue)
    }
}

@Composable
fun RitecsOutlinedTextField(
    value: String, onValueChange: (String) -> Unit, label: String, modifier: Modifier = Modifier.fillMaxWidth(),
    keyboardType: KeyboardType = KeyboardType.Text, readOnly: Boolean = false, trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange, label = { Text(label, fontSize = 13.sp) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType), readOnly = readOnly,
        singleLine = true, modifier = modifier, trailingIcon = trailingIcon,
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RitecsBlue, focusedLabelColor = RitecsBlue, cursorColor = RitecsBlue),
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
fun UploadButton(text: String, isUploaded: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick, modifier = Modifier.fillMaxWidth().height(44.dp),
        colors = ButtonDefaults.buttonColors(containerColor = if (isUploaded) Color(0xFF27AE60) else RitecsBlue.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(if (isUploaded) Icons.Default.Check else Icons.Default.CloudUpload, contentDescription = null, tint = if (isUploaded) Color.White else RitecsBlue)
        Spacer(modifier = Modifier.width(8.dp))
        Text(if (isUploaded) "File Berhasil Dipilih" else text, fontWeight = FontWeight.Bold, color = if (isUploaded) Color.White else RitecsBlue)
    }
}

fun getFileFromUri(context: Context, uri: Uri, fileName: String): File? {
    return try {
        val stream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, fileName)
        val out = FileOutputStream(file)
        stream?.copyTo(out)
        stream?.close(); out.close()
        file
    } catch (e: Exception) { null }
}