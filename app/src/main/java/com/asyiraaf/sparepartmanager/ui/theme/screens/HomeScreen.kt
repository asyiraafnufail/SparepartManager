package com.asyiraaf.sparepartmanager.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.asyiraaf.sparepartmanager.viewmodel.SparepartViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: SparepartViewModel) {
    val listSparepart by viewModel.listSparepart.observeAsState(emptyList())

    // 1. FIX BUG: Reset pencarian otomatis saat halaman ini dibuka kembali
    // Ini memastikan saat tombol 'Back' ditekan, list kembali penuh (reset).
    LaunchedEffect(Unit) {
        viewModel.setPencarian("")
    }

    // State untuk teks input search bar
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        // 2. UI FIX: Menggunakan CenterAlignedTopAppBar agar Judul di TENGAH
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Sparepart Manager",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // 3. UI FIX: Mengurangi jarak atas (top padding) agar tidak terlalu jauh dari header
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.setPencarian(it)
                },
                label = { Text("Cari Barang...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 16.dp), // Jarak atas diperkecil
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.LightGray,
                    focusedLabelColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                singleLine = true
            )

            if (listSparepart.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    // Pesan berbeda jika sedang mencari atau memang database kosong
                    if (searchQuery.isNotEmpty()) {
                        Text("Barang tidak ditemukan", color = Color.Gray)
                    } else {
                        Text("Belum ada data barang", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(listSparepart) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    navController.navigate("edit/${item.id}/${item.kode}/${item.nama}/${item.harga}/${item.stok}")
                                },
                            elevation = CardDefaults.cardElevation(2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    // Kode Barang
                                    Text(
                                        text = item.kode,
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Medium
                                    )
                                    // Nama Barang
                                    Text(
                                        text = item.nama,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    val formatRp = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(item.harga)
                                    Text(text = formatRp, fontSize = 14.sp, color = Color(0xFF388E3C))
                                }

                                // Bagian Stok
                                Surface(
                                    color = if (item.stok > 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                    shape = MaterialTheme.shapes.small
                                ) {
                                    Text(
                                        text = "${item.stok} Pcs",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.stok > 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}