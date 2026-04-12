package com.example.resapras

import com.google.gson.annotations.SerializedName  // ← tambahkan ini

data class Laporan(
    @SerializedName("id") val id: Int,
    @SerializedName("kode_laporan") val kodeLaporan: String,
    @SerializedName("judul") val judul: String,
    @SerializedName("prioritas") val prioritas: String,
    @SerializedName("status") val status: String,
    @SerializedName("dibuat_pada") val dibuatPada: String
)