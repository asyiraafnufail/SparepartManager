package com.asyiraaf.sparepartmanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spareparts")
data class Sparepart(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val kode: String,
    val nama: String,
    val harga: Long,
    val stok: Int
)