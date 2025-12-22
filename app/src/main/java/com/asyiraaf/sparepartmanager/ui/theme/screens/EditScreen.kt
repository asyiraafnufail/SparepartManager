package com.asyiraaf.sparepartmanager.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.asyiraaf.sparepartmanager.data.Riwayat
import com.asyiraaf.sparepartmanager.data.Sparepart
import com.asyiraaf.sparepartmanager.viewmodel.SparepartViewModel
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    navController: NavController,
    viewModel: SparepartViewModel,
    id: Int,
    kodeLama: String,
    namaLama: String,
    hargaLama: Long,
    stokLama: Int
) {
    var kode by remember { mutableStateOf(kodeLama) }
    var nama by remember { mutableStateOf(namaLama) }
    var harga by remember { mutableStateOf(hargaLama.toString()) }
    var stok by remember { mutableStateOf(stokLama.toString()) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Barang") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF4081),
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
            OutlinedTextField(
                value = kode,
                onValueChange = { kode = it },
                label = { Text("Kode Barang") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nama,
                onValueChange = { nama = it },
                label = { Text("Nama Barang") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = harga,
                onValueChange = { if (it.all { char -> char.isDigit() }) harga = it },
                label = { Text("Harga (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = stok,
                onValueChange = { if (it.all { char -> char.isDigit() }) stok = it },
                label = { Text("Stok (Ubah manual untuk koreksi)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (kode.isNotEmpty() && nama.isNotEmpty() && harga.isNotEmpty() && stok.isNotEmpty()) {
                        scope.launch {
                            val count = viewModel.checkDuplicateUpdate(id, kode, nama)

                            if (count > 0) {
                                Toast.makeText(context, "Kode/Nama sudah dipakai barang lain!", Toast.LENGTH_SHORT).show()
                            } else {
                                val stokBaru = stok.toInt()

                                // 1. --- FITUR BARU: CEK SELISIH STOK UNTUK HISTORY ---
                                val selisih = stokBaru - stokLama
                                if (selisih != 0) {
                                    // Jika selisih positif (Misal 10 jadi 12) = +2 (Koreksi Masuk)
                                    // Jika selisih negatif (Misal 10 jadi 8) = -2 (Koreksi Keluar)
                                    val jenisKoreksi = if (selisih > 0) "Koreksi Masuk" else "Koreksi Keluar"

                                    val riwayatKoreksi = Riwayat(
                                        id = 0,
                                        namaBarang = nama, // Gunakan nama baru jika diedit
                                        jenis = jenisKoreksi,
                                        jumlah = abs(selisih), // Ambil angka positifnya saja
                                        tanggal = System.currentTimeMillis()
                                    )
                                    viewModel.insertRiwayat(riwayatKoreksi)
                                }
                                // -----------------------------------------------------

                                // 2. Update Data Barang Utama
                                val barangUpdate = Sparepart(
                                    id = id,
                                    kode = kode,
                                    nama = nama,
                                    harga = harga.toLong(),
                                    stok = stokBaru
                                )
                                viewModel.update(barangUpdate)

                                Toast.makeText(context, "Data berhasil diupdate!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Text("UPDATE DATA", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Barang?") },
            text = { Text("Apakah Anda yakin ingin menghapus '$namaLama'? Data tidak bisa dikembalikan.") },
            confirmButton = {
                Button(
                    onClick = {
                        val barangHapus = Sparepart(id, kodeLama, namaLama, hargaLama, stokLama)
                        viewModel.delete(barangHapus)
                        showDeleteDialog = false
                        Toast.makeText(context, "Barang dihapus.", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }
}