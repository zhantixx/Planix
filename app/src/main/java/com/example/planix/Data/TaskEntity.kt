package com.example.planix.Data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.planix.Domain.PriorityLevel
import com.example.planix.Domain.TaskCategory
import com.example.planix.Domain.TaskStatus
import java.io.Serializable
import java.time.LocalDateTime

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val deadline: String,
    val priority: PriorityLevel,
    val category: TaskCategory,
    val status: TaskStatus,
    val attachmentUri: String? = null
) : Serializable
interface TaskActionListener {
    fun onTaskCompleted(task: TaskEntity)
    fun onTaskDeleted(task: TaskEntity)
}
