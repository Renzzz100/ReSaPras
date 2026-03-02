package com.example.resapras

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth
import io.ktor.client.engine.okhttp.OkHttp

object SupabaseClientProvider {
    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://qmsjyifraqzolqsuhncm.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFtc2p5aWZyYXF6b2xxc3VobmNtIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzEzOTg0MTcsImV4cCI6MjA4Njk3NDQxN30.be-KPcWxkIRs0qe8QTQI8CQMjPDlz2ITr17nIMmbB94"
    ) {
        install(Postgrest)
        install(Auth)
        httpEngine = OkHttp.create {
            config {
                followRedirects(true)
            }
        }
    }
}