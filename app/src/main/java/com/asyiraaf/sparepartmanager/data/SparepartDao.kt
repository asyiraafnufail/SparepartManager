package com.asyiraaf.sparepartmanager.data

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface SparepartDao {
    @Query("SELECT * FROM spareparts ORDER BY nama ASC")
    fun getAll(): Flow<List<Sparepart>>

    @Query("SELECT * FROM spareparts WHERE nama LIKE :query OR kode LIKE :query")
    fun search(query: String): Flow<List<Sparepart>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(sparepart: Sparepart)

    @Update
    suspend fun update(sparepart: Sparepart)

    @Delete
    suspend fun delete(sparepart: Sparepart)

    @Query("UPDATE spareparts SET stok = stok + :jumlah WHERE id = :id")
    suspend fun stokMasuk(id: Int, jumlah: Int)

    @Query("UPDATE spareparts SET stok = stok - :jumlah WHERE id = :id")
    suspend fun stokKeluar(id: Int, jumlah: Int)

    @Insert
    suspend fun insertRiwayat(riwayat: Riwayat)

    // Ambil semua riwayat, urutkan dari yang paling baru (DESC)
    @Query("SELECT * FROM riwayat_transaksi ORDER BY tanggal DESC")
    fun getAllRiwayat(): Flow<List<Riwayat>>

    @Query("SELECT COUNT(*) FROM spareparts WHERE kode = :kode COLLATE NOCASE OR nama = :nama COLLATE NOCASE")
    suspend fun checkDuplicate(kode: String, nama: String): Int

    // 2. Cek Duplikat Update (Abaikan Besar/Kecil)
    @Query("SELECT COUNT(*) FROM spareparts WHERE (kode = :kode COLLATE NOCASE OR nama = :nama COLLATE NOCASE) AND id != :id")
    suspend fun checkDuplicateUpdate(id: Int, kode: String, nama: String): Int

}