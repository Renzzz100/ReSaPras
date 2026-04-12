package com.example.resapras

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log
import com.example.resapras.BuildConfig

class DashboardScreen : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerMenu: ImageView
    private lateinit var profileImg: ImageView
    private lateinit var daftarLaporanNav: LinearLayout
    private lateinit var buatLaporanNav : LinearLayout
    private lateinit var adapter: LaporanAdapter
    private val apiKey = BuildConfig.SUPABASE_KEY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.dashboardscreen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        drawerLayout = findViewById(R.id.drawerLayout)
        drawerMenu = findViewById(R.id.drawerMenu)
        profileImg = findViewById(R.id.profileImg)
        daftarLaporanNav = findViewById(R.id.daftarlaporanNav)

        drawerMenu.setOnClickListener { openDrawer(drawerLayout) }
        profileImg.setOnClickListener {
            startActivity(Intent(this, ProfileScreen::class.java))
        }
        daftarLaporanNav.setOnClickListener {
            startActivity(Intent(this, DaftarLaporan::class.java))
        }

        setupRecyclerView()
        val dummyData = listOf(
            Laporan(
                id = 1,
                kodeLaporan = "LAP001",
                judul = "Test Laporan 1",
                prioritas = "Tinggi",
                status = "Pending",
                dibuatPada = "2026-04-11"
            ),
            Laporan(
                id = 2,
                kodeLaporan = "LAP002",
                judul = "Test Laporan 2",
                prioritas = "Sedang",
                status = "Proses",
                dibuatPada = "2026-04-11"
            )
        )
        adapter.updateData(dummyData)
        Log.d("DashboardScreen", "Dummy data loaded: ${dummyData.size}")

        fetchLaporan()
    }

    private fun setupRecyclerView() {
        // Inisialisasi adapter DULU
        adapter = LaporanAdapter(emptyList()) { laporan ->
            Log.d("DashboardScreen", "Item clicked: ${laporan.judul}")
            // Navigasi ke detail
            //val intent = Intent(this, DetaillaporanScreen::class.java)
            //intent.putExtra("laporan_id", laporan.id)
            //startActivity(intent)
        }

        val rv = findViewById<RecyclerView>(R.id.rv_laporan)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        // PENTING: Set hasFixedSize untuk performa
        rv.setHasFixedSize(true)


        Log.d("DashboardScreen", "RecyclerView setup complete")
    }

    private fun fetchLaporan() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.api.getLaporan(
                    apiKey = apiKey,
                    auth = "Bearer $apiKey"
                )

                Log.d("SUPABASE", "Code: ${response.code()}")

                if (response.isSuccessful) {
                    val data = response.body()
                    Log.d("SUPABASE", "Raw Body: ${response.body()?.toString()}")
                    Log.d("SUPABASE", "Parsed Data: $data")
                    Log.d("SUPABASE", "Data size: ${data?.size ?: 0}")

                    withContext(Dispatchers.Main) {
                        if (!data.isNullOrEmpty()) {
                            adapter.updateData(data)
                            Log.d("DashboardScreen", "Adapter updated with ${data.size} items")
                        } else {
                            Log.d("DashboardScreen", "Data is null or empty")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("SUPABASE", "Exception: ${e.message}", e)
            }
        }
    }

    private fun openDrawer(drawerLayout: DrawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun closeDrawer(drawerLayout: DrawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }
}