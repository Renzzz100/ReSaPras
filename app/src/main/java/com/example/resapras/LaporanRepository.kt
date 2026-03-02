package com.example.resapras

import io.github.jan.supabase.postgrest.from

class LaporanRepository {

    private val supabase = SupabaseClientProvider.client

    suspend fun getLaporan(): List<Laporan> {
        return supabase.from("laporan").select().decodeList<Laporan>()
    }

    suspend fun getLaporanByPelapor(pelaporId: Long): List<Laporan> {
        return supabase.from("laporan").select() {
            filter { eq("pelapor_id", pelaporId) }
        }.decodeList<Laporan>()
    }

    suspend fun getLaporanById(id: Long): Laporan {
        return supabase.from("laporan").select() {
            filter { eq("id", id) }
        }.decodeSingle<Laporan>()
    }

    suspend fun tambahLaporan(
        kodeLaporan: String,
        pelaporId: Long,
        kategoriId: Long?,
        lokasiId: Long?,
        asetId: Long?,
        judul: String,
        deskripsi: String?,
        prioritas: String = "rendah"
    ) {
        supabase.from("laporan").insert(
            Laporan(
                kodeLaporan = kodeLaporan,
                pelaporId = pelaporId,
                kategoriId = kategoriId,
                lokasiId = lokasiId,
                asetId = asetId,
                judul = judul,
                deskripsi = deskripsi,
                prioritas = prioritas,
                status = "baru"
            )
        )
    }

    suspend fun updateLaporan(id: Long, judul: String, deskripsi: String?, prioritas: String) {
        supabase.from("laporan").update({
            set("judul", judul)
            set("deskripsi", deskripsi)
            set("prioritas", prioritas)
        }) {
            filter { eq("id", id) }
        }
    }

    suspend fun verifikasiLaporan(id: Long, verifikatorId: Long) {
        supabase.from("laporan").update({
            set("status", "diverifikasi")
            set("diverifikasi_oleh", verifikatorId)
            set("diverifikasi_pada", "now()")
        }) {
            filter { eq("id", id) }
        }
    }

    suspend fun tolakLaporan(id: Long, penutupId: Long, alasan: String) {
        supabase.from("laporan").update({
            set("status", "ditolak")
            set("ditutup_oleh", penutupId)
            set("ditutup_pada", "now()")
            set("alasan_penolakan", alasan)
        }) {
            filter { eq("id", id) }
        }
    }

    suspend fun tutupLaporan(id: Long, penutupId: Long) {
        supabase.from("laporan").update({
            set("status", "selesai")
            set("ditutup_oleh", penutupId)
            set("ditutup_pada", "now()")
        }) {
            filter { eq("id", id) }
        }
    }

    suspend fun deleteLaporan(id: Long) {
        supabase.from("laporan").delete {
            filter { eq("id", id) }
        }
    }
}