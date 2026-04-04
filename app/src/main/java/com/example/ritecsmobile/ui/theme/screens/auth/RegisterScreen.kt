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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ritecsmobile.R
import com.example.ritecsmobile.data.local.AuthPreferences
import com.example.ritecsmobile.data.remote.RetrofitClient
import com.example.ritecsmobile.data.remote.dto.GoogleLoginRequest
import com.example.ritecsmobile.data.remote.dto.RegisterRequest
import kotlinx.coroutines.launch
import org.json.JSONObject

// 💡 Warna Khas Ritecs
val RitecsDarkBlue = Color(0xFF004191)
val RitecsLightBlue = Color(0xFF0091FF)
val RitecsBlue = Color(0xFF0062CD)

@Composable
fun RegisterScreen(
    onRegisterSuccess: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isGoogleLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val primaryColor = MaterialTheme.colorScheme.primary

    // 💡 SURFACE DINAMIS UNTUK BACKGROUND
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
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

                // LOGO RITECS NORMAL
                Image(
                    painter = painterResource(id = R.drawable.ritecs_logo),
                    contentDescription = "Logo Ritecs",
                    modifier = Modifier.size(90.dp).padding(bottom = 24.dp)
                )

                // KOTAK FORM
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // 💡 Dinamis
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 💡 Teks Judul Hitam/Putih
                        Text("Daftar Akun", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                        // 💡 Teks Subjudul Abu-abu Dinamis
                        Text("Bergabung bersama Ritecs", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 24.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            // FIELD NAMA DEPAN
                            OutlinedTextField(
                                value = firstName, onValueChange = { firstName = it }, label = { Text("Nama Depan") },
                                modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), singleLine = true,
                                // 💡 TextField Colors Dinamis
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = primaryColor,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    focusedLabelColor = primaryColor,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            // FIELD NAMA BELAKANG
                            OutlinedTextField(
                                value = lastName, onValueChange = { lastName = it }, label = { Text("Nama Belakang") },
                                modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), singleLine = true,
                                // 💡 TextField Colors Dinamis
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = primaryColor,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    focusedLabelColor = primaryColor,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // FIELD EMAIL
                        OutlinedTextField(
                            value = email, onValueChange = { email = it }, label = { Text("Email Address") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                            // 💡 TextField Colors Dinamis
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

                        // FIELD PASSWORD DENGAN MATA
                        OutlinedTextField(
                            value = password, onValueChange = { password = it }, label = { Text("Password (Min. 8)") },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                // 💡 Warna Mata Dinamis
                                IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(imageVector = image, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                            },
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                            // 💡 TextField Colors Dinamis
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

                        // FIELD KONFIRMASI PASSWORD DENGAN MATA
                        OutlinedTextField(
                            value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Konfirmasi Password") },
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                // 💡 Warna Mata Dinamis
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) { Icon(imageVector = image, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                            },
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                            // 💡 TextField Colors Dinamis
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryColor,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedLabelColor = primaryColor,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 💡 TOMBOL DAFTAR (TETAP GRADASI BIRU)
                        Button(
                            onClick = {
                                if (firstName.isNotEmpty() && email.isNotEmpty() && password.length >= 8) {
                                    if (password == confirmPassword) {
                                        coroutineScope.launch {
                                            isLoading = true
                                            try {
                                                val request = RegisterRequest(firstName, lastName, email, password, confirmPassword)
                                                val response = RetrofitClient.authApi.register(request)
                                                if (response.isSuccessful) {
                                                    Toast.makeText(context, "OTP dikirim ke email!", Toast.LENGTH_SHORT).show()
                                                    onRegisterSuccess(email)
                                                } else {
                                                    val errorJson = response.errorBody()?.string()?.let { JSONObject(it) }
                                                    Toast.makeText(context, errorJson?.optString("message") ?: "Gagal Mendaftar", Toast.LENGTH_LONG).show()
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Koneksi Error", Toast.LENGTH_SHORT).show()
                                            } finally { isLoading = false }
                                        }
                                    } else {
                                        Toast.makeText(context, "Password & Konfirmasi tidak sama!", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Lengkapi data & password min 8 karakter", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), // Set transparent
                            contentPadding = PaddingValues(), // Hapus padding default
                            enabled = !isLoading
                        ) {
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
                                else Text("Daftar Sekarang", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // PEMISAH "ATAU"
                Row(modifier = Modifier.fillMaxWidth(0.9f), verticalAlignment = Alignment.CenterVertically) {
                    // 💡 Garis Pemisah Dinamis
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                    // 💡 Teks ATAU Dinamis
                    Text(" ATAU ", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 8.dp))
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                }

                Spacer(modifier = Modifier.height(24.dp))

                val credentialManager = androidx.credentials.CredentialManager.create(context)

                // TOMBOL GOOGLE
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
                                            .setAutoSelectEnabled(false).build()
                                    ).build()

                                val result = credentialManager.getCredential(request = request, context = context)
                                val credential = result.credential

                                if (credential is androidx.credentials.CustomCredential && credential.type == com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                    val googleIdTokenCredential = com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.createFrom(credential.data)
                                    val nameParts = (googleIdTokenCredential.displayName ?: "User").split(" ", limit = 2)

                                    val apiRequest = GoogleLoginRequest(
                                        email = googleIdTokenCredential.id, google_id = googleIdTokenCredential.idToken,
                                        first_name = nameParts.getOrNull(0) ?: "User", last_name = nameParts.getOrNull(1),
                                        img_path = googleIdTokenCredential.profilePictureUri?.toString()
                                    )

                                    val response = RetrofitClient.authApi.googleLogin(apiRequest)
                                    if (response.isSuccessful) {
                                        val tokenDariServer = response.body()?.data?.token ?: ""
                                        val roleDariServer = response.body()?.data?.role ?: "user"
                                        AuthPreferences(context).saveToken(tokenDariServer, roleDariServer)
                                        Toast.makeText(context, "Berhasil Login dengan Google!", Toast.LENGTH_SHORT).show()
                                        onRegisterSuccess(googleIdTokenCredential.id)
                                    } else Toast.makeText(context, "Gagal sinkron server", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                android.util.Log.e("GOOGLE_AUTH", "Error", e)
                            } finally { isGoogleLoading = false }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.9f).height(50.dp), shape = RoundedCornerShape(12.dp),
                    // 💡 Warna tombol Google dinamis
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    enabled = !isGoogleLoading
                ) {
                    if (isGoogleLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    else {
                        Image(painter = painterResource(id = R.drawable.ic_google), contentDescription = null, modifier = Modifier.size(20.dp).padding(end = 8.dp))
                        // 💡 Teks Lanjutkan dengan Google Dinamis
                        Text("Lanjutkan dengan Google", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // LINK LOGIN BAWAH
                Row(modifier = Modifier.padding(bottom = 32.dp), verticalAlignment = Alignment.CenterVertically) {
                    // 💡 Teks Label Dinamis
                    Text("Sudah punya akun? ", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    // 💡 Teks Link tetap Primary (Biru)
                    Text("Masuk di sini", color = primaryColor, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.clickable { onNavigateBack() })
                }
            }
        }
    }
}