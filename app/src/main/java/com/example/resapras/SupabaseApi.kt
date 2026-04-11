package com.example.resapras

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SupabaseApi {
    @GET("rest/v1/laporan")
    suspend fun getLaporan(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("select") select: String = "id,kode_laporan,judul,prioritas,status,dibuat_pada",
        @Query("order") order: String = "id.desc",
        @Query("limit") limit: Int = 5
    ): Response<List<Laporan>>
}