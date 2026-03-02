package com.example.resapras

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_screen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etEmail = findViewById<EditText>(R.id.ph_email)
        val etSandi = findViewById<EditText>(R.id.ph_sandi)
        val btnLogin = findViewById<Button>(R.id.btnlogin)

        btnLogin.setOnClickListener {

            val email = etEmail.text.toString()
            val sandi = etSandi.text.toString()

            if (email.isEmpty() || sandi.isEmpty()) {
                Toast.makeText(this, "Isi semua field!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Login berhasil (dummy)", Toast.LENGTH_SHORT).show()
            }
        }
    }
}