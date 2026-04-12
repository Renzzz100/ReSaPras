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
import kotlinx.coroutines.launch

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
        setupClickListeners()
        loadProfileData()
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
    }

    private fun setupDrawer() {
        // Isi drawer dari session yang sudah tersimpan (tampil cepat tanpa network)
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
        buatLaporanNav.setOnClickListener {
            val intent = Intent(this, BuatlaporanScreen::class.java)
            startActivity(intent)
        }
    }

    /** Muat data profil dari Supabase via ViewModel */
    private fun loadProfileData() {
        viewModel.loadFullProfile()
    }

    /** Pantau perubahan data profil dari ViewModel */
    private fun observeViewModel() {
        lifecycleScope.launch {
            // Tampilkan loading state
            launch {
                viewModel.isProfileLoading.collect { loading ->
                    // Bisa tambahkan ProgressBar jika ada
                }
            }

            // Data nama
            launch {
                viewModel.profileNama.collect { nama ->
                    tvNama.text = nama
                    tvNamaAtas.text = nama
                }
            }

            // Data username (untuk drawer)
            launch {
                viewModel.username.collect { username ->
                    tvDrawerUsername.text = username
                }
            }

            // Data email
            launch {
                viewModel.userEmail.collect { email ->
                    tvEmail.text = email
                    tvDrawerEmail.text = email
                }
            }

            // Data no. telp
            launch {
                viewModel.profileNoHp.collect { noHp ->
                    tvNoTelp.text = noHp
                }
            }

            // Error
            launch {
                viewModel.profileError.collect { err ->
                    err?.let {
                        Toast.makeText(this@ProfileScreen, it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    /** Dialog konfirmasi logout */
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
        // 1. Hapus session lokal dulu (sinkron)
        SessionManager(this).clearSession()

        // 2. Langsung redirect
        val intent = Intent(this, LoginScreen::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()

        // 3. Logout dari Supabase di background (tidak perlu ditunggu)
        lifecycleScope.launch {
            viewModel.logout()
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