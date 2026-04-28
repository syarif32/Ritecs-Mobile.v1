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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ritecsmobile.R
import com.example.ritecsmobile.data.local.AuthPreferences
import com.example.ritecsmobile.data.remote.RetrofitClient
import com.example.ritecsmobile.data.remote.dto.LoginRequest
import com.example.ritecsmobile.ui.screens.books.RitecsDarkBlue
import com.example.ritecsmobile.ui.screens.books.RitecsLightBlue
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToOtp: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    var showPendingDialog by remember { mutableStateOf(false) }

    // 💡 STATE BARU UNTUK POP-UP LUPA PASSWORD
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    val primaryColor = MaterialTheme.colorScheme.primary

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Spacer(modifier = Modifier.height(32.dp))
                Image(
                    painter = painterResource(id = R.drawable.ritecs_logo),
                    contentDescription = "Logo Ritecs",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 24.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Selamat Datang",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Masuk untuk melanjutkan",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryColor,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedLabelColor = primaryColor,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = image, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryColor,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedLabelColor = primaryColor,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        // 💡 KLIK TEKS SEKARANG MEMUNCULKAN DIALOG DULU
                        Text(
                            text = "Lupa Password?",
                            color = primaryColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .clickable {
                                    showForgotPasswordDialog = true // Tampilkan pop up
                                },
                            textAlign = TextAlign.End
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (email.isNotEmpty() && password.isNotEmpty()) {
                                    coroutineScope.launch {
                                        isLoading = true
                                        try {
                                            val response = RetrofitClient.authApi.login(LoginRequest(email, password))
                                            if (response.isSuccessful) {
                                                val token = response.body()?.data?.token ?: ""
                                                val role = response.body()?.data?.role ?: "user"

                                                AuthPreferences(context).saveToken(token, role)
                                                Toast.makeText(context, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                                                onLoginSuccess(role)
                                            } else {
                                                val errorBody = response.errorBody()?.string()
                                                val jsonError = errorBody?.let { JSONObject(it) }
                                                val errorMessage = jsonError?.optString("message", "Login Gagal") ?: "Login Gagal"
                                                val code = response.code()

                                                if (code == 401 || errorMessage.contains("kredensial", ignoreCase = true)) {
                                                    Toast.makeText(context, "Email atau Password tidak sesuai.", Toast.LENGTH_LONG).show()
                                                }
                                                else if (errorMessage.contains("diproses", ignoreCase = true) || errorMessage.contains("admin", ignoreCase = true)) {
                                                    showPendingDialog = true
                                                }
                                                else if (code == 403 || errorMessage.contains("diverifikasi", ignoreCase = true)) {
                                                    Toast.makeText(context, "Akun belum aktif, silakan verifikasi OTP.", Toast.LENGTH_LONG).show()
                                                    onNavigateToOtp(email)
                                                }
                                                else if (code == 404 || errorMessage.contains("tidak ditemukan", ignoreCase = true)) {
                                                    Toast.makeText(context, "Akun tidak ditemukan. Silakan mendaftar.", Toast.LENGTH_LONG).show()
                                                    onNavigateToRegister()
                                                }
                                                else {
                                                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Koneksi Error", Toast.LENGTH_SHORT).show()
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Isi email dan password", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(brush = Brush.horizontalGradient(colors = listOf(RitecsDarkBlue, RitecsLightBlue)), shape = RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                else Text("Masuk Akun", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(modifier = Modifier.fillMaxWidth(0.9f), verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                    Text(" ATAU ", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 8.dp))
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                }
                Spacer(modifier = Modifier.height(24.dp))

                val credentialManager = androidx.credentials.CredentialManager.create(context)
                var isGoogleLoading by remember { mutableStateOf(false) }

                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            isGoogleLoading = true
                            try {
                                val request = androidx.credentials.GetCredentialRequest.Builder()
                                    .addCredentialOption(
                                        com.google.android.libraries.identity.googleid.GetGoogleIdOption.Builder()
                                            .setFilterByAuthorizedAccounts(false)
                                            .setServerClientId("74205596232-8shtov9l8k950qh1dapa4uvvk6os8lvj.apps.googleusercontent.com")
                                            .setAutoSelectEnabled(false)
                                            .build()
                                    )
                                    .build()

                                val result = credentialManager.getCredential(
                                    request = request,
                                    context = context,
                                )

                                val credential = result.credential
                                if (credential is androidx.credentials.CustomCredential &&
                                    credential.type == com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                                ) {
                                    val googleIdTokenCredential = com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.createFrom(credential.data)

                                    val googleEmail = googleIdTokenCredential.id
                                    val googleId = googleIdTokenCredential.idToken
                                    val fullName = googleIdTokenCredential.displayName ?: "User"
                                    val nameParts = fullName.split(" ", limit = 2)
                                    val firstName = nameParts.getOrNull(0) ?: "User"
                                    val lastName = nameParts.getOrNull(1)
                                    val profilePic = googleIdTokenCredential.profilePictureUri?.toString()

                                    val requestBody = com.example.ritecsmobile.data.remote.dto.GoogleLoginRequest(
                                        email = googleEmail,
                                        google_id = googleId,
                                        first_name = firstName,
                                        last_name = lastName,
                                        img_path = profilePic
                                    )

                                    val response = RetrofitClient.authApi.googleLogin(requestBody)

                                    if (response.isSuccessful) {
                                        val token = response.body()?.data?.token ?: ""
                                        val role = response.body()?.data?.role ?: "user"

                                        AuthPreferences(context).saveToken(token, role)
                                        Toast.makeText(context, "Login Google Berhasil!", Toast.LENGTH_SHORT).show()

                                        onLoginSuccess(role)
                                    } else {
                                        val errorCode = response.code()
                                        val errorBody = response.errorBody()?.string()
                                        Toast.makeText(context, "DITOLAK SERVER! Kode: $errorCode | Pesan: $errorBody", Toast.LENGTH_LONG).show()
                                    }

                                } else {
                                    Toast.makeText(context, "Tipe akun tidak didukung", Toast.LENGTH_SHORT).show()
                                }

                            } catch (e: androidx.credentials.exceptions.GetCredentialException) {
                                Toast.makeText(context, "Login Google dibatalkan.", Toast.LENGTH_LONG).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "KONEKSI CRASH: ${e.message}", Toast.LENGTH_LONG).show()
                            } finally {
                                isGoogleLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    enabled = !isGoogleLoading
                ) {
                    if (isGoogleLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Image(painter = painterResource(id = R.drawable.ic_google), contentDescription = null, modifier = Modifier.size(20.dp).padding(end = 8.dp))
                        Text("Lanjutkan dengan Google", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(modifier = Modifier.padding(bottom = 32.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Belum punya akun? ", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    Text(
                        text = "Daftar di sini",
                        color = primaryColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { onNavigateToRegister() }
                    )
                }
            }
        }
    }

    // --- DIALOG PENDING AKTIVASI ---
    if (showPendingDialog) {
        AlertDialog(
            onDismissRequest = { showPendingDialog = false },
            title = { Text("Aktivasi Sedang Diproses", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Text(
                    "Permintaan aktivasi manual Anda sedang ditinjau oleh tim Admin kami. Mohon menunggu 1x24 jam sebelum mencoba login kembali.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showPendingDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = RitecsDarkBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Saya Mengerti", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // ==========================================
    // 💡 POP-UP LUPA PASSWORD BARU
    // ==========================================
    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text("Lupa Password?", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Text(
                    "Untuk menjaga keamanan akun Anda, proses pemulihan password dilakukan melalui website resmi Ritecs.\n\nKlik tombol di bawah untuk menuju halaman reset password.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showForgotPasswordDialog = false // Tutup pop-up
                        // Arahkan ke web
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse("https://ritecs.org/forgot-password")
                        )
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RitecsDarkBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Buka Website", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("Batal", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}