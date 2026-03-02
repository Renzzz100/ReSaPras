package com.example.resapras

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LaporanViewModel : ViewModel() {

    private val repository = LaporanRepository()

    private val _laporan = MutableStateFlow<List<Laporan>>(emptyList())
    val laporan: StateFlow<List<Laporan>> = _laporan

    private val _selectedLaporan = MutableStateFlow<Laporan?>(null)
    val selectedLaporan: StateFlow<Laporan?> = _selectedLaporan

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSuccess = MutableStateFlow<String?>(null)
    val isSuccess: StateFlow<String?> = _isSuccess

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg: StateFlow<String?> = _errorMsg

    // READ
    fun fetchLaporan() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getLaporan()
                _laporan.value = result
                _isSuccess.value = "Koneksi berhasil! Data: ${result.size} laporan" // ✅
            } catch (e: Exception) {
                _errorMsg.value = "Gagal: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchLaporanByPelapor(pelaporId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _laporan.value = repository.getLaporanByPelapor(pelaporId)
            } catch (e: Exception) {
                _errorMsg.value = "Gagal mengambil data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchLaporanById(id: Long) {
        viewModelScope.launch {
            try {
                _selectedLaporan.value = repository.getLaporanById(id)
            } catch (e: Exception) {
                _errorMsg.value = "Gagal mengambil detail: ${e.message}"
            }
        }
    }

    // CREATE
    fun tambahLaporan(
        kodeLaporan: String,
        pelaporId: Long,
        kategoriId: Long?,
        lokasiId: Long?,
        asetId: Long?,
        judul: String,
        deskripsi: String?,
        prioritas: String = "rendah"
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.tambahLaporan(
                    kodeLaporan, pelaporId, kategoriId,
                    lokasiId, asetId, judul, deskripsi, prioritas
                )
                _isSuccess.value = "Laporan berhasil dibuat!"
                fetchLaporan()
            } catch (e: Exception) {
                _errorMsg.value = "Gagal membuat laporan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // UPDATE
    fun updateLaporan(id: Long, judul: String, deskripsi: String?, prioritas: String) {
        viewModelScope.launch {
            try {
                repository.updateLaporan(id, judul, deskripsi, prioritas)
                _isSuccess.value = "Laporan berhasil diupdate!"
                fetchLaporan()
            } catch (e: Exception) {
                _errorMsg.value = "Gagal mengupdate: ${e.message}"
            }
        }
    }

    fun verifikasiLaporan(id: Long, verifikatorId: Long) {
        viewModelScope.launch {
            try {
                repository.verifikasiLaporan(id, verifikatorId)
                _isSuccess.value = "Laporan berhasil diverifikasi!"
                fetchLaporan()
            } catch (e: Exception) {
                _errorMsg.value = "Gagal verifikasi: ${e.message}"
            }
        }
    }

    fun tolakLaporan(id: Long, penutupId: Long, alasan: String) {
        viewModelScope.launch {
            try {
                repository.tolakLaporan(id, penutupId, alasan)
                _isSuccess.value = "Laporan ditolak!"
                fetchLaporan()
            } catch (e: Exception) {
                _errorMsg.value = "Gagal menolak laporan: ${e.message}"
            }
        }
    }

    fun tutupLaporan(id: Long, penutupId: Long) {
        viewModelScope.launch {
            try {
                repository.tutupLaporan(id, penutupId)
                _isSuccess.value = "Laporan berhasil ditutup!"
                fetchLaporan()
            } catch (e: Exception) {
                _errorMsg.value = "Gagal menutup laporan: ${e.message}"
            }
        }
    }

    // DELETE
    fun deleteLaporan(id: Long) {
        viewModelScope.launch {
            try {
                repository.deleteLaporan(id)
                _isSuccess.value = "Laporan berhasil dihapus!"
                fetchLaporan()
            } catch (e: Exception) {
                _errorMsg.value = "Gagal menghapus: ${e.message}"
            }
        }
    }
}