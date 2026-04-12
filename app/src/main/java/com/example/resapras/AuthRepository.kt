package com.example.resapras

import android.content.Context
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.Serializable

@Serializable
data class Pengguna(
    val nama: String,
    val username: String,
    val email: String,
    val no_hp: String? = null,
    val role: String? = null
)

class AuthRepository(private val context: Context? = null) {

    private val supabase = SupabaseClientProvider.client
    private val sessionManager: SessionManager? = context?.let { SessionManager(it) }

    suspend fun login(email: String, password: String) {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }

        val session = supabase.auth.currentSessionOrNull()
        val userEmail = session?.user?.email ?: email
        val userId = session?.user?.id ?: ""

        var username = userEmail.substringBefore("@")
        var userRole = "siswa"

        try {
            // Query dengan kolom "peran"
            val result = supabase.from("pengguna")
                .select(columns = Columns.list("username", "peran")) {
                    filter { eq("email", userEmail) }
                }
                .decodeSingleOrNull<Map<String, String>>()

            result?.get("username")?.let { username = it }
            result?.get("peran")?.let { userRole = it }

            android.util.Log.d("AuthRepo", "Login - Role fetched: $userRole")
        } catch (e: Exception) {
            android.util.Log.e("AuthRepo", "Error fetching user data: ${e.message}")
        }

        sessionManager?.saveSession(
            accessToken  = session?.accessToken ?: "",
            refreshToken = session?.refreshToken ?: "",
            username     = username,
            email        = userEmail,
            userId       = userId
        )

        sessionManager?.saveRole(userRole)
        android.util.Log.d("AuthRepo", "Role saved: $userRole")
    }
    suspend fun register(email: String, password: String, nama: String, noHp: String) {
        // ... kode sign up ...

        val username = email.substringBefore("@")
        try {
            // Insert dengan kolom "peran"
            supabase.from("pengguna").insert(
                mapOf(
                    "nama" to nama,
                    "username" to username,
                    "email" to email,
                    "no_hp" to noHp,
                    "peran" to "siswa"  // Gunakan "peran" bukan "role"
                )
            )
        } catch (e: Exception) {
            android.util.Log.e("RepoDebug", "Insert pengguna gagal: ${e.message}", e)
        }

        // ... kode session ...
        sessionManager?.saveRole("siswa")
    }
    suspend fun logout() {
        supabase.auth.signOut()
        sessionManager?.clearSession()
    }

    suspend fun restoreSession() {
        try {
            val accessToken = sessionManager?.getAccessToken() ?: return
            val refreshToken = sessionManager?.getRefreshToken() ?: return
            if (accessToken.isEmpty() || refreshToken.isEmpty()) return

            // Import session ke Supabase Auth menggunakan token yang tersimpan
            supabase.auth.importSession(
                UserSession(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    providerToken = null,
                    providerRefreshToken = null,
                    type = "",
                    expiresIn = 0,
                    tokenType = "bearer"
                ),
                autoRefresh = true
            )
        } catch (_: Exception) {
            // Jika restore gagal (token expired), biarkan user login ulang
            sessionManager?.clearSession()
        }
    }

    /**
     * Ambil data profil pengguna dari tabel pengguna.
     * Fallback ke email dari SessionManager jika Supabase auth session tidak aktif.
     */
    suspend fun getCurrentUserProfile(): Pair<String, String>? {
        return try {
            // Coba dari Supabase auth, jika null gunakan email dari SessionManager
            val userEmail = supabase.auth.currentUserOrNull()?.email
                ?: sessionManager?.getEmail()
                ?: return null

            val result = supabase.from("pengguna")
                .select(columns = Columns.list("username", "email")) {
                    filter { eq("email", userEmail) }
                }
                .decodeSingleOrNull<Pengguna>()

            if (result != null) {
                Pair(result.username, result.email)
            } else {
                Pair(userEmail.substringBefore("@"), userEmail)
            }
        } catch (e: Exception) {
            null
        }
    }


    suspend fun getFullProfile(): Pengguna? {
        return try {
            val user = supabase.auth.currentUserOrNull() ?: return null
            val metadata = user.userMetadata

            val nama = metadata?.get("display_name")
                ?.let {
                    if (it is kotlinx.serialization.json.JsonPrimitive) it.content
                    else it.toString().trim('"')
                } ?: "-"

            val noHp = metadata?.get("phone")  // ← ganti dari "no_hp" ke "phone"
                ?.let {
                    if (it is kotlinx.serialization.json.JsonPrimitive) it.content
                    else it.toString().trim('"')
                } ?: "-"

            val email = user.email ?: sessionManager?.getEmail() ?: ""
            val username = email.substringBefore("@")

            Pengguna(nama = nama, username = username, email = email, no_hp = noHp)
        } catch (e: Exception) {
            android.util.Log.e("RepoDebug", "getFullProfile error: ${e.message}", e)
            null
        }
    }

    fun getCurrentUser() = supabase.auth.currentUserOrNull()

    fun isLoggedIn(): Boolean {
        // Cek dari Supabase session
        if (supabase.auth.currentUserOrNull() != null) return true
        // Fallback: cek dari local session
        return sessionManager?.isLoggedIn() == true
    }
    fun getAccessToken(): String? {
        return supabase.auth.currentSessionOrNull()?.accessToken
    }


    suspend fun getLaporan(): List<Laporan> {
        return supabase.from("laporan")
            .select {
                order(column = "id", order = Order.DESCENDING)
                limit(count = 5)
            }
            .decodeList<Laporan>()
    }
    fun getSessionManager(): SessionManager? = sessionManager
}