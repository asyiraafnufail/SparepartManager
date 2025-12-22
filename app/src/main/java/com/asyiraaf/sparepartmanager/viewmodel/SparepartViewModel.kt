package com.asyiraaf.sparepartmanager.viewmodel

import androidx.lifecycle.*
import com.asyiraaf.sparepartmanager.data.Riwayat
import com.asyiraaf.sparepartmanager.data.Sparepart
import com.asyiraaf.sparepartmanager.data.SparepartRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SparepartViewModel(private val repo: SparepartRepository) : ViewModel() {

    // 1. LOGIKA SEARCH & LIST DATA
    private val _searchQuery = MutableStateFlow("")

    // Perbaikan: Menggunakan 'repo.allSpareparts', bukan 'repo.allData'
    val listSparepart: LiveData<List<Sparepart>> = _searchQuery.flatMapLatest { query ->
        if (query.isEmpty()) {
            repo.allSpareparts
        } else {
            repo.search(query)
        }
    }.asLiveData()

    // Data Riwayat
    val listRiwayat: LiveData<List<Riwayat>> = repo.allRiwayat.asLiveData()

    fun setPencarian(query: String) {
        _searchQuery.value = query
    }

    // 2. OPERASI CRUD (Create, Read, Update, Delete)
    fun insert(sparepart: Sparepart) = viewModelScope.launch {
        repo.insert(sparepart)
    }

    fun update(sparepart: Sparepart) = viewModelScope.launch {
        repo.update(sparepart)
    }

    fun delete(sparepart: Sparepart) = viewModelScope.launch {
        repo.delete(sparepart)
    }

    // 3. OPERASI STOK
    fun stokMasuk(id: Int, jumlah: Int) = viewModelScope.launch {
        repo.stokMasuk(id, jumlah)
    }

    fun stokKeluar(id: Int, jumlah: Int) = viewModelScope.launch {
        repo.stokKeluar(id, jumlah)
    }

    // 4. OPERASI RIWAYAT
    fun insertRiwayat(riwayat: Riwayat) = viewModelScope.launch {
        repo.insertRiwayat(riwayat)
    }

    // 5. VALIDASI DUPLIKAT
    suspend fun checkDuplicate(kode: String, nama: String): Int {
        return repo.checkDuplicate(kode, nama)
    }

    suspend fun checkDuplicateUpdate(id: Int, kode: String, nama: String): Int {
        return repo.checkDuplicateUpdate(id, kode, nama)
    }
}