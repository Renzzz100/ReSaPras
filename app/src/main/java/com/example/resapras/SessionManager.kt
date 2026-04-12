package com.example.resapras

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "resapras_session"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    /** Simpan sesi setelah login berhasil */
    fun saveSession(
        accessToken: String,
        refreshToken: String,
        username: String,
        email: String
    ) {
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putString(KEY_USERNAME, username)
            putString(KEY_EMAIL, email)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    /** Update hanya data profil (username & email) tanpa mengubah token */
    fun updateUserInfo(username: String, email: String) {
        prefs.edit().apply {
            putString(KEY_USERNAME, username)
            putString(KEY_EMAIL, email)
            apply()
        }
    }

    /** Hapus semua data sesi saat logout */
    fun clearSession() {
        prefs.edit().clear().apply()
    }

    /** Cek apakah pengguna sudah login */
    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    /** Ambil access token yang tersimpan */
    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    /** Ambil refresh token yang tersimpan */
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    /** Ambil username pengguna */
    fun getUsername(): String = prefs.getString(KEY_USERNAME, "Pengguna") ?: "Pengguna"

    /** Ambil email pengguna */
    fun getEmail(): String = prefs.getString(KEY_EMAIL, "") ?: ""
}
