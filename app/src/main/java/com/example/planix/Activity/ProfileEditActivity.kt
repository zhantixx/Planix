// ProfileEditActivity.kt
package com.example.planix.Activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.planix.databinding.ActivityProfileEditBinding
import com.google.android.material.button.MaterialButton
import android.graphics.drawable.Drawable

class ProfileEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileEditBinding
    private val IMAGE_PICK_CODE = 1000
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("user_profile", MODE_PRIVATE)

        val nameInput = findViewById<EditText>(com.example.planix.R.id.input_name)
        val addressInput = findViewById<EditText>(com.example.planix.R.id.input_dob)
        val emailInput = findViewById<EditText>(com.example.planix.R.id.input_email)
        val phoneInput = findViewById<EditText>(com.example.planix.R.id.input_phone)
        val btnSave = findViewById<MaterialButton>(com.example.planix.R.id.btn_update)

        nameInput.setText(prefs.getString("name", ""))
        addressInput.setText(prefs.getString("address", ""))
        emailInput.setText(prefs.getString("email", ""))
        phoneInput.setText(prefs.getString("phone", ""))

        val imageUriString = prefs.getString("image_uri", null)
        if (imageUriString != null) {
            try {
                selectedImageUri = Uri.parse(imageUriString)
                val inputStream = contentResolver.openInputStream(selectedImageUri!!)
                val drawable = Drawable.createFromStream(inputStream, selectedImageUri.toString())
                binding.editImg.setImageDrawable(drawable)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        btnSave.setOnClickListener {
            val editor = prefs.edit()
            editor.putString("name", nameInput.text.toString())
            editor.putString("address", addressInput.text.toString())
            editor.putString("email", emailInput.text.toString())
            editor.putString("phone", phoneInput.text.toString())
            selectedImageUri?.let { editor.putString("image_uri", it.toString()) }
            editor.apply()
            setResult(Activity.RESULT_OK)
            finish()
        }

        binding.editImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        binding.backButton.setOnClickListener {
            startActivity(Intent(this@ProfileEditActivity, ProfileActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            try {
                contentResolver.takePersistableUriPermission(
                    selectedImageUri!!,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                val inputStream = contentResolver.openInputStream(selectedImageUri!!)
                val drawable = Drawable.createFromStream(inputStream, selectedImageUri.toString())
                binding.editImg.setImageDrawable(drawable)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
