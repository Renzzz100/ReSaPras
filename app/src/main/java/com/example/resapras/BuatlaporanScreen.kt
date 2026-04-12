package com.example.resapras

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BuatlaporanScreen : AppCompatActivity() {

    // Drawer
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerMenu: ImageView
    private lateinit var profileImg: ImageView
    private lateinit var dashboardNav: LinearLayout
    private lateinit var daftarLaporanNav: LinearLayout
    private lateinit var tvDrawerUsername: TextView
    private lateinit var tvDrawerEmail: TextView

    // Form
    private lateinit var inputJudul: EditText
    private lateinit var inputKategori: EditText
    private lateinit var inputLokasi: EditText
    private lateinit var spinnerPrioritas: Spinner   // DIGANTI: Spinner
    private lateinit var inputDeskripsi: EditText
    private lateinit var btnPickMedia: Button
    private lateinit var ivPreview: ImageView
    private lateinit var btnSubmit: Button
    private lateinit var btnBatal: Button

    // File yang dipilih user
    private var selectedFileUri: Uri? = null

    // Nilai enum yang sesuai dengan database (case-sensitive!)
    private val prioritasOptions = listOf("--Pilih Prioritas--", "rendah", "sedang", "tinggi", "darurat")

    private val pickMedia = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedFileUri = uri
            ivPreview.visibility = View.VISIBLE
            ivPreview.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.buat_laporan_screen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (!AuthRepository().isLoggedIn()) {
            startActivity(Intent(this, LoginScreen::class.java))
            finish()
            return
        }

        bindViews()
        setupSpinnerPrioritas()

        val sessionManager = SessionManager(this)
        tvDrawerUsername.text = sessionManager.getUsername()
        tvDrawerEmail.text = sessionManager.getEmail()

        setupNavigasi(sessionManager)

        btnPickMedia.setOnClickListener {
            pickMedia.launch("image/*")
        }

        btnBatal.setOnClickListener { finish() }
        btnSubmit.setOnClickListener { submitLaporan(sessionManager) }
    }

    private fun bindViews() {
        drawerLayout     = findViewById(R.id.main)
        drawerMenu       = findViewById(R.id.drawerMenu)
        profileImg       = findViewById(R.id.profileImg)
        daftarLaporanNav = findViewById(R.id.daftarlaporanNav)
        dashboardNav     = findViewById(R.id.dashboardNav)
        tvDrawerUsername = findViewById(R.id.username)
        tvDrawerEmail    = findViewById(R.id.email)

        inputJudul       = findViewById(R.id.buat_laporan_input_judul)
        inputKategori    = findViewById(R.id.buat_laporan_input_kategori)
        inputLokasi      = findViewById(R.id.buat_laporan_input_lokasi)
        spinnerPrioritas = findViewById(R.id.buat_laporan_input_prioritas)  // DIGANTI
        inputDeskripsi   = findViewById(R.id.buat_laporan_input_deskripsi)
        btnPickMedia     = findViewById(R.id.btnPickMedia)
        ivPreview        = findViewById(R.id.ivPreview)
        btnSubmit        = findViewById(R.id.buat_laporan_button)
        btnBatal         = findViewById(R.id.buat_laporan_batal_button)
    }

    private fun setupSpinnerPrioritas() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            prioritasOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPrioritas.adapter = adapter
    }

    private fun setupNavigasi(sessionManager: SessionManager) {
        drawerMenu.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        profileImg.setOnClickListener { startActivity(Intent(this, ProfileScreen::class.java)) }
        dashboardNav.setOnClickListener { startActivity(Intent(this, DashboardScreen::class.java)) }
        daftarLaporanNav.setOnClickListener { startActivity(Intent(this, DaftarLaporan::class.java)) }
    }

    private suspend fun uploadBukti(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val bytes = inputStream.readBytes()
            inputStream.close()

            val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
            val extension = mimeType.substringAfterLast("/")
            val fileName = "bukti_${System.currentTimeMillis()}.$extension"
            val filePath = "laporan/$fileName"

            SupabaseClientProvider.client.storage
                .from("bukti-laporan")
                .upload(path = filePath, data = bytes) {
                    upsert = false
                    contentType = io.ktor.http.ContentType.parse(mimeType)
                }

            SupabaseClientProvider.client.storage
                .from("bukti-laporan")
                .publicUrl(filePath)

        } catch (e: Exception) {
            Log.e("BuatLaporan", "Gagal upload bukti: ${e.message}")
            null
        }
    }

    private fun submitLaporan(sessionManager: SessionManager) {
        val judul     = inputJudul.text.toString().trim()
        val kategori  = inputKategori.text.toString().trim()
        val lokasi    = inputLokasi.text.toString().trim()
        val prioritas = spinnerPrioritas.selectedItem.toString()  // DIGANTI: ambil dari Spinner
        val deskripsi = inputDeskripsi.text.toString().trim()

        // Validasi
        if (judul.isEmpty()) { inputJudul.error = "Judul tidak boleh kosong"; return }
        if (kategori.isEmpty()) { inputKategori.error = "Kategori tidak boleh kosong"; return }
        if (lokasi.isEmpty()) { inputLokasi.error = "Lokasi tidak boleh kosong"; return }
        if (prioritas == "--Pilih Prioritas--") {
            Toast.makeText(this, "Pilih tingkat prioritas", Toast.LENGTH_SHORT).show()
            return
        }
        if (deskripsi.isEmpty()) { inputDeskripsi.error = "Deskripsi tidak boleh kosong"; return }

        btnSubmit.isEnabled = false
        btnSubmit.text = "Mengirim..."

        lifecycleScope.launch {
            try {
                // Upload foto dulu jika ada
                val buktiUrl = selectedFileUri?.let { uploadBukti(it) }

                val laporan = LaporanInsert(
                    kodeLaporan   = generateKodeLaporan(),
                    judul         = judul,
                    kategori      = kategori,
                    lokasi        = lokasi,
                    deskripsi     = deskripsi,
                    prioritas     = prioritas,  // nilai dari Spinner, sudah sesuai enum DB
                    status        = "baru",
                    buktiUrl      = buktiUrl,
                    pelaporIdUuid = sessionManager.getUserId()
                )

                SupabaseClientProvider.client.postgrest["laporan"].insert(laporan)

                runOnUiThread {
                    Toast.makeText(
                        this@BuatlaporanScreen,
                        "Laporan berhasil dikirim!",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@BuatlaporanScreen, DaftarLaporan::class.java))
                    finish()
                }
            } catch (e: Exception) {
                Log.e("BuatLaporan", "Gagal submit: ${e.message}")
                runOnUiThread {
                    Toast.makeText(
                        this@BuatlaporanScreen,
                        "Gagal: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    btnSubmit.isEnabled = true
                    btnSubmit.text = getString(R.string.buat_laporan_baru)
                }
            }
        }
    }

    private fun generateKodeLaporan(): String {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return "RPT-${sdf.format(Date())}"
    }
}