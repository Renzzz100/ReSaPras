package com.example.resapras

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout

class BuatLaporanScreen : AppCompatActivity() {

    // 1. Declare your views
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerMenu: ImageView
    private lateinit var profileImg: ImageView
    private lateinit var daftarLaporanNav: LinearLayout
    private lateinit var buatLaporanNav: LinearLayout
    private lateinit var dashboardNav: LinearLayout

    // Media Picker variables
    private lateinit var btnPickMedia: Button
    private lateinit var ivPreview: ImageView

    // 2. Register the Photo Picker launcher
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            ivPreview.visibility = View.VISIBLE
            ivPreview.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.buat_laporan_screen)

        // Handle System Bars (Edge-to-Edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 3. Initialize your views using findViewById
        drawerLayout = findViewById(R.id.main)
        drawerMenu = findViewById(R.id.drawerMenu)
        profileImg = findViewById(R.id.profileImg)
        daftarLaporanNav = findViewById(R.id.daftarlaporanNav)
        buatLaporanNav = findViewById(R.id.buatlaporanNav)
        dashboardNav = findViewById(R.id.dashboardNav)

        btnPickMedia = findViewById(R.id.btnPickMedia)
        ivPreview = findViewById(R.id.ivPreview)

        // 4. Set up Click Listeners
        drawerMenu.setOnClickListener {
            openDrawer(drawerLayout)
        }

        dashboardNav.setOnClickListener {
            startActivity(Intent(this, DashboardScreen::class.java))
        }

        profileImg.setOnClickListener {
            startActivity(Intent(this, ProfileScreen::class.java))
        }

        daftarLaporanNav.setOnClickListener {
            startActivity(Intent(this, DaftarLaporan::class.java))
        }

        // Fixed typo: BuatlaporanScreen -> BuatLaporanScreen
        buatLaporanNav.setOnClickListener {
            startActivity(Intent(this, BuatLaporanScreen::class.java))
        }

        // Trigger the Media Picker
        btnPickMedia.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        }
    }

    private fun openDrawer(drawerLayout: DrawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START)
    }
}