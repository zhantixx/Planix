package com.example.planix.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.planix.R
import com.example.planix.databinding.ActivityLoginBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loginEditText: EditText = findViewById(R.id.loginTxt)
        val passwordEditText: EditText = findViewById(R.id.passTxt)
        val loginButton: Button = findViewById(R.id.logBtn)


        binding.regTxt.setOnClickListener {
            startActivity(
                Intent(
                    this@LoginActivity,
                    RegistrationActivity::class.java
                )
            )
        }

        loginButton.setOnClickListener {
            val login = loginEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Проверяем, что поля не пустые
            if (login.isNotEmpty() && password.isNotEmpty()) {
                // Check for admin credentials first
                if (login == "admin" && password == "admin") {
                    Toast.makeText(this, "Admin kiru satti", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, AdminActivity::class.java) // Assuming AdminActivity exists
                    startActivity(intent)
                    finish() // Finish LoginActivity so admin can't go back
                } else {
                    // If not admin, proceed with regular user login via network request
                    loginUser(login, password)
                }
            } else {
                Toast.makeText(this, "Barlıq auqytlar toltyrılmalı", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(login: String, password: String) {
        val client = OkHttpClient()

        val requestBody = FormBody.Builder()
            .add("login", login)
            .add("password", password)
            .build()


        val request = Request.Builder()
            .url("http://10.0.2.2:8888/planix/login.php")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Serverge qosyla almadym", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseText = response.body?.string()

                    runOnUiThread {
                        if (responseText == "success") {
                            Toast.makeText(this@LoginActivity, "Kiru satti", Toast.LENGTH_SHORT).show()
                            val intent = Intent(
                                this@LoginActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {

                            Toast.makeText(this@LoginActivity, "Login nemese qupia soz qate", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Serverden jawap alalmadym", Toast.LENGTH_SHORT).show()

                    }
                }
            }
        })
    }
}
