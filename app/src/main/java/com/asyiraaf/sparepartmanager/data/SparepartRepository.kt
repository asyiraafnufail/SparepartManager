package com.asyiraaf.sparepartmanager.data

import kotlinx.coroutines.flow.Flow

class SparepartRepository(private val dao: SparepartDao) {

    // Mengambil semua data
    val allSpareparts: Flow<List<Sparepart>> = dao.getAll()

    // Mengambil data riwayat
    val allRiwayat: Flow<List<Riwayat>> = dao.getAllRiwayat()

    // Fungsi Pencarian
    fun search(query: String): Flow<List<Sparepart>> {
        return dao.search("%$query%")
    }

    // Operasi CRUD
    suspend fun insert(sparepart: Sparepart) {
        dao.insert(sparepart)
    }

    suspend fun update(sparepart: Sparepart) {
        dao.update(sparepart)
    }

    suspend fun delete(sparepart: Sparepart) {
        dao.delete(sparepart)
    }

    // Operasi Stok
    suspend fun stokMasuk(id: Int, jumlah: Int) {
        dao.stokMasuk(id, jumlah)
    }

    suspend fun stokKeluar(id: Int, jumlah: Int) {
        dao.stokKeluar(id, jumlah)
    }

    // Operasi Riwayat
    suspend fun insertRiwayat(riwayat: Riwayat) {
        dao.insertRiwayat(riwayat)
    }

    // Validasi
    suspend fun checkDuplicate(kode: String, nama: String): Int {
        return dao.checkDuplicate(kode, nama)
    }

    suspend fun checkDuplicateUpdate(id: Int, kode: String, nama: String): Int {
        return dao.checkDuplicateUpdate(id, kode, nama)
    }

    // --- PASTIKAN TIDAK ADA FUNGSI CHECKPOINT DI SINI ---
    // (Fungsi checkpoint sudah dihapus karena dipindah ke SparepartDatabase)
}