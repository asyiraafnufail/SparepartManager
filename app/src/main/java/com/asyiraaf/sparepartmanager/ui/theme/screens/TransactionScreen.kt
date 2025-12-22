package com.asyiraaf.sparepartmanager.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
    // tipeTransaksi: "masuk" atau "keluar"

    // Ambil semua data barang (untuk pengecekan validasi di balik layar)
    val listBarang by viewModel.listSparepart.observeAsState(emptyList())

    // State Input Manual
    var kodeInput by remember { mutableStateOf("") }
    var jumlahInput by remember { mutableStateOf("") }

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
                .verticalScroll(rememberScrollState())
        ) {

            // --- INPUT KODE BARANG (MANUAL) ---
            Text("Data Transaksi", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = kodeInput,
                onValueChange = { kodeInput = it },
                label = { Text("Ketik Kode Barang") },
                placeholder = { Text("Contoh: OLI001") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Info kecil (Validasi Realtime)
            val barangDitemukan = listBarang.find { it.kode.equals(kodeInput, ignoreCase = true) }
            if (kodeInput.isNotEmpty()) {
                if (barangDitemukan != null) {
                    Text("✓ Barang Ditemukan: ${barangDitemukan.nama}", color = Color(0xFF4CAF50), fontSize = 12.sp)
                    Text("   Sisa Stok: ${barangDitemukan.stok}", color = Color.Gray, fontSize = 12.sp)
                } else {
                    Text("✗ Barang tidak ditemukan", color = Color.Red, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- INPUT JUMLAH ---
            OutlinedTextField(
                value = jumlahInput,
                onValueChange = { if (it.all { char -> char.isDigit() }) jumlahInput = it },
                label = { Text("Jumlah") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- TOMBOL SIMPAN DENGAN VALIDASI (FITUR KEDUA) ---
            Button(
                onClick = {
                    val jumlah = jumlahInput.toIntOrNull() ?: 0

                    // 1. VALIDASI: Apakah kode barang ada?
                    if (barangDitemukan == null) {
                        Toast.makeText(context, "Kode Barang SALAH / Tidak Ditemukan!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // 2. VALIDASI: Apakah jumlah diisi?
                    if (jumlah <= 0) {
                        Toast.makeText(context, "Jumlah harus lebih dari 0!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // 3. VALIDASI KHUSUS KELUAR: Cek stok cukup gak?
                    if (!isMasuk && barangDitemukan.stok < jumlah) {
                        Toast.makeText(context, "Stok tidak cukup! Sisa: ${barangDitemukan.stok}", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    // --- JIKA LOLOS SEMUA VALIDASI -> PROSES ---
                    if (isMasuk) {
                        viewModel.stokMasuk(barangDitemukan.id, jumlah)
                    } else {
                        viewModel.stokKeluar(barangDitemukan.id, jumlah)
                    }

                    // Simpan ke Riwayat
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