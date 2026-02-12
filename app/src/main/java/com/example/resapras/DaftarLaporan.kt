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

class DaftarLaporan : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerMenu: ImageView
    private lateinit var profileImg : ImageView
    private lateinit var dashboardNav : LinearLayout
    private lateinit var daftarLaporanNav : LinearLayout
    private lateinit var buatLaporanNav : LinearLayout

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

    }
    private fun openDrawer(drawerLayout: DrawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }
}