package com.example.planix.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.planix.databinding.ActivityRegistrationBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Кнопка "У вас уже есть аккаунт?"
        binding.logTxt.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Кнопка "Зарегистрироваться"
        binding.regBtn.setOnClickListener {
            val login = binding.loginTxt.text.toString().trim()
            val email = binding.emailTxt.text.toString().trim()
            val password = binding.passTxt.text.toString().trim()
            val repeatPassword = binding.passTxt2.text.toString().trim()

            if (login.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
                Toast.makeText(this, "Барлық өрістерді толтырыңыз", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != repeatPassword) {
                Toast.makeText(this, "Құпия сөздер сәйкес емес", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerUser(login, email, password)
        }
    }

    private fun registerUser(login: String, email: String, password: String) {
        val client = OkHttpClient()

        val requestBody = FormBody.Builder()
            .add("login", login)
            .add("email", email)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url("http://10.0.2.2:8888/planix/register.php")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@RegistrationActivity, "Серверге қосыла алмадым", Toast.LENGTH_SHORT).show()
                }
                e.printStackTrace() // <-- важно для отладки
            }

            override fun onResponse(call: Call, response: Response) {
                val responseText = response.body?.string()
                runOnUiThread {
                    when (responseText?.trim()) {
                        "success" -> {
                            Toast.makeText(this@RegistrationActivity, "Тіркелу сәтті!", Toast.LENGTH_SHORT).show()
                            // Переход на экран логина или домой
                            val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        "exists" -> Toast.makeText(this@RegistrationActivity, "Бұл логин бұрын тіркелген", Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(this@RegistrationActivity, "Қате: $responseText", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}