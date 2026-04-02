package com.example.resapras

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.resapras.LoginScreen

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        //Kontrol SplashScreen
        splashScreen.setKeepOnScreenCondition { false }
        startActivity(Intent(this, LoginScreen::class.java))
        finish()
    }
}