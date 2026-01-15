package com.asyiraaf.sparepartmanager.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(navController: NavController, viewModel: SparepartViewModel) {
    var kode by remember { mutableStateOf("") }
    var nama by remember { mutableStateOf("") }
    var harga by remember { mutableStateOf("") }
    var stok by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Barang Baru") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
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
                label = { Text("Kode Barang (Unik)") },
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
                label = { Text("Stok Awal") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (kode.isNotEmpty() && nama.isNotEmpty() && harga.isNotEmpty() && stok.isNotEmpty()) {
                        scope.launch {
                            val count = viewModel.checkDuplicate(kode, nama)
                            if (count > 0) {
                                Toast.makeText(context, "Kode atau Nama barang sudah ada!", Toast.LENGTH_SHORT).show()
                            } else {
                                val stokInt = stok.toInt()

                                // 1. Simpan Barang
                                val barangBaru = Sparepart(
                                    id = 0,
                                    kode = kode,
                                    nama = nama,
                                    harga = harga.toLong(),
                                    stok = stokInt
                                )
                                viewModel.insert(barangBaru)

                                // 2. CATAT KE HISTORY (STOK AWAL)
                                if (stokInt > 0) {
                                    val riwayatAwal = Riwayat(
                                        id = 0,
                                        namaBarang = nama,
                                        jenis = "Stok Awal",
                                        jumlah = stokInt,
                                        tanggal = System.currentTimeMillis()
                                    )
                                    viewModel.insertRiwayat(riwayatAwal)
                                }

                                Toast.makeText(context, "Berhasil disimpan!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4081))
            ) {
                Text("SIMPAN DATA", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}