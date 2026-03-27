package com.example.ritecsmobile.ui.theme.screens.admin

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.ritecsmobile.data.local.AuthPreferences
import com.example.ritecsmobile.data.remote.RetrofitClient
import com.example.ritecsmobile.data.remote.dto.AdminJournalDto
import com.example.ritecsmobile.data.remote.dto.IdNameDto
import com.example.ritecsmobile.ui.screens.books.RitecsDarkBlue
import com.example.ritecsmobile.ui.screens.books.RitecsLightBlue
import com.example.ritecsmobile.ui.screens.profile.uriToFile
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

// 💡 Menggunakan warna dari file sebelumnya
val RitecsBlueJournal = Color(0xFF0062CD)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManageJournalsScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val token by remember { AuthPreferences(context) }.authToken.collectAsState(initial = "")

    var journals by remember { mutableStateOf<List<AdminJournalDto>>(emptyList()) }
    var availableKeywords by remember { mutableStateOf<List<IdNameDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var showFormDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedJournal by remember { mutableStateOf<AdminJournalDto?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    // Form States
    var formTitle by remember { mutableStateOf("") }
    var formUrl by remember { mutableStateOf("") }
    var selectedKeywords by remember { mutableStateOf(setOf<Int>()) }
    var coverUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) coverUri = uri
    }

    fun loadData() {
        scope.launch {
            if (!token.isNullOrEmpty()) {
                isLoading = true
                try {
                    val resJrn = RetrofitClient.authApi.getAdminJournals("Bearer $token")
                    if (resJrn.isSuccessful) journals = resJrn.body()?.data ?: emptyList()

                    val resForm = RetrofitClient.authApi.getJournalFormData("Bearer $token")
                    if (resForm.isSuccessful) availableKeywords = resForm.body()?.data?.keywords ?: emptyList()
                } catch (e: CancellationException) { throw e } catch (e: Exception) { } finally { isLoading = false }
            }
        }
    }

    LaunchedEffect(token) { loadData() }

    Scaffold(
        topBar = {
            // 💡 HEADER DENGAN GRADASI PROFESIONAL
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(colors = listOf(Color(0xFF004191), Color(0xFF0091FF))))
            ) {
                TopAppBar(
                    title = { Text("Kelola Jurnal", fontWeight = FontWeight.Bold, color = Color.White) },
                    navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedJournal = null; formTitle = ""; formUrl = ""
                    selectedKeywords = emptySet(); coverUri = null
                    showFormDialog = true
                },
                containerColor = RitecsBlueJournal,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Tambah Jurnal", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = RitecsBlueJournal) }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF5F6FA)).padding(16.dp)) {
                items(journals) { journal ->
                    AdminJournalCard(
                        journal = journal,
                        onEdit = {
                            selectedJournal = journal
                            formTitle = journal.title; formUrl = journal.url_path ?: ""
                            selectedKeywords = journal.keywords.map { it.id }.toSet(); coverUri = null
                            showFormDialog = true
                        },
                        onDelete = { selectedJournal = journal; showDeleteDialog = true }
                    )
                }
            }
        }

        // ==========================================
        // DIALOG HAPUS JURNAL
        // ==========================================
        if (showDeleteDialog && selectedJournal != null) {
            AlertDialog(
                onDismissRequest = { if(!isSubmitting) showDeleteDialog = false },
                title = { Text("Hapus Jurnal?", color = Color.Red, fontWeight = FontWeight.Bold) },
                text = { Text("Jurnal '${selectedJournal!!.title}' akan dihapus permanen dari sistem.") },
                confirmButton = {
                    Button(
                        onClick = {
                            isSubmitting = true
                            scope.launch {
                                try {
                                    val res = RetrofitClient.authApi.deleteAdminJournal("Bearer $token", selectedJournal!!.journal_id)
                                    if (res.isSuccessful) { Toast.makeText(context, "Dihapus!", Toast.LENGTH_SHORT).show(); showDeleteDialog = false; loadData() }
                                } catch (e: CancellationException) { throw e } catch (e: Exception) {} finally { isSubmitting = false }
                            }
                        }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) { if (isSubmitting) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp)) else Text("Hapus Permanen") }
                },
                dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Batal", color = Color.Gray) } },
                shape = RoundedCornerShape(16.dp)
            )
        }

        // ==========================================
        // DIALOG FORM TAMBAH / EDIT JURNAL
        // ==========================================
        if (showFormDialog) {
            AlertDialog(
                onDismissRequest = { if(!isSubmitting) showFormDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false), // 💡 Form lebih lebar
                modifier = Modifier.fillMaxWidth(0.95f).padding(vertical = 24.dp),
                shape = RoundedCornerShape(16.dp),
                title = {
                    Text(if (selectedJournal == null) "Tambah Jurnal Baru" else "Edit Data Jurnal", fontWeight = FontWeight.ExtraBold, color = Color(0xFF004191))
                },
                text = {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                        // AREA COVER IMAGE
                        Box(
                            modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFE2E8F0)).clickable {
                                imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }, contentAlignment = Alignment.Center) {
                            if (coverUri != null) {
                                AsyncImage(model = coverUri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                            } else if (!selectedJournal?.cover_path.isNullOrEmpty()) {
                                val coverUrl = if (selectedJournal!!.cover_path!!.startsWith("http")) selectedJournal!!.cover_path else "https://ritecs.org/${selectedJournal!!.cover_path}"
                                AsyncImage(model = coverUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                                    Spacer(Modifier.height(4.dp))
                                    Text("Klik untuk memilih Cover Jurnal", color = Color.Gray, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                        Text("Informasi Utama", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = RitecsBlueJournal)
                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(value = formTitle, onValueChange = { formTitle = it }, label = { Text("Judul Jurnal*") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = formUrl, onValueChange = { formUrl = it }, label = { Text("URL Link Jurnal (Optional)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))

                        Spacer(Modifier.height(16.dp))
                        Text("Kata Kunci (Keywords)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = RitecsBlueJournal)
                        Spacer(Modifier.height(8.dp))

                        // 💡 PERUBAHAN: Memanggil Select2Dropdown dari file Buku!
                        Select2Dropdown(
                            label = "Cari & Pilih Keywords*",
                            availableItems = availableKeywords,
                            selectedIds = selectedKeywords,
                            onItemSelected = { id -> selectedKeywords = selectedKeywords + id },
                            onItemRemoved = { id -> selectedKeywords = selectedKeywords - id },
                            onAddNew = null // 💡 Dibiarkan null karena kita tidak buat tombol nambah keyword baru
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (formTitle.isBlank() || selectedKeywords.isEmpty()) {
                                Toast.makeText(context, "Judul dan Keywords wajib diisi!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            isSubmitting = true
                            scope.launch {
                                try {
                                    val titleRb = formTitle.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val urlRb = formUrl.toRequestBody("text/plain".toMediaTypeOrNull())

                                    val keywordParts = selectedKeywords.map { MultipartBody.Part.createFormData("keywords[]", it.toString()) }

                                    var coverPart: MultipartBody.Part? = null
                                    coverUri?.let { uri ->
                                        uriToFile(context, uri)?.let { file ->
                                            coverPart = MultipartBody.Part.createFormData("coverImage", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
                                        }
                                    }

                                    val res = if (selectedJournal == null) {
                                        RetrofitClient.authApi.storeAdminJournal("Bearer $token", titleRb, urlRb, keywordParts, coverPart)
                                    } else {
                                        RetrofitClient.authApi.updateAdminJournal("Bearer $token", selectedJournal!!.journal_id, titleRb, urlRb, keywordParts, coverPart)
                                    }

                                    if (res.isSuccessful) {
                                        Toast.makeText(context, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show()
                                        showFormDialog = false
                                        loadData()
                                    } else {
                                        Toast.makeText(context, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: CancellationException) { throw e } catch (e: Exception) { Toast.makeText(context, "Koneksi Error", Toast.LENGTH_SHORT).show() } finally { isSubmitting = false }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RitecsBlueJournal),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) { if (isSubmitting) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp)) else Text("Simpan Data Jurnal", fontWeight = FontWeight.Bold) }
                },
                dismissButton = {
                    TextButton(onClick = { showFormDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Batal", color = Color.Gray) }
                }
            )
        }
    }
}

// --- CARD JURNAL ELEGAN ---
@Composable
fun AdminJournalCard(journal: AdminJournalDto, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            val imgUrl = if (journal.cover_path?.startsWith("http") == true) journal.cover_path else "https://ritecs.org/${journal.cover_path}"
            AsyncImage(
                model = imgUrl ?: "https://via.placeholder.com/150",
                contentDescription = null, contentScale = ContentScale.Crop,
                modifier = Modifier.size(65.dp, 90.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFE2E8F0))
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(journal.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                if (!journal.url_path.isNullOrEmpty()) {
                    Text("🔗 Tautan Tersedia", color = Color(0xFF27AE60), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(4.dp))
                Text(journal.keywords.joinToString { it.name }, color = Color.Gray, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Column {
                IconButton(onClick = onEdit) {
                    Surface(shape = CircleShape, color = RitecsBlueJournal.copy(alpha = 0.1f)) {
                        Icon(Icons.Default.Edit, null, tint = RitecsBlueJournal, modifier = Modifier.padding(8.dp).size(20.dp))
                    }
                }
                Spacer(Modifier.height(4.dp))
                IconButton(onClick = onDelete) {
                    Surface(shape = CircleShape, color = Color.Red.copy(alpha = 0.1f)) {
                        Icon(Icons.Default.Delete, null, tint = Color.Red, modifier = Modifier.padding(8.dp).size(20.dp))
                    }
                }
            }
        }
    }
}