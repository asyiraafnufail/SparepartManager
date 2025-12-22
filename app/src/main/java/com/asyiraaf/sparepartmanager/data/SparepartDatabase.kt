package com.asyiraaf.sparepartmanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [Sparepart::class, Riwayat::class],
    version = 2,
    exportSchema = false
)
abstract class SparepartDatabase : RoomDatabase() {
    abstract fun dao(): SparepartDao

    companion object {
        @Volatile private var INSTANCE: SparepartDatabase? = null

        // Tetap pakai password agar data tidak hilang/error saat dibuka
        private val PASSPHRASE = "KunciRahasiaDapurSparepart2025!@#".toByteArray()

        fun getDatabase(context: Context): SparepartDatabase {
            return INSTANCE ?: synchronized(this) {
                // Konfigurasi Standar SQLCipher
                val factory = SupportFactory(PASSPHRASE)

                Room.databaseBuilder(context.applicationContext, SparepartDatabase::class.java, "sparepart_db")
                    .fallbackToDestructiveMigration()
                    .openHelperFactory(factory) // Kunci Enkripsi tetap terpasang
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}