package com.example.resapras

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class DaftarLaporan : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerMenu: ImageView
    private lateinit var profileImg: ImageView
    private lateinit var dashboardNav: LinearLayout
    private lateinit var daftarLaporanNav: LinearLayout
    private lateinit var buatLaporanNav: LinearLayout

    private val viewModel: LaporanViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.daftar_laporan)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.daftarLaporanScreen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        initClickListeners()
        observeViewModel()

        // Fetch data laporan saat activity dibuka
        viewModel.fetchLaporan()
    }

    private fun initViews() {
        drawerLayout = findViewById(R.id.daftarLaporanScreen)
        drawerMenu = findViewById(R.id.drawerMenu)
        profileImg = findViewById(R.id.profileImg)
        dashboardNav = findViewById(R.id.dashboardNav)
        daftarLaporanNav = findViewById(R.id.daftarlaporanNav)
    }

    private fun initClickListeners() {
        drawerMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        profileImg.setOnClickListener {
            startActivity(Intent(this, ProfileScreen::class.java))
        }
        dashboardNav.setOnClickListener {
            startActivity(Intent(this, DashboardScreen::class.java))
        }
        daftarLaporanNav.setOnClickListener {
            // sudah di halaman ini, tutup drawer saja
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            // Observe data laporan
            launch {
                viewModel.laporan.collect { list ->
                    // TODO: tampilkan ke RecyclerView
                    // Sementara log dulu
                    list.forEach {
                        android.util.Log.d("DaftarLaporan", "${it.kodeLaporan} - ${it.judul} [${it.status}]")
                    }
                }
            }

            // Observe loading
            launch {
                viewModel.isLoading.collect { loading ->
                    // TODO: tampilkan/sembunyikan progress bar
                    android.util.Log.d("DaftarLaporan", "Loading: $loading")
                }
            }

            // Observe sukses
            launch {
                viewModel.isSuccess.collect { msg ->
                    msg?.let {
                        Toast.makeText(this@DaftarLaporan, it, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // Observe error
            launch {
                viewModel.errorMsg.collect { err ->
                    err?.let {
                        Toast.makeText(this@DaftarLaporan, it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun openDrawer(drawerLayout: DrawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START)
    }
}