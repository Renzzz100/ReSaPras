package com.example.resapras

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        val sessionManager = SessionManager(this)
        val repository = AuthRepository(this)

        // Restore Supabase session di background, lalu navigasi setelah splash selesai
        Handler(Looper.getMainLooper()).postDelayed({
            if (sessionManager.isLoggedIn()) {
                // Restore session Supabase agar query DB bisa berjalan tanpa login ulang
                CoroutineScope(Dispatchers.IO).launch {
                    repository.restoreSession()
                    withContext(Dispatchers.Main) {
                        navigateTo(DashboardScreen::class.java)
                    }
                }
            } else {
                navigateTo(LoginScreen::class.java)
            }
        }, 2000)
    }

    private fun navigateTo(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}