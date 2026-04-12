package com.example.resapras

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Kategori(
    @SerialName("id") val id: Int,
    @SerialName("nama") val nama: String // sesuaikan nama kolom di tabel kategori kamu
)