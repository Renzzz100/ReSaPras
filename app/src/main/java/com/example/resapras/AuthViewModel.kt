package com.example.resapras

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application.applicationContext)
    private val sessionManager = SessionManager(application.applicationContext)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg: StateFlow<String?> = _errorMsg

    private val _username = MutableStateFlow(sessionManager.getUsername())
    val username: StateFlow<String> = _username

    private val _userEmail = MutableStateFlow(sessionManager.getEmail())
    val userEmail: StateFlow<String> = _userEmail

    // State untuk data profil lengkap
    private val _profileNama = MutableStateFlow<String>("-")
    val profileNama: StateFlow<String> = _profileNama

    private val _profileNoHp = MutableStateFlow<String>("-")
    val profileNoHp: StateFlow<String> = _profileNoHp

    private val _isProfileLoading = MutableStateFlow(false)
    val isProfileLoading: StateFlow<Boolean> = _isProfileLoading

    private val _profileError = MutableStateFlow<String?>(null)
    val profileError: StateFlow<String?> = _profileError

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMsg.value = null
            try {
                repository.login(email, password)
                _isSuccess.value = true
                // Update state dari session yang baru disimpan
                _username.value = sessionManager.getUsername()
                _userEmail.value = sessionManager.getEmail()
            } catch (e: Exception) {
                _errorMsg.value = when {
                    e.message?.contains("Invalid login") == true -> "Email atau password salah"
                    e.message?.contains("Email not confirmed") == true -> "Email belum dikonfirmasi"
                    else -> "Login gagal: ${e.message}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(email: String, password: String, nama: String, noHp: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMsg.value = null
            try {
                repository.register(email, password, nama, noHp)
                _isSuccess.value = true
            } catch (e: Exception) {
                _errorMsg.value = when {
                    e.message?.contains("already registered") == true -> "Email sudah terdaftar"
                    e.message?.contains("Password") == true -> "Password minimal 6 karakter"
                    else -> "Register gagal: ${e.message}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                repository.logout()
                _isSuccess.value = false
                _username.value = "Pengguna"
                _userEmail.value = ""
            } catch (e: Exception) {
                _errorMsg.value = "Logout gagal: ${e.message}"
            }
        }
    }

    /** Muat data profil dari Supabase dan perbarui SessionManager */
    fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val profile = repository.getCurrentUserProfile()
                if (profile != null) {
                    _username.value = profile.first
                    _userEmail.value = profile.second
                    sessionManager.updateUserInfo(profile.first, profile.second)
                } else {
                    _username.value = sessionManager.getUsername()
                    _userEmail.value = sessionManager.getEmail()
                }
            } catch (_: Exception) {
                _username.value = sessionManager.getUsername()
                _userEmail.value = sessionManager.getEmail()
            }
        }
    }

    /**
     * Muat data profil LENGKAP dari Supabase untuk ditampilkan di ProfileScreen.
     * Mengisi state: profileNama, userEmail, profileNoHp, username
     */
    fun loadFullProfile() {
        viewModelScope.launch {
            _isProfileLoading.value = true
            _profileError.value = null
            try {
                android.util.Log.d("ProfileDebug", "loadFullProfile: start, email=${sessionManager.getEmail()}")
                val pengguna = repository.getFullProfile()
                android.util.Log.d("ProfileDebug", "loadFullProfile: result=$pengguna")
                if (pengguna != null) {
                    _profileNama.value = pengguna.nama
                    _username.value = pengguna.username
                    _userEmail.value = pengguna.email
                    _profileNoHp.value = pengguna.no_hp ?: "-"
                    // Perbarui session dengan data terkini
                    sessionManager.updateUserInfo(pengguna.username, pengguna.email)
                } else {
                    // Fallback dari session yang tersimpan
                    _username.value = sessionManager.getUsername()
                    _userEmail.value = sessionManager.getEmail()
                    _profileError.value = "Gagal memuat profil"
                    android.util.Log.w("ProfileDebug", "loadFullProfile: pengguna null, pakai session fallback")
                }
            } catch (e: Exception) {
                _username.value = sessionManager.getUsername()
                _userEmail.value = sessionManager.getEmail()
                _profileError.value = "Gagal memuat profil: ${e.message}"
                android.util.Log.e("ProfileDebug", "loadFullProfile error: ${e.message}", e)
            } finally {
                _isProfileLoading.value = false
            }
        }
    }

    fun resetState() {
        _isSuccess.value = false
        _errorMsg.value = null
    }

    fun isLoggedIn(): Boolean = repository.isLoggedIn()
}