package com.example.resapras

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg: StateFlow<String?> = _errorMsg

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMsg.value = null
            try {
                repository.login(email, password)
                _isSuccess.value = true
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
            } catch (e: Exception) {
                _errorMsg.value = "Logout gagal: ${e.message}"
            }
        }
    }

    fun resetState() {
        _isSuccess.value = false
        _errorMsg.value = null
    }
}