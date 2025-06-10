package com.example.planix.Activity // Убедитесь, что это правильный пакет для CategoryDetailActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planix.Adapter.TasksAdapter
import com.example.planix.Data.AppDatabase
import com.example.planix.Data.TaskEntity
import com.example.planix.databinding.ActivityCategoryDetailBinding
import com.example.planix.Domain.TaskStatus
import com.example.planix.ViewModel.MainViewModel
import com.example.planix.ViewModel.TaskViewModelFactory

class CategoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryDetailBinding
    private lateinit var adapter: TasksAdapter
    private val taskList = mutableListOf<TaskEntity>()

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = AppDatabase.getDatabase(this)
        val viewModelFactory = TaskViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        val categoryName = intent.getStringExtra("categoryName") ?: "Все категории" // По умолчанию "Все категории"


        adapter = TasksAdapter(
            taskList,
            onEditClick = { task ->

            },
            onDeleteClick = { task ->
                lifecycleScope.launchWhenStarted {
                    val updatedTask = task.copy(status = TaskStatus.JOYULGAN)
                    viewModel.updateTask(updatedTask)
                }
            },
            onCompleteClick = { task ->
                lifecycleScope.launchWhenStarted {
                    val updatedTask = task.copy(status = TaskStatus.ORYNDALGAN)
                    viewModel.updateTask(updatedTask)
                }
            }
        )
        binding.viewDetCat.layoutManager = LinearLayoutManager(this)
        binding.viewDetCat.adapter = adapter

        viewModel.getFilteredTasksByDisplayNames(categoryName).observe(this, Observer { tasks ->
            val plannedTasks = tasks.filter { it.status == TaskStatus.JOSPARLANGAN }
            taskList.clear()
            taskList.addAll(plannedTasks)
            adapter.notifyDataSetChanged()
        })


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}