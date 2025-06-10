package com.example.planix.ViewModel


import android.app.Application
import androidx.lifecycle.*
import com.example.planix.Data.AppDatabase
import com.example.planix.Data.TaskEntity
import com.example.planix.Domain.CategoryModel
import com.example.planix.Domain.PriorityLevel
import com.example.planix.Domain.TaskCategory
import com.example.planix.Domain.TaskStatus
import com.example.planix.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val taskDao = AppDatabase.getDatabase(application).taskDao()


    val allTasks: LiveData<List<TaskEntity>> = taskDao.getAllTasks()

    private val _categories = MutableLiveData<List<CategoryModel>>()
    val categories: LiveData<List<CategoryModel>> get() = _categories

    fun loadCategories() {
        val staticCategories = listOf(
            CategoryModel(1, "Kundelikti", R.drawable.everyday),
            CategoryModel(2, "Tömen", R.drawable.priority3),
            CategoryModel(3, "Ortaşa", R.drawable.priority2),
            CategoryModel(4, "Joğargy", R.drawable.priority1)
        )
        _categories.value = staticCategories
    }
    // --------------------------------------------------------------------------------------


    fun addTask(task: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.insertTask(task)
        }
    }

    fun updateTask(updatedTask: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.update(updatedTask)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.deleteTask(task)
        }
    }

    fun getTasksByStatuses(statuses: List<TaskStatus>): LiveData<List<TaskEntity>> {
        return taskDao.getTasksByStatuses(statuses)
    }


    fun getFilteredTasksByDisplayNames(filterName: String): LiveData<List<TaskEntity>> {
        return allTasks.map { list ->
            list.filter { task ->
                task.category.displayName.equals(filterName, ignoreCase = true) ||
                        // Фильтрация по PriorityLevel displayName
                        task.priority.displayName.equals(filterName, ignoreCase = true)
            }
        }
    }

    fun getFilteredTasksByEnum(category: TaskCategory?, priority: PriorityLevel?): LiveData<List<TaskEntity>> {
        return allTasks.map { list ->
            list.filter { task ->
                (category == null || task.category == category) &&
                        (priority == null || task.priority == priority)
            }
        }
    }
}