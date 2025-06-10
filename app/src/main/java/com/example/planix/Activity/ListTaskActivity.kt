package com.example.planix.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planix.Adapter.CategoryAdapter
import com.example.planix.Adapter.TasksAdapter
import com.example.planix.R
import com.example.planix.ViewModel.MainViewModel
import com.example.planix.databinding.ActivityListTaskBinding

class ListTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListTaskBinding
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        initList()

    }

    private fun initList() {
        binding.apply {
            progressBar33.visibility = View.VISIBLE

            viewModel.allTasks.observe(this@ListTaskActivity) { tasks ->
                viewList.layoutManager = LinearLayoutManager(
                    this@ListTaskActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                viewList.adapter = TasksAdapter(
                    taskList = tasks,
                    onEditClick = { task ->
                        val intent = Intent(this@ListTaskActivity, EditTaskActivity::class.java)
                        intent.putExtra("task", task)
                        startActivity(intent)
                    },
                    onDeleteClick = { task ->
                        viewModel.deleteTask(task)
                    },
                    onCompleteClick = { task -> // ← новое
                        viewModel.deleteTask(task)
                    }
                )
                progressBar33.visibility = View.GONE


                binding.backBtn2.setOnClickListener {
                    startActivity(Intent(this@ListTaskActivity, HomeActivity::class.java))

                }
            }
        }
    }
}


