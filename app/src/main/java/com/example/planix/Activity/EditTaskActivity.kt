package com.example.planix.Activity

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.planix.Data.TaskEntity
import com.example.planix.Domain.*
import com.example.planix.R
import org.threeten.bp.LocalDateTime
import java.io.Serializable
import java.util.*

class EditTaskActivity : AppCompatActivity() {

    private lateinit var backImg: ImageView
    private lateinit var selectedUri: String
    private lateinit var fileTextView: TextView
    private lateinit var dateField: TextView
    private lateinit var timeField: TextView
    private val calendar = Calendar.getInstance()

    private lateinit var task: TaskEntity

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            selectedUri = it.toString()
            fileTextView.text = "Прикреплено: ${Uri.parse(selectedUri).lastPathSegment}"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        val titleInput = findViewById<EditText>(R.id.inputTitle)
        val descInput = findViewById<EditText>(R.id.inputDescription)
        fileTextView = findViewById(R.id.attachedFileName)
        dateField = findViewById(R.id.dateField)
        timeField = findViewById(R.id.timeField)
        backImg = findViewById(R.id.backImg)

        val categorySpinner = findViewById<Spinner>(R.id.categorySpinner)
        val prioritySpinner = findViewById<Spinner>(R.id.prioritySpinner)
        val statusSpinner = findViewById<Spinner>(R.id.statusSpinner)

        // Получаем задачу
        task = intent.getSerializableExtra("task") as TaskEntity

        titleInput.setText(task.title)
        descInput.setText(task.description)

        selectedUri = task.attachmentUri ?: ""
        fileTextView.text = if (selectedUri.isNotEmpty())
            "Tırkelgen: ${Uri.parse(selectedUri).lastPathSegment}" else "Fail ne prikreplön"

        val dateTime = LocalDateTime.parse(task.deadline)
        calendar.set(dateTime.year, dateTime.monthValue - 1, dateTime.dayOfMonth, dateTime.hour, dateTime.minute)

        dateField.text = String.format("%02d.%02d.%d", dateTime.dayOfMonth, dateTime.monthValue, dateTime.year)
        timeField.text = String.format("%02d:%02d", dateTime.hour, dateTime.minute)

        // Настройка спиннеров
        categorySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, TaskCategory.values())
        prioritySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, PriorityLevel.values())
        statusSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, TaskStatus.values())

        categorySpinner.setSelection(task.category.ordinal)
        prioritySpinner.setSelection(task.priority.ordinal)
        statusSpinner.setSelection(task.status.ordinal)

        // Выбор даты
        dateField.setOnClickListener {
            val y = calendar.get(Calendar.YEAR)
            val m = calendar.get(Calendar.MONTH)
            val d = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                dateField.text = String.format("%02d.%02d.%d", day, month + 1, year)
            }, y, m, d).show()
        }

        // Выбор времени
        timeField.setOnClickListener {
            val h = calendar.get(Calendar.HOUR_OF_DAY)
            val min = calendar.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                timeField.text = String.format("%02d:%02d", hour, minute)
            }, h, min, true).show()
        }
        backImg.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))

        }

        findViewById<Button>(R.id.btnAttachFile).setOnClickListener {
            filePickerLauncher.launch(arrayOf("*/*"))
        }

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val updatedTask = task.copy(
                title = titleInput.text.toString(),
                description = descInput.text.toString(),
                deadline = LocalDateTime.of(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE)
                ).toString(),
                category = categorySpinner.selectedItem as TaskCategory,
                priority = prioritySpinner.selectedItem as PriorityLevel,
                status = statusSpinner.selectedItem as TaskStatus,
                attachmentUri = if (selectedUri.isNotEmpty()) selectedUri else null
            )

            val intent = Intent()
            intent.putExtra("updatedTask", updatedTask as Serializable)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}
