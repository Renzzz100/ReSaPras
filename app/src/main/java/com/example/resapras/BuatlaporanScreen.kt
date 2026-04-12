package com.example.resapras

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout

class BuatlaporanScreen : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerMenu: ImageView
    private lateinit var profileImg : ImageView
    private lateinit var dashboardNav : LinearLayout
    private lateinit var daftarLaporanNav : LinearLayout
    private lateinit var buatLaporanNav : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.buat_laporan_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        drawerLayout = findViewById(R.id.main)
        drawerMenu = findViewById(R.id.drawerMenu)
        profileImg = findViewById(R.id.profileImg)
        daftarLaporanNav = findViewById(R.id.daftarlaporanNav)
        buatLaporanNav = findViewById(R.id.buatlaporanNav)
        dashboardNav = findViewById(R.id.dashboardNav)


        drawerMenu.setOnClickListener {
            openDrawer(drawerLayout)
        }
        dashboardNav.setOnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            startActivity(intent)
        }
        profileImg.setOnClickListener {
            val intent = Intent(this, ProfileScreen::class.java)
            startActivity(intent)
        }
        daftarLaporanNav.setOnClickListener {
            val intent = Intent(this, DaftarLaporan::class.java)
            startActivity(intent)
        }
        buatLaporanNav.setOnClickListener {
            val intent = Intent(this, BuatlaporanScreen::class.java)
            startActivity(intent)
        }
        val kategoriSpinner : Spinner = findViewById(R.id.buat_laporan_input_kategori);
        val prioritasSpinner : Spinner = findViewById(R.id.buat_laporan_input_prioritas);
        val kategoriChoices = listOf("Listrik", "AC/Kipas", "Meja/Kursi", "Proyektor", "Pintu/Jendela", "Toilet", "Jaringan/Internet");
        val prioritasChoices = listOf("Rendah", "Sedang", "Tinggi", "Darurat")

        val kategoriAdapter = ArrayAdapter(
            this,
            R.layout.item_spinner_buatlaporan,
            kategoriChoices
        )

        kategoriAdapter.setDropDownViewResource(R.layout.item_spinner_buatlaporan)
        kategoriSpinner.adapter = kategoriAdapter

        val prioritasAdapter = ArrayAdapter(
            this,
            R.layout.item_spinner_buatlaporan,
            prioritasChoices
        )

        prioritasAdapter.setDropDownViewResource(R.layout.item_spinner_buatlaporan)
        prioritasSpinner.adapter = prioritasAdapter
    }
    private fun openDrawer(drawerLayout: DrawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }
}