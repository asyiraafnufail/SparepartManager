package com.asyiraaf.sparepartmanager.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.asyiraaf.sparepartmanager.data.Riwayat
import com.asyiraaf.sparepartmanager.viewmodel.SparepartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(navController: NavController, viewModel: SparepartViewModel, tipeTransaksi: String) {
    // 1. DATA
    val listBarang by viewModel.listSparepart.observeAsState(emptyList())

    // 2. STATE INPUT
    var kodeInput by remember { mutableStateOf("") }
    var jumlahInput by remember { mutableStateOf("") }
    var isRekomendasiAktif by remember { mutableStateOf(false) } // Kontrol manual tampil/sembunyi list

    // 3. LOGIKA FILTER (REKOMENDASI)
    val filteredOptions = remember(kodeInput, listBarang) {
        if (kodeInput.isEmpty()) {
            emptyList()
        } else {
            listBarang.filter {
                it.kode.contains(kodeInput, ignoreCase = true) ||
                        it.nama.contains(kodeInput, ignoreCase = true)
            }
        }
    }

    // Cari barang yang cocok persis (Untuk validasi)
    val barangDitemukan = listBarang.find { it.kode.equals(kodeInput, ignoreCase = true) }

    val context = LocalContext.current
    val isMasuk = tipeTransaksi == "masuk"
    val warnaTema = if (isMasuk) Color(0xFF4CAF50) else Color(0xFFF44336)
    val judul = if (isMasuk) "Stok Masuk (Kulakan)" else "Stok Keluar (Terjual)"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(judul) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = warnaTema,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            Text("Data Transaksi", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))

            // --- INPUT KODE BARANG (AUTO-COMPLETE LIST) ---
            OutlinedTextField(
                value = kodeInput,
                onValueChange = {
                    kodeInput = it
                    isRekomendasiAktif = true // Tampilkan rekomendasi saat mengetik
                },
                label = { Text("Cari Kode / Nama Barang") },
                placeholder = { Text("Ketik 'OLI' atau 'K001'...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = warnaTema,
                    focusedLabelColor = warnaTema
                )
            )

            // --- LIST REKOMENDASI (Muncul di bawah input) ---
            // Kita pakai Column biasa agar tidak crash (LazyColumn di dalam ScrollScreen = Crash)
            if (isRekomendasiAktif && filteredOptions.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column {
                        // Tampilkan maksimal 5 rekomendasi agar tidak terlalu panjang
                        filteredOptions.take(5).forEach { item ->
                            ListItem(
                                headlineContent = {
                                    Text("${item.kode} - ${item.nama}", fontWeight = FontWeight.Bold)
                                },
                                supportingContent = {
                                    Text("Sisa Stok: ${item.stok}", fontSize = 12.sp)
                                },
                                modifier = Modifier
                                    .clickable {
                                        kodeInput = item.kode // Isi otomatis
                                        isRekomendasiAktif = false // Sembunyikan list
                                    },
                                colors = ListItemDefaults.colors(containerColor = Color.White)
                            )
                            Divider(thickness = 0.5.dp, color = Color.LightGray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- INFO BARANG (Jika cocok) ---
            if (barangDitemukan != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("✓ Barang Terpilih:", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(barangDitemukan.nama, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Sisa Stok: ${barangDitemukan.stok}", fontSize = 14.sp, color = Color.Gray)

                        // Validasi stok untuk Transaksi Keluar
                        if (!isMasuk && barangDitemukan.stok <= 0) {
                            Text("⚠ Stok Habis!", color = Color.Red, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            } else if (kodeInput.isNotEmpty() && !isRekomendasiAktif) {
                // Info merah hanya muncul jika user selesai memilih/mengetik tapi tidak ketemu
                Text("✗ Kode tidak ditemukan", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- INPUT JUMLAH ---
            OutlinedTextField(
                value = jumlahInput,
                onValueChange = { if (it.all { char -> char.isDigit() }) jumlahInput = it },
                label = { Text("Jumlah") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = warnaTema,
                    focusedLabelColor = warnaTema
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- TOMBOL SIMPAN ---
            Button(
                onClick = {
                    val jumlah = jumlahInput.toIntOrNull() ?: 0

                    if (barangDitemukan == null) {
                        Toast.makeText(context, "Pilih barang yang valid dulu!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (jumlah <= 0) {
                        Toast.makeText(context, "Jumlah minimal 1!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (!isMasuk && barangDitemukan.stok < jumlah) {
                        Toast.makeText(context, "Stok tidak cukup! Sisa: ${barangDitemukan.stok}", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    // PROSES
                    if (isMasuk) {
                        viewModel.stokMasuk(barangDitemukan.id, jumlah)
                    } else {
                        viewModel.stokKeluar(barangDitemukan.id, jumlah)
                    }

                    // RIWAYAT
                    val riwayatBaru = Riwayat(
                        id = 0,
                        namaBarang = barangDitemukan.nama,
                        jenis = if (isMasuk) "Masuk" else "Keluar",
                        jumlah = jumlah,
                        tanggal = System.currentTimeMillis()
                    )
                    viewModel.insertRiwayat(riwayatBaru)

                    Toast.makeText(context, "Transaksi Berhasil!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = warnaTema)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("SIMPAN TRANSAKSI")
            }
        }
    }
}