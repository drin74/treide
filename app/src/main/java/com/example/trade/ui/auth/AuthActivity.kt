package com.example.trade.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.trade.databinding.ActivityAuthBinding
import com.example.trade.ui.main.MainActivity
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private val viewModel = AuthViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (viewModel.isLoggedIn()) {
            goToMain()
            return
        }

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val success = viewModel.login(email, password)
                if (success) {
                    goToMain()
                } else {
                    Toast.makeText(this@AuthActivity, "Ошибка входа", Toast.LENGTH_SHORT).show()
                }
            }
        }


        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Пароль должен быть не менее 6 символов", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val success = viewModel.register(email, password)
                if (success) {
                    Toast.makeText(this@AuthActivity, "Регистрация успешна! Вам начислено $10,000", Toast.LENGTH_LONG).show()
                    goToMain()
                } else {
                    Toast.makeText(this@AuthActivity, "Ошибка регистрации", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}