package com.example.resapras

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Laporan(
    @SerialName("id") val id: Int,
    @SerialName("kode_laporan") val kodeLaporan: String,
    @SerialName("judul") val judul: String,
    @SerialName("prioritas") val prioritas: String,
    @SerialName("status") val status: String,
    @SerialName("dibuat_pada") val dibuatPada: String,
    @SerialName("kategori") val kategori: String? = null,
    @SerialName("lokasi") val lokasi: String? = null,
    @SerialName("deskripsi") val deskripsi: String? = null,
    @SerialName("bukti_url") val buktiUrl: String? = null,
    @SerialName("pelapor_id_uuid") val pelaporIdUuid: String? = null,
    @SerialName("dihapus_pada") val dihapusPada: String? = null
)