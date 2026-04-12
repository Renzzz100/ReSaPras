package com.example.resapras

import android.content.Context
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
    val no_hp: String? = null
)

class AuthRepository(private val context: Context? = null) {

    private val supabase = SupabaseClientProvider.client
    private val sessionManager: SessionManager? = context?.let { SessionManager(it) }

    suspend fun login(email: String, password: String) {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }

        // Setelah login berhasil, ambil token dan data profil lalu simpan ke sesi
        val session = supabase.auth.currentSessionOrNull()
        val userEmail = session?.user?.email ?: email

        // Coba ambil data pengguna dari tabel pengguna
        var username = userEmail.substringBefore("@")
        try {
            val result = supabase.from("pengguna")
                .select(columns = Columns.list("username")) {
                    filter { eq("email", userEmail) }
                }
                .decodeSingleOrNull<Map<String, String>>()
            result?.get("username")?.let { username = it }
        } catch (_: Exception) {
            // Gunakan fallback dari email jika gagal
        }

        // Simpan ke session manager
        sessionManager?.saveSession(
            accessToken = session?.accessToken ?: "",
            refreshToken = session?.refreshToken ?: "",
            username = username,
            email = userEmail
        )
    }

    suspend fun register(email: String, password: String, nama: String, noHp: String) {
        // 1. Sign up + simpan nama & noHp ke user_metadata
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = buildJsonObject {
                put("display_name", nama)
                put("phone", noHp)        // ← pakai key custom, bukan "phone"
            }
        }

        // 2. Login agar session aktif
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }

        // 3. Insert ke tabel pengguna (session sudah aktif)
        val username = email.substringBefore("@")
        try {
            supabase.from("pengguna").insert(
                Pengguna(nama = nama, username = username, email = email, no_hp = noHp)
            )
        } catch (e: Exception) {
            android.util.Log.e("RepoDebug", "Insert pengguna gagal: ${e.message}", e)
        }

        // 4. Simpan session lokal
        val session = supabase.auth.currentSessionOrNull()
        sessionManager?.saveSession(
            accessToken = session?.accessToken ?: "",
            refreshToken = session?.refreshToken ?: "",
            username = username,
            email = email
        )
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

    fun getSessionManager(): SessionManager? = sessionManager
}