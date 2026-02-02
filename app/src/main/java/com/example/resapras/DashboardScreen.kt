package com.example.resapras

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import android.widget.LinearLayout
import android.view.View
import android.content.Intent

class DashboardScreen : AppCompatActivity() {

    private lateinit var profileInfo: LinearLayout
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerMenu: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.dashboardscreen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        drawerLayout = findViewById(R.id.drawerLayout)
        drawerMenu = findViewById(R.id.drawerMenu)

        val drawerNavLayout = findViewById<View>(R.id.drawerNavLayout)

        profileInfo = drawerNavLayout.findViewById(R.id.profileInfo)

        drawerMenu.setOnClickListener {
            openDrawer(drawerLayout)
        }

        profileInfo.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
            closeDrawer(drawerLayout)
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
