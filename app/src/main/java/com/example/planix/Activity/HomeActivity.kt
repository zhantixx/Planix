package com.example.planix.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planix.Adapter.CategoryAdapter
import com.example.planix.Adapter.TasksAdapter
import com.example.planix.Data.TaskEntity
import com.example.planix.Domain.TaskStatus
import com.example.planix.R
import com.example.planix.ViewModel.MainViewModel
import com.example.planix.ViewModel.TaskViewModelFactory
import com.example.planix.databinding.ActivityHomeBinding


import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: TasksAdapter
    private val taskList = mutableListOf<TaskEntity>()
    private var allTasksFromDb: List<TaskEntity> = emptyList()

    private val viewModel: MainViewModel by viewModels { TaskViewModelFactory(application) }

    private val addTaskLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, "Satty oryndaldy!", Toast.LENGTH_SHORT).show()
        }
    }

    private val editTaskLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, "Satty oryndaldy!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNav()
        initRecycler()
        observeTasks()

        initCategory()
        binding.searchTxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterTasks(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupBottomNav() {
        binding.listTxt.setOnClickListener {
            startActivity(Intent(this, ListTaskActivity::class.java))
        }

        binding.bottomBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
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
                    startActivity(Intent(this, HistoryActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
        binding.bottomBar.selectedItemId = R.id.navigation_home

        binding.notBtn.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }
    }

    private fun initRecycler() {
        adapter = TasksAdapter(
            taskList,
            onEditClick = { task ->
                val intent = Intent(this, EditTaskActivity::class.java)
                intent.putExtra("task", task)
                editTaskLauncher.launch(intent)
            },
            onDeleteClick = { task ->
                lifecycleScope.launch {
                    val updatedTask = task.copy(status = TaskStatus.JOYULGAN)
                    viewModel.updateTask(updatedTask)
                    Toast.makeText(this@HomeActivity, "'${task.title}' Joyuldy!", Toast.LENGTH_SHORT).show()
                }
            },
            onCompleteClick = { task ->
                lifecycleScope.launch {
                    val updatedTask = task.copy(status = TaskStatus.ORYNDALGAN)
                    viewModel.updateTask(updatedTask)
                    Toast.makeText(this@HomeActivity, "'${task.title}' Oryndaldy!", Toast.LENGTH_SHORT).show()
                }
            }
        )
        binding.viewTask.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.viewTask.adapter = adapter
    }

    private fun observeTasks() {
        binding.progressBarTask.visibility = View.VISIBLE

        viewModel.allTasks.observe(this, Observer { tasks ->
            allTasksFromDb = tasks
            val plannedTasks = tasks.filter { it.status == TaskStatus.JOSPARLANGAN }
            taskList.clear()
            taskList.addAll(plannedTasks)
            adapter.notifyDataSetChanged()
            binding.progressBarTask.visibility = View.GONE
        })
    }

    private fun initCategory() {
        binding.progressBarCategory.visibility = View.VISIBLE

        viewModel.categories.observe(this) { categoryList ->
            binding.viewCat.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.viewCat.adapter = CategoryAdapter(categoryList) { selectedCategory ->
                val intent = Intent(this, CategoryDetailActivity::class.java)
                intent.putExtra("categoryName", selectedCategory.name)
                startActivity(intent)
            }

            binding.progressBarCategory.visibility = View.GONE
        }

        viewModel.loadCategories()
    }

    private fun filterTasks(query: String) {
        val filteredAndPlanned = allTasksFromDb.filter {
            it.status == TaskStatus.JOSPARLANGAN &&
                    (it.title.contains(query, ignoreCase = true) ||
                            it.description.contains(query, ignoreCase = true))
        }
        adapter.updateList(filteredAndPlanned)
    }
}


