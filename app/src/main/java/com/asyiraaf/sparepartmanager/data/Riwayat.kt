package com.asyiraaf.sparepartmanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "riwayat_transaksi")
data class Riwayat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val namaBarang: String,
    val jenis: String, // "Masuk" atau "Keluar"
    val jumlah: Int,
    val tanggal: Long = System.currentTimeMillis() // Otomatis catat jam saat ini
)