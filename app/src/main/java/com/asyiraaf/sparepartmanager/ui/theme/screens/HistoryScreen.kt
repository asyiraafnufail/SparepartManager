package com.asyiraaf.sparepartmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.asyiraaf.sparepartmanager.viewmodel.SparepartViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController, viewModel: SparepartViewModel) {
    // Ambil data dari database
    val listRiwayat by viewModel.listRiwayat.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Transaksi") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF673AB7), // Warna Ungu
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (listRiwayat.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Belum ada riwayat transaksi", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // --- PERBAIKAN SORTING DI SINI ---
                // Mengurutkan berdasarkan Tanggal (Descending = Terbesar/Terbaru ke Terkecil/Terlama)
                val sortedList = listRiwayat.sortedByDescending { it.tanggal }

                items(sortedList) { riwayat ->
                    HistoryItem(
                        namaBarang = riwayat.namaBarang,
                        jenis = riwayat.jenis,
                        jumlah = riwayat.jumlah,
                        tanggalMillis = riwayat.tanggal
                    )
                }
            }
        }
    }
}

// --- KOMPONEN KARTU HISTORY ---
@Composable
fun HistoryItem(namaBarang: String, jenis: String, jumlah: Int, tanggalMillis: Long) {
    // Cek jenis transaksi untuk menentukan warna & icon
    // Kita anggap "Masuk" dan "Stok Awal" & "Koreksi Masuk" sebagai warna HIJAU
    val isMasuk = jenis.contains("Masuk", ignoreCase = true) || jenis.contains("Awal", ignoreCase = true)

    val warna = if (isMasuk) Color(0xFF4CAF50) else Color(0xFFF44336)
    val icon = if (isMasuk) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward

    // Format Tanggal
    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    val tanggalString = try {
        formatter.format(Date(tanggalMillis))
    } catch (e: Exception) {
        "-"
    }

    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Bulat
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(warna.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = warna)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Detail Teks
            Column(modifier = Modifier.weight(1f)) {
                // Tampilkan Label Jenis (Kecil di atas)
                Text(
                    text = jenis,
                    fontSize = 11.sp,
                    color = warna,
                    fontWeight = FontWeight.Bold
                )

                // Nama Barang
                Text(
                    text = namaBarang,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                // Tanggal
                Text(
                    text = tanggalString,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Jumlah Barang
            Text(
                text = "${if (isMasuk) "+" else "-"}$jumlah",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = warna
            )
        }
    }
}