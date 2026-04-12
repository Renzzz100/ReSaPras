package com.example.resapras

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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
import coil.load
import com.google.gson.Gson
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class DetaillaporanScreen : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerMenu: ImageView
    private lateinit var profileImg: ImageView
    private lateinit var daftarLaporanNav: LinearLayout
    private lateinit var buatLaporanNav: LinearLayout
    private lateinit var dashboardNav: LinearLayout

    private lateinit var tvKodeLaporan: TextView
    private lateinit var tvJudul: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvKategori: TextView
    private lateinit var tvPrioritas: TextView
    private lateinit var tvLokasi: TextView
    private lateinit var tvTanggalWaktu: TextView
    private lateinit var tvDeskripsi: TextView
    private lateinit var ivBukti: ZoomableImageView

    // Admin views
    private lateinit var layoutAdminActions: LinearLayout
    private lateinit var btnUpdateStatus: TextView
    private lateinit var btnTolakLaporan: TextView

    private lateinit var sessionManager: SessionManager
    private var currentLaporan: Laporan? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.detaillaporan_screen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sessionManager = SessionManager(this)

        drawerLayout = findViewById(R.id.main)
        drawerMenu = findViewById(R.id.drawerMenu)
        profileImg = findViewById(R.id.profileImg)
        daftarLaporanNav = findViewById(R.id.daftarlaporanNav)
        buatLaporanNav = findViewById(R.id.buatlaporanNav)
        dashboardNav = findViewById(R.id.dashboardNav)

        tvKodeLaporan = findViewById(R.id.kode_laporan)
        tvJudul = findViewById(R.id.judul)
        tvStatus = findViewById(R.id.status)
        tvKategori = findViewById(R.id.kategori)
        tvPrioritas = findViewById(R.id.prioritas)
        tvLokasi = findViewById(R.id.lokasi)
        tvTanggalWaktu = findViewById(R.id.tanggal_waktu)
        tvDeskripsi = findViewById(R.id.deskripsi)
        ivBukti = findViewById(R.id.bukti_image)

        // Admin views
        layoutAdminActions = findViewById(R.id.layout_admin_actions)
        btnUpdateStatus = findViewById(R.id.btn_update_status)
        btnTolakLaporan = findViewById(R.id.btn_tolak_laporan)

        val isLoggedIn = AuthRepository().isLoggedIn()
        if (!isLoggedIn) {
            startActivity(Intent(this, LoginScreen::class.java))
            finish()
            return
        }

        val laporanDataJson = intent.getStringExtra("laporan_data")
        if (laporanDataJson != null) {
            currentLaporan = Gson().fromJson(laporanDataJson, Laporan::class.java)
            currentLaporan?.let { displayLaporanData(it) }
        }
        syncRoleFromDatabase()
        setupClickListeners()
    }
    private fun syncRoleFromDatabase() {
        lifecycleScope.launch {
            try {
                val email = sessionManager.getEmail()
                if (email.isEmpty()) return@launch

                Log.d("DetaillaporanScreen", "Syncing role for email: $email")

                val supabase = SupabaseClientProvider.client

                // Query dengan kolom "peran" (sesuai nama di database)
                val result = withContext(Dispatchers.IO) {
                    supabase.from("pengguna")
                        .select(columns = Columns.list("peran")) {
                            filter { eq("email", email) }
                        }
                        .decodeSingleOrNull<Map<String, String>>()
                }

                Log.d("DetaillaporanScreen", "Query result: $result")

                // Ambil role dari key "peran"
                val role = result?.get("peran") ?: "siswa"

                Log.d("DetaillaporanScreen", "Final role determined: $role")

                // Update SessionManager
                sessionManager.saveRole(role)

                withContext(Dispatchers.Main) {
                    if (role == "admin") {
                        layoutAdminActions.visibility = View.VISIBLE
                        Log.d("DetaillaporanScreen", "Admin actions set to VISIBLE")
                    } else {
                        Log.d("DetaillaporanScreen", "Not admin, role: $role")
                    }

                    // Refresh display
                    currentLaporan?.let { displayLaporanData(it) }
                }
            } catch (e: Exception) {
                Log.e("DetaillaporanScreen", "Error syncing role: ${e.message}", e)
            }
        }
    }
    private fun displayLaporanData(laporan: Laporan) {
        tvKodeLaporan.text = laporan.kodeLaporan
        tvJudul.text = laporan.judul
        tvStatus.text = laporan.status
        tvPrioritas.text = laporan.prioritas
        tvKategori.text = laporan.kategori ?: "-"
        tvLokasi.text = laporan.lokasi ?: "-"
        tvDeskripsi.text = laporan.deskripsi ?: "-"
        tvTanggalWaktu.text = laporan.dibuatPada.take(19).replace("T", " ")
        val role = sessionManager.getRole()
        Log.d("DetaillaporanScreen", "User role: $role")
        if (!laporan.buktiUrl.isNullOrEmpty()) {
            ivBukti.visibility = View.VISIBLE
            ivBukti.load(laporan.buktiUrl) {
                crossfade(true)
                placeholder(R.drawable.bukti_placeholder)
                error(R.drawable.bukti_placeholder)
            }
        } else {
            ivBukti.visibility = View.GONE
        }

        when (laporan.status.lowercase()) {
            "selesai" -> tvStatus.setTextColor(getColor(R.color.bg_selesai))
            "diproses" -> tvStatus.setTextColor(getColor(R.color.bg_diproses))
            "baru" -> tvStatus.setTextColor(getColor(R.color.bg_baru))
            "ditolak" -> tvStatus.setTextColor(getColor(R.color.bg_ditolak))
        }

        // Tampilkan tombol admin jika role = admin
        if (sessionManager.getRole() == "admin") {
            layoutAdminActions.visibility = View.VISIBLE
        }
    }

    private fun setupClickListeners() {
        drawerMenu.setOnClickListener { openDrawer(drawerLayout) }

        dashboardNav.setOnClickListener {
            startActivity(Intent(this, DashboardScreen::class.java))
            finish()
        }
        profileImg.setOnClickListener {
            startActivity(Intent(this, ProfileScreen::class.java))
        }
        daftarLaporanNav.setOnClickListener {
            startActivity(Intent(this, DaftarLaporan::class.java))
            finish()
        }
        buatLaporanNav.setOnClickListener {
            startActivity(Intent(this, BuatlaporanScreen::class.java))
        }

        btnUpdateStatus.setOnClickListener {
            showUpdateStatusDialog()
        }

        btnTolakLaporan.setOnClickListener {
            showTolakLaporanDialog()
        }
    }

    private fun showUpdateStatusDialog() {
        val statusList = arrayOf("baru", "diproses", "selesai")
        var selectedStatus = currentLaporan?.status ?: "baru"

        AlertDialog.Builder(this)
            .setTitle("Update Status Laporan")
            .setSingleChoiceItems(statusList, statusList.indexOfFirst {
                it.lowercase() == selectedStatus.lowercase()
            }) { _, which ->
                selectedStatus = statusList[which]
            }
            .setPositiveButton("Simpan") { _, _ ->
                updateStatus(selectedStatus)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showTolakLaporanDialog() {
        AlertDialog.Builder(this)
            .setTitle("Tolak Laporan")
            .setMessage("Laporan ini akan ditolak dan dihapus otomatis dalam 7 hari. Lanjutkan?")
            .setPositiveButton("Ya, Tolak") { _, _ ->
                tolakLaporan()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun updateStatus(newStatus: String) {
        val laporan = currentLaporan ?: return
        val supabase = SupabaseClientProvider.client

        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    supabase.from("laporan")
                        .update({ set("status", newStatus) }) {
                            filter {
                                eq("id", laporan.id)
                            }
                        }
                }
                tvStatus.text = newStatus
                when (newStatus.lowercase()) {
                    "selesai" -> tvStatus.setTextColor(getColor(R.color.bg_selesai))
                    "diproses" -> tvStatus.setTextColor(getColor(R.color.bg_diproses))
                    "baru" -> tvStatus.setTextColor(getColor(R.color.bg_baru))
                }
                Toast.makeText(this@DetaillaporanScreen, "Status berhasil diupdate", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("DetaillaporanScreen", "Error update status: ${e.message}", e)
                Toast.makeText(this@DetaillaporanScreen, "Gagal update status: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun tolakLaporan() {
        val laporan = currentLaporan ?: return
        val supabase = SupabaseClientProvider.client

        // Waktu sekarang + 7 hari
        val dihapusPada = Clock.System.now()
            .plus(7, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
            .toString()

        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    supabase.from("laporan")
                        .update({
                            set("status", "ditolak")
                            set("dihapus_pada", dihapusPada)
                        }) {
                            filter {
                                eq("id", laporan.id)
                            }
                        }
                }
                Toast.makeText(
                    this@DetaillaporanScreen,
                    "Laporan ditolak, akan dihapus dalam 7 hari",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            } catch (e: Exception) {
                Log.e("DetaillaporanScreen", "Error tolak laporan: ${e.message}", e)
                Toast.makeText(this@DetaillaporanScreen, "Gagal menolak laporan: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun openDrawer(drawerLayout: DrawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START)
    }
}