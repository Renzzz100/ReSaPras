package com.example.resapras

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Lokasi(
    @SerialName("id") val id: Int,
    @SerialName("nama") val nama: String // sesuaikan nama kolom di tabel lokasi kamu
)