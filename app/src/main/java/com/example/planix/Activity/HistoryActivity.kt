// com/example/planix/presentation/history/HistoryActivity.kt
package com.example.planix.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planix.Data.AppDatabase
import com.example.planix.Domain.TaskStatus
import com.example.planix.R
import com.example.planix.databinding.ActivityHistoryBinding
import com.example.planix.ViewModel.MainViewModel
import com.example.planix.ViewModel.TaskViewModelFactory
import com.example.planix.Activity.TaskActivity
import com.example.planix.Activity.ProfileActivity
import com.example.planix.Adapter.HistoryTasksAdapter

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var taskViewModel: MainViewModel
    private lateinit var historyTasksAdapter: HistoryTasksAdapter
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var addTaskLauncher: ActivityResultLauncher<Intent> // Для запуска TaskActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация ViewModel
        val viewModelFactory = TaskViewModelFactory(application)
        taskViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        // Настройка RecyclerView
        historyRecyclerView = binding.historyRecyclerView
        historyRecyclerView.layoutManager = LinearLayoutManager(this)

        historyTasksAdapter = HistoryTasksAdapter()
        historyRecyclerView.adapter = historyTasksAdapter

        taskViewModel.getTasksByStatuses(listOf(TaskStatus.ORYNDALGAN, TaskStatus.JOYULGAN)).observe(this) { tasks ->
            historyTasksAdapter.submitList(tasks)
        }



        addTaskLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == RESULT_OK) {

            }
        }


        binding.bottomBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {

                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_tasks -> {
                    addTaskLauncher.launch(Intent(this, TaskActivity::class.java))
                    true
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_story -> {
                    true
                }
                else -> false
            }
        }
        binding.bottomBar.selectedItemId = R.id.navigation_story
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}