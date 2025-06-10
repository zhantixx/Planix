package com.example.planix.Data

import androidx.room.TypeConverter
import com.example.planix.Domain.PriorityLevel
import com.example.planix.Domain.TaskCategory
import com.example.planix.Domain.TaskStatus

class Converters {

    @TypeConverter
    fun fromPriority(priority: PriorityLevel): String = priority.name

    @TypeConverter
    fun toPriority(value: String): PriorityLevel = PriorityLevel.valueOf(value)

    @TypeConverter
    fun fromStatus(status: TaskStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): TaskStatus = TaskStatus.valueOf(value.uppercase())
    @TypeConverter
    fun fromCategory(category: TaskCategory): String = category.name

    @TypeConverter
    fun toCategory(value: String): TaskCategory = TaskCategory.valueOf(value.uppercase())
}
