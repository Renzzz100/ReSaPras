package com.example.resapras

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.resapras.LoginScreen

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen
        val splashScreen = installSplashScreen()

        // Set keep on screen condition
        splashScreen.setKeepOnScreenCondition { false }

        super.onCreate(savedInstanceState)

        // Langsung pindah ke LoginScreen
        startActivity(Intent(this, LoginScreen::class.java))
        finish()
    }
}