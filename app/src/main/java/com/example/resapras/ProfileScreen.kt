package com.example.resapras

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout

class ProfileScreen : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerMenu: ImageView
    private lateinit var profileImg : ImageView
    private lateinit var dashboardNav : LinearLayout
    private lateinit var daftarLaporanNav : LinearLayout
    private lateinit var buatLaporanNav : LinearLayout

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

        drawerMenu.setOnClickListener {
            openDrawer(drawerLayout)
        }
        dashboardNav.setOnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            startActivity(intent)
        }
        daftarLaporanNav.setOnClickListener {
            val intent = Intent(this, DaftarLaporan::class.java)
            startActivity(intent)
        }
    }
    private fun initViews(){
        drawerLayout = findViewById(R.id.profileScreen);
        drawerMenu = findViewById(R.id.drawerMenu);
        profileImg = findViewById(R.id.profileImg)
        dashboardNav = findViewById(R.id.dashboardNav)
        daftarLaporanNav = findViewById(R.id.daftarlaporanNav)
        profileImg.visibility = View.GONE
    }
    private fun openDrawer(drawerLayout: DrawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }
}