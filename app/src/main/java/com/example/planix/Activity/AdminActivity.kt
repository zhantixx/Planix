package com.example.planix.Activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.WindowCompat
import com.example.planix.R
import com.example.planix.databinding.ActivityAdminBinding

import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException
import com.example.planix.Activity.User
import android.util.Log

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var usersRecyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var loadingProgressBar: ProgressBar

    private val TAG = "AdminActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = Color.TRANSPARENT
        WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usersRecyclerView = binding.usersRecyclerView
        loadingProgressBar = binding.loadingProgressBar

        usersRecyclerView.layoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter(emptyList()) { userToDelete ->
            deleteUser(userToDelete)
        }
        usersRecyclerView.adapter = userAdapter

        fetchUsers()
    }

    private fun fetchUsers() {
        loadingProgressBar.visibility = View.VISIBLE
        val client = OkHttpClient()

        val url = "http://10.0.2.2:8888/planix/get_users.php"
        Log.d(TAG, "Qoldanushylar $url-dan juktelude")

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    loadingProgressBar.visibility = View.GONE
                    Log.e(TAG, "Jeli suranysy sattlek siz boldy: ${e.message}", e)
                    Toast.makeText(this@AdminActivity, "Qoldanushylardy jukteu qatesi: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    loadingProgressBar.visibility = View.GONE
                }

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d(TAG, "Satti jawap. Denesi: $responseBody")
                    if (responseBody != null) {
                        try {
                            val jsonArray = JSONArray(responseBody)
                            val usersList = mutableListOf<User>()
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val id = jsonObject.getString("id")
                                val login = jsonObject.getString("login")
                                val email = jsonObject.getString("email")

                                usersList.add(User(id, login, email))
                            }

                            runOnUiThread {
                                userAdapter.updateUsers(usersList)
                                if (usersList.isEmpty()) {
                                    Toast.makeText(this@AdminActivity, "Qoldanushylar tabylmady.", Toast.LENGTH_SHORT).show()
                                }
                            }

                        } catch (e: Exception) {
                            runOnUiThread {
                                Log.e(TAG, "JSON parseleu qatesi: ${e.message}", e)
                                Toast.makeText(this@AdminActivity, "Derektelerdi ondeu qatesi: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        runOnUiThread {
                            Log.w(TAG, "Jawap denesi nul")
                            Toast.makeText(this@AdminActivity, "Serverden jawap joq", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        Log.e(TAG, "Server qatesi: ${response.code}, habarlamasy: ${response.message}")
                        Toast.makeText(this@AdminActivity, "Server qatesi: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun deleteUser(user: User) {
        loadingProgressBar.visibility = View.VISIBLE
        val client = OkHttpClient()

        val url = "http://10.0.2.2:8888/planix/delete_user.php"
        Log.d(TAG, "Qoldanushyny ID: ${user.id} oshirude: $url-dan")

        val requestBody = FormBody.Builder()
            .add("id", user.id)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    loadingProgressBar.visibility = View.GONE
                    Log.e(TAG, "Oshiru suranysy sattlek siz boldy: ${e.message}", e)
                    Toast.makeText(this@AdminActivity, "Qoldanushyny joyu qatesi: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    loadingProgressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val responseText = response.body?.string()
                        Log.d(TAG, "Oshiru jawaby: $responseText")
                        if (responseText == "success") {
                            Toast.makeText(this@AdminActivity, "Qoldanushy satti joyyldy", Toast.LENGTH_SHORT).show()
                            fetchUsers()
                        } else {
                            Toast.makeText(this@AdminActivity, "Qoldanushyny joyu qatesi: $responseText", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Log.e(TAG, "Oshiru server qatesi: ${response.code}, habarlamasy: ${response.message}")
                        Toast.makeText(this@AdminActivity, "Qoldanushyny joyu server qatesi: ${response.code}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }
}
