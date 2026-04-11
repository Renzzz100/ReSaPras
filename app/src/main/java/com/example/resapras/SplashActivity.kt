package com.example.resapras

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Terapkan theme khusus SEBELUM super.onCreate
        setTheme(R.style.Theme_Splash)
        super.onCreate(savedInstanceState)

        // Set fullscreen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Tampilkan layout splash
        setContentView(R.layout.splashscreen)

        // Animasi fade in untuk logo
        val logo = findViewById<ImageView>(R.id.logo)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        logo.startAnimation(fadeIn)

        // Delay 2 detik lalu pindah ke LoginScreen
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginScreen::class.java)
            startActivity(intent)

            // Animasi transisi
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

            // Tutup SplashActivity
            finish()
        }, 2000) // 2000 ms = 2 detik
    }
}