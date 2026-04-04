package com.example.ritecsmobile.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ritecsmobile.R
import com.example.ritecsmobile.data.local.AuthPreferences
import com.example.ritecsmobile.data.remote.RetrofitClient
import com.example.ritecsmobile.data.remote.dto.ManualActivationRequest
import com.example.ritecsmobile.data.remote.dto.ResendOtpRequest
import com.example.ritecsmobile.data.remote.dto.VerifyOtpRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyOtpScreen(
    email: String,
    onVerifySuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var otpCode by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    var timeLeft by remember { mutableStateOf(60) }
    var showManualHelpBtn by remember { mutableStateOf(false) }

    var showRulesDialog by remember { mutableStateOf(false) }
    var showManualDialog by remember { mutableStateOf(false) }

    var selectedReason by remember { mutableStateOf("") }
    var otherReasonDetail by remember { mutableStateOf("") }
    var isSubmittingManual by remember { mutableStateOf(false) }

    // TIMER LOGIC
    LaunchedEffect(timeLeft) {
        if (timeLeft > 0) {
            delay(1000L)
            timeLeft--
            if (timeLeft == 30) showManualHelpBtn = true
        }
    }

    // 💡 BACKGROUND UTAMA DINAMIS (Terang/Gelap menyesuaikan HP)
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(
            modifier = Modifier.fillMaxSize().imePadding().padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // TOMBOL KEMBALI KECIL DI POJOK KIRI ATAS
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    // 💡 Ikon otomatis hitam/putih
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.onSurface) }
                }

                // LOGO
                Image(
                    painter = painterResource(id = R.drawable.ritecs_logo),
                    contentDescription = "Logo Ritecs",
                    modifier = Modifier.size(80.dp).padding(bottom = 16.dp)
                )

                // CARD UTAMA
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    // 💡 Warna Latar Belakang Kartu Dinamis
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 💡 Teks Judul Dinamis
                        Text("Verifikasi Email", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(8.dp))
                        // 💡 Teks Deskripsi Dinamis
                        Text("Kode 6 digit telah dikirim ke:\n$email", fontSize = 14.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.height(32.dp))

                        // INPUT OTP (Tengah, Spasi Lebar)
                        OutlinedTextField(
                            value = otpCode,
                            onValueChange = { if (it.length <= 6) otpCode = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            // 💡 Warna Teks dan Placeholder Dinamis
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 28.sp, fontWeight = FontWeight.Bold, letterSpacing = 12.sp, color = MaterialTheme.colorScheme.onSurface),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RitecsBlue,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                cursorColor = RitecsBlue
                            ),
                            placeholder = { Text("_ _ _ _ _ _", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        //  TOMBOL VERIFIKASI (DIBIKIN GRADASI BIRU KEREN)
                        Button(
                            onClick = {
                                if (otpCode.length == 6) {
                                    coroutineScope.launch {
                                        isLoading = true
                                        try {
                                            val response = RetrofitClient.authApi.verifyOtp(VerifyOtpRequest(email, otpCode))
                                            if (response.isSuccessful) {
                                                val tokenDariServer = response.body()?.data?.token ?: ""
                                                val roleDariServer = response.body()?.data?.role ?: "user"
                                                AuthPreferences(context).saveToken(tokenDariServer, roleDariServer)
                                                Toast.makeText(context, "Verifikasi Berhasil!", Toast.LENGTH_SHORT).show()
                                                onVerifySuccess()
                                            } else {
                                                val errorJson = response.errorBody()?.string()?.let { JSONObject(it) }
                                                Toast.makeText(context, errorJson?.optString("message") ?: "OTP Salah", Toast.LENGTH_LONG).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Koneksi Error", Toast.LENGTH_SHORT).show()
                                        } finally { isLoading = false }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), // Set transparent
                            contentPadding = PaddingValues(), // Hapus padding default
                            enabled = !isLoading && otpCode.length == 6
                        ) {
                            // 💡 Efek Gradasi pada Box di dalam Button
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(RitecsDarkBlue, RitecsLightBlue) // Gradasi Kiri ke Kanan
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                else Text("Verifikasi Akun", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // TOMBOL BANTUAN KECIL DI BAWAH CARD
                TextButton(onClick = { showRulesDialog = true }) {
                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp), tint = RitecsBlue)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Bantuan & Aturan OTP", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = RitecsBlue)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // TIMER & RESEND LOGIC
                if (timeLeft > 0) {
                    // 💡 Teks Timer Otomatis
                    Text(text = "Kirim ulang dalam $timeLeft detik", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                } else {
                    Text(
                        text = "Kirim Ulang OTP", color = RitecsBlue, fontWeight = FontWeight.Bold, fontSize = 15.sp,
                        modifier = Modifier.clickable {
                            coroutineScope.launch {
                                timeLeft = 60
                                showManualHelpBtn = false
                                try {
                                    val response = RetrofitClient.authApi.resendOtp(ResendOtpRequest(email))
                                    if (response.isSuccessful) Toast.makeText(context, "OTP Baru Terkirim!", Toast.LENGTH_SHORT).show()
                                    else Toast.makeText(context, "Limit tercapai.", Toast.LENGTH_LONG).show()
                                } catch (e: Exception) { Toast.makeText(context, "Gagal mengirim", Toast.LENGTH_SHORT).show() }
                            }
                        }.padding(8.dp)
                    )
                }

                // MUNCUL SETELAH 30 DETIK JIKA BELUM DAPAT EMAIL
                if (showManualHelpBtn) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { showManualDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                    ) {
                        Text("Minta Bantuan Aktivasi Admin", fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // ==========================================
    // DIALOG 1: ATURAN & BANTUAN
    // ==========================================
    if (showRulesDialog) {
        AlertDialog(
            onDismissRequest = { showRulesDialog = false },
            // 💡 Teks Dinamis
            title = { Text("Panduan Verifikasi", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Column {
                    // 💡 Teks Dinamis
                    Text("1. Periksa Folder Spam/Junk", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text("Kode seringkali tidak masuk ke Inbox utama.\n", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Text("2. Cek Penyimpanan (Storage)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text("Pastikan kapasitas Google Drive/Email Anda tidak penuh.\n", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Text("3. Batas Kirim Ulang", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.error)
                    Text("Maksimal permintaan OTP adalah 3 kali dalam 24 jam demi keamanan.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = { TextButton(onClick = { showRulesDialog = false }) { Text("Saya Mengerti", fontWeight = FontWeight.Bold, color = RitecsBlue) } },
            shape = RoundedCornerShape(16.dp),
            // 💡 Latar Belakang Dialog Dinamis
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // ==========================================
    // DIALOG 2: FORM AKTIVASI MANUAL
    // ==========================================
    if (showManualDialog) {
        AlertDialog(
            onDismissRequest = { showManualDialog = false },
            // 💡 Teks Dinamis
            title = { Text("Bantuan Aktivasi", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Column {
                    // 💡 Teks Dinamis
                    Text("Permintaan akan dikirim ke Admin. Proses pengecekan maksimal 1x24 Jam.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Apa kendala Anda?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

                    val reasons = listOf("Email Penuh", "Tidak Masuk Spam", "Salah Ketik Email", "Lainnya")
                    reasons.forEach { reason ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { selectedReason = reason }) {
                            RadioButton(
                                selected = selectedReason == reason,
                                onClick = { selectedReason = reason },
                                colors = RadioButtonDefaults.colors(selectedColor = RitecsBlue, unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            // 💡 Teks Pilihan Dinamis
                            Text(reason, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    if (selectedReason == "Lainnya") {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = otherReasonDetail, onValueChange = { otherReasonDetail = it },
                            label = { Text("Jelaskan masalah") }, modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface), shape = RoundedCornerShape(8.dp),
                            // 💡 Warna Kotak Input Dinamis
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RitecsBlue,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedLabelColor = RitecsBlue,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedReason.isNotEmpty()) {
                            coroutineScope.launch {
                                isSubmittingManual = true
                                try {
                                    val req = ManualActivationRequest(email, selectedReason, if (selectedReason == "Lainnya") otherReasonDetail else null)

                                    // 🔍 LOG 1: Cek apakah request berhasil dibuat sebelum dikirim
                                    android.util.Log.d("RITECS_LOG_MANUAL", "Mencoba mengirim ke API: $req")

                                    val response = RetrofitClient.authApi.requestManualActivation(req)

                                    if (response.isSuccessful) {
                                        // 🔍 LOG 2: Berhasil masuk Laravel
                                        android.util.Log.d("RITECS_LOG_MANUAL", "Sukses! Response: ${response.body()}")

                                        Toast.makeText(context, "Permintaan terkirim!", Toast.LENGTH_LONG).show()
                                        showManualDialog = false
                                        onNavigateBack()
                                    } else {
                                        // 🔍 LOG 3: Laravel menolak (Error 400, 404, 422, 500)
                                        val errorBody = response.errorBody()?.string()
                                        android.util.Log.e("RITECS_LOG_MANUAL", "DITOLAK LARAVEL! HTTP Code: ${response.code()} | Body: $errorBody")

                                        Toast.makeText(context, "Gagal Mengirim. Cek Logcat!", Toast.LENGTH_LONG).show()
                                    }
                                } catch (e: Exception) {
                                    // 🔍 LOG 4: Aplikasi Crash atau Internet Mati sebelum nyampe Laravel
                                    android.util.Log.e("RITECS_LOG_MANUAL", "CRASH/EXCEPTION: ${e.localizedMessage}", e)
                                    Toast.makeText(context, "Error Koneksi", Toast.LENGTH_SHORT).show()
                                }
                                finally { isSubmittingManual = false }
                            }
                        } else Toast.makeText(context, "Pilih alasan", Toast.LENGTH_SHORT).show()
                    },
                    enabled = !isSubmittingManual, shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RitecsBlue)
                ) {
                    if (isSubmittingManual) CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    else Text("Kirim", color = Color.White)
                }
            },
            // 💡 Tombol Batal warna dinamis
            dismissButton = { TextButton(onClick = { showManualDialog = false }) { Text("Batal", color = MaterialTheme.colorScheme.onSurfaceVariant) } },
            shape = RoundedCornerShape(16.dp),
            // 💡 Latar Belakang Dialog Dinamis
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}