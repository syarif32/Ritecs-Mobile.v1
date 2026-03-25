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
import com.example.ritecsmobile.data.remote.dto.LoginRequest
import kotlinx.coroutines.launch
import org.json.JSONObject

// 💡 Warna Khas Ritecs
val RitecsDarkBlue = Color(0xFF004191)
val RitecsLightBlue = Color(0xFF0091FF)
val RitecsBlue = Color(0xFF0062CD)

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToOtp: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val primaryColor = MaterialTheme.colorScheme.primary

    // 💡 KEMBALI MENGGUNAKAN SURFACE PUTIH CLEAN
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
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Masuk untuk melanjutkan",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        // FIELD EMAIL
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
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // FIELD PASSWORD
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                val description = if (passwordVisible) "Sembunyikan password" else "Tampilkan password"

                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = image, contentDescription = description, tint = Color.Gray)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryColor,
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 💡 TOMBOL LOGIN (DIBIKIN GRADASI BIRU KEREN)
                        Button(
                            onClick = {
                                if (email.isNotEmpty() && password.isNotEmpty()) {
                                    coroutineScope.launch {
                                        isLoading = true
                                        try {
                                            val response = RetrofitClient.authApi.login(LoginRequest(email, password))
                                            if (response.isSuccessful) {
                                                val token = response.body()?.data?.token ?: ""
                                                AuthPreferences(context).saveToken(token)
                                                Toast.makeText(context, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                                                onLoginSuccess()
                                            } else {
                                                val errorBody = response.errorBody()?.string()
                                                val jsonError = errorBody?.let { JSONObject(it) }
                                                val errorMessage = jsonError?.optString("message", "Login Gagal") ?: "Login Gagal"
                                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()

                                                if (response.code() == 403 && errorMessage.contains("Belum diverifikasi", ignoreCase = true)) {
                                                    onNavigateToOtp(email)
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), // Set transparent biar Box di dalamnya bisa kelihatan
                            contentPadding = PaddingValues() // Hapus padding default
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
                                else Text("Masuk Akun", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // PEMISAH KEMBALI WARNA NORMAL
                Row(modifier = Modifier.fillMaxWidth(0.9f), verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFCBD5E1))
                    Text(" ATAU ", fontSize = 12.sp, color = Color(0xFF64748B), modifier = Modifier.padding(horizontal = 8.dp))
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFCBD5E1))
                }

                Spacer(modifier = Modifier.height(24.dp))

                val credentialManager = androidx.credentials.CredentialManager.create(context)
                var isGoogleLoading by remember { mutableStateOf(false) }

                // TOMBOL GOOGLE KEMBALI WARNA NORMAL
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

                                    val apiRequest = com.example.ritecsmobile.data.remote.dto.GoogleLoginRequest(
                                        email = googleEmail,
                                        google_id = googleId,
                                        first_name = firstName,
                                        last_name = lastName,
                                        img_path = profilePic
                                    )

                                    val response = RetrofitClient.authApi.googleLogin(apiRequest)
                                    if (response.isSuccessful) {
                                        val token = response.body()?.data?.token ?: ""
                                        AuthPreferences(context).saveToken(token)
                                        Toast.makeText(context, "Login Google Berhasil!", Toast.LENGTH_SHORT).show()
                                        onLoginSuccess()
                                    } else {
                                        Toast.makeText(context, "Gagal sinkron ke server Ritecs.", Toast.LENGTH_SHORT).show()
                                    }

                                } else {
                                    Toast.makeText(context, "Tipe akun tidak didukung", Toast.LENGTH_SHORT).show()
                                }

                            } catch (e: androidx.credentials.exceptions.GetCredentialException) {
                                android.util.Log.e("GOOGLE_AUTH", "Error detail: ", e)
                                Toast.makeText(context, "Batal/Error: ${e.message}", Toast.LENGTH_LONG).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isGoogleLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    enabled = !isGoogleLoading
                ) {
                    if (isGoogleLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Image(painter = painterResource(id = R.drawable.ic_google), contentDescription = null, modifier = Modifier.size(20.dp).padding(end = 8.dp))
                        Text("Lanjutkan dengan Google", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // LINK REGISTER KEMBALI WARNA NORMAL
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
}