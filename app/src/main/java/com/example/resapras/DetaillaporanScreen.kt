package com.example.resapras

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.gson.Gson
import coil.load

class DetaillaporanScreen : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerMenu: ImageView
    private lateinit var profileImg : ImageView
    private lateinit var daftarLaporanNav : LinearLayout
    private lateinit var buatLaporanNav : LinearLayout
    private lateinit var dashboardNav : LinearLayout

    // TextView components
    private lateinit var tvKodeLaporan: TextView
    private lateinit var tvJudul: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvKategori: TextView
    private lateinit var tvPrioritas: TextView
    private lateinit var tvLokasi: TextView
    private lateinit var tvTanggalWaktu: TextView
    private lateinit var tvDeskripsi: TextView
    private lateinit var ivBukti: ZoomableImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.detaillaporan_screen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        drawerLayout = findViewById(R.id.main)
        drawerMenu = findViewById(R.id.drawerMenu)
        profileImg = findViewById(R.id.profileImg)
        daftarLaporanNav = findViewById(R.id.daftarlaporanNav)
        buatLaporanNav = findViewById(R.id.buatlaporanNav)
        dashboardNav = findViewById(R.id.dashboardNav)

        // Initialize TextViews
        tvKodeLaporan = findViewById(R.id.kode_laporan)
        tvJudul = findViewById(R.id.judul)
        tvStatus = findViewById(R.id.status)
        tvKategori = findViewById(R.id.kategori)
        tvPrioritas = findViewById(R.id.prioritas)
        tvLokasi = findViewById(R.id.lokasi)
        tvTanggalWaktu = findViewById(R.id.tanggal_waktu)
        tvDeskripsi = findViewById(R.id.deskripsi)
        ivBukti = findViewById(R.id.bukti_image)

        // Get data from intent
        val laporanDataJson = intent.getStringExtra("laporan_data")
        val isLoggedIn = AuthRepository().isLoggedIn()
        Log.d("DEBUG", "Is logged in: $isLoggedIn")

        if (!isLoggedIn) {
            Log.e("DEBUG", "USER TIDAK LOGIN!")
            startActivity(Intent(this, LoginScreen::class.java))
            finish()
            return
        }

        if (laporanDataJson != null) {
            val laporan = Gson().fromJson(laporanDataJson, Laporan::class.java)
            displayLaporanData(laporan)
        }

        setupClickListeners()
    }

    private fun displayLaporanData(laporan: Laporan) {
        tvKodeLaporan.text = laporan.kodeLaporan
        tvJudul.text = laporan.judul
        tvStatus.text = laporan.status
        tvPrioritas.text = laporan.prioritas
        tvKategori.text = laporan.kategori ?: "-"
        tvLokasi.text = laporan.lokasi ?: "-"
        tvDeskripsi.text = laporan.deskripsi ?: "-"

        val formattedDate = laporan.dibuatPada.take(19).replace("T", " ")
        tvTanggalWaktu.text = formattedDate

        // Load bukti image
        if (!laporan.buktiUrl.isNullOrEmpty()) {
            ivBukti.visibility = android.view.View.VISIBLE
            ivBukti.load(laporan.buktiUrl) {
                crossfade(true)
                placeholder(R.drawable.bukti_placeholder)
                error(R.drawable.bukti_placeholder)
            }
        } else {
            ivBukti.visibility = android.view.View.GONE
        }

        when (laporan.status.lowercase()) {
            "selesai" -> tvStatus.setTextColor(getColor(R.color.bg_selesai))
            "diproses" -> tvStatus.setTextColor(getColor(R.color.bg_diproses))
            "baru" -> tvStatus.setTextColor(getColor(R.color.bg_baru))
            "ditolak" -> tvStatus.setTextColor(getColor(R.color.bg_ditolak))
        }
    }

    private fun setupClickListeners() {
        drawerMenu.setOnClickListener {
            openDrawer(drawerLayout)
        }

        dashboardNav.setOnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            startActivity(intent)
            finish()
        }

        profileImg.setOnClickListener {
            val intent = Intent(this, ProfileScreen::class.java)
            startActivity(intent)
        }

        daftarLaporanNav.setOnClickListener {
            val intent = Intent(this, DaftarLaporan::class.java)
            startActivity(intent)
            finish()
        }

        buatLaporanNav.setOnClickListener {
            val intent = Intent(this, BuatlaporanScreen::class.java)
            startActivity(intent)
        }
    }

    private fun openDrawer(drawerLayout: DrawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START)
    }
}