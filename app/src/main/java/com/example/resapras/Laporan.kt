package com.example.resapras

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Laporan(
    val id: Long = 0,

    @SerialName("kode_laporan")
    val kodeLaporan: String = "",

    @SerialName("pelapor_id")
    val pelaporId: Long? = null,

    @SerialName("kategori_id")
    val kategoriId: Long? = null,

    @SerialName("lokasi_id")
    val lokasiId: Long? = null,

    @SerialName("aset_id")
    val asetId: Long? = null,

    val judul: String,

    val deskripsi: String? = null,

    val prioritas: String? = "rendah", // enum: rendah, sedang, tinggi (sesuaikan)

    val status: String? = "baru", // enum: baru, proses, selesai, ditolak (sesuaikan)

    @SerialName("diverifikasi_oleh")
    val diverifikasiOleh: Long? = null,

    @SerialName("diverifikasi_pada")
    val diverifikasiPada: String? = null,

    @SerialName("ditutup_oleh")
    val ditutupOleh: Long? = null,

    @SerialName("ditutup_pada")
    val ditutupPada: String? = null,

    @SerialName("alasan_penolakan")
    val alasanPenolakan: String? = null,

    @SerialName("dibuat_pada")
    val dibuatPada: String? = null,

    @SerialName("diperbarui_pada")
    val diperbaruiPada: String? = null
)