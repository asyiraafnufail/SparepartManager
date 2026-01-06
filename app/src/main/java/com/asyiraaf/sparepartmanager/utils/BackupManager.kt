package com.asyiraaf.sparepartmanager.utils

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.asyiraaf.sparepartmanager.data.SparepartDatabase
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.system.exitProcess

object BackupManager {

    private const val DB_NAME = "sparepart_db"

    private fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, message, duration).show()
        }
    }

    /**
     * EXPORT DATABASE (BACKUP)
     * Menggunakan metode Close-Copy-Restart untuk menjamin data WAL tergabung.
     */
    fun exportDatabase(context: Context, destUri: Uri) {
        val dbFile = context.getDatabasePath(DB_NAME)

        if (!dbFile.exists()) {
            showToast(context, "Database belum dibuat.")
            return
        }

        try {
            // 1. Dapatkan Instance Database
            val db = SparepartDatabase.getDatabase(context)

            // 2. PAKSA CHECKPOINT (Pindahkan data memori ke file)
            // Menggunakan query raw untuk memastikan perintah tereksekusi
            val checkpointQuery = "PRAGMA wal_checkpoint(FULL)"
            db.openHelper.writableDatabase.query(checkpointQuery).use { cursor ->
                if (cursor.moveToFirst()) {
                    // Checkpoint sukses dipanggil
                }
            }

            // 3. TUTUP DATABASE (CRITICAL STEP)
            // Menutup database memastikan semua file temporary (-wal, -shm) digabung ke .db utama
            if (db.isOpen) {
                db.close()
            }

            // 4. SALIN FILE KE TUJUAN
            context.contentResolver.openOutputStream(destUri)?.use { output ->
                FileInputStream(dbFile).use { input ->
                    input.copyTo(output)
                }
            }

            showToast(context, "Backup Sukses! Aplikasi akan restart...", Toast.LENGTH_LONG)

            // 5. RESTART APLIKASI
            // Wajib restart karena kita baru saja menutup koneksi database Singleton.
            // Jika tidak restart, aplikasi akan crash saat mencoba akses DB lagi.
            restartApp(context)

        } catch (e: Exception) {
            e.printStackTrace()
            showToast(context, "Gagal Backup: ${e.message}")
            // Coba buka lagi db jika gagal, untuk mencegah crash
            SparepartDatabase.getDatabase(context)
        }
    }

    fun importDatabase(context: Context, sourceUri: Uri) {
        val dbFile = context.getDatabasePath(DB_NAME)
        val walFile = context.getDatabasePath("$DB_NAME-wal")
        val shmFile = context.getDatabasePath("$DB_NAME-shm")

        try {
            context.contentResolver.openInputStream(sourceUri)?.use {} // Cek akses

            // 1. Tutup koneksi database
            val db = SparepartDatabase.getDatabase(context)
            if (db.isOpen) db.close()

            // 2. Timpa file database utama
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(dbFile).use { output ->
                    input.copyTo(output)
                }
            }

            // 3. Hapus file temporary (WAL & SHM) sisa database lama
            if (walFile.exists()) walFile.delete()
            if (shmFile.exists()) shmFile.delete()

            showToast(context, "Restore Berhasil! Restarting...", Toast.LENGTH_LONG)

            // 4. Restart Aplikasi
            restartApp(context)

        } catch (e: Exception) {
            e.printStackTrace()
            showToast(context, "Gagal Restore: File rusak/password beda.", Toast.LENGTH_LONG)
        }
    }

    private fun restartApp(context: Context) {
        Thread.sleep(1500) // Jeda biar Toast terbaca
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent?.component
        val mainIntent = android.content.Intent.makeRestartActivityTask(componentName)
        context.startActivity(mainIntent)
        exitProcess(0)
    }
}