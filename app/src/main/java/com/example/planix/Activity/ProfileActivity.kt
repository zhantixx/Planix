package com.example.planix.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.planix.R
import com.example.planix.databinding.ActivityProfileBinding
import java.io.FileNotFoundException
import java.io.InputStream

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadProfileData()

        binding.exitImg.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
        }
        binding.kelImg.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, TermsActivity::class.java))
        }
        binding.qupImg.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, PrivacyActivity::class.java))
        }
        binding.bottomBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.navigation_tasks -> {
                    startActivity(Intent(this, TaskActivity::class.java))
                    true
                }
                R.id.navigation_profile -> true

                R.id.navigation_story -> {
            startActivity(Intent(this, HistoryActivity::class.java))
            true}
                else -> false
            }
        }
        binding.profBtn.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, ProfileEditActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfileData()
    }

    private fun loadProfileData() {
        val prefs = getSharedPreferences("user_profile", MODE_PRIVATE)

        binding.profileName.text = prefs.getString("name", "Example")
        binding.profileEmail.text = prefs.getString("email", "example@example.com")
        binding.profileAddress.text = prefs.getString("address", "example")
        binding.profileLogin.text =prefs.getString("name","Example")
        binding.profilePhone.text = prefs.getString("phone", "+7 *** *** ** **")

        val imageUriString = prefs.getString("image_uri", null)
        if (imageUriString != null) {
            try {
                val imageUri = Uri.parse(imageUriString)
                val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
                if (inputStream != null) {
                    binding.imageView4.setImageURI(imageUri)
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }
}
