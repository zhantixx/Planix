package com.example.planix.Data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.planix.Domain.TaskStatus

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)


    @Query("SELECT * FROM tasks ORDER BY deadline ASC")
    fun getAllTasks(): LiveData<List<TaskEntity>>


    @Query("SELECT * FROM tasks WHERE status IN (:statuses) ORDER BY deadline DESC")
    fun getTasksByStatuses(statuses: List<TaskStatus>): LiveData<List<TaskEntity>>

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Update
    suspend fun update(task: TaskEntity)
}