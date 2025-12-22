package com.asyiraaf.sparepartmanager.ui.screens

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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.asyiraaf.sparepartmanager.viewmodel.SparepartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionMenuScreen(navController: NavController, viewModel: SparepartViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Menu Transaksi") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
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
            // MENU 1: Tambah Barang
            MenuCard(
                judul = "Tambah Barang Baru",
                deskripsi = "Daftarkan sparepart baru",
                icon = Icons.Default.Add,
                warna = Color(0xFFFF4081)
            ) { navController.navigate("add") }

            // MENU 2: Stok Masuk
            MenuCard(
                judul = "Stok Masuk",
                deskripsi = "Barang datang / kulakan",
                icon = Icons.Default.ArrowDownward,
                warna = Color(0xFF4CAF50)
            ) { navController.navigate("transaction/masuk") }

            // MENU 3: Stok Keluar
            MenuCard(
                judul = "Stok Keluar",
                deskripsi = "Barang terjual / terpakai",
                icon = Icons.Default.ArrowUpward,
                warna = Color(0xFFF44336)
            ) { navController.navigate("transaction/keluar") }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

// Komponen Kartu Menu
@Composable
fun MenuCard(judul: String, deskripsi: String, icon: ImageVector, warna: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = warna.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = warna,
                shape = RoundedCornerShape(50),
                modifier = Modifier.size(48.dp)
            ) {
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