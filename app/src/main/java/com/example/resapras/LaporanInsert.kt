package com.example.resapras

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LaporanInsert(
    @SerialName("kode_laporan") val kodeLaporan: String,
    @SerialName("judul") val judul: String,
    @SerialName("kategori") val kategori: String? = null,
    @SerialName("lokasi") val lokasi: String? = null,
    @SerialName("deskripsi") val deskripsi: String? = null,
    @SerialName("prioritas") val prioritas: String? = null,
    @SerialName("status") val status: String = "baru",
    @SerialName("bukti_url") val buktiUrl: String? = null,
    @SerialName("pelapor_id_uuid") val pelaporIdUuid: String? = null
)