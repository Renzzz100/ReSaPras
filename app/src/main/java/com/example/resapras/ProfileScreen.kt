package com.example.resapras

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log

class ProfileScreen : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerMenu: ImageView
    private lateinit var profileImg: ImageView
    private lateinit var daftarLaporanNav: LinearLayout
    private lateinit var buatLaporanNav: LinearLayout
    private lateinit var dashboardNav: LinearLayout

    // Drawer
    private lateinit var tvDrawerUsername: TextView
    private lateinit var tvDrawerEmail: TextView

    // Konten profil
    private lateinit var tvNamaAtas: TextView
    private lateinit var tvNama: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvNoTelp: TextView
    private lateinit var btnGantiPassword: TextView
    private lateinit var btnKeluar: TextView

    // Riwayat laporan
    private lateinit var rvRiwayatLaporan: RecyclerView
    private lateinit var tvEmptyRiwayat: TextView
    private lateinit var riwayatAdapter: RiwayatLaporanAdapter

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.profile_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profileScreen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupDrawer()
        setupRecyclerView()
        setupClickListeners()
        loadProfileData()
        loadRiwayatLaporan()
        observeViewModel()
    }

    private fun initViews() {
        drawerLayout = findViewById(R.id.profileScreen)
        drawerMenu = findViewById(R.id.drawerMenu)
        profileImg = findViewById(R.id.profileImg)
        daftarLaporanNav = findViewById(R.id.daftarlaporanNav)
        dashboardNav = findViewById(R.id.dashboardNav)
        buatLaporanNav = findViewById(R.id.buatlaporanNav)

        // Drawer header
        tvDrawerUsername = findViewById(R.id.username)
        tvDrawerEmail = findViewById(R.id.email)

        // Konten profil
        tvNamaAtas = findViewById(R.id.tv_profile_nama_atas)
        tvNama = findViewById(R.id.tv_profile_nama)
        tvEmail = findViewById(R.id.tv_profile_email)
        tvNoTelp = findViewById(R.id.tv_profile_notelp)
        btnGantiPassword = findViewById(R.id.btn_ganti_password)
        btnKeluar = findViewById(R.id.btn_keluar)

        // Riwayat laporan
        rvRiwayatLaporan = findViewById(R.id.rv_riwayat_laporan)
        tvEmptyRiwayat = findViewById(R.id.tv_empty_riwayat)
    }

    private fun setupRecyclerView() {
        riwayatAdapter = RiwayatLaporanAdapter(emptyList()) { laporan ->
            // Klik item riwayat laporan
            val intent = Intent(this, DetaillaporanScreen::class.java)
            intent.putExtra("laporan_data", Gson().toJson(laporan))
            startActivity(intent)
        }

        rvRiwayatLaporan.layoutManager = LinearLayoutManager(this)
        rvRiwayatLaporan.adapter = riwayatAdapter
        rvRiwayatLaporan.setHasFixedSize(true)
    }

    private fun setupDrawer() {
        val sessionManager = SessionManager(this)
        tvDrawerUsername.text = sessionManager.getUsername()
        tvDrawerEmail.text = sessionManager.getEmail()

        drawerMenu.setOnClickListener { openDrawer(drawerLayout) }
    }

    private fun setupClickListeners() {
        dashboardNav.setOnClickListener {
            startActivity(Intent(this, DashboardScreen::class.java))
        }
        daftarLaporanNav.setOnClickListener {
            startActivity(Intent(this, DaftarLaporan::class.java))
        }
        buatLaporanNav.setOnClickListener {
            startActivity(Intent(this, BuatlaporanScreen::class.java))
        }

        btnGantiPassword.setOnClickListener {
            Toast.makeText(this, "Fitur ganti password akan segera hadir", Toast.LENGTH_SHORT).show()
        }

        btnKeluar.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun loadProfileData() {
        viewModel.loadFullProfile()
    }

    private fun loadRiwayatLaporan() {
        val sessionManager = SessionManager(this)
        val userId = sessionManager.getUserId() ?: return

        lifecycleScope.launch {
            try {
                val supabase = SupabaseClientProvider.client

                val data = withContext(Dispatchers.IO) {
                    supabase.from("laporan")
                        .select {
                            filter {
                                eq("pelapor_id_uuid", userId)
                            }
                            order("id", order = Order.DESCENDING)
                        }
                        .decodeList<Laporan>()
                }

                withContext(Dispatchers.Main) {
                    if (data.isNotEmpty()) {
                        riwayatAdapter.updateData(data)
                        rvRiwayatLaporan.visibility = View.VISIBLE
                        tvEmptyRiwayat.visibility = View.GONE
                    } else {
                        rvRiwayatLaporan.visibility = View.GONE
                        tvEmptyRiwayat.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfileScreen", "Error loading riwayat: ${e.message}")
                withContext(Dispatchers.Main) {
                    rvRiwayatLaporan.visibility = View.GONE
                    tvEmptyRiwayat.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            launch {
                viewModel.isProfileLoading.collect { loading ->
                    // Loading state jika perlu
                }
            }

            launch {
                viewModel.profileNama.collect { nama ->
                    tvNama.text = nama
                    tvNamaAtas.text = nama
                }
            }

            launch {
                viewModel.username.collect { username ->
                    tvDrawerUsername.text = username
                }
            }

            launch {
                viewModel.userEmail.collect { email ->
                    tvEmail.text = email
                    tvDrawerEmail.text = email
                }
            }

            launch {
                viewModel.profileNoHp.collect { noHp ->
                    tvNoTelp.text = noHp
                }
            }

            launch {
                viewModel.profileError.collect { err ->
                    err?.let {
                        Toast.makeText(this@ProfileScreen, it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Keluar dari Akun")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya, Keluar") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun performLogout() {
        SessionManager(this).clearSession()

        val intent = Intent(this, LoginScreen::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()

        lifecycleScope.launch {
            viewModel.logout()
        }
    }

    private fun openDrawer(drawerLayout: DrawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START)
    }
}