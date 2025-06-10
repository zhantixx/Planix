package com.example.planix.Activity

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.planix.Data.TaskEntity
import com.example.planix.Domain.*
import com.example.planix.R
import com.example.planix.ViewModel.MainViewModel
import com.example.planix.ViewModel.TaskViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.threeten.bp.LocalDateTime
import java.util.*


class TaskActivity : AppCompatActivity() {

    private lateinit var selectedUri: String
    private lateinit var fileTextView: TextView
    private lateinit var dateField: TextView
    private lateinit var timeField: TextView
    private val calendar = Calendar.getInstance()

    private val viewModel: MainViewModel by viewModels { TaskViewModelFactory(application) }

    private var isEditMode: Boolean = false
    private var existingTaskId: Int = 0

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            selectedUri = it.toString()
            fileTextView.text = "Tirkeldi: ${Uri.parse(selectedUri).lastPathSegment}"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task) // Убедитесь, что это layout для TaskActivity

        selectedUri = ""
        fileTextView = findViewById(R.id.attachedFileName)
        dateField = findViewById(R.id.dateField)
        timeField = findViewById(R.id.timeField)

        val titleInput = findViewById<EditText>(R.id.inputTitle)
        val descInput = findViewById<EditText>(R.id.inputDescription)

        val categorySpinner = findViewById<Spinner>(R.id.categorySpinner)
        val prioritySpinner = findViewById<Spinner>(R.id.prioritySpinner)
        val statusSpinner = findViewById<Spinner>(R.id.statusSpinner)

        categorySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, TaskCategory.values().map { it.displayName }) // Используйте displayName
        prioritySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, PriorityLevel.values().map { it.displayName }) // Используйте displayName
        statusSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, TaskStatus.values().map { it.displayName }) // Используйте displayName

        val taskToEdit = intent.getSerializableExtra("task") as? TaskEntity
        taskToEdit?.let { task ->
            isEditMode = true
            existingTaskId = task.id
            titleInput.setText(task.title)
            descInput.setText(task.description)

            try {
                val dateTime = LocalDateTime.parse(task.deadline) // Предполагаем формат ISO 8601
                calendar.set(dateTime.year, dateTime.monthValue - 1, dateTime.dayOfMonth, dateTime.hour, dateTime.minute)
                dateField.text = String.format("%02d.%02d.%d", dateTime.dayOfMonth, dateTime.monthValue, dateTime.year)
                timeField.text = String.format("%02d:%02d", dateTime.hour, dateTime.minute)
            } catch (e: Exception) {
                dateField.text = task.deadline
                timeField.text = ""
            }


            categorySpinner.setSelection(TaskCategory.values().indexOf(task.category))
            prioritySpinner.setSelection(PriorityLevel.values().indexOf(task.priority))
            statusSpinner.setSelection(TaskStatus.values().indexOf(task.status))

            task.attachmentUri?.let { uri ->
                selectedUri = uri
                fileTextView.text = "Tirkeldi: ${Uri.parse(selectedUri).lastPathSegment}"
            }
            findViewById<Button>(R.id.btnSave).text = "Сохранить изменения" // Меняем текст кнопки
        }


        dateField.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, y, m, d ->
                calendar.set(Calendar.YEAR, y)
                calendar.set(Calendar.MONTH, m)
                calendar.set(Calendar.DAY_OF_MONTH, d)
                dateField.text = String.format("%02d.%02d.%d", d, m + 1, y) // m+1, т.к. месяц 0-11
            }, year, month, day).show()
        }

        timeField.setOnClickListener {
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, h, m ->
                calendar.set(Calendar.HOUR_OF_DAY, h)
                calendar.set(Calendar.MINUTE, m)
                timeField.text = String.format("%02d:%02d", h, m)
            }, hour, minute, true).show()
        }

        findViewById<Button>(R.id.btnAttachFile).setOnClickListener {
            filePickerLauncher.launch(arrayOf("*/*"))
        }

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val title = titleInput.text.toString()
            val description = descInput.text.toString()

            if (dateField.text.isEmpty() || timeField.text.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, выберите дату и время", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val deadline = LocalDateTime.of(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1, // Месяц в Calendar 0-11, в LocalDateTime 1-12
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)
            )

            val category = TaskCategory.values()[categorySpinner.selectedItemPosition]
            val priority = PriorityLevel.values()[prioritySpinner.selectedItemPosition]
            val status = TaskStatus.values()[statusSpinner.selectedItemPosition]

            val task = TaskEntity(
                id = if (isEditMode) existingTaskId else 0, // Устанавливаем ID для редактирования
                title = title,
                description = description,
                deadline = deadline.toString(), // LocalDateTime.toString() использует ISO 8601 формат
                category = category,
                priority = priority,
                status = status,
                attachmentUri = if (selectedUri.isNotEmpty()) selectedUri else null
            )

            if (isEditMode) {
                viewModel.updateTask(task)
                Toast.makeText(this, "Задача обновлена!", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.addTask(task)
                Toast.makeText(this, "Задача добавлена!", Toast.LENGTH_SHORT).show()
            }

            setResult(Activity.RESULT_OK) // Не нужно передавать Intent с задачей обратно
            finish()
        }

        val bottomBar = findViewById<BottomNavigationView>(R.id.bottomBar)
        bottomBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_tasks -> {
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
        bottomBar.selectedItemId = R.id.navigation_tasks
    }
}