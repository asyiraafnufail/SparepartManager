package com.asyiraaf.sparepartmanager.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.asyiraaf.sparepartmanager.utils.BackupManager
import com.asyiraaf.sparepartmanager.viewmodel.SparepartViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionMenuScreen(navController: NavController, viewModel: SparepartViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 1. LAUNCHER EXPORT (Simpan File)
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/x-sqlite3") // Mime Type SQL/DB
    ) { uri ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                BackupManager.exportDatabase(context, uri)
            }
        }
    }

    // 2. LAUNCHER IMPORT (Buka File)
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            // Konfirmasi sebelum menimpa data
            scope.launch(Dispatchers.IO) {
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "Memproses data...", Toast.LENGTH_SHORT).show()
                }
                BackupManager.importDatabase(context, uri)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Menu Transaksi", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // MENU STANDAR
            MenuCard("Tambah Barang Baru", "Daftarkan sparepart baru", Icons.Default.Add, Color(0xFFFF4081)) {
                navController.navigate("add")
            }
            MenuCard("Stok Masuk", "Barang datang / kulakan", Icons.Default.ArrowDownward, Color(0xFF4CAF50)) {
                navController.navigate("transaction/masuk")
            }
            MenuCard("Stok Keluar", "Barang terjual / terpakai", Icons.Default.ArrowUpward, Color(0xFFF44336)) {
                navController.navigate("transaction/keluar")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()

            Text("Keamanan Data (Backup)", fontWeight = FontWeight.Bold, color = Color.Gray)

            // TOMBOL EXPORT (BACKUP)
            MenuCard("Backup Database", "Simpan data (Terenkripsi)", Icons.Default.CloudUpload, Color(0xFF2196F3)) {
                val tanggal = SimpleDateFormat("ddMMM_HHmm", Locale.getDefault()).format(Date())
                exportLauncher.launch("Backup_Sparepart_$tanggal.db")
            }

            // TOMBOL IMPORT (RESTORE)
            MenuCard("Restore Database", "Kembalikan data dari file", Icons.Default.CloudDownload, Color(0xFFFF9800)) {
                // Filter hanya file db/semua file
                importLauncher.launch(arrayOf("*/*"))
            }

            Text(
                text = "Info: File backup berformat .db dan terenkripsi. Aman disimpan di Google Drive/WhatsApp.",
                fontSize = 12.sp,
                color = Color.LightGray,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}

@Composable
fun MenuCard(judul: String, deskripsi: String, icon: ImageVector, warna: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(90.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = warna.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(color = warna, shape = RoundedCornerShape(50), modifier = Modifier.size(48.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(judul, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(deskripsi, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}