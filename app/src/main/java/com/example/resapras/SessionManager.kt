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
        private const val KEY_USER_ID = "user_id" // tambah ini
    }

    fun saveSession(
        accessToken: String,
        refreshToken: String,
        username: String,
        email: String,
        userId: String = "" // tambah parameter ini
    ) {
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putString(KEY_USERNAME, username)
            putString(KEY_EMAIL, email)
            putString(KEY_USER_ID, userId) // simpan UUID
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun updateUserInfo(username: String, email: String) {
        prefs.edit().apply {
            putString(KEY_USERNAME, username)
            putString(KEY_EMAIL, email)
            apply()
        }
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)
    fun getUsername(): String = prefs.getString(KEY_USERNAME, "Pengguna") ?: "Pengguna"
    fun getEmail(): String = prefs.getString(KEY_EMAIL, "") ?: ""
    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null) // perbaikan di sini
}