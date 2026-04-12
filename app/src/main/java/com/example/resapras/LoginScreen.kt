package com.example.resapras

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LoginScreen : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvSignUp: TextView

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)

        // Cek session — jika sudah login, langsung ke Dashboard
        val sessionManager = SessionManager(this)
        if (sessionManager.isLoggedIn()) {
            navigateToDashboard()
            return
        }

        initViews()
        initClickListeners()
        observeViewModel()
    }

    private fun initViews() {
        etEmail = findViewById(R.id.ph_email)
        etPassword = findViewById(R.id.ph_sandi)
        btnLogin = findViewById(R.id.buttonLogin)
        tvSignUp = findViewById(R.id.signUptext)
    }

    private fun initClickListeners() {
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            if (!validateInput(email, password)) return@setOnClickListener
            viewModel.login(email, password)
        }

        tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpScreen::class.java))
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            launch {
                viewModel.isLoading.collect { loading ->
                    btnLogin.isEnabled = !loading
                    btnLogin.text = if (loading) "Loading..." else getString(R.string.login)
                }
            }
            launch {
                viewModel.isSuccess.collect { success ->
                    if (success) navigateToDashboard()
                }
            }
            launch {
                viewModel.errorMsg.collect { err ->
                    err?.let {
                        Toast.makeText(this@LoginScreen, it, Toast.LENGTH_SHORT).show()
                        viewModel.resetState()
                    }
                }
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            etEmail.error = "Email tidak boleh kosong"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Format email tidak valid"
            return false
        }
        if (password.isEmpty()) {
            etPassword.error = "Password tidak boleh kosong"
            return false
        }
        if (password.length < 6) {
            etPassword.error = "Password minimal 6 karakter"
            return false
        }
        return true
    }

    private fun navigateToDashboard() {
        startActivity(Intent(this, DashboardScreen::class.java))
        finish()
    }
}