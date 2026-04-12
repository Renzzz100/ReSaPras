package com.example.resapras

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class SignUpScreen : AppCompatActivity() {

    private lateinit var etNama: EditText
    private lateinit var etEmail: EditText
    private lateinit var etNoHp: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up_screen)
        initViews()
        initClickListeners()
        observeViewModel()
    }

    private fun initViews() {
        etNama = findViewById(R.id.ph_nama)
        etEmail = findViewById(R.id.ph_email)
        etNoHp = findViewById(R.id.ph_no_handphone)
        etPassword = findViewById(R.id.ph_sandi)
        btnRegister = findViewById(R.id.buttonRegister)
    }

    private fun initClickListeners() {
        btnRegister.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val noHp = etNoHp.text.toString().trim()
            val password = etPassword.text.toString().trim()
            if (!validateInput(nama, email, noHp, password)) return@setOnClickListener
            viewModel.register(email, password, nama, noHp)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            launch {
                viewModel.isLoading.collect { loading ->
                    btnRegister.isEnabled = !loading
                    btnRegister.text = if (loading) "Loading..." else getString(R.string.register)
                }
            }
            launch {
                viewModel.isSuccess.collect { success ->
                    if (success) {
                        Toast.makeText(
                            this@SignUpScreen,
                            "Register berhasil! Silakan login.",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                }
            }
            launch {
                viewModel.errorMsg.collect { err ->
                    err?.let {
                        Toast.makeText(this@SignUpScreen, it, Toast.LENGTH_SHORT).show()
                        viewModel.resetState()
                    }
                }
            }
        }
    }

    private fun validateInput(
        nama: String,
        email: String,
        noHp: String,
        password: String
    ): Boolean {
        if (nama.isEmpty()) {
            etNama.error = "Nama tidak boleh kosong"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Format email tidak valid"
            return false
        }
        if (noHp.isEmpty()) {
            etNoHp.error = "No handphone tidak boleh kosong"
            return false
        }
        if (password.length < 6) {
            etPassword.error = "Password minimal 6 karakter"
            return false
        }
        return true
    }
}