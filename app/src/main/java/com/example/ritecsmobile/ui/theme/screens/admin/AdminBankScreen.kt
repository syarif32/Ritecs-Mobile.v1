package com.example.ritecsmobile.ui.theme.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.ritecsmobile.data.remote.dto.AdminBankDto
import com.example.ritecsmobile.data.remote.dto.BankRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBankScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authPreferences = remember { AuthPreferences(context) }
    val token by authPreferences.authToken.collectAsState(initial = "")

    var banks by remember { mutableStateOf<List<AdminBankDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Dialog States
    var showFormDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedBank by remember { mutableStateOf<AdminBankDto?>(null) } // Jika null berarti CREATE, jika ada isinya berarti EDIT

    // Form States
    var formBankName by remember { mutableStateOf("") }
    var formAccountName by remember { mutableStateOf("") }
    var formAccountNumber by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    // Fetch Data
    fun loadBanks() {
        scope.launch {
            if (!token.isNullOrEmpty()) {
                isLoading = true
                try {
                    val res = RetrofitClient.authApi.getAdminBanks("Bearer $token")
                    if (res.isSuccessful) banks = res.body()?.data ?: emptyList()
                } catch (e: CancellationException) { throw e }
                catch (e: Exception) { Toast.makeText(context, "Gagal memuat bank", Toast.LENGTH_SHORT).show() }
                finally { isLoading = false }
            }
        }
    }

    LaunchedEffect(token) { loadBanks() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bank Data", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RitecsBlue, titleContentColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedBank = null
                    formBankName = ""; formAccountName = ""; formAccountNumber = ""
                    showFormDialog = true
                },
                containerColor = RitecsBlue
            ) { Icon(Icons.Default.Add, contentDescription = "Tambah Bank", tint = Color.White) }
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = RitecsBlue) }
        } else if (banks.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Data Bank Kosong", color = Color.Gray) }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF5F6FA)).padding(16.dp)) {
                items(banks) { bank ->
                    BankCard(
                        bank = bank,
                        onEdit = {
                            selectedBank = bank
                            formBankName = bank.bank_name
                            formAccountName = bank.account_name
                            formAccountNumber = bank.account_number
                            showFormDialog = true
                        },
                        onDelete = {
                            selectedBank = bank
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }

        // --- FORM DIALOG (CREATE / EDIT) ---
        if (showFormDialog) {
            AlertDialog(
                onDismissRequest = { if (!isSubmitting) showFormDialog = false },
                title = { Text(if (selectedBank == null) "Tambah Bank Baru" else "Edit Data Bank", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        OutlinedTextField(value = formBankName, onValueChange = { formBankName = it }, label = { Text("Nama Bank (ex: BCA, BRI)") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = formAccountNumber, onValueChange = { formAccountNumber = it }, label = { Text("Nomor Rekening") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = formAccountName, onValueChange = { formAccountName = it }, label = { Text("Atas Nama") }, modifier = Modifier.fillMaxWidth())
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (formBankName.isBlank() || formAccountName.isBlank() || formAccountNumber.isBlank()) {
                                Toast.makeText(context, "Harap isi semua kolom!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            isSubmitting = true
                            scope.launch {
                                try {
                                    val req = BankRequest(formBankName, formAccountName, formAccountNumber)
                                    val res = if (selectedBank == null) {
                                        RetrofitClient.authApi.createAdminBank("Bearer $token", req)
                                    } else {
                                        RetrofitClient.authApi.updateAdminBank("Bearer $token", selectedBank!!.id, req)
                                    }

                                    if (res.isSuccessful) {
                                        Toast.makeText(context, "Sukses menyimpan bank!", Toast.LENGTH_SHORT).show()
                                        showFormDialog = false
                                        loadBanks() // Reload data
                                    } else {
                                        Toast.makeText(context, "Gagal menyimpan data.", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: CancellationException) { throw e }
                                catch (e: Exception) { Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show() }
                                finally { isSubmitting = false }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RitecsBlue)
                    ) { if (isSubmitting) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp)) else Text("Simpan") }
                },
                dismissButton = { TextButton(onClick = { showFormDialog = false }, enabled = !isSubmitting) { Text("Batal", color = Color.Gray) } }
            )
        }

        // --- DELETE DIALOG ---
        if (showDeleteDialog && selectedBank != null) {
            AlertDialog(
                onDismissRequest = { if (!isSubmitting) showDeleteDialog = false },
                title = { Text("Hapus Bank?", fontWeight = FontWeight.Bold, color = Color(0xFFE74C3C)) },
                text = { Text("Apakah Anda yakin ingin menghapus bank ${selectedBank!!.bank_name}?") },
                confirmButton = {
                    Button(
                        onClick = {
                            isSubmitting = true
                            scope.launch {
                                try {
                                    val res = RetrofitClient.authApi.deleteAdminBank("Bearer $token", selectedBank!!.id)
                                    if (res.isSuccessful) {
                                        Toast.makeText(context, "Bank berhasil dihapus", Toast.LENGTH_SHORT).show()
                                        showDeleteDialog = false
                                        loadBanks()
                                    }
                                } catch (e: CancellationException) { throw e }
                                catch (e: Exception) { Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show() }
                                finally { isSubmitting = false }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE74C3C))
                    ) { if (isSubmitting) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp)) else Text("Hapus") }
                },
                dismissButton = { TextButton(onClick = { showDeleteDialog = false }, enabled = !isSubmitting) { Text("Batal", color = Color.Gray) } }
            )
        }
    }
}

@Composable
fun BankCard(bank: AdminBankDto, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = RitecsBlue, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(bank.bank_name, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = AdminDark)
                    Text("No Rek: ${bank.account_number}", color = Color.DarkGray, fontSize = 14.sp)
                    Text("a/n ${bank.account_name}", color = Color.Gray, fontSize = 14.sp)
                }
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Edit", tint = RitecsBlue) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color(0xFFE74C3C)) }
            }
        }
    }
}