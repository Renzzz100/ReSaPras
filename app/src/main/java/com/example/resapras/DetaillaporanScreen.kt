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
import com.google.gson.Gson

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
    private lateinit var ivBukti: ImageView

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

        // Format tanggal
        val formattedDate = laporan.dibuatPada.take(19).replace("T", " ")
        tvTanggalWaktu.text = formattedDate

        // TODO: Set data tambahan yang mungkin belum ada di model Laporan
        // Jika data kategori, lokasi, deskripsi belum ada di model, Anda perlu:
        // 1. Update model Laporan dengan field-field tersebut
        // 2. Atau fetch data lengkap dari API berdasarkan ID

        // Set status color
        when (laporan.status.lowercase()) {
            "selesai" -> {
                tvStatus.setTextColor(getColor(R.color.bg_selesai))
            }
            "diproses" -> {
                tvStatus.setTextColor(getColor(R.color.bg_diproses))
            }
            "baru" -> {
                tvStatus.setTextColor(getColor(R.color.bg_baru))
            }
            "ditolak" -> {
                tvStatus.setTextColor(getColor(R.color.bg_ditolak))
            }
        }

        // TODO: Load image jika ada URL bukti
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