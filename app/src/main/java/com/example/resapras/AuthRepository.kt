package com.example.resapras

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable

@Serializable
data class Pengguna(
    val nama: String,
    val username: String,
    val email: String,
    val no_hp: String? = null
)

class AuthRepository {

    private val supabase = SupabaseClientProvider.client

    suspend fun login(email: String, password: String) {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun register(email: String, password: String, nama: String, noHp: String) {
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }

        supabase.from("pengguna").insert(
            Pengguna(
                nama = nama,
                username = email.substringBefore("@"), // ambil dari email, misal "john" dari "john@gmail.com"
                email = email,
                no_hp = noHp
            )
        )
    }

    suspend fun logout() {
        supabase.auth.signOut()
    }

    fun getCurrentUser() = supabase.auth.currentUserOrNull()

    fun isLoggedIn() = supabase.auth.currentUserOrNull() != null
}