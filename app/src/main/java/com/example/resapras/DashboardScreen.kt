package com.example.resapras

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log
import com.google.gson.Gson
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.withContext

class DashboardScreen : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerMenu: ImageView
    private lateinit var profileImg: ImageView
    private lateinit var daftarLaporanNav: LinearLayout
    private lateinit var tvDrawerUsername: TextView
    private lateinit var tvDrawerEmail: TextView
    private lateinit var adapter: LaporanAdapter
    private lateinit var buatLaporanNav: LinearLayout
    private val apiKey = BuildConfig.SUPABASE_KEY

    private val viewModel: AuthViewModel by viewModels()

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
        buatLaporanNav = findViewById(R.id.buatlaporanNav)

        val isLoggedIn = AuthRepository().isLoggedIn()
        Log.d("DEBUG", "Is logged in: $isLoggedIn")

        if (!isLoggedIn) {
            Log.e("DEBUG", "USER TIDAK LOGIN!")
            startActivity(Intent(this, LoginScreen::class.java))
            finish()
            return
        }
        val sharedPref = getSharedPreferences("supabase", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("user_id", null)
        Log.d("DEBUG", "User ID from SharedPref: $userId")

        // Cek apakah user sudah login
        if (userId == null) {
            Log.e("DEBUG", "USER TIDAK LOGIN!")
            // Arahkan ke login screen
        }
        tvDrawerUsername = findViewById(R.id.username)
        tvDrawerEmail = findViewById(R.id.email)

        // Isi drawer dengan data dari session yang tersimpan
        val sessionManager = SessionManager(this)
        tvDrawerUsername.text = sessionManager.getUsername()
        tvDrawerEmail.text = sessionManager.getEmail()

        drawerMenu.setOnClickListener { openDrawer(drawerLayout) }

        buatLaporanNav.setOnClickListener {
            startActivity(Intent(this, BuatlaporanScreen::class.java))
        }

        profileImg.setOnClickListener {
            startActivity(Intent(this, ProfileScreen::class.java))
        }
        daftarLaporanNav.setOnClickListener {
            startActivity(Intent(this, DaftarLaporan::class.java))
        }

        setupRecyclerView()
        fetchLaporan()

        // Muat profil dari Supabase (update drawer jika ada perubahan)
        viewModel.loadUserProfile()
        observeUserProfile()
    }

    /** Pantau perubahan profil dari ViewModel dan perbarui tampilan drawer */
    private fun observeUserProfile() {
        lifecycleScope.launch {
            launch {
                viewModel.username.collect { username ->
                    if (username.isNotEmpty()) {
                        tvDrawerUsername.text = username
                    }
                }
            }
            launch {
                viewModel.userEmail.collect { email ->
                    if (email.isNotEmpty()) {
                        tvDrawerEmail.text = email
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        // Inisialisasi adapter DULU
        adapter = LaporanAdapter(emptyList()) { laporan ->
            Log.d("DashboardScreen", "Item clicked: ${laporan.judul}")
            // Kirim data laporan ke DetailLaporanScreen
            val intent = Intent(this, DetaillaporanScreen::class.java)
            intent.putExtra("laporan_data", Gson().toJson(laporan))
            startActivity(intent)
        }

        val rv = findViewById<RecyclerView>(R.id.rv_laporan)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
        rv.setHasFixedSize(true)

        Log.d("DashboardScreen", "RecyclerView setup complete")
    }

    private fun fetchLaporan() {
        val supabase = SupabaseClientProvider.client

        lifecycleScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    supabase.from("laporan")
                        .select {
                            order("id", order = Order.DESCENDING)
                            limit(5)
                        }
                        .decodeList<Laporan>()
                }

                Log.d("SUPABASE", "Data size: ${data.size}")

                if (data.isNotEmpty()) {
                    adapter.updateData(data)
                    Log.d("DashboardScreen", "Adapter updated with ${data.size} items")
                } else {
                    Log.d("DashboardScreen", "Data kosong")
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