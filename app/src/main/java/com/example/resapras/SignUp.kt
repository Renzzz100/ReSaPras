package com.example.resapras

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SignUp : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.signup_screen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etNama = findViewById<EditText>(R.id.ph_nama)
        val etEmail = findViewById<EditText>(R.id.ph_email)
        val etNoHp = findViewById<EditText>(R.id.ph_no_handphone)
        val etSandi = findViewById<EditText>(R.id.ph_sandi)
        val btnSignup = findViewById<Button>(R.id.btnsignup)

        btnSignup.setOnClickListener {

            val nama = etNama.text.toString()
            val email = etEmail.text.toString()
            val noHp = etNoHp.text.toString()
            val sandi = etSandi.text.toString()

            if (nama.isEmpty() || email.isEmpty() || noHp.isEmpty() || sandi.isEmpty()) {
                Toast.makeText(this, "Semua field wajib diisi!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Berhasil daftar (dummy dulu)", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


