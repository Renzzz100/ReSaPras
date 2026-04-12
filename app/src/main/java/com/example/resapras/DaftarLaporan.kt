package com.example.resapras

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import com.google.gson.Gson
import io.github.jan.supabase.postgrest.from

class DaftarLaporan : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerMenu: ImageView
    private lateinit var profileImg: ImageView
    private lateinit var dashboardNav: LinearLayout
    private lateinit var buatLaporanNav: LinearLayout
    private lateinit var rvLaporan: RecyclerView
    private lateinit var adapter: DaftarLaporanAdapter
    private val apiKey = BuildConfig.SUPABASE_KEY
    private lateinit var daftarLaporanNav : LinearLayout
    private lateinit var tvDrawerUsername: TextView
    private lateinit var tvDrawerEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.daftar_laporan)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.daftarLaporanScreen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        drawerLayout = findViewById(R.id.daftarLaporanScreen);
        drawerMenu = findViewById(R.id.drawerMenu);
        profileImg = findViewById(R.id.profileImg)
        dashboardNav = findViewById(R.id.dashboardNav)
        buatLaporanNav = findViewById(R.id.buatlaporanNav)
        tvDrawerUsername = findViewById(R.id.username)
        tvDrawerEmail = findViewById(R.id.email)
        rvLaporan = findViewById(R.id.rv_all_laporan)

        // Isi drawer dengan data dari session
        val sessionManager = SessionManager(this)
        tvDrawerUsername.text = sessionManager.getUsername()
        tvDrawerEmail.text = sessionManager.getEmail()
        val isLoggedIn = AuthRepository().isLoggedIn()
        Log.d("DEBUG", "Is logged in: $isLoggedIn")

        if (!isLoggedIn) {
            Log.e("DEBUG", "USER TIDAK LOGIN!")
            startActivity(Intent(this, LoginScreen::class.java))
            finish()
            return
        }
        // Setup RecyclerView
        setupRecyclerView()

        // Load semua data laporan
        loadAllLaporan()

        // Setup click listeners
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = DaftarLaporanAdapter(emptyList()) { laporan ->
            Log.d("DaftarLaporan", "Detail clicked: ${laporan.judul}")
            // Kirim data laporan ke DetailLaporanScreen
            val intent = Intent(this, DetaillaporanScreen::class.java)
            intent.putExtra("laporan_data", Gson().toJson(laporan))
            startActivity(intent)
        }

        rvLaporan.layoutManager = LinearLayoutManager(this)
        rvLaporan.adapter = adapter
        rvLaporan.setHasFixedSize(true)

        Log.d("DaftarLaporan", "RecyclerView setup complete")
    }

    private fun loadAllLaporan() {
        val supabase = SupabaseClientProvider.client

        lifecycleScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    supabase.from("laporan")
                        .select()
                        .decodeList<Laporan>()
                }

                Log.d("DaftarLaporan", "Data size: ${data.size}")

                if (data.isNotEmpty()) {
                    adapter.updateData(data)
                } else {
                    Toast.makeText(this@DaftarLaporan, "Belum ada data laporan", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("DaftarLaporan", "Exception: ${e.message}", e)
                Toast.makeText(this@DaftarLaporan, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupClickListeners() {
        drawerMenu.setOnClickListener {
            openDrawer(drawerLayout)
        }
        profileImg.setOnClickListener {
            val intent = Intent(this, ProfileScreen::class.java)
            startActivity(intent)
        }
        dashboardNav.setOnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            startActivity(intent)
        }
        buatLaporanNav.setOnClickListener {
            val intent = Intent(this, BuatlaporanScreen::class.java)
            startActivity(intent)
        }

        val btnBuatLaporan = findViewById<TextView>(R.id.buat_laporan_button)
        btnBuatLaporan?.setOnClickListener {
            startActivity(Intent(this, BuatlaporanScreen::class.java))
        }
    }
    private fun openDrawer(drawerLayout: DrawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }
}